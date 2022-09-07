package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Course;
import dueDates.Database;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class LeaveCommand implements SlashCommand{
    @Override
    public String name() {
        return "leave";
    }

    private Mono<Void> trySaveRemoval(ChatInputInteractionEvent event, Course c){
        try{
            Database.save();
        }
        catch (IOException e){
            return event.reply("You have left the course but there was an error saving to disk. Please contact the developer.").withEphemeral(true);
        }
        return event.reply("You have left the course " + c).withEphemeral(true);
    }

    @Override
    public Mono<Void> handleCommand(ChatInputInteractionEvent event) {
        EventAdapter eventAdapter = new EventAdapter(event);
        String courseName = eventAdapter.optionAsString("course");
        Long userId = event.getInteraction().getUser().getId().asLong();
        return Database
                .getInstance()
                .getCourses()
                .stream()
                .filter(c->c
                        .getName()
                        .equalsIgnoreCase(courseName))
                .findFirst()
                .map(c-> c.removeUser(userId)
                        ? trySaveRemoval(event, c)
                        : event.reply("You are not in the course " + c).withEphemeral(true))
                .orElse(event.reply("There is no course " + courseName.toUpperCase()).withEphemeral(true));




    }
}
