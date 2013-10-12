package org.meanworks.render.texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.meanworks.engine.EngineLogger;

public class Texture {

	/**
	 * 
	 */
	public static void enable() {
		glEnable(GL_TEXTURE_2D);
	}

	/**
	 * 
	 */
	public static void disable() {
		glDisable(GL_TEXTURE_2D);
	}

	/*
	 * The id of the texture
	 */
	private int textureId;

	/*
	 * Texture data values
	 */
	private int width;
	private int height;

	/*
	 * Is this texture mipmapped?
	 */
	private boolean mipMapping = false; // default false

	/**
	 * Empty constructor, nothing fancy
	 */
	public Texture() {
		create();
	}

	/**
	 * Check whether this texture uses mip maps or not
	 * 
	 * @return
	 */
	public boolean isMipMapping() {
		return mipMapping;
	}

	/**
	 * Set whether this texture uses mip maps or not
	 * 
	 * @param mipMapping
	 */
	public void setMipMapping(boolean mipMapping) {
		this.mipMapping = mipMapping;
	}

	/**
	 * Check whether this is a valid texture or not
	 * 
	 * @return
	 */
	public boolean isValid() {
		return textureId > 0;
	}

	/**
	 * Helper function for mipmapping
	 */
	public void mipmap() {
		enable();
		bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	}

	/**
	 * Apply nearest filter for both min and mag
	 */
	public void nearestFiltering() {
		bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	}

	/**
	 * Apply linear mag filter and nearest min filter
	 */
	public void linearNearestFiltering() {
		bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	}

	/**
	 * Apply linear texture filtering to this texture
	 */
	public void linearFiltering() {
		bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return textureId;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Creates a texture
	 * 
	 * @return
	 */
	public boolean create() {
		textureId = GL11.glGenTextures();

		int errorCode = GL11.glGetError();
		if (errorCode != 0) {
			EngineLogger.error("Could not create texture.");
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 */
	public void bind2DArray() {
		bind2DArray(0);
	}
	
	/**
	 * 
	 */
	public void unbind2DArray() {
		GL11.glBindTexture(GL12.GL_TEXTURE_3D, 0);
	}
	
	/**
	 * 
	 * @param textureUnit
	 */
	public void bind2DArray(int textureUnit) {
		textureUnit(textureUnit);
		bindTexture(GL12.GL_TEXTURE_3D);
	}

	/**
	 * Binds this texture to the active texture unit
	 */
	public void bind() {
		bind(0);
	}

	/**
	 * If the texture unit is something crazy opengl will die But for the moment
	 * we don't really care
	 * 
	 * @param textureUnit
	 */
	public void bind(int textureUnit) {
		textureUnit(textureUnit);
		bindTexture(GL11.GL_TEXTURE_2D);
	}

	/**
	 * Bind this texture
	 */
	private void bindTexture(int cap) {
		GL11.glBindTexture(cap, textureId);
	}
	
	/**
	 * Activate the given texture unit
	 * @param textureUnit
	 */
	public static void textureUnit(int textureUnit) {
		if (textureUnit > 31) {
			EngineLogger.warning("Tried to make texture unit " + textureUnit
					+ " active.");
			return;
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureUnit);
	}

}
