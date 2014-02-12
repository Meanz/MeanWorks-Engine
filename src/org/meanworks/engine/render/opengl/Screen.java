package org.meanworks.engine.render.opengl;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public class Screen {

	/*
	 * The currently active window instance
	 */
	private static Screen windowInstance;

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
	public static int getWidth() {
		return windowInstance.windowWidth;
	}

	/**
	 * Get the height of the window
	 * 
	 * @return
	 */
	public static int getHeight() {
		return windowInstance.windowHeight;
	}

	/**
	 * Get the aspect ration of the window
	 * 
	 * @return
	 */
	public static float getAspect() {
		return ((float)windowInstance.windowWidth / windowInstance.windowHeight);
	}

	/**
	 * Create a window with the given dimensions
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static Screen createWindow(int width, int height) {

		int monWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int monHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

		return createWindow(width, height, monWidth / 2 - width / 2, monHeight
				/ 2 - height / 2);
	}

	/**
	 * Create a window with the given dimensions at the specified position
	 * 
	 * @param width
	 * @param height
	 */
	public static Screen createWindow(int width, int height, int x, int y) {
		windowInstance = new Screen();
		windowInstance.pixelFormat = new PixelFormat(0, 8, 0);
		windowInstance.displayMode = new DisplayMode(width, height);
		windowInstance.windowWidth = width;
		windowInstance.windowHeight = height;
		try {
			Display.setDisplayMode(windowInstance.displayMode);
			Display.setLocation(x, y);
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
	public static boolean resize(int newWidth, int newHeight) {
		windowInstance.displayMode = new DisplayMode(newWidth, newHeight);
		try {
			Display.setDisplayMode(windowInstance.displayMode);
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
