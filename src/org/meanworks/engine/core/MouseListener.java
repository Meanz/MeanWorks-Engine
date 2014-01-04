package org.meanworks.engine.core;
/**
 * Copyright (C) 2013 Steffen Evensen
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Meanz
 */
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
