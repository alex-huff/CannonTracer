package dev.phonis.cannontracer.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.phonis.cannontracer.networking.CTClear;
import dev.phonis.cannontracer.networking.CTLineType;
import dev.phonis.cannontracer.networking.CTManager;
import dev.phonis.cannontracer.serializable.TracerUser;

import java.util.List;

public class CommandClear extends EntityTracerCommand {

    public CommandClear() {
        super("clear");
        this.addAlias("c");
        this.args.add("tnt");
        this.args.add("sand");
        this.args.add("player");
    }

    @Override
    public List<String> topTabComplete(String[] args) {
        return this.argsAutocomplete(args, 1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        throw new CommandException(CommandException.consoleError);
    }

    @Override
    public void execute(Player player, String[] args) throws CommandException {
        TracerUser tu;

        if (args.length < 1) {
            tu = TracerUser.getUser(player.getUniqueId());

            if (CTManager.isSubscribed(player.getUniqueId())) {
                CTManager.sendToPlayer(player, new CTClear(CTLineType.ALL));
            }

            tu.clearParticles();
            player.sendMessage("Cleared particles");
        } else if (args[0].equals("tnt") || args[0].equals("t")) {
            tu = TracerUser.getUser(player.getUniqueId());

            if (CTManager.isSubscribed(player.getUniqueId())) {
                CTManager.sendToPlayer(player, new CTClear(CTLineType.TNT));
            }

            tu.clearTNT();
            player.sendMessage("Cleared TNT particles");
        } else if (args[0].equals("sand") || args[0].equals("s")) {
            tu = TracerUser.getUser(player.getUniqueId());

            if (CTManager.isSubscribed(player.getUniqueId())) {
                CTManager.sendToPlayer(player, new CTClear(CTLineType.SAND));
            }

            tu.clearSand();
            player.sendMessage("Cleared sand particles");
        } else if (args[0].equals("player") || args[0].equals("p")) {
            tu = TracerUser.getUser(player.getUniqueId());

            if (CTManager.isSubscribed(player.getUniqueId())) {
                CTManager.sendToPlayer(player, new CTClear(CTLineType.PLAYER));
            }

            tu.clearPlayer();
            player.sendMessage("Cleared player particles");
        } else {
            throw new CommandException("Invalid toggle command");
        }
    }

}
