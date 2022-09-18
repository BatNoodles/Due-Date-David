import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import dueDates.Course;
import dueDates.Database;
import dueDates.DueDate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Handles reminding of due dates by checking every minute.
 * I want to entirely redo this, with a flux in the database that emits due dates whenever the message needs to be sent, and changing all of this stuff
 * from static to not, where the handleRemind is called as a subscribed method to the flux.
 *
 */
public class DueDateSubscriber {
    public static void subscribeToReminder(Flux<DueDate> flux, GatewayDiscordClient client){
        flux.subscribe(d -> handleRemind(client, d));
    }

    public static void subscribeToRemoval(Flux<DueDate> flux){
        flux.subscribe(d -> {
            Database.getInstance().removeDueDate(d);
            try {
                Database.save();
            } catch (IOException ignored) {} //Want to change this, probably not the best idea to ignore IOExceptions :\
        });
    }


    /**
     * Handles sending the reminder message for all due dates that due in one hour. (It would be dumb to send a reminder exactly as the message is due)
     * @param client GatewayDiscordClient
     */
    private static void handleRemind(GatewayDiscordClient client, DueDate date) {
        Optional<Long> idOptional = Database.getInstance().getChannel();
        idOptional
                .ifPresent(id -> client
                        .getChannelById(Snowflake.of(id))
                        .ofType(MessageChannel.class)
                        .subscribe(channel -> sendDueDate(date, channel)));

    }

    private static void sendDueDate(DueDate dueDate, MessageChannel channel) {
        sendMentions(dueDate, channel);
        channel.createMessage(String.format("**Due soon:** %s (%s) is due in one hour! Make sure that you have submitted.", dueDate.getName(), dueDate.getCourse())).subscribe();

    }

    private static void sendMentions(DueDate dueDate, MessageChannel channel) {
        dueDate.getCourse().getMembers().forEach(id -> sendMentionForUser(id, channel));
    }

    private static void sendMentionForUser(Long userId, MessageChannel channel){
                channel.createMessage("<@" + userId + ">").subscribe(message -> message.delete().subscribe());
    }


    public static void subscribeToDailyReminders(GatewayDiscordClient client){
        Mono.just(client)
                .repeatWhen(fluxDelay -> Flux.interval(
                        Duration.between(
                                LocalDateTime.now(),
                                LocalDateTime.now().withHour(9).withMinute(0).plusDays(1)),
                        Duration.ofDays(1)))
                .subscribe(DueDateSubscriber::handleDailySubscription);
    }

    public static void handleDailySubscription(GatewayDiscordClient client){
        Database database = Database.getInstance();

        if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.MONDAY)){
            sendReminder("**Due this week:\n**", database.filterDueDates((d) -> d.getTimeUntil().toDays() <= 7), client);
        }
        else{
            sendReminder("**Due today:\n**", database.filterDueDates((d) -> d.getTimeUntil().toHours() <= 15), client);
        }
    }

    public static void sendReminder(String message, List<DueDate> dueDates, GatewayDiscordClient client){
        if (dueDates.isEmpty()) return;
        Optional<Long> idOptional = Database.getInstance().getChannel();
        idOptional
                .ifPresent(id -> client
                        .getChannelById(Snowflake.of(id))
                        .ofType(MessageChannel.class)
                        .subscribe(channel -> {
                            dueDates.stream().flatMap(d -> d.getCourse().getMembers().stream()).distinct().forEach(c -> sendMentionForUser(c, channel));
                            channel
                                    .createMessage(message +
                                            dueDates
                                            .stream()
                                            .map(d->"`" + d + "`")
                                            .collect(Collectors.joining("\n"))).subscribe();
                        }));
    }




}
