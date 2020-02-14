package com.spaceman.mapsUtil;

import com.spaceman.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class NewMapTool {
    
    private static HashMap<String, NewMapTool> mapTools = new HashMap<>();
    
    public static final int transparentValue = -1;
    public static final int photoBackgroundValue = -2;
    
    private final String photosDir;
    
    
    private BufferedImage image;
    private String mapToolName;
    private String imageName;
    private ArrayList<Integer> mapIDs = new ArrayList<>();
    private int width = 0;
    private int height = 0;
    private boolean keepCentered = true;
    private boolean keepRatio = false;
    
    private int background = new Color(0, 0, 0).getRGB();
    private String photoBackgroundName = null;
    private BufferedImage photoBackground = null;
    private boolean photoBackgroundAsOne = false;
    
    
    
    public NewMapTool(String image, String name) throws IllegalArgumentException {
        this.photosDir = Main.getInstance().getDataFolder() + "/maps";
        this.imageName = image;
        this.mapToolName = name;
        
        if (mapTools.containsKey(name)) {
            throw new IllegalArgumentException("MapTool name '" + name + "' already exist");
        }
        
        try {
            File f = new File(photosDir, image);
            if (!f.exists()) {
                InputStream in = Main.getInstance().getResource("NoImageFound.png");
                
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
    
    public void setBackground(String color) {
        
        if (color.equalsIgnoreCase("none")) {
            background = transparentValue;
            this.loadMap();
            return;
        }
        if (color.equalsIgnoreCase("photo")) {
            background = photoBackgroundValue;
            if (photoBackgroundName != null) {
                this.loadMap();
            }
            return;
        }
        if (color.equalsIgnoreCase("asOne")) {
            photoBackgroundAsOne = !photoBackgroundAsOne;
            if (photoBackgroundName != null) {
                this.loadMap();
            }
            return;
        }
        
        try {
            this.background = ((Color) Color.class.getField(color).get(null)).getRGB();
            this.loadMap();
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Color " + color + " doesn't exist");
        }
    }
    
    public void setPhotoBackground(String name) {
        photoBackgroundName = name;
        photoBackground = null;
        background = photoBackgroundValue;
        this.loadMap();
    }
    
    public boolean isKeepCentered() {
        return keepCentered;
    }
    
    public void setKeepCentered(boolean keepCentered) {
        this.keepCentered = keepCentered;
        this.loadMap();
    }
    
    public boolean isKeepRatio() {
        return keepRatio;
    }
    
    public void setKeepRatio(boolean keepRatio) {
        this.keepRatio = keepRatio;
        this.loadMap();
    }
    
    private void loadMap() {}
    
}
