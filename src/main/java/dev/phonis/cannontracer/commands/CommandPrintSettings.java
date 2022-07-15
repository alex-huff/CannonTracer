package dev.phonis.cannontracer.commands;

import dev.phonis.cannontracer.serializable.TracerUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public
class CommandPrintSettings extends EntityTracerCommand
{

    public
    CommandPrintSettings()
    {
        super("print");
        this.addAlias("p");
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
    void execute(Player player, String[] args)
    {
        TracerUser tu = TracerUser.getUser(player.getUniqueId());

        player.sendMessage(tu.printSettings());
    }

}
