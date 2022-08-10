import commands.AddCommand;
import commands.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public class SlashCommandHandler {

    private final static List<SlashCommand> commands = List.of(new AddCommand());

    public static Mono<Void> handleCommand(ChatInputInteractionEvent e){
        return commands.stream().filter(c -> c.name().equals(e.getCommandName())).map(c-> c.handleCommand(e)).findFirst().orElse(Mono.empty());
    }


}