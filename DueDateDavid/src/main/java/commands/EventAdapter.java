package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

/**
 * Hides the ugly part of getting options from a ChatInputInteraction event. Has no null checking so this only works for options that are required.
 */
public class EventAdapter {
    private final  ChatInputInteractionEvent event;

    public EventAdapter(ChatInputInteractionEvent event){
        this.event = event;
    }

    /**
     * Gets the ApplicationCommandInteractionOptionValue of a specified string.
     * @param name - String name of the option.
     * @return the value of the specified option.
     */
    private ApplicationCommandInteractionOptionValue get(String name){
        return event.getOption(name).get().getValue().get();
    }

    /**
     * Gets the option as a string.
     * @param name - String name of the option.
     * @return String value of option
     */
    public String optionAsString(String name){return get(name).asString();}

    /**
     * Gets the option as a long.
     * @param name - String name of the option.
     * @return Long value of the option.
     */
    public Long optionAsLong(String name){return get(name).asLong();}
}
