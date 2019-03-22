package com.spaceman.commandHander;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class HeadCommand implements TabCompleter, CommandExecutor {

    /*
    //command subCommand <variable> <command1|command2>

    EmptyCommand emptyCommand = new EmptyCommand(){
        @Override
        public String getName(String arg) {
            return arg;
        }
    };
    emptyCommand.setRunnable((args, player) -> {
        if (args.length > 2) {
            if (!runCommands(emptyCommand.getActions(), args[2], args, player)) {
                player.sendMessage(formatError("Usage: %s", "/command subCommand <variable> <command1|command2>"));
            }
        } else {
            player.sendMessage(formatError("Usage: %s", "/command subCommand <variable> <command1|command2>"));
        }
    });
    emptyCommand.setTabRunnable((args, player) -> {
        ArrayList<String> list = new ArrayList<>();
        for (SubCommand subCommand : emptyCommand.getActions()) {
            list.add(subCommand.getName(null));
        }
        return list;
    });
    emptyCommand.addAction(new SubCommand());//register here your commands
    this.addAction(emptyCommand);
    */

    private ArrayList<SubCommand> actions = new ArrayList<>();

    public static List<String> filterContaining(String arg, Collection<String> fullList) {
        ArrayList<String> list = new ArrayList<>();
        for (String ss : fullList) {
            if (ss.toLowerCase().contains(arg.toLowerCase())) {
                list.add(ss);
            }
        }
        return list;
    }

    private static List<String> tabList(List<SubCommand> actions, String[] args, Player player, int i) {

        //first tier subCommands
        if (args.length == 1) {
            ArrayList<String> tabList = new ArrayList<>();
            for (SubCommand subCommand : actions) {
                tabList.add(subCommand.getName(args[0]));
            }
            return filterContaining(args[0], tabList);
        }

        if (args.length == i) {
            ArrayList<String> tabList = new ArrayList<>();

            for (SubCommand subCommand : actions) {

                if (subCommand.getName(args[i - 2]).equalsIgnoreCase(args[i - 2])) {
                    tabList.addAll(filterContaining(args[i - 1], subCommand.tabList(player, args)));
                }
                if (subCommand instanceof EmptyCommand) {
                    if (!subCommand.getName(args[i - 2]).equals("")) {
                        if (subCommand.getName(args[i - 2]).equalsIgnoreCase(args[i - 2])) {
                            tabList.addAll(filterContaining(args[i - 1], subCommand.tabList(player, args)));
                        }
                    } else {
                        tabList.addAll(filterContaining(args[i - 1], subCommand.tabList(player, args)));
                    }
                }
            }
            return tabList;
        } else {
            try {
                for (SubCommand subCommand : actions) {
                    if (subCommand.getName(args[i - 2]).equalsIgnoreCase(args[i - 2])) {
                        return tabList(subCommand.getActions(), args, player, i + 1);
                    }
                    if (subCommand instanceof EmptyCommand) {
                        return tabList(subCommand.getActions(), args, player, i + 1);
                    }
                }
            } catch (Exception ignore) {
            }
            return tabList(actions, args, player, i + 1);
        }
    }

    public static boolean runCommands(List<SubCommand> actions, String arg, String[] args, Player player) {

        for (SubCommand action : actions) {
            if (action.getName(arg).equalsIgnoreCase(arg) || action.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(arg))) {
                action.run(args, player);
                return true;
            }
        }
        return false;
    }

    public List<String> tabList(String[] args, Player player) {
        return tabList(actions, args, player, 1);
    }

    public void addAction(SubCommand subCommand) {
        actions.add(subCommand);
    }

    public SubCommand getAction(String action) {
        return getAction(action, null);
    }

    public SubCommand getAction(String action, SubCommand def) {
        for (SubCommand subCommand : getActions()) {
            if (subCommand.getName(action).equalsIgnoreCase(action)) {
                return subCommand;
            }
        }
        return def;
    }

    public ArrayList<SubCommand> getActions() {
        return actions;
    }

    protected boolean runCommands(String arg, String[] args, Player player) {
        return runCommands(actions, arg, args, player);
    }

    @Override
    public abstract boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings);

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return tabList(strings, (Player) commandSender);
    }

}
