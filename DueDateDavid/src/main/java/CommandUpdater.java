import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Updates all the global slash commands for the bot.
 */
public class CommandUpdater {
    protected final RestClient restClient;

    private static final String commandPath = "commands/";

    public CommandUpdater(RestClient restClient){
        this.restClient = restClient;
    }

    /**
     * Updates all the commands in the given list of json files.
     * @param filenames - Filenames of the json files (must be in the resource folder)
     * @throws RuntimeException - Error opening one of the resource files
     */
    public void UpdateCommands(List<String> filenames) throws RuntimeException {
        //Object mapper for discord4j classes
        final JacksonResources commandMapper = JacksonResources.create();

        List<ApplicationCommandRequest> commands = filenames.stream().map(s ->
        {
            try {
                return commandMapper.getObjectMapper().readValue(getFileAsString(s), ApplicationCommandRequest.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        bulkOverwriteCommands(restClient.getApplicationService(), restClient.getApplicationId().block(), commands);


    }


    /**
     * Bulk overrides all the given global slash commands.
     * @param appService - Application service
     * @param applicationId - Application ID
     * @param commands - List of commands to be overridden
     */
    protected void bulkOverwriteCommands(ApplicationService appService, Long applicationId, List<ApplicationCommandRequest> commands){
        appService.bulkOverwriteGlobalApplicationCommand(applicationId, commands).doOnNext(cmd -> System.out.println("Global command updated: " + cmd.name())).subscribe();
    }

    /**
     * Gets the contents of the file in the resource folder as a String, if it exists.
     * @param filename - The path to the file in the resource folder, excluding the "resources/"
     * @return Contents of the file, as a String
     * @throws IOException - If an exception occurs opening the file
     */
    private String getFileAsString(String filename) throws IOException{
        try(InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(commandPath + filename)){
            if (inputStream == null) throw new IOException("The InputStream is null!");
            try(InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)){
                return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }


    }




}
