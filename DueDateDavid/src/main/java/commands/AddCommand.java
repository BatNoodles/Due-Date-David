package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import dueDates.DueDate;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * Command for adding a due date.
 */
public class AddCommand implements SlashCommand{
    @Override
    public String name(){return "add";}

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        final String name, course, date, time;
        EventAdapter adapter = new EventAdapter(event);

        name = adapter.optionAsString("name");
        course = adapter.optionAsString("course");
        date = adapter.optionAsString("date");
        time = adapter.optionAsString("time");

        if (!DueDate.dateTimeIsValid(date, time)) return event.reply("The date or time is invalid").withEphemeral(true);

        Database database = Database.getInstance();

        database.addDueDate(new DueDate(name, course, date, time));

        String message = "Due date  \""  + name +  "\" added";
        if (database.getChannel().isEmpty()) message += "\n*Warning: I have not been assigned a channel to send reminders to!\nSet a channel using `/channel set`*";

        try {
            Database.save();
        } catch (IOException e) {
            return event.reply("The due date was added but there was an error saving to disk. Please contact the developer.").withEphemeral(true);
        }
        return event.reply(message).withEphemeral(true);

    }
}
