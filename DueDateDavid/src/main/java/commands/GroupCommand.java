package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class GroupCommand implements SlashCommand{
    private final List<SlashCommand> subCommands;

    protected  GroupCommand(List<SlashCommand> subCommands){this.subCommands = subCommands;}

    /**
     * Passes on the handling of the event to the appropriate sub command based on the name of the first option.
     * @param event - ChatInputInteractionEvent, the event for the sent slash command
     * @return Action to be taken
     */
    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        return subCommands
                .stream()
                .filter(c -> c.name()
                        .equals(event.getOptions()
                                .get(0).
                                getName()))
                .map(c -> c.handleCommand(event))
                .findFirst()
                .orElse(Mono.empty());
    }

}
