import commands.*;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Handles the slash commands by filtering the command name and getting the relevant command handler.
 */
public class SlashCommandHandler {

    private final static List<SlashCommand> commands = List.of(new AddCommand(), new JoinCommand(), new ShowCommand());

    public static Mono<Void> handleCommand(ChatInputInteractionEvent e){
        return commands
                .stream()
                .filter(c -> c
                        .name()
                        .equals(e.getCommandName()))
                .map(c-> c.handleCommand(e))
                .findFirst()
                .orElse(Mono.empty());
    }


}
