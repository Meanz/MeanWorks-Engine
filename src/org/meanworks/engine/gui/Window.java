package org.meanworks.engine.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;

import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.render.FontRenderer;

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
public class Window extends Component {

	/*
	 * The title of the window
	 */
	private String title;

	/*
	 * The opacity of this window
	 */
	private float opacity = 0.2f;

	/*
	 * The background color of this window
	 */
	private Vec3 backgroundColor = new Vec3(0.6f, 0.6f, 0.6f);

	/*
	 * The close button for this window
	 */
	private Button closeButton = null;

	/*
	 * Whether or not we are dragging this window
	 */
	private boolean dragging = false;

	/**
	 * Construct a new window
	 * 
	 * @param title
	 *            The title of the window
	 * @param x
	 *            The x position of the window
	 * @param y
	 *            The y position of the window
	 * @param width
	 *            The width of the window
	 * @param height
	 *            The height of the window
	 */
	public Window(String title, int x, int y, int width, int height) {
		super("window" + getNextId(), x, y, width, height);
		this.title = title;

		final Window window = this;
		closeButton = new Button(" X", x + width - 5 - 16, y + 5, 16, 16, false) {

			@Override
			public void onButtonClick() {
				window.setVisible(false); // Hides this window
				onClose();
			}

		};
		closeButton.setBackgroundColor(new Vec3(0.4f, 0.4f, 0.4f));
		closeButton.setOpacity(0.2f);
		add(closeButton);
		onCreate();
	}

	/**
	 * Called when the x button is pressed
	 */
	public void onClose() {

	}

	/**
	 * Called when the window is created
	 */
	public void onCreate() {

	}

	/**
	 * Get the opacity of this window
	 * 
	 * @return
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Set the opacity of this window, E[0-1] 1.0f = 100% opacity
	 * 
	 * @param opacity
	 */
	public void setOpacity(float opacity) {
		if (opacity < 0.0f) {
			opacity = 0.0f;
		}
		if (opacity > 1.0f) {
			opacity = 1.0f;
		}
		this.opacity = opacity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meanworks.engine.gui.Component#onMouseMove(int, int, int, int)
	 */
	@Override
	public boolean onMouseMove(int mouseX, int mouseY, int mouseDeltaX,
			int mouseDeltaY) {
		// TODO Auto-generated method stub
		if (dragging) {
			this.setPosition(getX() + mouseDeltaX, getY() + mouseDeltaY);
			// Update all components relative!
			for (Component c : this.getComponents()) {
				c.setPosition(c.getX() + mouseDeltaX, c.getY() + mouseDeltaY);
			}
			return true;
		} else {
			if (isInside(mouseX, mouseY)) {
				return true;
			}
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meanworks.engine.gui.Component#onMouseDown(int, int, int)
	 */
	@Override
	public boolean onMouseDown(int button, int mouseX, int mouseY) {
		if (button == 0) {
			if (mouseY > getY() && mouseY < getY() + 25 && mouseX > getX()
					&& mouseX < getX() + getWidth()) {
				// This is being dragged now
				dragging = true;
			}
		}
		if (isInside(mouseX, mouseY)) {
			toFront();
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meanworks.engine.gui.Component#onMouseDown(int, int, int)
	 */
	@Override
	public boolean onMouseUp(int button, int mouseX, int mouseY) {
		if (button == 0) {
			if (dragging) {
				// This is being dragged now
				dragging = false;
				return true;
			}
			if (isInside(mouseX, mouseY)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meanworks.engine.gui.Component#render()
	 */
	@Override
	public void render() {

		/*
		 * Render background
		 */
		// Draw background part of tooltip
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		{
			glColor4f(backgroundColor.x, backgroundColor.y, backgroundColor.z,
					1.0f - opacity);
			drawQuad(getX(), getY(), getWidth(), getHeight());
		}
		glEnd();

		glColor4f(0.8f, 0.8f, 0.8f, 0.8f);
		drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight());
		drawLine(getX(), getY() + 25, getX() + getWidth(), getY() + 25);

		if (title != null) {
			FontRenderer.arial14_white.drawString(title, getX() + 10,
					getY() + 5);
		}

	}

}
