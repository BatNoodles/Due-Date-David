package commands.channel;

import commands.GroupCommand;

import java.util.List;

public class ChannelCommand extends GroupCommand {
    public ChannelCommand(){
        super(List.of(new ChannelSetCommand(), new ChannelRemoveCommand()));
    }

    @Override
    public String name() {
        return "channel";
    }

}
