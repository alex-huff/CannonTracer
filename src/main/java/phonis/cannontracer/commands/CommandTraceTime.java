package phonis.cannontracer.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import phonis.cannontracer.serializable.TracerUser;

import java.util.List;

public class CommandTraceTime extends EntityTracerCommand {

    public CommandTraceTime() {
        super("tracetime");
        this.addAlias("tt");
        this.args.add("(Ticks a trace will last)");
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

            tu.setTraceTime(EntityTracerCommand.parseInt(args[0]));
            player.sendMessage("Trace time is now: " + tu.getTraceTime() + " ticks");
        } else {
            throw new CommandException("No value entered for ticks");
        }
    }

}
