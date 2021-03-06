package com.spaceman.cooldown;

import com.spaceman.Main;
import com.spaceman.commandHander.EmptyCommand;
import com.spaceman.commandHander.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.spaceman.Permissions.hasPermission;
import static com.spaceman.colorFormatter.ColorTheme.*;

public class CooldownSubCommand extends SubCommand {
    
    /*
     * This pre-made sub-command uses the following utils:
     * - Pre-made Main template
     * - Permission
     * - CommandHandler
     * - ColorTheme (or ColorFormatter, this is all commented out)
     * */
    
    private static final String pluginName = Main.getInstance().getDescription().getName();
    private static final String permissionRoot = pluginName;
    
    public CooldownSubCommand() {
        EmptyCommand e = new EmptyCommand();
        e.setTabRunnable((args, player) -> {
            if (!hasPermission(player, false, true, permissionRoot + ".cooldown.set", permissionRoot + ".admin.cooldown")) {
                return new ArrayList<>();
            }
            
            ArrayList<String> originalList = new ArrayList<>();
            Arrays.stream(CooldownManager.values()).map(CooldownManager::name).forEach(originalList::add);
            ArrayList<String> list = new ArrayList<>(originalList);
            list.add("permission");
            list.remove(args[1]);
            if (originalList.contains(args[1])) {
                return list;
            }
            
            return new ArrayList<>();
        });
        addAction(e);
    }
    
    @Override
    public String getName(String arg) {
        return "cooldown";
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        Arrays.stream(CooldownManager.values()).map(CooldownManager::name).forEach(list::add);
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
//        //command cooldown <cooldown> [value] //fixme -> base command name
        
        if (args.length == 2) {
            
            if (!hasPermission(player, permissionRoot + ".command.cooldown")) {
                return;
            }
            if (CooldownManager.contains(args[1])) {
//                player.sendMessage(formatInfo("Cooldown value of %s is set to %s", args[1], CooldownManager.valueOf(args[1]).value()));
                sendInfoTheme(player, "Cooldown value of %s is set to %s", args[1], CooldownManager.valueOf(args[1]).value());
            } else {
//                player.sendMessage(formatError("Cooldown %s does not exist", args[1]));
                sendErrorTheme(player, "Cooldown %s does not exist", args[1]);
            }
            
        } else if (args.length > 2) {
            
            if (!hasPermission(player, true, true, permissionRoot + ".command.cooldown.set", permissionRoot + ".admin.cooldown")) {
                return;
            }
            if (CooldownManager.contains(args[1])) {
                try {
                    Long.parseLong(args[2]);
                } catch (NumberFormatException nfe) {
                    if (!args[2].equals("permission")) {
                        if (!CooldownManager.contains(args[2])) {
//                            player.sendMessage(formatError("%s is not a valid value, it must be a number or another cooldown name", args[2]));
                            sendErrorTheme(player, "%s is not a valid value, it must be a number or another cooldown name", args[2]);
                            return;
                        } else if (args[1].equals(args[2])) {
//                            player.sendMessage(formatError("The value of a cooldown can not be set to it self"));
                            sendErrorTheme(player, "The value of a cooldown can not be set to it self");
                            return;
                        }
                    }
                }
                
                CooldownManager.valueOf(args[1]).edit(args[2]);
//                player.sendMessage(formatSuccess("Successfully set cooldown value of %s to %s", args[1], args[2]));
                sendSuccessTheme(player, "Successfully set cooldown value of %s to %s", args[1], args[2]);
            } else {
//                player.sendMessage(formatError("Cooldown %s does not exist", args[1]));
                sendErrorTheme(player, "Cooldown %s does not exist", args[1]);
            }
        } else {
//            player.sendMessage(formatError("Usage: %s", "/" + pluginName + " cooldown <cooldown> [value]"));
            sendErrorTheme(player, "Usage: %s", "/" + pluginName + " cooldown <cooldown> [value]");
        }
        
    }
}
