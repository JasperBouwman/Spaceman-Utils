package com.spaceman.terrain.modes;

import com.spaceman.fileHander.GettingFiles;
import com.spaceman.terrain.TerrainGenData;
import com.spaceman.terrain.TerrainMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

@SuppressWarnings("unused, WeakerAccess, unchecked")
public class AddTrees extends TerrainMode.DataBased<Integer> {

    public AddTrees(Integer data) {
        super(data);
    }
    public AddTrees(){}

    @Override
    public boolean isFinalMode() {
        return true;
    }

    @Override
    public TerrainMode get(Object i) {
        return new AddTrees((Integer) i);
    }

    @Override
    public String getModeName() {
        return "addTrees";
    }

    @Override
    public void useMode(int x, int z, int genHeight, int highest, int start, int lowest, World world, TerrainGenData data) {
        if (getModeData() == 0) {
            return;
        }
        if (genHeight == highest && genHeight > 0) {

            Random random = new Random();

            int chance = random.nextInt(1000);

            if (getModeData() > chance) {
                new BukkitRunnable() {
                    public void run() {
                        TreeTypes types = (TreeTypes) data.getMode("treeTypes");
                        if (types != null) {
                            TreeType tree = types.useMode();
                            if (tree == null) {
                                tree = TreeType.JUNGLE;
                            }
                            Block block = new Location(world, x, genHeight, z).getBlock();
                            Location treeBlock = new Location(world, x, genHeight + 1, z);
                            if (treeBlock.getBlock().getType().equals(Material.AIR)) {
                                block.setType(Material.DIRT);
                                world.generateTree(treeBlock, tree);
                            }
                        }
                    }
                }.runTask(GettingFiles.p);
            }
        }
    }
}
