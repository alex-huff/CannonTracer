package phonis.cannontracer.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import phonis.cannontracer.serializable.TracerUser;

import java.util.List;

public class CommandMinDistance extends EntityTracerCommand {

    public CommandMinDistance() {
        super("mindistance");
        this.addAlias("md");
        this.args.add("(Minimum distance entity must travel for tracking)");
    }

    @Override
    public List<String> topTabComplete(String[] args) {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        throw new CommandException(CommandException.consoleError);
    }

    @Override
    public void execute(Player player, String[] args) throws CommandException {
        if (args.length > 0) {
            TracerUser tu = TracerUser.getUser(player.getUniqueId());

            tu.setMinDistance(EntityTracerCommand.parseDouble(args[0]));
            player.sendMessage("Min distance is now: " + tu.getMinDistance());
        } else {
            throw new CommandException("No value entered for distance");
        }
    }

}
