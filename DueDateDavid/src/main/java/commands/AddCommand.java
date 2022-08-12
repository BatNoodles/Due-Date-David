package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import dueDates.DueDate;
import reactor.core.publisher.Mono;

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

        Database.getInstance().addDueDate(new DueDate(name, course, date, time));

        return event.reply("Due date added");


    }
}
