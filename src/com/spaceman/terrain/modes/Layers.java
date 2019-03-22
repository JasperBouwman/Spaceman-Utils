package com.spaceman.terrain.modes;

import com.spaceman.terrain.TerrainGenData;
import com.spaceman.terrain.TerrainMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;

@SuppressWarnings("unused, WeakerAccess, unchecked")
public class Layers extends TerrainMode.MapBased<String, Integer> {

    public Layers(HashMap<String, Integer> data) {
        super(data);
    }

    public Layers() {
    }

    @Override
    public boolean isFinalMode() {
        return false;
    }

    @Override
    public TerrainMode get(Object i) {
        return new Layers((HashMap<String, Integer>) i);
    }

    @Override
    public String getModeName() {
        return "layers";
    }

    @Override
    public void useMode(int x, int z, int genHeight, int highest, int start, int lowest, World world, TerrainGenData data) {

        int fromY = 0;

        for (String m : getModeData().keySet()) {
            for (int mY = 0; mY < getModeData().get(m); mY++) {
                if (start < highest - fromY) {
                    Block block = new Location(world, x, genHeight - fromY, z).getBlock();
                    if (!m.equalsIgnoreCase(Material.STRUCTURE_VOID.name().toLowerCase())) {
                        setType(block, Material.getMaterial(m));
                    }
                    fromY++;
                }
            }
        }
    }
}
