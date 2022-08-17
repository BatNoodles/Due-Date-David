import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import io.github.cdimascio.dotenv.Dotenv;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Objects;


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


        Mono.just(client).repeatWhen(fluxDelay -> Flux.interval(Duration.ofSeconds(60))).subscribe(DueDateReminderHandler::handleRemind);


        client.on(ChatInputInteractionEvent.class, SlashCommandHandler::handleCommand).then(client.onDisconnect()).block(); //Listens for SlashCommands used in the chat until the bot disconnects
    }
}
