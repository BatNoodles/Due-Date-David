import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import dueDates.Database;
import dueDates.DueDate;


/**
 * Handles reminding of due dates by checking every minute.
 * I want to entirely redo this, with a flux in the database that emits due dates whenever the message needs to be sent, and changing all of this stuff
 * from static to not, where the handleRemind is called as a subscribed method to the flux.
 *
 */
public class DueDateReminderHandler {

    private static final Snowflake TESTING_CHANNEL = Snowflake.of(445386053042307072L);

    /**
     * Handles sending the reminder message for all due dates that due in one hour. (It would be dumb to send a reminder exactly as the message is due)
     * @param client GatewayDiscordClient
     */
    public static void handleRemind(GatewayDiscordClient client) {
        Database.getInstance().filterDueDates(d -> d.getTimeUntil().getSeconds() <= 3600 && d.getTimeUntil().getSeconds() >= 3540) //Duration.getHourPart was inconsistently giving one or zero, so getSeconds is used for precision
                .forEach(dueDate -> client.getChannelById(TESTING_CHANNEL)
                        .ofType(MessageChannel.class).
                        subscribe(channel -> sendDueDate(dueDate, channel)));

    }

    private static void sendDueDate(DueDate dueDate, MessageChannel channel) {
        sendMentions(dueDate, channel);
        channel.createMessage(String.format("%s (%s) is due in one hour! Make sure that you have submitted.", dueDate.getName(), dueDate.getCourse())).subscribe();

    }

    private static void sendMentions(DueDate dueDate, MessageChannel channel) {
        dueDate.getCourse().getMembers()
                .forEach(id -> channel.createMessage("<@" + id + ">").subscribe(message -> message.delete().subscribe()));


    }

}
