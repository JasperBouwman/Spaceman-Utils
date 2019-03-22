package com.spaceman.mapsUtil;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class ImageMapRenderer extends MapRenderer {

    private BufferedImage image;
    private boolean updated = false;
    private int xOffset = 0;
    private int yOffset = 0;

    ImageMapRenderer(BufferedImage image) {
        this.image = image;
    }

    ImageMapRenderer(BufferedImage image, int xOffset, int yOffset) {
        this.image = image;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {

        MapRenderer renderer = mapCanvas.getMapView().getRenderers().get(0);
        if (renderer instanceof ImageMapRenderer) {
            ImageMapRenderer mapRenderer = (ImageMapRenderer) renderer;
            if (!mapRenderer.updated) {
                mapCanvas.drawImage(xOffset, yOffset, image);
                mapRenderer.updated = true;
            }
        }
    }

    public void update() {
        updated = false;
    }

    public void update(BufferedImage image) {
        this.image = image;
        updated = false;
    }
}
