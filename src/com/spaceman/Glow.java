package com.spaceman;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;

@SuppressWarnings("all")
public class Glow extends Enchantment {
    
    /*
    * This util allows you to add the glow effect to items
    *
    * In your onEnable:
    * Glow.RegisterGlow();
    *
    * To use:
    * addGlow(itemStack);
    * Or:
    * addGlow(itemMeta);
    *
    * */
    
    public Glow() {
        super(new NamespacedKey(Main.getInstance(), Main.getInstance().getDescription().getName()));
    }
    
    public static void addGlow(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        if (im == null) {
            im = Bukkit.getItemFactory().getItemMeta(item.getType());
        }
        addGlow(im);
        item.setItemMeta(im);
    }
    
    public static void addGlow(ItemMeta meta) {
        meta.addEnchant(new Glow(), 1, true);
    }
    
    public static void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Enchantment.registerEnchantment(new Glow());
        } catch (IllegalArgumentException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public int getMaxLevel() {
        return 0;
    }
    
    @Override
    public int getStartLevel() {
        return 0;
    }
    
    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }
    
    @Override
    public boolean isTreasure() {
        return false;
    }
    
    @Override
    public boolean isCursed() {
        return false;
    }
    
    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }
    
    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return false;
    }
}
