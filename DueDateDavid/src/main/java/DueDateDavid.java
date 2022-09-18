import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import dueDates.Database;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import java.util.Objects;


public class DueDateDavid {
    public static void main(String[] args){
        final Dotenv dotenv = Dotenv.load();
        final GatewayDiscordClient client = Objects.requireNonNull(DiscordClientBuilder.create(dotenv.get("TOKEN")).build().login().block(), "Client cannot be null");

        try{
            new GuildCommandUpdater(client.getRestClient(), Long.parseLong(dotenv.get("GUILD"))).UpdateCommands(List.of("add.json", "show.json", "join.json", "leave.json", "remove.json",  "channel.json")); //Updates all the slash commands
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        DueDateSubscriber.subscribeToReminder(Database.getInstance().getReminderFlux(), client);
        DueDateSubscriber.subscribeToRemoval(Database.getInstance().getRemovalFlux());
        DueDateSubscriber.subscribeToDailyReminders(client);
        Database.loadInstance();



        client.on(ChatInputInteractionEvent.class, SlashCommandHandler::handleCommand).then(client.onDisconnect()).block(); //Listens for SlashCommands used in the chat until the bot disconnects
    }
}
