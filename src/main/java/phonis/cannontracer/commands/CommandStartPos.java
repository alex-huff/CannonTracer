package phonis.cannontracer.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import phonis.cannontracer.serializable.TracerUser;

import java.util.List;

public class CommandStartPos extends EntityTracerCommand {

    public CommandStartPos() {
        super("startpos");
        this.addAlias("sp");
        this.args.add("tnt");
        this.args.add("sand");
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
    public void execute(Player player, String[] args) {
        TracerUser tu;

        if (args.length < 1) {
            player.sendMessage(this.getCommandString(0));
        } else if (args[0].equals("tnt") || args[0].equals("t")) {
            tu = TracerUser.getUser(player.getUniqueId());

            tu.toggleStartPosTNT();
            player.sendMessage("TNT start positions are now: " + tu.isStartPosTNT());
        } else if (args[0].equals("sand") || args[0].equals("s")) {
            tu = TracerUser.getUser(player.getUniqueId());

            tu.toggleStartPosSand();
            player.sendMessage("Sand start positions are now: " + tu.isStartPosSand());
        }
    }

}