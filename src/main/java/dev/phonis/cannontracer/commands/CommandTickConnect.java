package dev.phonis.cannontracer.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.phonis.cannontracer.serializable.TracerUser;

import java.util.List;

public class CommandTickConnect extends EntityTracerCommand {

    public CommandTickConnect() {
        super("tickconnect");
        this.addAlias("tc");
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
    public void execute(Player player, String[] args) {
        TracerUser tu = TracerUser.getUser(player.getUniqueId());

        tu.toggleTickConnect();
        player.sendMessage("Tick connection is now: " + tu.isTickConnect());
    }

}
