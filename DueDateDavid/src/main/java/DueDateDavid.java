import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import reactor.core.publisher.Mono;

public class DueDateDavid {
    /**
     * Runs the bots
     * @param args First arugment should be the token
     */
    public static void main(String[] args){
        DiscordClient client = DiscordClient.create(args[0]);

        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> Mono.empty());

        login.block();
    }
}
