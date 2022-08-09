import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import java.util.List;

/**
 * Updates the guild slash commands for the bot. This is better for testing than the global slash commands as it can take up to on hour to update global commands.
 * Guild commands do not take as long.
 */
public class GuildCommandUpdater extends CommandUpdater{
    private final Long guildId;

    public GuildCommandUpdater(RestClient restClient, Long guildId){
        super(restClient);
        this.guildId = guildId;
    }

    @Override
    protected void bulkOverwriteCommands(ApplicationService appService, Long applicationId, List<ApplicationCommandRequest> commands){
        appService.bulkOverwriteGuildApplicationCommand(applicationId, guildId, commands).doOnNext(cmd -> System.out.println("Guild command updated: " + cmd.name())).subscribe();
    }

}
