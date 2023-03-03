import commands.*;
import commands.channel.ChannelCommand;
import commands.show.ShowCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Handles the slash commands by filtering the command name and getting the relevant command handler.
 */
public class SlashCommandHandler {
    private final static List<SlashCommand> commands = List.of(new AddCommand(), new JoinCommand(), new ShowCommand(), new LeaveCommand(), new RemoveDueDateCommand(), new ChannelCommand());

    /**
     * Handles the command by sending it to the correct SlashCommand to handle.
     * @param e The slash command the user sent
     * @return Action to be taken
     */
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
