package com.spaceman.terrain.modes;

import com.spaceman.terrain.TerrainGenData;
import com.spaceman.terrain.TerrainMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Random;

@SuppressWarnings("unused, WeakerAccess, unchecked")
public class TopR extends TerrainMode.MapBased<String, Integer> {

    public TopR(HashMap<String, Integer> data) {
        super(data);
    }
    public TopR(){}
    @Override
    public boolean isFinalMode() {
        return true;
    }

    @Override
    public TerrainMode get(Object i) {
        return new TopR((HashMap<String, Integer>) i);
    }

    @Override
    public String getModeName() {
        return "topR";
    }

    @Override
    public void useMode(int x, int z, int genHeight, int highest, int start, int lowest, World world, TerrainGenData data) {
        if (genHeight == highest && genHeight > 0) {

            int total = 0;

            for (String m : getModeData().keySet()) {
                total += getModeData().get(m);
            }

            Random random = new Random();

            int mSelected = random.nextInt(total);

            total = 0;

            for (String m : getModeData().keySet()) {
                total += getModeData().get(m);
                if (total > mSelected) {
                    if (!Material.getMaterial(m).equals(Material.STRUCTURE_VOID)) {
                        setType(new Location(world, x, highest, z).getBlock(), Material.getMaterial(m));
                    }
                    return;
                }
            }
        }
    }
}
