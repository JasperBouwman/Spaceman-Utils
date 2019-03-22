package com.spaceman.terrain.modes;

import com.spaceman.terrain.TerrainGenData;
import com.spaceman.terrain.TerrainMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.noise.SimplexOctaveGenerator;

@SuppressWarnings("unused, WeakerAccess, unchecked")
public class Bedrock extends TerrainMode.DataBased<Boolean> {

    private SimplexOctaveGenerator gen1;

    public Bedrock() {
    }

    public Bedrock(Boolean data) {
        super(data);
        gen1 = new SimplexOctaveGenerator(1432672635347583L, 8);
        gen1.setScale(2);
    }

    @Override
    public boolean isFinalMode() {
        return false;
    }

    @Override
    public TerrainMode get(Object i) {
        return new Bedrock((Boolean) i);
    }

    @Override
    public String getModeName() {
        return "bedrock";
    }

    @Override
    public void useMode(int x, int z, int genHeight, int highest, int start, int lowest, World world, TerrainGenData data) {
        if (getModeData()) {
            double bedrockHeight = gen1.noise(x, z, 5, 0.5) + lowest + 1;
            for (int y = lowest; y < bedrockHeight; y++) {
                setType(new Location(world, x, y, z).getBlock(), Material.BEDROCK);
            }
        }

    }
}
