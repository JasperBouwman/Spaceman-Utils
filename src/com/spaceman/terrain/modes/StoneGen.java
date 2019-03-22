package com.spaceman.terrain.modes;

import com.spaceman.terrain.TerrainGenData;
import com.spaceman.terrain.TerrainMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import static com.spaceman.terrain.TerrainGenerator.terrainGenData;
import static com.spaceman.terrain.TerrainGenerator.useModes;

@SuppressWarnings("unused, WeakerAccess, unchecked")
public class StoneGen extends TerrainMode.DataBased<String> {

    public StoneGen(String data) {
        super(data);
    }

    public StoneGen() {
    }

    @Override
    public String getModeName() {
        return "stoneGen";
    }

    @Override
    public boolean isFinalMode() {
        return false;
    }

    @Override
    public TerrainMode get(Object i) {
        return new StoneGen((String) i);
    }

    @Override
    public void useMode(int x, int z, int genHeight, int highest, int start, int lowest, World world, TerrainGenData data) {

        //newGenHeight is the start of this generator

        TerrainGenData newData = terrainGenData.get(getModeData());

        SimplexOctaveGenerator gen = new SimplexOctaveGenerator(newData.getSeed(), 8);
        gen.setScale(newData.getScale());
        int newGenHeight = (int) (gen.noise(x, z, newData.getFrequency(), newData.getAmplitude()) * newData.getMultitude() + newData.getHeight());

        TerrainMode layersMode = newData.getMode("layers");

        for (int y = genHeight; y > newGenHeight && y > start; y--) {
            // internal modes: 'layers'

            Block block = new Location(world, x, y, z).getBlock();

            if (newData.getBiome() != null) {
                block.setBiome(newData.getBiome());
            }

            if (layersMode == null) {
                if (!newData.getMaterial().equals(Material.STRUCTURE_VOID)) {
                    setType(block, newData.getMaterial());
                }
            }
        }

        useModes(newData, x, z, Math.max(newGenHeight, genHeight), highest, Math.max(newGenHeight, start), lowest, world);
    }
}
