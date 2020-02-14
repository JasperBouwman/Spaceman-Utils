package com.spaceman.commandHander;

import com.spaceman.Pair;
import com.spaceman.fancyMessage.Message;
import com.spaceman.fancyMessage.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.spigotmc.CustomTimingsHandler;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.spaceman.commandHander.SubCommand.lowerCaseFirst;

public abstract class CommandTemplate extends Command implements CommandExecutor, TabCompleter {
    
    /*
    * This is the main file, your command should extend this class (HeadCommand.java is older version)
    * This Permission Util uses the following utils:
    * - FancyMessage
    * - ColorTheme
    *
    * This allows you to easily manage your commands
    *
    * To use the made help command for your command ->
    * In your CommandTemplate you add the HelpCommand your its actions.
    * Each sub-command should have set the command name and argument type to generate the help command
    */
    
    /* example 1
    /command subCommand [sub1 (only integers)]
    /command subCommand [sub2...]
    
    EmptyCommand sub1 = new SubCommand(){
        @Override
        public void getName(String argument) {
            return argument
        }
    };
    sub1.setCommandName("sub1", ArgumentType.OPTIONAL);
    sub1.setRunnable((args, player) -> {*//*your command code*//*});
    
    EmptyCommand sub2 = new EmptyCommand() {
        @Override
        public String getName(String argument) {
            //only return 'argument' if its the right one,
            // in this case sub1 only accepts integers
            try { Integer.parseInt(argument); return ""; }
            catch (NumberFormatException nfe) { return argument; }
        }
    };
    sub2.setCommandName("sub2", ArgumentType.OPTIONAL);
    sub2.setLooped(true);
    
    addAction(sub2); //REGISTER sub2 BEFORE sub1
    addAction(sub1);
    
    /* example 2
    /command subCommand <variable> <command1|command2>

    EmptyCommand variableCommand = new EmptyCommand(){
        @Override
        public String getName(String arg) {
            return arg;
        }
    };
    variableCommand.setRunnable((args, player) -> {
        if (args.length > 2) {
            if (!runCommands(variableCommand.getActions(), args[2], args, player)) {
                //right command not given (/command subCommand <variable> <command3>)
                player.sendMessage(formatError("Usage: %s", "/command subCommand <variable> <command1|command2>"));
            }
        } else {
            //not enough arguments (/command subCommand <variable>)
            player.sendMessage(formatError("Usage: %s", "/command subCommand <variable> <command1|command2>"));
        }
    });
    variableCommand.setTabRunnable((args, player) -> {
        ArrayList<String> list = new ArrayList<>();
        for (SubCommand subCommand : variableCommand.getActions()) {
            list.add(subCommand.getName(null));
        }
        return list;
    });
    variableCommand.addAction(new SubCommand());//register here your commands
    this.addAction(variableCommand);
    
    /* example 3
    /command <subCommand>
    /command <subCommand> <arg>
    
    EmptyCommand empty = new EmptyCommand() {
        @Override
        public String getName(String argument) {
            return "";
        }
    };
    
    EmptyCommand emptyArg = new EmptyCommand();
    
    subCommand.addAction(empty);
    subCommand.addAction(emptyArg);
    
    You have to register a 'dummy' command to make the help command work properly.
    
    */
    
    public static boolean executeInternal(CommandSender sender, String command, String args) {
        return executeInternal(sender, command, args.split(" "));
    }
    
