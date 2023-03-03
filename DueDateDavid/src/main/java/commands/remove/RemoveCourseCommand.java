package commands.remove;

import commands.EventAdapter;
import commands.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Course;
import dueDates.Database;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;

public class RemoveCourseCommand implements SlashCommand {
    @Override
    public String name() {
        return "course";
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {

        EventAdapter eventAdapter = new EventAdapter(event, 1);

        String code = eventAdapter.optionAsString("code");
        Database database = Database.getInstance();
        Optional<Course> courseOptional = database.getCourse(code);
        if (courseOptional.isEmpty()) return event.reply("Course: " + code + " does not exist").withEphemeral(true);
        Course course = courseOptional.get();

        if (!database.filterDueDates(d -> d.getCourse().equals(course)).isEmpty())
            return event.reply("Course: " + course + " still has due dates associated with it. Please remove them before removing the course").withEphemeral(true);

        database.removeCourse(course);
        try{
            Database.save();
        }
        catch (IOException e){
            return event.reply("The course was removed but there was an error saving to disk. Please contact the developer.").withEphemeral(true);
        }

        return event.reply("Course: " + course + " was removed.").withEphemeral(true);
    }
}
