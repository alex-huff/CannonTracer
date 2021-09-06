package dev.phonis.cannontracer.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.phonis.cannontracer.serializable.TracerUser;

import java.util.List;

public class CommandMaxParticles extends EntityTracerCommand {

    public CommandMaxParticles() {
        super("maxparticles");
        this.addAlias("mp");
        this.args.add("(Particle amount per 5 ticks)");
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

            tu.setMaxParticles(parseInt(args[0]));
            player.sendMessage("Max particles is now: " + tu.getMaxParticles());
        } else {
            throw new CommandException("No value entered for particles");
        }
    }

}
