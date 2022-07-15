package dev.phonis.cannontracer.commands;

import dev.phonis.cannontracer.CannonTracer;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract
class EntityTracerCommand implements CommandExecutor, TabCompleter
{

    private final String                   name;
    private final Set<EntityTracerCommand> subCommands = new HashSet<>();
    private final Set<String>              aliases     = new HashSet<>();
    protected     Set<String>              args        = new HashSet<>();

    public
    EntityTracerCommand(String name)
    {
        this.name = name;
    }

    private static
    String[] truncate(String[] strings)
    {
        String[] ret = new String[strings.length - 1];

        System.arraycopy(strings, 1, ret, 0, strings.length - 1);

        return ret;
    }

    private static
    String[] arrayToLower(String[] strings)
    {
        for (int i = 0; i < strings.length; i++)
        {
            strings[i] = strings[i].toLowerCase();
        }

        return strings;
    }

    public static
    void registerCommand(CannonTracer cannonTracer, EntityTracerCommand etc)
    {
        PluginCommand pc = cannonTracer.getCommand(etc.getName());

        if (pc == null)
        {
            return;
        }

        pc.setExecutor(etc);
        pc.setTabCompleter(etc);
    }

    public static
    double parseDouble(String arg) throws CommandException
    {
        try
        {
            return Double.parseDouble(arg);
        }
        catch (NumberFormatException e)
        {
            throw new CommandException("Invalid double: " + arg);
        }
    }

    public static
    int parseInt(String arg) throws CommandException
    {
        try
        {
            return Integer.parseInt(arg);
        }
        catch (NumberFormatException e)
        {
            throw new CommandException("Invalid int: " + arg);
        }
    }

    public
    List<String> argsAutocomplete(String[] args, int size)
    {
        List<String> ret = new ArrayList<>();

        if (args.length > size)
        {
            return ret;
        }

        for (String traceType : this.args)
        {
            if (traceType.startsWith(args[args.length - 1]))
            {
                ret.add(traceType);
            }
        }

        return ret;
    }

    public abstract
    List<String> topTabComplete(String[] args);

    public
    List<String> tabComplete(String label, String[] args)
    {
        List<String> ret = new ArrayList<>();

        if (args.length > 0)
        {
            for (EntityTracerCommand subETC : this.subCommands)
            {
                if (args[0].equalsIgnoreCase(subETC.getName()) || subETC.getAliases().contains(args[0]))
                {
                    return subETC.tabComplete(args[0], EntityTracerCommand.truncate(args));
                }
            }

            List<String> top = this.topTabComplete(args);

            if (top != null)
            {
                ret = top;
            }

            if (args.length > 1)
            {
                return ret;
            }

            for (EntityTracerCommand subETC : this.subCommands)
            {
                String subName = subETC.getName();

                if (subName.startsWith(args[0]))
                {
                    ret.add(subName);
                }
            }

            return ret;
        }

        if (this.getName().startsWith(label))
        {
            ret.add(this.getName());
        }

        return ret;
    }

    public abstract
    void execute(CommandSender sender, String[] args) throws CommandException;

    public abstract
    void execute(Player player, String[] args) throws CommandException;

    public
    void executeTree(CommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 0)
        {
            for (EntityTracerCommand subETC : this.subCommands)
            {
                if (args[0].equalsIgnoreCase(subETC.getName()) || subETC.getAliases().contains(args[0]))
                {
                    subETC.executeTree(sender, truncate(args));

                    return;
                }
            }
        }

        if (sender instanceof Player)
        {
            execute((Player) sender, args);
        }
        else
        {
            execute(sender, args);
        }
    }

    @Override
    public
    List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String alias,
                               @Nonnull String[] args)
    {
        return this.tabComplete(alias, EntityTracerCommand.arrayToLower(args));
    }

    @Override
    public
    boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label,
                      @Nonnull String[] args)
    {
        try
        {
            this.executeTree(sender, EntityTracerCommand.arrayToLower(args));
        }
        catch (CommandException e)
        {
            sender.sendMessage(e.getMessage());
        }

        return true;
    }

    public
    void addSubCommand(EntityTracerCommand subETC)
    {
        this.subCommands.add(subETC);
    }

    public
    void addAlias(String alias)
    {
        this.aliases.add(alias.toLowerCase());
    }

    private
    Set<String> getAliases()
    {
        return this.aliases;
    }

    public
    String getName()
    {
        return name;
    }

    public
    String getCommandString(int depth)
    {
        StringBuilder message = new StringBuilder();

        for (int i = 0; i < depth; i++)
        {
            message.append("   ");
        }

        String name = "" + ChatColor.RESET + ChatColor.AQUA + this.getName() + "\n" + ChatColor.GRAY;
        message.append(name);

        depth += 1;

        for (String arg : this.args)
        {
            for (int i = 0; i < depth; i++)
            {
                message.append("   ");
            }
            message.append(arg).append("\n");
        }

        if (!this.subCommands.isEmpty())
        {
            for (EntityTracerCommand etc : this.subCommands)
            {
                message.append(etc.getCommandString(depth));
            }
        }

        return message.toString();
    }
}
