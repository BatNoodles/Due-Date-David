import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;

import java.io.IOException;
import java.util.List;

public class GuildCommandUpdater extends CommandUpdater{
    private final Long guildId;

    public GuildCommandUpdater(RestClient restClient, Long guildId){
        super(restClient);
        this.guildId = guildId;
    }


    private void bulkOverwriteCommands(ApplicationService appService, Long applicationId, List<ApplicationCommandRequest> commands){
        appService.bulkOverwriteGuildApplicationCommand(applicationId, guildId, commands).doOnNext(cmd -> System.out.println("Guild command updated: " + cmd.name())).subscribe();
    }







}
