package com.spaceman.terrain;

import com.spaceman.fileHander.Files;
import com.spaceman.fileHander.GettingFiles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.spaceman.fileHander.GettingFiles.getFiles;

@SuppressWarnings({"unused", "WeakerAccess"})
public class TerrainGenerator {

    public static HashMap<String, TerrainGenData> terrainGenData = new HashMap<>();

    public static void setType(final Block b, final Material m) {
        new BukkitRunnable() {
            public void run() {
                b.setType(m);
            }
        }.runTask(GettingFiles.p);
    }

    public static void addGen(String name, TerrainGenData data) {
        terrainGenData.put(name, data);
    }

    public static void addGen(Player player, TerrainGenData data) {
        terrainGenData.put(player.getUniqueId().toString(), data);
    }

    public static void generate(String terrainGenDataName, int x1, int z1, Location startL) {
        TerrainGenData mainGenerator = terrainGenData.get(terrainGenDataName);

        if (mainGenerator == null) {
            throw new IllegalArgumentException("TerrainGenData is null");
        }

        ArrayList<String> internalGenerators = new ArrayList<>();
        internalGenerators.add(terrainGenDataName);//add main generator so it would start with this one
        internalGenerators.addAll(getGenerators(mainGenerator.getGenerators()));//add all other generators
        HashMap<String, Integer> highestStorage = new HashMap<>();
        HashMap<String, Integer> genHeightStorage = new HashMap<>();
        boolean first = true;

        for (String internalGenerator : internalGenerators) {
            TerrainGenData i = terrainGenData.get(internalGenerator);

            if (i != null) {

                SimplexOctaveGenerator gen = new SimplexOctaveGenerator(i.getSeed(), 8);
                gen.setScale(i.getScale());

                TerrainMode layersMode = i.getMode("layers");

                for (int x = startL.getBlockX(); x < x1 + startL.getBlockX(); x++) {
                    for (int z = startL.getBlockZ(); z < z1 + startL.getBlockZ(); z++) {

                        int highest = highestStorage.getOrDefault(x + ";" + z, 0);
                        int oldHighest = (first ? i.getStart() : highest);

                        int genHeight = (int) (gen.noise(x, z, i.getFrequency(), i.getAmplitude()) * i.getMultitude() + i.getHeight());

                        genHeightStorage.put(x + ";" + z + ";" + internalGenerator, genHeight);

                        if (genHeight > highest) {
                            highest = genHeight;
                        }

                        for (int y = genHeight; y > oldHighest; y--) {
                            // internal modes: 'layers' 'generator'

                            Block block = new Location(startL.getWorld(), x, y, z).getBlock();

                            if (i.getBiome() != null) {
                                block.setBiome(i.getBiome());
                            }

                            if (layersMode == null) {
                                if (!i.getMaterial().equals(Material.STRUCTURE_VOID)) {
                                    setType(block, i.getMaterial());
                                }
                            }
                        }

                        useModes(i, x, z, genHeight, highest, oldHighest, i.getStart(), startL.getWorld());

                        if (oldHighest < highest) {
                            highestStorage.put(x + ";" + z, highest);
                        }
                    }
                }
            }
            first = false;
        }
        useFinalModes(internalGenerators, x1, z1, startL, highestStorage, genHeightStorage);
    }

    private static ArrayList<String> getGenerators(ArrayList<String> d) {

        if (d == null) {
            return new ArrayList<>();
        }

        ArrayList<String> list = new ArrayList<>(d);
        for (String data : d) {
            for (String tmpData : getGenerators(terrainGenData.get(data).getGenerators())) {
                if (!list.contains(tmpData)) {
                    list.add(tmpData);
                }
            }
        }
        return list;
    }

    public static ArrayList<String> getGenerators(String... d) {
        return getGenerators(new ArrayList<>(Arrays.asList(d)));
    }

    public static void useModes(TerrainGenData data,
                                 int x,
                                 int z,
                                 int genHeight,
                                 int highest,
                                 int start,
                                 int lowest,
                                 World world) {
        for (TerrainMode mode : data.getModes()) {
            if (!mode.isFinalMode()) {
                mode.useMode(x, z, genHeight, highest, start, lowest, world, data);
            }
        }
    }

    private static void useFinalModes(ArrayList<String> dataList,
                                      int x1,
                                      int z1,
                                      Location startL,
                                      HashMap<String, Integer> highestStorage,
                                      HashMap<String, Integer> genHeightStorage) {

        for (int x = startL.getBlockX(); x < x1 + startL.getBlockX(); x++) {
            for (int z = startL.getBlockZ(); z < z1 + startL.getBlockZ(); z++) {

                for (String sData : dataList) {
                    TerrainGenData data = terrainGenData.get(sData);

                    for (TerrainMode mode : data.getModes()) {
                        if (mode.isFinalMode()) {
                            mode.useMode(x,
                                    z,
                                    genHeightStorage.getOrDefault(x + ";" + z + ";" + sData, 0),
                                    highestStorage.getOrDefault(x + ";" + z, 0),
                                    0,
                                    0,
                                    startL.getWorld(),
                                    data);
                        }
                    }
                }
            }
        }
    }

