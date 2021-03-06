package com.spaceman.colorFormatter;

import com.spaceman.commandHander.ArgumentType;
import com.spaceman.commandHander.EmptyCommand;
import com.spaceman.commandHander.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spaceman.colorFormatter.ColorTheme.*;
import static com.spaceman.fancyMessage.TextComponent.textComponent;

public class ColorThemeCommand extends SubCommand {
    
    private final String headCommand = "command";
    
    public ColorThemeCommand() {
        EmptyCommand emptySetTheme = new EmptyCommand();
        emptySetTheme.setCommandName("theme", ArgumentType.REQUIRED);
        emptySetTheme.setCommandDescription(textComponent("This command is used to set your theme to the given default theme", ColorType.infoColor));
        
        EmptyCommand emptySetTypeColor = new EmptyCommand();
        emptySetTypeColor.setCommandName("color", ArgumentType.REQUIRED);
        emptySetTypeColor.setCommandDescription(textComponent("This command is used to set the color of the given color type", ColorType.infoColor));
        
        EmptyCommand emptySetType = new EmptyCommand();
        emptySetType.setCommandName("type", ArgumentType.REQUIRED);
        emptySetType.setTabRunnable((args, player) -> {
            if (ColorType.getTypes().contains(args[2])) {
                return Arrays.stream(ChatColor.values()).filter(ChatColor::isColor).map(ChatColor::name).collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
        emptySetType.addAction(emptySetTypeColor);
        
        EmptyCommand emptySet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "set";
            }
        };
        emptySet.setCommandName("set", ArgumentType.FIXED);
        emptySet.setTabRunnable((args, player) -> Stream.concat(ColorTheme.getDefaultThemes().stream(), ColorType.getTypes().stream()).collect(Collectors.toList()));
        emptySet.addAction(emptySetType);
        emptySet.addAction(emptySetTheme);
    
        
        EmptyCommand emptyGetType = new EmptyCommand();
        emptyGetType.setCommandName("type", ArgumentType.REQUIRED);
        emptyGetType.setCommandDescription(textComponent("This command is used to get the color of the given color type", ColorType.infoColor));
        
        EmptyCommand emptyGet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "get";
            }
        };
        emptyGet.setCommandName("get", ArgumentType.FIXED);
        emptyGet.setTabRunnable((args, player) -> ColorType.getTypes());
        emptyGet.addAction(emptyGetType);
    
    
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("");
        empty.setCommandDescription(textComponent("This command is used to see that your theme is set to", ColorType.infoColor));
        
        addAction(empty);
        addAction(emptySet);
        addAction(emptyGet);
    }
    
    @Override
    public String getName(String arg) {
        return "colorTheme";
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("set");
        list.add("get");
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        //command colorTheme
        //command colorTheme set <theme>
        //command colorTheme set <type> <color>
        //command colorTheme get <type>
        
        if (args.length == 1) {
            sendInfoTheme(player, "This is your %s color", "info");
            sendSuccessTheme(player, "This is your %s color", "success");
            sendErrorTheme(player, "This is your %s color", "error");
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("set")) {
                sendErrorTheme(player, "Usage: %s", "/" + headCommand + " colorTheme set <theme|<type> <color>");
            } else if (args[1].equalsIgnoreCase("get")) {
                sendErrorTheme(player, "Usage: %s", "/" + headCommand + " colorTheme get <type>");
            } else {
                sendErrorTheme(player, "Usage: %s", "/" + headCommand + " colorTheme <set|get>");
            }
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("set")) {
                if (ColorType.getTypes().contains(args[2])) {
                    sendErrorTheme(player, "Usage: %s", "/" + headCommand + " colorTheme set <type> <color>");
                } else if (ColorTheme.getDefaultThemes().contains(args[2])) {
                    ColorTheme.setDefaultTheme(player, args[2]);
                    sendSuccessTheme(player, "Successfully set color theme to %s", args[2]);
                } else {
                    sendErrorTheme(player, "%s is not a color type nor a default color theme", args[2]);
                }
            } else if (args[1].equalsIgnoreCase("get")) {
                if (ColorType.getTypes().contains(args[2])) {
                    sendInfoTheme(player, "The color of type %s is set to " + ColorType.valueOf(args[2]).getColor(player) + "this", args[2]);
                } else {
                    sendErrorTheme(player, "color type %s does not exist", args[2]);
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/" + headCommand + " colorTheme <set|get>");
            }
        } else if (args.length == 4) {
            if (args[1].equalsIgnoreCase("set")) {
                if (ColorType.getTypes().contains(args[2])) {
                    if (Arrays.stream(ChatColor.values()).map(ChatColor::name).collect(Collectors.toList()).contains(args[3].toUpperCase())) {
                        ColorType.valueOf(args[2]).setColor(player, ChatColor.valueOf(args[3].toUpperCase()));
                        sendSuccessTheme(player, "Successfully set color type %s to " + ColorType.valueOf(args[2]).getColor(player) + "this", args[2]);
                    } else {
                        sendErrorTheme(player, "color %s does not exist", args[3].toUpperCase());
                    }
                } else {
                    sendErrorTheme(player, "color type %s does not exist", args[2]);
                }
            } else if (args[1].equalsIgnoreCase("get")) {
                sendErrorTheme(player, "Usage: %s", "/" + headCommand + " colorTheme get <type>");
            } else {
                sendErrorTheme(player, "Usage: %s", "/" + headCommand + " colorTheme [set|get]");
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/" + headCommand + " colorTheme [set|get]");
        }
        
    }
    
}
