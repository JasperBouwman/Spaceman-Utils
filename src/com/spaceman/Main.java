package com.spaceman;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends JavaPlugin {
    
    public static Main getInstance() {
        return JavaPlugin.getPlugin(Main.class);
    }
    
    public static <O> O getOrDefault(O object, O def) {
        return object == null ? def : object;
    }
    
    public static void log(String s) {
        log(Level.INFO, s);
    }
    
    public static void log(Level l, String s) {
        Bukkit.getLogger().log(l, "[" + Main.getInstance().getDescription().getName() + "] " + s);
    }
    
    public static ArrayList<ItemStack> giveItems(Player player, List<ItemStack> items) {
        ArrayList<ItemStack> returnList = new ArrayList<>();
        for (ItemStack item : player.getInventory().addItem(items.toArray(new ItemStack[0])).values()) {
            player.getWorld().dropItem(player.getLocation(), item);
            returnList.add(item);
        }
        return returnList;
    }
    
    public static ArrayList<ItemStack> giveItems(Player player, ItemStack... items) {
        return giveItems(player, Arrays.asList(items));
    }
    
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
    
    public static boolean containsSpecialCharacter(String s) {
        if (s == null || s.trim().isEmpty()) {
            return true;
        }
        Pattern p = Pattern.compile("[^A-Za-z0-9_-]");
        Matcher m = p.matcher(s);
        
        return m.find();
    }
    
    @SuppressWarnings("unused")
    public static void println(String text) {
        ClassData d = ___8d4rd3148796d_Xaf();
        if (d == null) {
            System.out.println(text + " (null:null)");
            return;
        }
        System.out.println(text + " (" + d.className + ":" + d.line + ")");
    }
    
    private static ClassData ___8d4rd3148796d_Xaf() {
        boolean thisOne = false;
        int thisOneCountDown = 1;
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : elements) {
            String methodName = element.getMethodName();
            int lineNum = element.getLineNumber();
            if (thisOne && (thisOneCountDown == 0)) {
                return new ClassData(lineNum, element.getClassName());
            } else if (thisOne) {
                thisOneCountDown--;
            }
            //experimental (not tested, if not working use the below one)
            if (methodName.equals(new Throwable().getStackTrace()[0].getMethodName())) {
                thisOne = true;
            }
//            if (methodName.equals("___8d4rd3148796d_Xaf")) {
//                thisOne = true;
//            }
        }
        return null;
    }
    
    private static class ClassData {
        private final int line;
        private final String className;
        
        private ClassData(int line, String className) {
            this.line = line;
            this.className = className;
        }
    }
}
