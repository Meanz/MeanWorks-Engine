package org.fractalstudio.render.opengl.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.fractalstudio.render.texture.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class TextureUtil {
	
	  public static void saveTexture(String fileName, Texture texture) {
	        int width = texture.getWidth();
	        int height = texture.getHeight();
	        int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
	        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
	        //glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        texture.bind();
	        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	        //Take a snapshot of the color buffer thingy from the shadowmap
	        File file = new File(fileName); // The file to save to.
	        String format = "PNG"; // Example: "PNG" or "JPG"
	        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	        for (int x = 0; x < width; x++) {
	            for (int y = 0; y < height; y++) {
	                int i = (x + (width * y)) * bpp;
	                int r = buffer.get(i) & 0xFF;
	                int g = buffer.get(i + 1) & 0xFF;
	                int b = buffer.get(i + 2) & 0xFF;
	                image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
	            }
	        }

	        try {
	            ImageIO.write(image, format, file);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public static Texture createTextureDepth(int width, int height) {
	        return createTexture(width, height, GL11.GL_DEPTH_COMPONENT, GL11.GL_DEPTH_COMPONENT);
	    }

	    /**
	     * Creates a new texture
	     *
	     * @param width
	     * @param height
	     * @return
	     */
	    public static Texture createTextureRGBA(int width, int height) {
	        return createTexture(width, height, GL11.GL_RGBA, GL11.GL_RGBA);
	    }

	    public static Texture createTexture(int width, int height, int internalformat, int format) {
	        //Create a new texture
	    	Texture glTexture = new Texture();
	    	glTexture.create();
	    	
	        //Bind the new texture
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTexture.getId());
	        //Create a float buffer where the pixels can be stored
	        FloatBuffer fb = BufferUtils.createFloatBuffer(width * height);
	        //Create a new image
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalformat, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, fb);
	        //Add some params to them
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
	        //Create a new texture and return it
	        
	        glTexture.setWidth(width);
	        glTexture.setHeight(height);
	        return glTexture;
	    }
}
