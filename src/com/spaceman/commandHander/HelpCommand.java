package com.spaceman.commandHander;

import com.spaceman.commandHander.customRunnables.RunRunnable;
import com.spaceman.fancyMessage.Message;
import com.spaceman.fancyMessage.TextComponent;
import com.spaceman.fancyMessage.events.ClickEvent;
import com.spaceman.fancyMessage.events.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//import static com.spaceman.colorFormatter.ColorFormatter.*;
import static com.spaceman.colorFormatter.ColorTheme.ColorType.*;
import static com.spaceman.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.commandHander.CommandTemplate.runCommands;
import static com.spaceman.fancyMessage.TextComponent.textComponent;

public class HelpCommand extends SubCommand {
    
    /*
    * This help sub command is created with ColorTheme and ColorFormatter,
    * to use ColorTheme import: 'import static com.spaceman.colorFormatter.ColorTheme.ColorType.*;'
    * to use ColorFormatter import: 'import static com.spaceman.colorFormatter.ColorFormatter.*;'
    *
    * as default its set to ColorTheme
    *
    * Example on how to use:
    *
    * in your CommandTemplate you can override the registerActions method
    * put there:
    *
    * HelpCommand helpCommand = new HelpCommand(this);
    * addAction(helpCommand);
    *
    * This is a simple help command. It automatic collects all the commands that are in the CommandTemplate.
    * You have to be sure that ALL your sub commands have the commandName and commandDescription set.
    *
    * If you want more help pages (for extra explanation):
    * helpCommand.addExtraHelp("ExtraHelpName", new Message(textComponent("your extra help info to be send on execution"))); //or
    * helpCommand.addExtraHelp("ExtraHelpName", (args, player) -> {//your code to be executed})); //or
    * EmptyCommand extraHelpCommand = new EmptyCommand(){
    *    @Override
    *    public String getName(String arg) {
    *        return "ExtraHelpName";
    *    }
    *};
    * extraHelpCommand.setRunnable((args, player) -> {//your code to be executed});
    * helpCommand.addExtraHelp("ExtraHelpName", extraHelpCommand);
    *
    * */
    
    private int listSize = 10;
    private CommandTemplate template;
    private Message commandMessage;
    private List<String> extraHelp = new ArrayList<>();
    
    public HelpCommand(CommandTemplate template) {
        this(template, new Message(textComponent("This command is used to get all the help you need for this command", infoColor)));
        Bukkit.getLogger().info("No help command description given, using default one for /" + template.getName());
    }
    
