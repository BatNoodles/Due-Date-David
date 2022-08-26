package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;
import java.util.List;


public class ShowCommand implements SlashCommand{
    private static final List<SlashCommand> subCommands = List.of(new ShowCoursesCommand(), new ShowDueDatesCommand());
    @Override
    public String name() {
        return "show";
    }

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
