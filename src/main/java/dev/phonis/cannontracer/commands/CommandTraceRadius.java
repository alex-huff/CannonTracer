package dev.phonis.cannontracer.commands;

import dev.phonis.cannontracer.serializable.TracerUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public
class CommandTraceRadius extends EntityTracerCommand
{

    public
    CommandTraceRadius()
    {
        super("radius");
        this.addAlias("r");
        this.args.add("(Radius for tracing)");
    }

    @Override
    public
    List<String> topTabComplete(String[] args)
    {
        return null;
    }

    @Override
    public
    void execute(CommandSender sender, String[] args) throws CommandException
    {
        throw new CommandException(CommandException.consoleError);
    }

    @Override
    public
    void execute(Player player, String[] args) throws CommandException
    {
        if (args.length > 0)
        {
            TracerUser tu = TracerUser.getUser(player.getUniqueId());

            tu.setTraceRadius(parseDouble(args[0]));
            player.sendMessage("Trace radius is now: " + tu.getTraceRadius());
        }
        else
        {
            throw new CommandException("No value entered for radius");
        }
    }

}
