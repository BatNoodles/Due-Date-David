package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

/**
 * Hides the ugly part of getting options from a ChatInputInteraction event. Has no null checking so this only works for options that are required.
 */
public class EventAdapter {
    private ChatInputInteractionEvent event;

    public EventAdapter(ChatInputInteractionEvent event){
        this.event = event;
    }

    private ApplicationCommandInteractionOptionValue get(String name){
        return event.getOption(name).get().getValue().get();
    }

    public String optionAsString(String name){return get(name).asString();}

    public Long optionAsInt(String name){return get(name).asLong();}
}
