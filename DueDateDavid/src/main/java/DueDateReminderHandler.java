import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import dueDates.Database;

/**
 * Handles reminding of due dates by checking every minute.
 */
public class DueDateReminderHandler {
    public static void handleRemind(GatewayDiscordClient client){
        Database database = Database.getInstance();
//        database.getDueDates().forEach(dueDate -> System.out.println("Time until due date: " + dueDate.getTimeUntil().toHoursPart())); //Temporary, for debugging
        database.getDueDates()
                .stream()
                .filter(d -> d.getTimeUntil().toHoursPart() == 1).
                forEach(dueDate ->
                        client.getChannelById(Snowflake.of(445386053042307072L))
                                .subscribe(channel -> channel.getRestChannel().createMessage("The following is due in one hour. Make sure to submit!\n" + dueDate).subscribe())
                );

    }
}
