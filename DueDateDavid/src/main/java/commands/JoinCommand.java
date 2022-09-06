package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class JoinCommand implements SlashCommand{

    @Override
    public String name() {
        return "join";
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        Database database = Database.getInstance();
        EventAdapter adapter = new EventAdapter(event);
        Long userId = event.getInteraction().getUser().getId().asLong();
        String course = adapter.optionAsString("course").toUpperCase();

        if(database.userInCourse(course, userId)) return event.reply("You are already in " + course).withEphemeral(true);

        database.joinCourse(course, userId);
        try {
            Database.save();
        } catch (IOException e) {
            return event.reply("You have joined the course but there was an error saving to disk. Please contact the developer.").withEphemeral(true);
        }

        return event.reply("Successfully joined the course " + course).withEphemeral(true);
    }
}
