package com.spaceman.commandHander;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public abstract class CommandTemplate extends Command implements CommandExecutor, TabCompleter {
    
    protected ArrayList<SubCommand> actions = new ArrayList<>();
    
    private void setDescription(CommandDescription description) {
        if (description.getName() == null) {
            description.setName(this.getClass().getSimpleName());
            description.setUsage("/" + this.getClass().getSimpleName());
        }
        this.setName(description.getName());
        this.setLabel(description.getName());
        this.setDescription(description.getDescription());
        this.setUsage(description.getUsage());
    }
    
    public CommandTemplate(boolean register) {
        this(register, new CommandDescription(null, "Unknown", null, new ArrayList<>()));
    }

    public CommandTemplate(boolean register, CommandDescription description) {
        super(null);
        setDescription(description);
        if (register) {
            register(this);
        }
        registerActions();
    }
    
    public static void register(CommandTemplate template) {
//        ((CraftServer)Bukkit.getServer()).getCommandMap().register("", new ExtendedCommand());
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register("", template);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "An error occurred while registering the command '/" + template.getName() + "'");
        }
    }
    
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
    
    public void registerActions() {}
    
    public static boolean runCommands(List<SubCommand> actions, String arg, String[] args, Player player) {
        
        for (SubCommand action : actions) {
            if (action.getName(arg).equalsIgnoreCase(arg) || action.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(arg))) {
                action.run(args, player);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public abstract boolean execute(CommandSender sender, String command, String[] args);
    
    @Override
    public final boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        return execute(commandSender, alias, args);
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        if (sender instanceof Player) {
            return tabList(args, (Player) sender);
        } else {
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return tabComplete(sender, alias, args, null);
    }
    
    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return tabComplete(sender, alias, args, null);
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
    
    public static class CommandDescription {
        private String name;
        private String description;
        private String usage;
        private List<String> aliases;
        
        public CommandDescription(String name, String description, String usage, List<String> aliases) {
            this.name = name;
            this.description = description;
            this.usage = usage;
            this.aliases = aliases;
        }
    
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        public List<String> getAliases() {
            return aliases;
        }
        
        public void setAliases(List<String> aliases) {
            this.aliases = aliases;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getUsage() {
            return usage;
        }
        
        public void setUsage(String usage) {
            this.usage = usage;
        }
        
        public boolean addAlias(String alias) {
            return this.aliases.add(alias);
        }
        
        public boolean removeAlias(String alias) {
            return this.aliases.remove(alias);
        }
    }
}
