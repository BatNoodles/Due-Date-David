package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

/**
 * Representation of a slash command.
 */
public interface SlashCommand {
    String name();
    Mono<Void> handleCommand(ChatInputInteractionEvent event);
}
