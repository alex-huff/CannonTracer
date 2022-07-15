package dev.phonis.cannontracer.commands;

import dev.phonis.cannontracer.serializable.TracerUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public
class CommandEndPos extends EntityTracerCommand
{

    public
    CommandEndPos()
    {
        super("endpos");
        this.addAlias("ep");
        this.args.add("tnt");
        this.args.add("sand");
    }

    @Override
    public
    List<String> topTabComplete(String[] args)
    {
        return this.argsAutocomplete(args, 1);
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
        TracerUser tu;

        if (args.length < 1)
        {
            player.sendMessage(this.getCommandString(0));
        }
        else if (args[0].equals("tnt") || args[0].equals("t"))
        {
            tu = TracerUser.getUser(player.getUniqueId());

            tu.toggleEndPosTNT();
            player.sendMessage("TNT end positions are now: " + tu.isEndPosTNT());
        }
        else if (args[0].equals("sand") || args[0].equals("s"))
        {
            tu = TracerUser.getUser(player.getUniqueId());

            tu.toggleEndPosSand();
            player.sendMessage("Sand end positions are now: " + tu.isEndPosSand());
        }
    }

}
