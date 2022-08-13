import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;
import java.util.Objects;

//TODO: add a listener every minute to check if a due date is happening in the next hour. Also add a recurring event every morning and every monday morning (for daily and weekly reminders of due dates)

public class DueDateDavid {
    public static void main(String[] args){
        final Dotenv dotenv = Dotenv.load();
        final GatewayDiscordClient client = Objects.requireNonNull(DiscordClientBuilder.create(dotenv.get("TOKEN")).build().login().block(), "Client cannot be null");

        try{
            new GuildCommandUpdater(client.getRestClient(), Long.parseLong(dotenv.get("GUILD"))).UpdateCommands(List.of("add.json", "show.json", "join.json")); //Updates all the slash commands
        }
        catch (Exception e){
            System.out.println("Error trying to update slash commands: " + e);
        }

        client.on(ChatInputInteractionEvent.class, SlashCommandHandler::handleCommand).then(client.onDisconnect()).block(); //Listens for SlashCommands used in the chat until the bot disconnects
    }
}