    public static void generate(Player player, int x, int z) {
        generate(player.getUniqueId().toString(), x, z, player.getLocation());
    }

    public static boolean isUUID(String string) {
        return string.matches("[a-f0-9]{8}-[a-f0-9]{4}-4[0-9]{3}-[89ab][a-f0-9]{3}-[0-9a-f]{12}");
    }

    public static void saveGenerators() {
        Files terrainData = getFiles("terrainData");
        terrainData.getConfig().set("terrainData", null);

        if (terrainGenData.isEmpty()) {
            return;
        }

        for (String name : terrainGenData.keySet()) {
            TerrainGenData genData = terrainGenData.get(name);

            terrainData.getConfig().set("terrainGenData." + name + ".data.frequency", genData.getFrequency());
            terrainData.getConfig().set("terrainGenData." + name + ".data.amplitude", genData.getAmplitude());
            terrainData.getConfig().set("terrainGenData." + name + ".data.multitude", genData.getMultitude());
            terrainData.getConfig().set("terrainGenData." + name + ".data.scale", genData.getScale());
            terrainData.getConfig().set("terrainGenData." + name + ".data.height", genData.getHeight());
            terrainData.getConfig().set("terrainGenData." + name + ".data.start", genData.getStart());
            terrainData.getConfig().set("terrainGenData." + name + ".data.seed", genData.getSeed());
            terrainData.getConfig().set("terrainGenData." + name + ".data.material", genData.getMaterial().toString());
            terrainData.getConfig().set("terrainGenData." + name + ".data.generators", genData.getGenerators());
            if (genData.getBiome() != null) {
                terrainData.getConfig().set("terrainGenData." + name + ".data.biome", genData.getBiome().toString());
            }

            //save terrainModes
            for (TerrainMode mode : genData.getModes()) {
                if (mode instanceof TerrainMode.DataBased) {
                    terrainData.getConfig().set("terrainGenData." + name + ".terrainMode." + mode.getClass().getName().replace(".", "/") + ".data", ((TerrainMode.DataBased) mode).getModeData());
                }
                if (mode instanceof TerrainMode.MapBased) {
                    terrainData.getConfig().set("terrainGenData." + name + ".terrainMode." + mode.getModeName() + ".data", ((TerrainMode.MapBased) mode).getModeData());
                }
                if (mode instanceof TerrainMode.ArrayBased) {
                    terrainData.getConfig().set("terrainGenData." + name + ".terrainMode." + mode.getModeName() + ".data", ((TerrainMode.ArrayBased) mode).getModeData());
                }
            }
        }
        terrainData.saveConfig();
    }

    public static void initGenerators() {
        Files terrainData = getFiles("terrainData");

        if (terrainData.getConfig().contains("terrainGenData")) {
            for (String name : terrainData.getConfig().getConfigurationSection("terrainGenData").getKeys(false)) {

                TerrainGenData genData = new TerrainGenData();

                genData.setFrequency(terrainData.getConfig().getDouble("terrainGenData." + name + ".data.frequency"));
                genData.setAmplitude(terrainData.getConfig().getDouble("terrainGenData." + name + ".data.amplitude"));
                genData.setMultitude(terrainData.getConfig().getDouble("terrainGenData." + name + ".data.multitude"));
                genData.setScale(terrainData.getConfig().getDouble("terrainGenData." + name + ".data.scale"));
                genData.setHeight(terrainData.getConfig().getInt("terrainGenData." + name + ".data.height"));
                genData.setStart(terrainData.getConfig().getInt("terrainGenData." + name + ".data.start"));
                genData.setSeed(terrainData.getConfig().getLong("terrainGenData." + name + ".data.seed"));
                genData.setMaterial(Material.getMaterial(terrainData.getConfig().getString("terrainGenData." + name + ".data.material")));
                genData.setGenerators((ArrayList<String>) terrainData.getConfig().getStringList("terrainGenData." + name + ".data.generators"));
                if (terrainData.getConfig().contains("terrainGenData." + name + ".data.biome")) {
                    genData.setBiome(Biome.valueOf(terrainData.getConfig().getString("terrainGenData." + name + ".data.biome")));
                }

                ArrayList<TerrainMode> terrainModes = terrainGenData.getOrDefault(name, new TerrainGenData()).getModes();
                for (String modeName : terrainData.getConfig().getConfigurationSection("terrainGenData." + name + ".terrainMode").getKeys(false)) {

                    try {
                        Class<?> clazz = Class.forName(modeName.replace("/", "."));

                        Object o = terrainData.getConfig().get("terrainGenData." + name + ".terrainMode." + modeName + ".data");

                        Object c = clazz.newInstance();

                        Object mode = clazz.getMethod("get", Object.class)
                                .invoke(c, o);

                        terrainModes.add((TerrainMode) mode);

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    genData.addModes(terrainModes);

                    terrainGenData.put(name, genData);
                }
            }
        }
    }
}
