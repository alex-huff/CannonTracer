package dev.phonis.cannontracer.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandBounds extends EntityTracerCommand {

    public CommandBounds() {
        super("bounds");
        this.addAlias("b");
        this.addSubCommand(new CommandMinDistance());
        this.addSubCommand(new CommandMaxParticles());
        this.addSubCommand(new CommandTraceRadius());
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
