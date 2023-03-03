package commands.remove;

import commands.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class RemoveCourseCommand implements SlashCommand {
    @Override
    public String name() {
        return "course";
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        return null;
    }
}
