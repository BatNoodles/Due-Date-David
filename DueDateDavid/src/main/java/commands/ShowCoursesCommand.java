package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class ShowCoursesCommand implements SlashCommand{
    @Override
    public String name() {
        return "courses";
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        Database database = Database.getInstance();
        if (database.getCourses().isEmpty()) return event.reply("There are currently no courses.");
        return event.reply("Courses:\n" + database
                .getCourses()
                .stream()
                .map(course -> "`" + course + "`")
                .collect(Collectors.joining("\n")));
    }
}
