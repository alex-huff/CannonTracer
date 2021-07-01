package phonis.cannontracer.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import phonis.cannontracer.CannonTracer;

import java.util.List;

public class CommandTracer extends EntityTracerCommand {

    public CommandTracer(CannonTracer cannonTracer, String name) {
        super(name);
        this.addSubCommand(new CommandToggle());
        this.addSubCommand(new CommandClear());
        this.addSubCommand(new CommandTraceTime());
        this.addSubCommand(new CommandSettings());
        this.addSubCommand(new CommandBounds());
        EntityTracerCommand.registerCommand(cannonTracer, this);
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
        if (args.length == 0) {
            player.sendMessage(this.getCommandString(0));
        } else {
            throw new CommandException("Incorrect usage of command " + this.getName());
        }
    }

}
