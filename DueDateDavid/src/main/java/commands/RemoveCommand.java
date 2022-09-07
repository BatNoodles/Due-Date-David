package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import dueDates.DueDate;
import reactor.core.publisher.Mono;

public class RemoveCommand implements SlashCommand{
    @Override
    public String name() {
        return "remove";
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        EventAdapter eventAdapter = new EventAdapter(event);
        int index = Math.toIntExact(eventAdapter.optionAsLong("index"));

        Database database = Database.getInstance();

        if (database.getDueDates().size() <= index || index  < 0) return event.reply("That is not a valid index! Use */show* to make sure you have the correct index.").withEphemeral(true);
        DueDate date = database.getDueDates().get(index);
        database.getDueDates().remove(index);

        return event.reply(date + " was removed.").withEphemeral(true);

    }
}
