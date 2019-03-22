package com.spaceman.terrain.modes;

import com.spaceman.terrain.TerrainGenData;
import com.spaceman.terrain.TerrainMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

@SuppressWarnings("unused, WeakerAccess, unchecked")
public class Top extends TerrainMode.DataBased<Material> {


    public Top(Material data) {
        super(data);
    }

    public Top() {
    }

    @Override
    public boolean isFinalMode() {
        return true;
    }

    @Override
    public TerrainMode get(Object i) {
        return new Top((Material) i);
    }

    @Override
    public String getModeName() {
        return "top";
    }

    @Override
    public void useMode(int x, int z, int genHeight, int highest, int start, int lowest, World world, TerrainGenData data) {
        if (genHeight == highest && genHeight > 0) {
            if (!getModeData().equals(Material.STRUCTURE_VOID)) {
                setType(new Location(world, x, genHeight, z).getBlock(), getModeData());
            }
        }
    }
}
