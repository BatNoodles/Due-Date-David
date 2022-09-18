package commands.show;

import commands.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Course;
import dueDates.Database;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Shows all of the courses, split by whether the user has joined that course.
 */
public class ShowCoursesCommand implements SlashCommand {
    @Override
    public String name() {
        return "courses";
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        Database database = Database.getInstance();
        Long userId = event.getInteraction().getUser().getId().asLong();
        List<Course> courses = database.getCourses();
        if (courses.isEmpty()) return event.reply("There are currently no courses.");
        Map<Boolean, List<Course>> partitionedCourses = courses.stream().collect(Collectors.partitioningBy(c -> c.containsUser(userId)));

        String message = "";

        if (!partitionedCourses.get(true).isEmpty()) message = formatCourses(partitionedCourses.get(true), "Courses you are in:");
        if (!partitionedCourses.get(false).isEmpty()) message += formatCourses(partitionedCourses.get(false), "Courses you are not in:");
        return event.reply(message).withEphemeral(true);

    }

    /**
     * Formats a list of courses using ` (code block markup in discord), and prepends the list with a provided heading
     * @param courses List of courses to be formatted
     * @param heading Heading to be prepended before the list. Newline is automatically added.
     * @return Formatted String of courses, wrapped in a codeblock and separated by newlines.
     */
    private String formatCourses(List<Course> courses, String heading){
        return heading + "\n" +
                courses
                .stream()
                .map(c -> "`" + c + "`")
                .collect(Collectors.joining("\n")) + "\n";
    }

}
