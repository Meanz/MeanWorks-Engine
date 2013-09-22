package org.fractalstudio.engine.gui;

public interface MouseListener {
	/**
	 * Called whenever a mouse button is pressed
	 * 
	 * @param ie
	 *            The InputEvent that contains all input information
	 */
	public void mousePressed(int key, int x, int y);

	/**
	 * Called whenever a mouse button is released
	 * 
	 * @param ie
	 *            The InputEvent that contains all input information
	 */
	public void mouseReleased(int key, int x, int y);

	/**
	 * Called whenever the mouse is moved
	 * 
	 * @param ie
	 *            The InputEvent that contains all input information
	 */
	public void mouseMoved(int dx, int dy);

	/**
     * 
     */
	// public void mouseHeld();
}
