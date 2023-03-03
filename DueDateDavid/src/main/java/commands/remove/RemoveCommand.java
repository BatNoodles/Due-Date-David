package commands.remove;

import commands.GroupCommand;

import java.util.List;

public class RemoveCommand extends GroupCommand{
    /**
     * Handles calling the correct remove command.
     */
        public RemoveCommand(){super(List.of(new RemoveCourseCommand(), new RemoveDueDateCommand()));}
        @Override
        public String name() {
            return "remove";
        }
    }
