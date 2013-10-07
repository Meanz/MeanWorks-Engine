package org.meanworks.render.opengl;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public class Window {

	/*
	 * The currently active window instance
	 */
	private static Window windowInstance;

	/*
	 * Window size
	 */
	private int windowWidth = 800;
	private int windowHeight = 640;

	/*
	 * 
	 */
	private PixelFormat pixelFormat;
	private DisplayMode displayMode;

	/**
	 * Get the width of the window
	 * 
	 * @return
	 */
	public int getWidth() {
		return windowWidth;
	}

	/**
	 * Get the height of the window
	 * 
	 * @return
	 */
	public int getHeight() {
		return windowHeight;
	}

	/**
	 * Get the aspect ration of the window
	 * 
	 * @return
	 */
	public float getAspect() {
		return windowWidth / windowHeight;
	}

	/**
	 * Create a window
	 * 
	 * @param width
	 * @param height
	 */
	public static Window createWindow(int width, int height) {
		windowInstance = new Window();
		windowInstance.pixelFormat = new PixelFormat(0, 8, 0);
		windowInstance.displayMode = new DisplayMode(width, height);
		windowInstance.windowWidth = width;
		windowInstance.windowHeight = height;
		try {
			Display.setDisplayMode(windowInstance.displayMode);
			Display.create(windowInstance.pixelFormat);
			return windowInstance;
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Resize the window
	 * 
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public boolean resize(int newWidth, int newHeight) {
		displayMode = new DisplayMode(newWidth, newHeight);
		try {
			Display.setDisplayMode(displayMode);
			return true;
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Check whether a window close is requested
	 * 
	 * @return
	 */
	public boolean isCloseRequested() {
		return Display.isCloseRequested();
	}

	/**
	 * Update the window
	 */
	public void update() {
		Display.update(false);
		Display.sync(250);
	}
}