    public static boolean executeInternal(CommandSender sender, String command, String[] args) {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            
            commandMap.getCommand(command).execute(sender, "", args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    protected ArrayList<SubCommand> actions = new ArrayList<>();
    private Message commandDescription = null;
    
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
        //to check if the reflection works
//        ((org.bukkit.craftbukkit.v1_14_R1.CraftServer)Bukkit.getServer()).getCommandMap().register("",
//                new CommandTemplate(false) { @Override public boolean execute(CommandSender sender, String command, String[] args) { return false; } });
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
            if (ss != null && arg != null) {
                if (ss.toLowerCase().contains(arg.toLowerCase())) {
                    list.add(ss);
                }
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
                        if (subCommand instanceof EmptyCommand && ((EmptyCommand) subCommand).isLooped()) {
                            return filterContaining(args[i], subCommand.tabList(player, args));
                        }
                        return tabList(subCommand.getActions(), args, player, i + 1);
                    }
                    if (subCommand instanceof EmptyCommand && subCommand.getName(args[i - 2]) == null) {
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
    
    private void setDescription(CommandDescription description) {
        if (description.getName() == null) {
            //if name is not given, set name to class name
            description.setName(lowerCaseFirst(this.getClass().getSimpleName()));
        }
        if (description.getUsage() == null) {
            //if usage not given, set usage to '/<name>'
            description.setUsage("/" + description.getName());
        }
        
        this.setName(description.getName());
        this.setUsage(description.getUsage());
        this.setLabel(description.getName());
        this.setDescription(description.getDescription());
        this.timings = new CustomTimingsHandler("** Command: " + description.getName());
    }
    
    public void registerActions() {
    }
    
    public static String convertToArgs(List<SubCommand> commands, boolean optional) {
        return (optional ? "[" : "<") + commands.stream().map(SubCommand::getCommandName).collect(Collectors.joining("|")) + (optional ? "]" : ">");
    }
    
    private void collectActions(SubCommand actions, Pair<String, Message> pre, boolean b, LinkedHashMap<String, Message> commandList) {
        for (SubCommand action : actions.getActions()) {
            String name = "";
            String suffix = "";
            if (action.isLinked()) {
                if (!b) {
                    name = "[";
                    b = true;
                    commandList.put(pre.getLeft().trim(), pre.getRight());
                } else if (action.getActions().isEmpty() || !action.getActions().get(0).isLinked()) {
                    suffix = "]";
                    b = false;
                }
            }
        
            switch (action.getArgumentType()) {
                case REQUIRED:
                    name += "<" + action.getCommandName() + ">";
                    break;
                case OPTIONAL:
                    commandList.put(pre.getLeft().trim(), pre.getRight());
                    if (pre.getRight() == null) {
                        name += "[" + action.getCommandName() + "]";
                    } else {
                        name += "<" + action.getCommandName() + ">";
                    }
                    break;
                case FIXED:
                default:
                    name += action.getCommandName();
                    break;
            }
            name += suffix;
        
            if (action instanceof EmptyCommand) {
                if (((EmptyCommand) action).isLooped()) {
                    if (action.isLinked()) {
                        name += "]";
                    }
                    commandList.put((pre.getLeft() + " " + name).trim(), action.getCommandDescription());
                    continue;
                }
            }
            if (action.getActions().isEmpty()) {
                commandList.put((pre.getLeft() + " " + name).trim(), action.getCommandDescription());
            } else {
                collectActions(action, new Pair<>(pre.getLeft() + " " + name, action.getCommandDescription()), b, commandList);
            }
        }
    }
    
    public LinkedHashMap<String, Message> collectActions() {
        //requirements:
        /*
         * AN: Argument name
         * r: required argument
         * o: optional argument
         *
         * should support:
         * /command AN <r> [o]
         *   breaks into:
         *   /command AN <r>
         *   /command AN <r> [o]
         *
         * /command [o]
         *   breaks into:
         *   /command
         *   /command [o]
         *
         * /command [<r> <r>]
         *   breaks into:
         *   /command
         *   /command [<r> <r>]
         *
         * not:
         * /command [o] <r>
         * solution:
         * /command <o> <r1>
         * /command <r2>
         * where r1 and r2 are linked
         * */
        LinkedHashMap<String, Message> commandList = new LinkedHashMap<>();
    
        for (SubCommand action : this.getActions()) {
            if (action instanceof EmptyCommand) {
                if (((EmptyCommand) action).isLooped()) {
                    continue;
                }
            }
        
            if (action.getActions().isEmpty()) {
                commandList.put(("/" + this.getName() + " " + action.getCommandName()).trim(), action.getCommandDescription());
            } else {
                collectActions(action, new Pair<>("/" + this.getName() + " " + action.getCommandName(), action.getCommandDescription()), false, commandList);
            }
        }
        return commandList;
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
    
    public boolean removeAction(SubCommand subCommand) {
        return actions.remove(subCommand);
    }
    
    public SubCommand removeAction(String actionName) {
        for (SubCommand sub : actions) {
            if (sub.getName(actionName).equals(actionName)) {
                if (actions.remove(sub)) {
                    return sub;
                }
            }
        }
        return null;
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
    
    public Message getCommandDescription() {
        return commandDescription;
    }
    public void setCommandDescription(Message commandDescription) {
        this.commandDescription = commandDescription;
    }
    public void setCommandDescription(TextComponent... textComponents) {
        setCommandDescription(new Message(textComponents));
    }
    
    protected boolean runCommands(String arg, String[] args, Player player) {
        return runCommands(actions, arg, args, player);
    }
    
    public static class CommandDescription {
        private String name;
        private String description;
        private String usage;
        private List<String> aliases;
        
        public CommandDescription(String name, String description, String usage) {
            this(name, description, usage, new ArrayList<>());
        }
        
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
