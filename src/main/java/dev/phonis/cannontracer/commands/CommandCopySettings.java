package dev.phonis.cannontracer.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.phonis.cannontracer.serializable.TracerUser;

import java.util.ArrayList;
import java.util.List;

public class CommandCopySettings extends EntityTracerCommand {

    public CommandCopySettings() {
        super("copysettings");
        this.addAlias("copy");
        this.addAlias("cs");
        this.args.add("(Player)");
    }

    @Override
    public List<String> topTabComplete(String[] args) {
        List<String> ret = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = player.getName();

                if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    ret.add(name);
                }
            }
        }

        return ret;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        throw new CommandException(CommandException.consoleError);
    }

    @Override
    public void execute(Player player, String[] args) throws CommandException {
        if (args.length > 0) {
            Player otherPlayer = Bukkit.getPlayer(args[0]);

            if (otherPlayer != null) {
                TracerUser tu = TracerUser.getUser(player.getUniqueId());
                TracerUser otherTu = TracerUser.getUser(otherPlayer.getUniqueId());

                tu.copySettings(otherTu);

                player.sendMessage(
                    ChatColor.BLUE + "Changing your settings to: \n" + ChatColor.RESET +
                        tu.printSettings()
                );

                otherPlayer.sendMessage(
                    ChatColor.AQUA + player.getName() + " has taken your settings"
                );
            } else {
                throw new CommandException("Player not found");
            }
        } else {
            throw new CommandException("No player given");
        }
    }

}
