package com.spaceman.mapsUtil.gif;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class GifMapRenderer extends MapRenderer {

//    public ArrayList<BufferedImage> getFrames(File gif) throws IOException {
//        ArrayList<BufferedImage> frames = new ArrayList<>();
//        ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
//        ir.setInput(ImageIO.createImageInputStream(gif));
//        for(int i = 0; i < ir.getNumImages(true); i++)
//            frames.add(ir.read(i));
//        return frames;
//    }

    private int frame = 0;
    private ImageFrame[] frames;

    private BufferedImage resize(int w, int h, BufferedImage image) {
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
        return img;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {

        MapRenderer renderer = mapCanvas.getMapView().getRenderers().get(0);
        if (renderer instanceof GifMapRenderer) {

            if (frame != frames.length) {
                mapCanvas.drawImage(0, 0, resize(128, 128, frames[frame].getImage()));
            } else {
                frame = -1;
            }

            frame++;

        }
    }

    public GifMapRenderer(String gif) {
        try {
            frames = FrameHandler.readGif(new FileInputStream("D:\\Users\\jphbo\\Minecraft servers\\test server\\plugins\\Stranded\\" + gif));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
