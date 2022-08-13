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
        EventAdapter adapter = new EventAdapter(event);
        String course = adapter.optionAsString("course").toUpperCase();
        Database.getInstance().joinCourse(course, event.getInteraction().getUser().getId().asLong());
        return event.reply("Successfully joined the course " + course).withEphemeral(true);
    }
}
