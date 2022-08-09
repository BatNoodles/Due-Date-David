import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;
import java.util.Objects;


public class DueDateDavid {
    public static void main(String[] args){
        final Dotenv dotenv = Dotenv.load();
        final GatewayDiscordClient client = Objects.requireNonNull(DiscordClientBuilder.create(dotenv.get("TOKEN")).build().login().block(), "Client cannot be null");

        try{

            new GuildCommandUpdater(client.getRestClient(), Long.parseLong(dotenv.get("GUILD"))).UpdateCommands(List.of("add.json"));
        }
        catch (Exception e){
            System.out.println("Error trying to update slash commands: " + e);
        }

        client.on(ChatInputInteractionEvent.class).then(client.onDisconnect()).block();


    }
}
