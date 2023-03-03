package commands.remove;

import commands.EventAdapter;
import commands.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import dueDates.DueDate;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * Command allowing a user to remove a due date.
 */
public class RemoveDueDateCommand implements SlashCommand {
    @Override
    public String name() {
        return "date";
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        EventAdapter eventAdapter = new EventAdapter(event);
        int index = Math.toIntExact(eventAdapter.optionAsLong("index"));
        Database database = Database.getInstance();
        DueDate removed = database.removeDueDate(index);
        if (database.getDueDates().size() <= index || index  < 0) return event.reply("That is not a valid index! Use */show* to make sure you have the correct index.").withEphemeral(true);
        try{
            Database.save();
        }
        catch (IOException e){
            return event.reply("The due date was removed but there was an error saving to disk. Please contact the developer.").withEphemeral(true);
        }

        return event.reply(removed  + " was removed.").withEphemeral(true);

    }
}
