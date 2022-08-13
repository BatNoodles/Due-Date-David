package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import reactor.core.publisher.Mono;

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
        return event.reply("Successfully joined the course " + course).withEphemeral(true);
    }
}
