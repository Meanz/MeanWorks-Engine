package org.meanworks.render.texture;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_BITMAP;
import static org.lwjgl.opengl.GL11.GL_BLUE;
import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_COLOR_INDEX;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_GREEN;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_LUMINANCE;
import static org.lwjgl.opengl.GL11.GL_LUMINANCE_ALPHA;
import static org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_PACK_ROW_LENGTH;
import static org.lwjgl.opengl.GL11.GL_PACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL11.GL_PACK_SKIP_ROWS;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SHORT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_INDEX;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ROW_LENGTH;
import static org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_ROWS;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.util.glu.GLU.GLU_INVALID_ENUM;
import static org.lwjgl.util.glu.GLU.GLU_INVALID_VALUE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;
import org.meanworks.engine.EngineLogger;

public class TextureLoader {

	/**
	 * A simple struct class for TextureData
	 * 
	 * @author meanz
	 * 
	 */
	private static class TextureData {

		int width;
		int height;
		int srcPixelFormat;
		ByteBuffer data;

	}

	/**
	 * The colour model including alpha for the GL image
	 */
	private static ColorModel glAlphaColorModel;
	/**
	 * The colour model for the GL image
	 */
	private static ColorModel glColorModel;

	/**
	 * Create a new texture loader
	 */
	static {
		glAlphaColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {
						8, 8, 8, 8
				}, true, false, ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		glColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {
						8, 8, 8, 0
				}, false, false, ComponentColorModel.OPAQUE,
				DataBuffer.TYPE_BYTE);
	}

	/**
	 * Loads the texture data into the given texture data struct
	 * 
	 * @param textureData
	 * @param path
	 */
	private static boolean loadTextureData(TextureData textureData,
			String resourcePath) {
		BufferedImage bufferedImage;
		try {
			bufferedImage = loadImage(resourcePath);
		} catch (IOException e) {
			EngineLogger.info("Could not load texture " + resourcePath);
			return false;
		}
		if (bufferedImage == null) {
			EngineLogger.info("Could not load texture " + resourcePath);
			return false;
		}
		textureData.width = (bufferedImage.getWidth());
		textureData.height = (bufferedImage.getHeight());

		if (bufferedImage.getColorModel().hasAlpha()) {
			textureData.srcPixelFormat = GL_RGBA;
		} else {
			textureData.srcPixelFormat = GL_RGB;
		}
		// convert that image into a byte buffer of texture data
		ByteBuffer textureBuffer = convertImageData(bufferedImage);
		textureData.data = textureBuffer;
		return true;
	}

	/**
	 * Load a simple texture
	 * 
	 * @param resourcePath
	 * @return
	 */
	public static Texture loadTexture(String resourcePath) {
		return loadTexture(resourcePath, false);
	}

	/**
	 * Load a texture
	 * 
	 * @param resourcePath
	 *            The path to the texture
	 * @param mipmapping
	 *            Whether to mipmap the image or not
	 * @return
	 */
	public static Texture loadTexture(String resourcePath, boolean mipmapping) {

		TextureData textureData = new TextureData();

		if (loadTextureData(textureData, resourcePath) == false) {
			return null;
		}

		// Create a texture object
		Texture texture = new Texture();
		texture.setMipMapping(mipmapping);
		texture.bind();

		loadTexture(texture, textureData, resourcePath);

		return texture;
	}

	/**
	 * Used to update a texture instead of creating a new one
	 * 
	 * @param texture
	 * @param resourcePath
	 */
	public static boolean loadTexture(Texture texture, String resourcePath) {
		TextureData textureData = new TextureData();

		if (loadTextureData(textureData, resourcePath) == false) {
			return false;
		}

		loadTexture(texture, textureData, resourcePath);
		return true;
	}

	/**
	 * The core loading function
	 * 
	 * @param texture
	 * @param resourcePath
	 */
	public static void loadTexture(Texture texture, TextureData textureData,
			String resourcePath) {
		// bind this texture
		texture.bind();

		// produce a texture from the byte buffer

		if (texture.isMipMapping()) {
			texture.mipmap();

			// GLU.gluBuild2DMipmaps(target, components, width, height, format,
			// type, data);
			GLU.gluBuild2DMipmaps(GL_TEXTURE_2D,
					textureData.srcPixelFormat == GL_RGB ? 3 : 4,
					textureData.width, textureData.height, GL_RGBA,
					GL_UNSIGNED_BYTE, textureData.data);
		} else {
			texture.linearFiltering();

			// GL11.glTexImage2D(target, level, internalformat, width, height,
			// border, format, type, pixels);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureData.width,
					textureData.height, 0, textureData.srcPixelFormat,
					GL_UNSIGNED_BYTE, textureData.data);
		}
	}

	/**
	 * Loads a texture array from the given textures
	 * 
	 * @return
	 */
	public static TextureArray loadTextureArray(String[] paths) {
		TextureData[] textureData = new TextureData[paths.length];

		for (int i = 0; i < textureData.length; i++) {
			textureData[i] = new TextureData();
			if (loadTextureData(textureData[i], paths[i]) == false) {
				return null;
			}
		}

		TextureArray textureArray = new TextureArray();

		loadTextureArray(textureArray, textureData);

		return textureArray;
	}

	/**
	 * Setup's a texture array
	 * 
	 * @param texture
	 * @param textureData
	 */
	public static void loadTextureArray(TextureArray texture,
			TextureData[] textureData) {

		texture.bind2DArray();

		// GL12.glTexImage3D(target, level, internalFormat, width, height,
		// depth, border, format, type, pixels);

		GL12.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL11.GL_RGBA,
				textureData[0].width, textureData[0].height,
				textureData.length, 0, GL_RGBA, GL_UNSIGNED_BYTE,
				(ByteBuffer) null);

		int errorCode = GL11.glGetError();
		if (errorCode != 0) {
			System.err.println("Bind tex array " + errorCode + " "
					+ GLU.gluErrorString(errorCode));
		}

		for (int i = 0; i < textureData.length; i++) {
			// Upload pixel data.
			// GL12.glTexSubImage3D(target, level, xoffset, yoffset, zoffset,
			// width, height, depth, format, type, pixels);
			/*GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, i,
					textureData[i].width, textureData[i].height, 1,
					textureData[i].srcPixelFormat, GL_UNSIGNED_BYTE,
					textureData[i].data);*/
			
			gluBuild3DMipmaps(GL30.GL_TEXTURE_2D_ARRAY, textureData[i].srcPixelFormat, textureData[i].width, textureData[i].height, textureData[i].srcPixelFormat, 
					GL_UNSIGNED_BYTE, i, textureData[i].data);

			errorCode = GL11.glGetError();
			if (errorCode != 0) {
				System.err.println("TEX[" + i + "] " + errorCode + " "
						+ GLU.gluErrorString(errorCode));
			}

		}
		// Always set reasonable texture parameters
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY,
				GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY,
				GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_S,
				GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_T,
				GL12.GL_CLAMP_TO_EDGE);

		texture.unbind2DArray();
	}

	/**
	 * Method gluBuild2DMipmaps
	 * 
	 * @param target
	 * @param components
	 * @param width
	 * @param height
	 * @param format
	 * @param type
	 * @param data
	 * @return int
	 */
	public static int gluBuild3DMipmaps(final int target, final int components,
			final int width, final int height, final int format,
			final int type, final int zOffset, final ByteBuffer data) {
		if (width < 1 || height < 1)
			return GLU_INVALID_VALUE;

		final int bpp = bytesPerPixel(format, type);
		if (bpp == 0)
			return GLU_INVALID_ENUM;

		final int maxSize = glGetIntegerv(GL_MAX_TEXTURE_SIZE);

		int w = get2Fold(width);
		if (w > maxSize)
			w = maxSize;

		int h = get2Fold(height);
		if (h > maxSize)
			h = maxSize;

		// Get current glPixelStore state
		PixelStoreState pss = new PixelStoreState();

		// set pixel packing
		glPixelStorei(GL_PACK_ROW_LENGTH, 0);
		glPixelStorei(GL_PACK_ALIGNMENT, 1);
		glPixelStorei(GL_PACK_SKIP_ROWS, 0);
		glPixelStorei(GL_PACK_SKIP_PIXELS, 0);

		ByteBuffer image;
		int retVal = 0;
		boolean done = false;

		if (w != width || h != height) {
			// must rescale image to get "top" mipmap texture image
			image = BufferUtils.createByteBuffer((w + 4) * h * bpp);
			int error = GLU.gluScaleImage(format, width, height, type, data, w,
					h, type, image);
			if (error != 0) {
				retVal = error;
				done = true;
			}

			/* set pixel unpacking */
			glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
			glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
		} else {
			image = data;
		}

		ByteBuffer bufferA = null;
		ByteBuffer bufferB = null;

		int level = 0;
		while (!done) {
			if (image != data) {
				/* set pixel unpacking */
				glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
				glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
				glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
				glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
			}

			// GL11.glTexImage2D(target, level, internalformat, width, height,
			// border, format, type, pixels);

			// glTexImage2D(target, level, components, w, h, 0, format, type,
			// image);

			// GL12.glTexSubImage3D(target, level, xoffset, yoffset, zoffset,
			// width, height, depth, format, type, pixels);

			GL12.glTexSubImage3D(target, level, 0, 0, zOffset, w, h, 1, format,
					type, image);

			if (w == 1 && h == 1)
				break;

			final int newW = (w < 2) ? 1 : w >> 1;
			final int newH = (h < 2) ? 1 : h >> 1;

			final ByteBuffer newImage;

			if (bufferA == null)
				newImage = (bufferA = BufferUtils.createByteBuffer((newW + 4)
						* newH * bpp));
			else if (bufferB == null)
				newImage = (bufferB = BufferUtils.createByteBuffer((newW + 4)
						* newH * bpp));
			else
				newImage = bufferB;

			int error = GLU.gluScaleImage(format, w, h, type, image, newW,
					newH, type, newImage);
			if (error != 0) {
				retVal = error;
				done = true;
			}

			image = newImage;
			if (bufferB != null)
				bufferB = bufferA;

			w = newW;
			h = newH;
			level++;
			
			int errorCode = GL11.glGetError();
			if (errorCode != 0) {
				System.err.println("TEX[" + zOffset + "][" + (level - 1) + "] " + errorCode + " "
						+ GLU.gluErrorString(errorCode));
			}			
		}

		// Restore original glPixelStore state
		pss.save();

		return retVal;
	}

	/**
	 * Get the closest greater power of 2 to the fold number
	 * 
	 * @param fold
	 *            The target number
	 * @return The power of 2
	 */
	@SuppressWarnings("unused")
	private static int get2Fold(int fold) {
		int ret = 2;
		while (ret < fold) {
			ret *= 2;
		}
		return ret;
	}

	/**
	 * Convert the buffered image to a texture
	 * 
	 * @param bufferedImage
	 *            The image to convert to a texture
	 * @param texture
	 *            The texture to store the data into
	 * @return A buffer containing the data
	 */
	private static ByteBuffer convertImageData(BufferedImage bufferedImage) {
		ByteBuffer imageBuffer;
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth = bufferedImage.getWidth();
		int texHeight = bufferedImage.getHeight();
		// find the closest power of 2 for the width and height
		// of the produced texture
		/*
		 * while (texWidth < bufferedImage.getWidth()) { texWidth *= 2; } while
		 * (texHeight < bufferedImage.getHeight()) { texHeight *= 2; }
		 */
		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 4, null);
			texImage = new BufferedImage(glAlphaColorModel, raster, false,
					new Hashtable());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 3, null);
			texImage = new BufferedImage(glColorModel, raster, false,
					new Hashtable());
		}

		// copy the source image into the produced image
		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);

		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
				.getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();

		return imageBuffer;
	}

	/**
	 * Load a given resource as a buffered image
	 * 
	 * @param ref
	 *            The location of the resource to load
	 * @return The loaded buffered image
	 * @throws IOException
	 *             Indicates a failure to find a resource
	 */
	private static BufferedImage loadImage(String ref) throws IOException {

		if (true) {
			try {
				BufferedImage bufimg = ImageIO.read(new File(ref));
				// System.err.println("Loaded image: " + ref);
				return bufimg;
			} catch (Exception ex) {
				// ex.printStackTrace();
				System.err.println("Error loading image: " + ref);
				return null;
			}
		}

		URL url = TextureLoader.class.getClassLoader().getResource(ref);

		if (url == null) {
			throw new IOException("Cannot find: " + ref);
		}

		// due to an issue with ImageIO and mixed signed code
		// we are now using good oldfashioned ImageIcon to load
		// images and the paint it on top of a new BufferedImage
		Image img = new ImageIcon(url).getImage();
		BufferedImage bufferedImage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = bufferedImage.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return bufferedImage;
	}

	static class PixelStoreState {
		public int unpackRowLength;
		public int unpackAlignment;
		public int unpackSkipRows;
		public int unpackSkipPixels;
		public int packRowLength;
		public int packAlignment;
		public int packSkipRows;
		public int packSkipPixels;

		/**
		 * Constructor for PixelStoreState.
		 */
		public PixelStoreState() {
			load();
		}

		public void load() {
			unpackRowLength = glGetIntegerv(GL_UNPACK_ROW_LENGTH);
			unpackAlignment = glGetIntegerv(GL_UNPACK_ALIGNMENT);
			unpackSkipRows = glGetIntegerv(GL_UNPACK_SKIP_ROWS);
			unpackSkipPixels = glGetIntegerv(GL_UNPACK_SKIP_PIXELS);
			packRowLength = glGetIntegerv(GL_PACK_ROW_LENGTH);
			packAlignment = glGetIntegerv(GL_PACK_ALIGNMENT);
			packSkipRows = glGetIntegerv(GL_PACK_SKIP_ROWS);
			packSkipPixels = glGetIntegerv(GL_PACK_SKIP_PIXELS);
		}

		public void save() {
			glPixelStorei(GL_UNPACK_ROW_LENGTH, unpackRowLength);
			glPixelStorei(GL_UNPACK_ALIGNMENT, unpackAlignment);
			glPixelStorei(GL_UNPACK_SKIP_ROWS, unpackSkipRows);
			glPixelStorei(GL_UNPACK_SKIP_PIXELS, unpackSkipPixels);
			glPixelStorei(GL_PACK_ROW_LENGTH, packRowLength);
			glPixelStorei(GL_PACK_ALIGNMENT, packAlignment);
			glPixelStorei(GL_PACK_SKIP_ROWS, packSkipRows);
			glPixelStorei(GL_PACK_SKIP_PIXELS, packSkipPixels);
		}
	}

	/**
	 * temp IntBuffer of one for getting an int from some GL functions
	 */
	private static IntBuffer scratch = BufferUtils.createIntBuffer(16);

	/**
	 * Convenience method for returning an int, rather than getting it out of a
	 * buffer yourself.
	 * 
	 * @param what
	 * 
	 * @return int
	 */
	protected static int glGetIntegerv(int what) {
		scratch.rewind();
		glGetInteger(what, scratch);
		return scratch.get();
	}

	/**
	 * Method bytesPerPixel.
	 * 
	 * @param format
	 * @param type
	 * 
	 * @return int
	 */
	protected static int bytesPerPixel(int format, int type) {
		int n, m;

		switch (format) {
		case GL_COLOR_INDEX:
		case GL_STENCIL_INDEX:
		case GL_DEPTH_COMPONENT:
		case GL_RED:
		case GL_GREEN:
		case GL_BLUE:
		case GL_ALPHA:
		case GL_LUMINANCE:
			n = 1;
			break;
		case GL_LUMINANCE_ALPHA:
			n = 2;
			break;
		case GL_RGB:
		case GL_BGR:
			n = 3;
			break;
		case GL_RGBA:
		case GL_BGRA:
			n = 4;
			break;
		default:
			n = 0;
		}

		switch (type) {
		case GL_UNSIGNED_BYTE:
			m = 1;
			break;
		case GL_BYTE:
			m = 1;
			break;
		case GL_BITMAP:
			m = 1;
			break;
		case GL_UNSIGNED_SHORT:
			m = 2;
			break;
		case GL_SHORT:
			m = 2;
			break;
		case GL_UNSIGNED_INT:
			m = 4;
			break;
		case GL_INT:
			m = 4;
			break;
		case GL_FLOAT:
			m = 4;
			break;
		default:
			m = 0;
		}

		return n * m;
	}
}
