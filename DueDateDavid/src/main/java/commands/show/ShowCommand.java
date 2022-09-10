package commands.show;
import commands.GroupCommand;

import java.util.List;


public class ShowCommand extends GroupCommand {
    public ShowCommand(){super(List.of(new ShowCoursesCommand(), new ShowDueDatesCommand()));}
    @Override
    public String name() {
        return "show";
    }
}
