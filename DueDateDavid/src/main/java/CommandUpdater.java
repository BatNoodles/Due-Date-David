import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandUpdater {
    protected final RestClient restClient;

    public CommandUpdater(RestClient restClient){
        this.restClient = restClient;
    }

    public void UpdateCommands(List<String> filenames) throws IOException {
        //Object mapper for discord4j classes
        final JacksonResources commandMapper = JacksonResources.create();

        List<ApplicationCommandRequest> commands = filenames.stream().map(s -> {
            try {
                return commandMapper.getObjectMapper().readValue(s, ApplicationCommandRequest.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        bulkOverwriteCommands(restClient.getApplicationService(), restClient.getApplicationId().block(), commands);


    }


    private void bulkOverwriteCommands(ApplicationService appService, Long applicationId, List<ApplicationCommandRequest> commands){
        appService.bulkOverwriteGlobalApplicationCommand(applicationId, commands).doOnNext(cmd -> System.out.println("Global command updated: " + cmd.name())).subscribe();
    }

    /**
     * Gets the contents of the file in the resource folder as a String, if it exists.
     * @param filename - The path to the file in the resource folder, excluding the "resources/"
     * @return Contents of the file, as a String
     * @throws IOException - If an exception occurs opening the file
     */
    private String getFileAsString(String filename) throws IOException{
        try(InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(filename)){
            if (inputStream == null) return null;
            try(InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)){
                return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }


    }




}
