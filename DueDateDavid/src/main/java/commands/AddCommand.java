package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class AddCommand implements SlashCommand{
    @Override
    public String name(){return "add";}

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        return event.reply("This has not been implemented yet");
    }
}
