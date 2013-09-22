package org.fractalstudio.render.opengl.util;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

public class HeightMap {

    private float[][] heightMapValues;
    private int width, height;

    public HeightMap(BufferedImage picture) {

        width = picture.getWidth();
        height = picture.getHeight();

        heightMapValues = new float[width][height];

        int[] pixels = new int[width * height];
        PixelGrabber pg =
                new PixelGrabber(picture, 0, 0, width, height, pixels, 0, width);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int x = 0; x < picture.getWidth(); x++) {
            for (int y = 0; y < picture.getHeight(); y++) {
                int c = pixels[x + (y * width)];  // or  pixels[x * width + y]
                int red = (c & 0x00ff0000) >> 16;
                int green = (c & 0x0000ff00) >> 8;
                int blue = c & 0x000000ff;
                heightMapValues[x][y] = red;
            }
        }
    }

    public float getPoint(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return 0.0f;
        }
        return heightMapValues[x][y] * 0.2f;

    }
}
