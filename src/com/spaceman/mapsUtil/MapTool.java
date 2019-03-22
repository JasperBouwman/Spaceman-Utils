package com.spaceman.mapsUtil;

import com.spaceman.Main;
import com.spaceman.fileHander.Files;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import static com.spaceman.fileHander.GettingFiles.getFile;

@SuppressWarnings({"unused", "WeakerAccess", "deprecation"})
public class MapTool {

    private static HashMap<String, MapTool> mapTools = new HashMap<>();
    private BufferedImage image;
    private ArrayList<Short> mapIDs = new ArrayList<>();
    private int width = 0;
    private int height = 0;
    private int background = new Color(0, 0, 0).getRGB();
    private boolean keepCentered = true;
    private boolean keepRatio = false;
    private String mapToolName;
    private JavaPlugin p;
    private String imageName;

    public MapTool(JavaPlugin p, String image, String name) throws IllegalArgumentException {
        this.p = p;
        this.imageName = image;
        this.mapToolName = name;

        if (mapTools.containsKey(name)) {
            throw new IllegalArgumentException("MapTool name '" + name + "' already exist");
        }

        try {
            File f = new File(p.getDataFolder() + "/maps", image);
            if (!f.exists()) {
                InputStream in = p.getResource("NoImageFound.png");

                if (in == null) {
                    return;
                }
                this.image = ImageIO.read(in);
            } else {
                this.image = ImageIO.read(f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapTools.put(name, this);
    }

    public static MapTool getMapTool(String name) {
        return mapTools.getOrDefault(name, null);
    }

    public static void saveMapTool(String name) {
        Files maps = getFile("maps");
        MapTool tool = mapTools.get(name);

        maps.getConfig().set("mapTools." + name + ".width", tool.width);
        maps.getConfig().set("mapTools." + name + ".height", tool.height);
        maps.getConfig().set("mapTools." + name + ".keepCentered", tool.keepCentered);
        maps.getConfig().set("mapTools." + name + ".keepRatio", tool.keepRatio);
        maps.getConfig().set("mapTools." + name + ".imageName", tool.imageName);
        maps.getConfig().set("mapTools." + name + ".background", tool.background);
        maps.getConfig().set("mapTools." + name + ".mapIDs", tool.mapIDs);

        maps.saveConfig();
    }

    public static void saveMapTools() {
        getFile("maps").getConfig().set("mapTools", null);
        for (String name : mapTools.keySet()) {
            saveMapTool(name);
        }
    }

    public static void loadMaps(JavaPlugin p) {
        Files maps = getFile("maps");
        if (maps.getConfig().contains("mapTools")) {
            for (String name : maps.getConfig().getConfigurationSection("mapTools").getKeys(false)) {
                loadMap(p, name);
            }
        }
    }

    public static void loadMap(JavaPlugin p, String name) {
        Files maps = getFile("maps");

        int width = maps.getConfig().getInt("mapTools." + name + ".width");
        int height = maps.getConfig().getInt("mapTools." + name + ".height");
        String imageName = maps.getConfig().getString("mapTools." + name + ".imageName");
        boolean keepCentered = maps.getConfig().getBoolean("mapTools." + name + ".keepCentered");
        boolean keepRatio = maps.getConfig().getBoolean("mapTools." + name + ".keepRatio");
        int background = maps.getConfig().getInt("mapTools." + name + ".background");
        ArrayList<Short> mapIDs = (ArrayList<Short>) maps.getConfig().getShortList("mapTools." + name + ".mapIDs");

        if (width <= 0 || height <= 0) {
            Main.log(Level.SEVERE + "mapTool '" + name + "' does not have the correct dimensions. This mapTool won't be updated");
            return;
        }

        new MapTool(p, imageName, name).loadMap(width, height, keepCentered, keepRatio, background, mapIDs);
    }

    @SuppressWarnings("all")
    private static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
//        if (original_width > bound_width) {
        //scale width to fit
        new_width = bound_width;
        //scale height to maintain aspect ratio
        new_height = (new_width * original_height) / original_width;
//        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    private static BufferedImage ResizeImage(BufferedImage source, int destinationW, int destinationH) {
        float ratioW = destinationW / source.getWidth();
        float ratioH = destinationH / source.getHeight();
        int finalH;
        int finalW;
        if (ratioW < ratioH) {
            finalW = destinationW;
            finalH = (int) (source.getHeight() * ratioW);
        } else {
            finalW = (int) (source.getWidth() * ratioH);
            finalH = destinationH;
        }
        int x = (destinationW - finalW) / 2;
        int y = (destinationH - finalH) / 2;

        BufferedImage newImage = new BufferedImage(destinationW, destinationH, 2);

        Graphics graphics = newImage.getGraphics();
        graphics.drawImage(source, x, y, finalW, finalH, null);
        graphics.dispose();
        return newImage;
    }

    @SuppressWarnings("all")
    public void loadMap(int width, int height, boolean keepCentered, boolean keepRatio, int background, ArrayList<Short> mapIDs) throws IllegalArgumentException {

        //tests if had legal width and height
        if (width <= 0 || height <= 0) {
            return;
        }

        //tests if width/height is not reassigned
        if (this.width != 0) {
            if (this.width != width || this.height != height) {
                throw new IllegalArgumentException("Width or height can not be reassigned");
            }
        }

        int xOffset = 0;
        int yOffset = 0;

        if (keepRatio) {
            Dimension imgSize = new Dimension(image.getWidth(), image.getHeight());
            Dimension boundary = new Dimension(width * 128, height * 128);

            Dimension d = getScaledDimension(imgSize, boundary);

            resize((int) d.getWidth(), (int) d.getHeight());
//            image = ResizeImage(image,(int) d.getWidth(), (int) d.getHeight());

            if (keepCentered) {
                xOffset = (width * 128 - image.getWidth()) / 2;
                yOffset = (height * 128 - image.getHeight()) / 2;
            }

        } else {
            resize(width * 128, height * 128);
        }

        int map = 0;

        boolean transparent = background == 1;
        int picXOffset = xOffset;
        int picYOffset = yOffset;

        int picYTotal = 0;
        int picHeight = 0;
        int emptyHeight = 0;
        boolean isAtBottom = false;

        ArrayList<Integer> emptyWidth = new ArrayList<>();
        ArrayList<Integer> halfWidth = new ArrayList<>();
        int emptyWidthNum = (width * 128 - image.getWidth()) / 128;
        int tmpW = 0;

        if (keepCentered) {
            for (int i = 0; i < emptyWidthNum / 2; i++) {
                emptyWidth.add(tmpW);
                tmpW++;
                picXOffset -= 128;
            }
            ArrayList<Integer> tmpList = new ArrayList<>();
            for (int i : emptyWidth) {
                tmpList.add(width - 1 - i);
            }
            emptyWidth.addAll(tmpList);
        }


        halfWidth.add(tmpW);
        int otherHalfWidth = width - tmpW;
        if (!keepCentered) {
            halfWidth.clear();
            otherHalfWidth = width - emptyWidthNum;
            double tmp = ((double) width * 128D - (double) image.getWidth()) / 128D;
            picXOffset = (int) ((tmp - emptyWidthNum) * 128);
        }

        while (map < mapIDs.size()) {

            for (int h = 0; h < height; h++) {

                if (picYOffset > 128 && transparent) { //trims the top
                    picYOffset -= 128;
                    picYTotal += 128;
                    if (picYOffset > 0) {

                        for (int w = 0; w < width; w++) {
                            MapView view = Bukkit.getMap(mapIDs.get(map));
                            for (MapRenderer m : view.getRenderers()) {
                                view.removeRenderer(m);
                            }
                            map++;
                        }

                        emptyHeight++;
                        continue;
                    }
                }
                if (transparent && picHeight + 128 > image.getHeight()) { //trims bottom
                    picYOffset = yOffset - emptyHeight * 128;
                    if (!keepCentered) {

                        picYOffset = (height * 128 - image.getHeight()) - emptyHeight * 128;
//                        emptyHeight = (height * 128 - image.getHeight()) / 128;

                        while (picYOffset > 128) {
                            emptyHeight++;
                            picYOffset = (height * 128 - image.getHeight()) - emptyHeight * 128;
                        }

                    }
                    if (isAtBottom) {
                        for (int w = 0; w < width; w++) {
                            MapView view = Bukkit.getMap(mapIDs.get(map));
                            for (MapRenderer m : view.getRenderers()) {
                                view.removeRenderer(m);
                            }
                            map++;
                        }
                        continue;
                    }
                    isAtBottom = true;
                }

                boolean heightLoop = true;

                for (int w = 0; w < width; w++) {

                    if (transparent && emptyWidth.contains(w)) { //trims empty lefts
                        MapView view = Bukkit.getMap(mapIDs.get(map));
                        for (MapRenderer m : view.getRenderers()) {
                            view.removeRenderer(m);
                        }
                        map++;
                        continue;
                    }
                    if (transparent && !keepCentered && width - emptyWidthNum <= w) {
                        MapView view = Bukkit.getMap(mapIDs.get(map));
                        for (MapRenderer m : view.getRenderers()) {
                            view.removeRenderer(m);
                        }
                        map++;
                        continue;
                    }

                    BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

                    if (!transparent) {
                        for (int x = 0; x < img.getWidth(); x++) {
                            for (int y = 0; y < img.getHeight(); y++) {
                                int tmpX = x + w * 128 - xOffset;
                                int tmpY = y + h * 128 - yOffset;

                                if (tmpX >= image.getWidth() || tmpY >= image.getHeight() || tmpX < 0 || tmpY < 0) {
                                    img.setRGB(x, y, background);
                                } else {
                                    img.setRGB(x, y, image.getRGB(tmpX, tmpY));
                                }
                            }
                        }
                    } else {

                        img = new BufferedImage(
                                128 - (halfWidth.contains(w) && keepCentered ? picXOffset : 0) - (otherHalfWidth - 1 == w ? picXOffset + (keepCentered ? 1 : 0) : 0),
                                128 - picYOffset,
                                1 /*BufferedImage.TYPE_INT_RGB*/);

                        if (heightLoop) {
                            picHeight += img.getHeight();
                            heightLoop = false;
                        }

                        for (int x = 0; x < img.getWidth(); x++) {
                            for (int y = 0; y < img.getHeight(); y++) {
                                int tmpX = x + w * 128 - (halfWidth.contains(w) ? -(picXOffset - xOffset) : xOffset);
                                int tmpY = y + h * 128 - (img.getHeight() == 128 || isAtBottom ? yOffset : yOffset - picYOffset);

                                if (tmpX >= image.getWidth() || tmpY >= image.getHeight() || tmpX < 0 || tmpY < 0) {
                                    img.setRGB(x, y, Color.RED.getRGB());
                                } else {
                                    img.setRGB(x, y, image.getRGB(tmpX, tmpY));
                                }
                            }
                        }
                    }

                    MapView view = Bukkit.getMap(mapIDs.get(map));
                    for (MapRenderer m : view.getRenderers()) {
                        view.removeRenderer(m);
                    }
                    if (transparent && picYOffset < 128) {
                        view.addRenderer(new ImageMapRenderer(img, (halfWidth.contains(w) ? picXOffset : 0), (isAtBottom ? 0 : picYOffset)));
                    } else if (!transparent) {
                        view.addRenderer(new ImageMapRenderer(img));
                    } else {
                        try {
                            view.addRenderer(new ImageMapRenderer(ImageIO.read(p.getResource("NoImageFound.png"))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    map++;
                }
                picYOffset = 0; //reset the picYOffset
            }
        }

        this.mapIDs = mapIDs;
        this.height = height;
        this.width = width;
        this.keepCentered = keepCentered;
        this.keepRatio = keepRatio;

    }

    public void loadMap() {

        try {
            File f = new File(p.getDataFolder() + "/maps", imageName);
            if (!f.exists()) {
                InputStream in = p.getResource("NoImageFound.png");

                if (in == null) {
                    return;
                }
                this.image = ImageIO.read(in);
            } else {
                this.image = ImageIO.read(f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadMap(width, height, keepCentered, keepRatio, background, mapIDs);
    }

    public void removeMapTool() {
        mapTools.remove(mapToolName);

        for (short mapID : mapIDs) {
            MapView view = Bukkit.getMap(mapID);
            for (MapRenderer renderer : view.getRenderers()) {
                view.removeRenderer(renderer);
            }
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ItemFrame) {
                    ItemFrame frame = (ItemFrame) entity;
                    if (frame.getItem().getType().equals(Material.MAP)) {
                        ItemStack map = frame.getItem();
                        if (mapIDs.contains(map.getDurability())) {
                            frame.remove();
                        }
                    }
                }
            }
        }
    }

    private void resize(int w, int h) {
        BufferedImage img =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int x, y;
        int ww = image.getWidth();
        int hh = image.getHeight();
        int[] ys = new int[h];
        for (y = 0; y < h; y++)
            ys[y] = y * hh / h;
        for (x = 0; x < w; x++) {
            int newX = x * ww / w;
            for (y = 0; y < h; y++) {
                int col = image.getRGB(newX, ys[y]);
                img.setRGB(x, y, col);
            }
        }
        image = img;
    }

    public void setMaps(int width, int height, boolean keepCentered, boolean keepRatio, Block mapLocation, Player player) throws IllegalArgumentException {
        //tests if had legal width and height
        if (width <= 0 || height <= 0) {
            return;
        }

        //tests if width/height is not reassigned
        if (this.width != 0) {
            if (this.width != width || this.height != height) {
                throw new IllegalArgumentException("Width or height can not be reassigned");
            }
        }

        ArrayList<ItemStack> maps = getMaps(width, height, keepCentered, keepRatio);

        BlockFace face = BlockFace.NORTH;
        float yaw = player.getLocation().getYaw();
        boolean invert = false;
        if (yaw < 0) {
            invert = true;
            yaw = Math.abs(yaw);
        }
        /*if (yaw < 45) {
            face = BlockFace.NORTH;
        } else*/ if (yaw < 135) {
            face = (invert ? BlockFace.EAST : BlockFace.WEST);
        } else if (yaw < 225) {
            face = BlockFace.SOUTH;
        } else if (yaw < 315) {
            face = (invert ? BlockFace.WEST : BlockFace.EAST);
        }

        boolean b = testAccessibility(mapLocation, face, width, height);
        if (!b) {
            player.sendMessage("not enough space to place all the maps");
            return;
        }

        int i = 0;
        if (face.equals(BlockFace.EAST) || face.equals(BlockFace.WEST)) {
            for (int h = height - 1; h >= 0; h--) {
                for (int w = 0; w < width; w++) {
                    ItemStack map = maps.get(i);
                    spawnItemFrame(mapLocation, face, map, player, w, h);
                    i++;
                }
            }
        } else {
            for (int h = height - 1; h >= 0; h--) {
                for (int w = width - 1; w >= 0; w--) {
                    ItemStack map = maps.get(i);
                    spawnItemFrame(mapLocation, face, map, player, w - width + 1, h);
                    i++;
                }
            }
        }

    }

    public void setMaps(Block mapLocation, Player player) {
        setMaps(width, height, keepCentered, keepRatio, mapLocation, player);
    }

    private boolean testAccessibility(Block mapLocation, BlockFace face, int width, int height) {

        if (face.equals(BlockFace.EAST) || face.equals(BlockFace.WEST)) {
            for (int h = height - 1; h >= 0; h--) {
                for (int w = 0; w < width; w++) {

                    int x = 0;
                    int z = (face.equals(BlockFace.EAST) ? w : 0) + (face.equals(BlockFace.WEST) ? -w : 0);

                    int xOffset = 0;
                    int zOffset = 0;

                    if (face.equals(BlockFace.SOUTH)) {
                        zOffset += 1;
                    } else if (face.equals(BlockFace.NORTH)) {
                        zOffset += -1;
                    } else if (face.equals(BlockFace.WEST)) {
                        xOffset += 1;
                    } else if (face.equals(BlockFace.EAST)) {
                        xOffset += -1;
                    }

                    if (mapLocation.getLocation().clone().add(x, h, z).getBlock().isEmpty()) {
                        return false;
                    }
                    if (!mapLocation.getLocation().clone().add(x + xOffset, h, z + zOffset).getBlock().isEmpty()) {
                        return false;
                    }
                }
            }
        } else {
            for (int h = height - 1; h >= 0; h--) {
                for (int w = 0; w < width; w++) {

                    int x = (face.equals(BlockFace.NORTH) ? -w : 0) + (face.equals(BlockFace.SOUTH) ? w : 0);
                    int z = 0;

                    int xOffset = 0;
                    int zOffset = 0;

                    if (face.equals(BlockFace.SOUTH)) {
                        zOffset += 1;
                    } else if (face.equals(BlockFace.NORTH)) {
                        zOffset += -1;
                    } else if (face.equals(BlockFace.WEST)) {
                        xOffset += 1;
                    } else if (face.equals(BlockFace.EAST)) {
                        xOffset += -1;
                    }

                    if (mapLocation.getLocation().clone().add(x, h, z).getBlock().isEmpty()) {
                        return false;
                    }
                    if (!mapLocation.getLocation().clone().add(x + xOffset, h, z + zOffset).getBlock().isEmpty()) {
                        return false;
                    }

                }
            }
        }
        return true;
    }

    private void spawnItemFrame(Block mapLocation, BlockFace face, ItemStack map, Player player, int width, int height) {

        int x = (face.equals(BlockFace.NORTH) ? width : 0) + (face.equals(BlockFace.SOUTH) ? -width : 0);
        int z = (face.equals(BlockFace.EAST) ? width : 0) + (face.equals(BlockFace.WEST) ? -width : 0);

        if (face.equals(BlockFace.SOUTH)) {
            z += 1;
        } else if (face.equals(BlockFace.NORTH)) {
            z += -1;
        } else if (face.equals(BlockFace.WEST)) {
            x += 1;
        } else if (face.equals(BlockFace.EAST)) {
            x += -1;
        }

        try {
            ItemFrame frame = mapLocation.getWorld().spawn(mapLocation.getLocation().clone().add(x, height, z), ItemFrame.class);
            frame.setItem(map);
            frame.setFacingDirection(face);

            HangingPlaceEvent hEvent = new HangingPlaceEvent(frame, player, mapLocation, face.getOppositeFace());
            p.getServer().getPluginManager().callEvent(hEvent);
        } catch (IllegalArgumentException ignore) {
            player.sendMessage("This map could not been set");
            player.getInventory().addItem(map);
        }
    }

    public ArrayList<ItemStack> getMaps(int width, int height, boolean keepCentered, boolean keepRatio) throws IllegalArgumentException {

        //tests if had legal width and height
        if (width <= 0 || height <= 0) {
            return new ArrayList<>();
        }

        //tests if width/height is not reassigned
        if (this.width != 0) {
            if (this.width != width || this.height != height) {
                throw new IllegalArgumentException("Width or height can not be reassigned");
            }
        }

        int xOffset = 0;
        int yOffset = 0;

        if (keepRatio) {
            Dimension imgSize = new Dimension(image.getWidth(), image.getHeight());
            Dimension boundary = new Dimension(width * 128, height * 128);

            Dimension d = getScaledDimension(imgSize, boundary);

            resize((int) d.getWidth(), (int) d.getHeight());

            if (keepCentered) {
                xOffset = (width * 128 - image.getWidth()) / 2;
                yOffset = (height * 128 - image.getHeight()) / 2;

            }

        } else {
            resize(width * 128, height * 128);
        }

        this.height = height;
        this.width = width;
        this.keepRatio = keepRatio;
        this.keepCentered = keepCentered;

        ArrayList<ItemStack> mapItems = new ArrayList<>();

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {

                BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

                for (int x = 0; x < img.getWidth(); x++) {
                    for (int y = 0; y < img.getHeight(); y++) {

                        int tmpX = x + w * 128 - xOffset;
                        int tmpY = y + h * 128 - yOffset;

                        if (tmpX >= image.getWidth() || tmpY >= image.getHeight() || tmpX < 0 || tmpY < 0) {
                            img.setRGB(x, y, new Color(0, 0, 0).getRGB());
                        } else {
                            img.setRGB(x, y, image.getRGB(tmpX, tmpY));
                        }
                    }
                }

                ItemStack is = new ItemStack(Material.MAP);
                MapView mapView = Bukkit.createMap(Bukkit.getWorld("world"));
                for (MapRenderer m : mapView.getRenderers()) {
                    mapView.removeRenderer(m);
                }

                ImageMapRenderer map = new ImageMapRenderer(img);
                mapView.addRenderer(map);

                short mapID = mapView.getId();
                is.setDurability(mapID);
                mapIDs.add(mapID);

                ItemMeta im = is.getItemMeta();
                im.setLore(Arrays.asList("MapTool Name: " + this.mapToolName, "Width: " + (w+1), "Height: " + (h + 1)));
                is.setItemMeta(im);

                mapItems.add(is);

//                try {
//                    ImageIO.write(img, "jpg", new File("D:\\Users\\jphbo\\Minecraft servers\\test server\\plugins\\Stranded\\images\\" + h + "X" + w + ".jpg"));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        }

        return mapItems;
    }

    public ArrayList<ItemStack> getMaps(int width, int height) {
        return getMaps(width, height, true, true);
    }

    public ArrayList<ItemStack> getMaps() {
        return getMaps(width, height, keepCentered, keepRatio);
    }

    public void setKeepCentered(boolean keepCentered) {
        this.keepCentered = keepCentered;
        this.loadMap();
    }

    public void setKeepRatio(boolean keepRatio) {
        this.keepRatio = keepRatio;
        this.loadMap();
    }

    public void setBackground(String color) {

        if (color.equalsIgnoreCase("none")) {
            background = 1;
            this.loadMap();
            return;
        }

        try {
            this.background = ((Color) Color.class.getField(color).get(null)).getRGB();
            this.loadMap();
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Color " + color + " doesn't exist");
        }
    }

    public void setBackground(String r, String g, String b) {

        int red = 0;
        int green = 0;
        int blue = 0;

        String error = "";

        try {
            red = Integer.parseInt(r);
            if (red < 0) {
                error += "The red value is too low. range: > 0 and < 256";
            } else if (red > 255) {
                error += "The red value is too high. range: > 0 and < 256";
            }
        } catch (NumberFormatException nfo) {
            error += "The red value isn't a number. range: > 0 and < 256";
        }
        try {
            green = Integer.parseInt(g);
            if (green < 0) {
                error += (error.equals("") ? "" : "\n") + "The green value is too low. range: > 0 and < 256";
            } else if (green > 255) {
                error += (error.equals("") ? "" : "\n") + "The green value is too high. range: > 0 and < 256";
            }
        } catch (NumberFormatException nfo) {
            error += (error.equals("") ? "" : "\n") + "The green value isn't a number. range: > 0 and < 256";
        }
        try {
            blue = Integer.parseInt(b);
            if (blue < 0) {
                error += (error.equals("") ? "" : "\n") + "The blue value is too low. range: > 0 and < 256";
            } else if (blue > 255) {
                error += (error.equals("") ? "" : "\n") + "The blue value is too high. range: > 0 and < 256";
            }
        } catch (NumberFormatException nfo) {
            error += (error.equals("") ? "" : "\n") + "The blue value isn't a number. range: > 0 and < 256";
        }
        if (!error.equals("")) {
            throw new IllegalArgumentException(error);
        }

        background = new Color(red, green, blue).getRGB();
        this.loadMap();
    }

    public static HashMap<String, MapTool> getMapTools() {
        return mapTools;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getMapToolName() {
        return mapToolName;
    }

    public String getImageName() {
        return imageName;
    }

    public int getBackground() {
        return background;
    }

    public boolean isKeepCentered() {
        return keepCentered;
    }

    public boolean isKeepRatio() {
        return keepRatio;
    }
}
