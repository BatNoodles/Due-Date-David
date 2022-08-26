package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

/**
 * Representation of a slash command.
 */
public interface SlashCommand {
    /**
     * Gets the name of the slash command
     * @return String - Name
     */
    String name();

    /**
     * Handles the slash command
     * @param event The slash command sent by the user
     * @return The action to be taken
     */
    Mono<Void> handleCommand(ChatInputInteractionEvent event);
}
