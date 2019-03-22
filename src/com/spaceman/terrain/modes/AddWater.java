package com.spaceman.terrain.modes;

import com.spaceman.terrain.TerrainGenData;
import com.spaceman.terrain.TerrainMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

@SuppressWarnings("unused, WeakerAccess, unchecked")
public class AddWater extends TerrainMode.DataBased<Integer> {

    public AddWater(Integer data) {
        super(data);
    }

    public AddWater() {
    }

    @Override
    public boolean isFinalMode() {
        return true;
    }

    @Override
    public TerrainMode get(Object i) {
        return new AddWater((Integer) i);
    }


    @Override
    public String getModeName() {
        return "addWater";
    }

    @Override
    public void useMode(int x, int z, int genHeight, int highest, int start, int lowest, World world, TerrainGenData data) {
        for (int y = highest + 1; y <= getModeData(); y++) {
            new Location(world, x, y, z).getBlock().setType(Material.WATER);
        }
    }
}
