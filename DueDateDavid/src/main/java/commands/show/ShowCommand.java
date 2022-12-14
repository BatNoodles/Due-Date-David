package commands.show;
import commands.GroupCommand;

import java.util.List;

/**
 * Handles calling the correct show command.
 */
public class ShowCommand extends GroupCommand {
    public ShowCommand(){super(List.of(new ShowCoursesCommand(), new ShowDueDatesCommand()));}
    @Override
    public String name() {
        return "show";
    }
}
