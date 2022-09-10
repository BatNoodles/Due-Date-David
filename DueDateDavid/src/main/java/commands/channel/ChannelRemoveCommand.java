package commands.channel;

import commands.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class ChannelRemoveCommand implements SlashCommand {
    @Override
    public String name() {
        return "remove";
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        if (Database.getInstance().getChannel().isEmpty()) return event.reply("A channel has not been set as a reminder channel, so it cannot be removed.\nUse */channel set* next time!").withEphemeral(true);
        Database.getInstance().setChannel(null);
        try {
            Database.save();
        } catch (IOException e) {
            return event.reply("The channel was removed but there was an error saving to disk. Please contact the developer.").withEphemeral(true);
        }
        return event.reply("The set channel has be removed. Make sure to use */channel set* if you still want to receive reminders.");
    }
}
