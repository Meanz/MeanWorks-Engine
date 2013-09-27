package org.fractalstudio.engine.gui.impl;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;

import org.fractalstudio.engine.gui.Component;
import org.lwjgl.opengl.GL11;

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
public class RadialMenu extends Component {

	/*
	 * Whether to draw the menu or nto
	 */
	private boolean showMenu = false;
	/*
	 * The place where we opened the menu
	 */
	private int menuOpenX = 0;
	private int menuOpenY = 0;

	/**
	 * Construct a new radial menu
	 */
	public RadialMenu() {
		// Name, x, y, width, height
		super("radialMenu", 0, 0, getWindowWidth(), getWindowHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#onMouseDown(int, int, int)
	 */
	@Override
	public boolean onMouseDown(int button, int mouseX, int mouseY) {
		if (button == 2) {
			activateInputLock();
			showMenu = true;
			menuOpenX = mouseX;
			menuOpenY = mouseY;
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#onMouseUp(int, int, int)
	 */
	@Override
	public boolean onMouseUp(int button, int mouseX, int mouseY) {
		if (button == 2) {
			deactivateInputLock();
			showMenu = false;
			return true;
		}
		return false;
	}

	/**
	 * Renders the radial menu
	 */
	@Override
	public void render() {

		if (showMenu) {
			glDisable(GL_TEXTURE_2D);
			glBegin(GL_QUADS);
			{
				GL11.glColor3f(0.5f, 0.5f, 0.5f);
				drawQuad(menuOpenX - 20, menuOpenY - 20, 40, 40);
			}
			glEnd();

			// Draw a line
			GL11.glColor3f(1.0f, 0.0f, 0.0f);
			drawLine(menuOpenX, menuOpenY, getMouseX(), getMouseY());
		}

	}

}
