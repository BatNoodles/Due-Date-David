package commands.channel;

import commands.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class ChannelSetCommand implements SlashCommand {
    @Override
    public String name() {
        return "set";
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        Database.getInstance().setChannel(event.getInteraction().getChannelId().asLong()); //May want to make Database store the snowflake as a snowflake instead of long-ing and de-longing.
        try {
            Database.save();
        } catch (IOException e) {
            return event.reply("The channel was set but there was an error saving to disk. Please contact the developer.").withEphemeral(true);
        }
        return event.reply("This channel will now be sent reminders for due dates.");
    }
}
