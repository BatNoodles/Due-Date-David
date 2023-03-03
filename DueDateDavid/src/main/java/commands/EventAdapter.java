package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

/**
 * Hides the ugly part of getting options from a ChatInputInteraction event. Has no null checking so this only works for options that are required.
 */
public class EventAdapter {
    private final  ChatInputInteractionEvent event;

    /**
     * Commands with sub-options (like remove) have a depth of options that need to be "entered" before the actual OptionValues are accessible
     */
    private final int depth;

    /**
     * Default constructor with a depth of zero
     * @param event Event to be adapted
     */
    public EventAdapter(ChatInputInteractionEvent event){
        this.event = event;
        this.depth = 0;
    }

    public EventAdapter(ChatInputInteractionEvent event, int depth){
        this.event = event;
        this.depth = depth;
    }
    /**
     * Gets the ApplicationCommandInteractionOptionValue of a specified string.
     * @param name - String name of the option.
     * @return the value of the specified option.
     */
    private ApplicationCommandInteractionOptionValue get(String name){

        var optionsList = event.getOptions();

        for (int d = 0; d < depth; d++){
            optionsList = optionsList.get(0).getOptions();
        }

        return optionsList.stream().filter(o -> o.getName().equals(name)).map(o -> o.getValue().get()).findFirst().get();
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
