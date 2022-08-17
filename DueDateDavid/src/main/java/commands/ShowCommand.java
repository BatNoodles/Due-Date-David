package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import dueDates.DueDate;
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
        return event.reply(Database.getInstance().getDueDates().stream().map(DueDate::toString).collect(Collectors.joining("\n")));
    }
}