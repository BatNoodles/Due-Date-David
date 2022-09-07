package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
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

        return event.reply(database.removeDueDate(index) + " was removed.").withEphemeral(true);

    }
}