    public HelpCommand(CommandTemplate template, Message commandMessage) {
        this.template = template;
        this.commandMessage = commandMessage;
        
        EmptyCommand commandList = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                try {
                    Integer.parseInt(argument);
                    return argument;
                } catch (NumberFormatException nfe) {
                    return "";
                }
            }
        };
        commandList.setCommandName("page", ArgumentType.REQUIRED);
        commandList.setCommandDescription(textComponent("This command is used to get the help page", infoColor));
        commandList.setRunnable((args, player) -> {
            
            int startIndex;
            try {
                startIndex = (Integer.parseInt(args[1]) - 1) * listSize;
            } catch (NumberFormatException nfe) {
//                player.sendMessage(formatError(args[1] + " is not a number"));
                sendErrorTheme(player, "%s is not a number", args[1]);//fixme ColorTheme or ColorFormatter
                return;
            }
            
            Message message = new Message();
            HashMap<String, Message> commandMap = template.collectActions();
            
            if (startIndex > commandMap.size()) {
                startIndex = (commandMap.size() / listSize) * listSize;
            }
            if (startIndex < 0) {
                startIndex = 0;
            }
            
            message.addText(textComponent("/" + template.getName(), varInfoColor));
            message.addText(textComponent(" help page ", infoColor));
            int page = startIndex / 10 + 1;
            message.addText(textComponent(String.valueOf(page), varInfoColor));
            
            List<String> commandArrayList = new ArrayList<>(commandMap.keySet());
            if (commandArrayList.size() > listSize) {
                
                message.addText(textComponent(" (", infoColor));
                HoverEvent forwardHover = new HoverEvent(textComponent("/" + template.getName() + " help " + (page + 1), infoColor));
                ClickEvent forwardClick = ClickEvent.runCommand("/" + template.getName() + " help " + (page + 1));
                HoverEvent backwardHover = new HoverEvent(textComponent("/" + template.getName() + " help " + (page - 1), infoColor));
                ClickEvent backwardClick = ClickEvent.runCommand("/" + template.getName() + " help " + (page - 1));
                if (page != 1) {
                    message.addText(textComponent("<-", varInfoColor, backwardHover, backwardClick));
                }
                if (tabList(player, new String[]{}).contains(String.valueOf(page + 1))) {
                    if (page != 1) message.addText(" ");
                    message.addText(textComponent("->", varInfoColor, forwardHover, forwardClick));
                }
                message.addText(textComponent(")", infoColor));
            }
            message.addText(textComponent(":\n", varInfoColor));
            
            boolean color = true;
            
            for (int i = startIndex; i < startIndex + listSize && i < commandArrayList.size(); i++) {
                String command = commandArrayList.get(i);
                
                TextComponent textComponent = new TextComponent();
                textComponent.setText(command);
                textComponent.setColor(color ? varInfoColor : varInfo2Color);
                
                HoverEvent hoverEvent = new HoverEvent();
                hoverEvent.addMessage(commandMap.get(command));
                textComponent.addTextEvent(hoverEvent);
                
                message.addText(textComponent);
                
                message.addText("\n");
                
                color = !color;
            }
            message.removeLast();
            
            message.sendMessage(player);
        });
        
        EmptyCommand commandHelp = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return template.getName();
            }
        };
        commandHelp.setCommandName(template.getName() + " command...", ArgumentType.REQUIRED);
        commandHelp.setCommandDescription(textComponent("This command is used to get the help of the specified command", infoColor));
        commandHelp.setRunnable((args, player) -> {
            HashMap<String, Message> commandMap = template.collectActions();
            String command = "/" + String.join(" ", Arrays.asList(args).subList(1, args.length));
            
            Message message = new Message();
            
            message.addText(textComponent("Results for ", infoColor));
            message.addText(textComponent(command, varInfoColor));
            message.addText(textComponent(":\n", infoColor));
            
            boolean color = true;
            
            for (String tmpCommand : commandMap.keySet()) {
                if (tmpCommand.toLowerCase().startsWith(command.toLowerCase()) || command.equalsIgnoreCase(tmpCommand)) {
                    TextComponent textComponent = new TextComponent(tmpCommand);
                    textComponent.setColor(color ? varInfoColor : varInfo2Color);
                    
                    HoverEvent hEvent = new HoverEvent();
                    hEvent.addMessage(commandMap.get(tmpCommand));
                    textComponent.addTextEvent(hEvent);
                    
                    message.addText(textComponent);
                    
                    
                    message.addText("\n");
                    color = !color;
                }
            }
            message.removeLast();
            message.sendMessage(player);
        });
        commandHelp.setTabRunnable((args, player) -> {
            String s = String.join(" ", Arrays.asList(args).subList(1, args.length - 1)) + " ";
            return template.collectActions().keySet().stream()
                    .map(c -> c.substring(1).toLowerCase())
                    .filter(c -> c.startsWith(s.toLowerCase()))
                    .map(c -> c.replaceFirst("(?i)" + s, ""))
                    .collect(Collectors.toList());
        });
        commandHelp.setLooped(true);
        
        if (commandMessage != null) {
            addAction(new EmptyCommand(){
                @Override
                public String getCommandName() {
                    return "";
                }
                
                @Override
                public Message getCommandDescription() {
                    return commandMessage;
                }
                
                @Override
                public String getName(String argument) {
                    return "";
                }
            });
        }
        addAction(commandList);
        addAction(commandHelp);
    }
    
    
    public void addExtraHelp(String helpName, Message message, TextComponent commandDescription) {
        addExtraHelp(helpName, (args, player) -> message.sendMessage(player), new Message(commandDescription));
    }
    public void addExtraHelp(String helpName, Message message, Message commandDescription) {
        addExtraHelp(helpName, (args, player) -> message.sendMessage(player), commandDescription);
    }
    public void addExtraHelp(String helpName, Message message) {
        addExtraHelp(helpName, (args, player) -> message.sendMessage(player), null);
    }
    
    public void addExtraHelp(String helpName, RunRunnable command, Message commandDescription) {
        EmptyCommand helpCommand = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return helpName;
            }
        };
        helpCommand.setRunnable(command);
        helpCommand.setCommandDescription(commandDescription == null ?
                new Message(textComponent("To get additional information about this topic", infoColor)) :
                commandDescription);
        addExtraHelp(helpName, helpCommand);
    }
    
    public void addExtraHelp(String helpName, EmptyCommand helpCommand) {
        extraHelp.add(helpName);
        helpCommand.setCommandName(helpName, ArgumentType.FIXED);
        addAction(helpCommand);
    }
    
    public boolean removeExtraHelp(String dataName) {
        extraHelp.remove(dataName);
        return removeAction(dataName) != null;
    }
    
    @Override
    public String getName(String arg) {
        return "help";
    }
    
    public int getListSize() {
        return listSize;
    }
    
    public void setListSize(int listSize) {
        this.listSize = Math.max(1, listSize);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add(template.getName());
        list.addAll(extraHelp);
        int commandSize = template.collectActions().size();
        IntStream.range(0, commandSize / listSize).mapToObj(i -> String.valueOf(i + 1)).forEach(list::add);
        if (commandSize % listSize != 0) {
            list.add(String.valueOf(commandSize / listSize + 1));
        }
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        if (args.length <= 1) {
            if (commandMessage != null) {
                commandMessage.sendMessage(player);
                return;
            }
        } else {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
//        player.sendMessage(formatError("Usage: %s", "/" + template.getName() + " help " + (commandMessage == null ? "<" : "[") + "page|" + template.getName() + " command..." +
//                extraHelp.stream().collect(Collectors.joining("|", (extraHelp.size() == 0 ? "" : "|"), "")) + (commandMessage == null ? ">" : "]")));
        sendErrorTheme(player, "Usage: %s", "/" + template.getName() + " help " + (commandMessage == null ? "<" : "[") + "page|" + template.getName() + " command..." +
                extraHelp.stream().collect(Collectors.joining("|", (extraHelp.size() == 0 ? "" : "|"), "")) + (commandMessage == null ? ">" : "]"));
        //fixme ColorTheme or ColorFormatter
    }
}
