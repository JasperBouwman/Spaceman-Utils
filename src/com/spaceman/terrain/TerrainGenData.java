package com.spaceman.terrain;

import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.util.ArrayList;

@SuppressWarnings({"unused", "WeakerAccess"})
public class TerrainGenData {

    private double frequency = 5;
    private double amplitude = 0.01;
    private double multitude = 5;
    private double scale = 0.015625;
    private int height = 63;
    private int start = 60;
    private long seed = 1;
    private Material material = Material.GRASS;
    private Biome biome = null;

    private ArrayList<TerrainMode> modes = new ArrayList<>();
    private ArrayList<String> generators = new ArrayList<>();

    public TerrainGenData() {
    }

    public void addMode(TerrainMode mode) {
        for (TerrainMode oldMode : modes) {
            if (oldMode.getModeName().equals(mode.getModeName())) {
                modes.remove(oldMode);
                this.modes.add(mode);
                return;
            }
        }
        this.modes.add(mode);
    }

    public void addModes(ArrayList<TerrainMode> modes) {
        this.modes.addAll(modes);
    }

    public ArrayList<TerrainMode> getModes() {
        return modes;
    }

    public void setModes(ArrayList<TerrainMode> modes) {
        this.modes = modes;
    }

    public ArrayList<String> getGenerators() {
        return generators;
    }

    public void setGenerators(ArrayList<String> generators) {
        this.generators = generators;
    }

    public void addGenerator(String data) {
        if (!generators.contains(data)) {
            generators.add(data);
        }
    }

    public boolean hadMode(String mode) {
        return (getMode(mode) == null);
    }

    public TerrainMode getMode(String mode) {

        for (TerrainMode tmpMode : getModes()) {
            if ((tmpMode).getModeName().equalsIgnoreCase(mode)) {
                return tmpMode;
            }
        }
        return null;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getMultitude() {
        return multitude;
    }

    public void setMultitude(double multitude) {
        this.multitude = multitude;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Biome getBiome() {
        return biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }


}