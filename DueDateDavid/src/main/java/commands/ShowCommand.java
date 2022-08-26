package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class ShowCommand implements SlashCommand{
    @Override
    public String name() {
        return "show";
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        Database database = Database.getInstance();
        if (database.getDueDates().isEmpty()) return event.reply("There are no due dates.");
        return event.reply("Upcoming due dates:\n" + database.getDueDates().stream().map(dueDate -> "`" + dueDate + "`").collect(Collectors.joining("\n")));
    }
}
