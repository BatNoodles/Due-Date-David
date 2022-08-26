package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import reactor.core.publisher.Mono;

public class LeaveCommand implements SlashCommand{
    @Override
    public String name() {
        return "leave";
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
                        ? event.reply("You have left the course " + c).withEphemeral(true)
                        : event.reply("You are not in the course " + c).withEphemeral(true))
                .orElse(event.reply("There is no course " + courseName.toUpperCase()).withEphemeral(true));




    }
}
