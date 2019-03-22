package com.spaceman.terrain;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class TerrainMode {

    protected static void setType(Block block, Material material) {
        TerrainGenerator.setType(block, material);
    }

    public abstract TerrainMode get(Object i);

    public abstract String getModeName();

    public abstract boolean isFinalMode();

    public abstract void useMode(int x, int z, int genHeight, int highest, int start, int lowest, World world, TerrainGenData data);

    public static abstract class DataBased<D> extends TerrainMode {

        D modeData;

        public DataBased() {
        }
        public DataBased(D data) {
            this.modeData = data;
        }

        public D getModeData() {
            return modeData;
        }

        public void setData(D modeData) {
            this.modeData = modeData;
        }
    }

    public static abstract class ArrayBased<D> extends TerrainMode {

        private ArrayList<D> modeData;

        public ArrayBased(ArrayList<D> data) {
            this.modeData = data;
        }
        public ArrayBased() {
        }

        public ArrayList<D> getModeData() {
            return modeData;
        }

        public boolean addData(D data) {
            if (!modeData.contains(data)) {
                return modeData.add(data);
            }
            return false;
        }

        public boolean removeData(D data) {
            return modeData.remove(data);
        }

    }

    public static abstract class MapBased<V, D> extends TerrainMode {

        private HashMap<V, D> modeData;

        public MapBased(HashMap<V, D> data) {
            this.modeData = data;
        }
        public MapBased(){}

        public HashMap<V, D> getModeData() {
            return modeData;
        }

        public void addData(V value, D data) {
            modeData.put(value, data);
        }

        public D removeData(V value) {
            return modeData.remove(value);
        }


    }
}
