package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import reactor.core.publisher.Mono;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShowDueDatesCommand implements SlashCommand{

    @Override
    public String name() {
        return "due";
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        Database database = Database.getInstance();
        if (database.getDueDates().isEmpty()) return event.reply("There are no due dates.");
        return event.reply("Upcoming due dates:\n" + IntStream.range(0, database.getDueDates().size()).mapToObj(i -> i + ": `" + database.getDueDates().get(i) + "`").collect(Collectors.joining("\n")));
    }
}
