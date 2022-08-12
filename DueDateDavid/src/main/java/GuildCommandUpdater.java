import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Updates the guild slash commands for the bot. This is better for testing than the global slash commands as it can take up to on hour to update global commands.
 * Guild commands do not take as long.
 */
public class GuildCommandUpdater extends CommandUpdater{
    private final Long guildId;

    private final Logger logger = Logger.getLogger(GuildCommandUpdater.class.getName());

    public GuildCommandUpdater(RestClient restClient, Long guildId){
        super(restClient);
        this.guildId = guildId;
    }

    @Override
    protected void bulkOverwriteCommands(ApplicationService appService, Long applicationId, List<ApplicationCommandRequest> commands){
        appService.bulkOverwriteGuildApplicationCommand(applicationId, guildId, commands).doOnNext(cmd -> logger.log(Level.FINE, "Guild command updated: " + cmd.name())).subscribe();
    }

}
