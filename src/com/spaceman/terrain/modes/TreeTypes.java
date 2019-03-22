package com.spaceman.terrain.modes;

import com.spaceman.terrain.TerrainGenData;
import com.spaceman.terrain.TerrainMode;
import org.bukkit.TreeType;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Random;

@SuppressWarnings("unused, WeakerAccess, unchecked")
public class TreeTypes extends TerrainMode.MapBased<String, Integer> {

    public TreeTypes(HashMap<String, Integer> data) {
        super(data);
    }

    public TreeTypes() {
    }

    @Override
    public boolean isFinalMode() {
        return false;
    }

    @Override
    public TerrainMode get(Object i) {
        return new TreeTypes((HashMap<String, Integer>) i);
    }

    @Override
    public String getModeName() {
        return "treeTypes";
    }

    @Override
    public void useMode(int x, int z, int genHeight, int highest, int start, int lowest, World world, TerrainGenData data) {
    }

    public TreeType useMode() {

        int total = 0;

        for (String tree : getModeData().keySet()) {
            total += getModeData().get(tree);
        }

        Random random = new Random();

        int mSelected = random.nextInt(total);

        total = 0;

        for (String tree : getModeData().keySet()) {
            total += getModeData().get(tree);
            if (total > mSelected) {
                return TreeType.valueOf(tree);
            }
        }
        return null;
    }
}
