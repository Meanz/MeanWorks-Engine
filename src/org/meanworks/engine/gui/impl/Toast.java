package org.meanworks.engine.gui.impl;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;

import java.util.LinkedList;

import org.meanworks.engine.core.Application;
import org.meanworks.engine.gui.Component;
import org.meanworks.engine.gui.FontRenderer;

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
public class Toast extends Component {

	/*
	 * The stack of toasts
	 */
	private static LinkedList<Toast> topToasts = new LinkedList<>();

	/*
	 * Calculates the toast Y position
	 */
	private static int getToastY(int toastIdx) {
		return 10 + (toastIdx * 40);
	}

	/**
	 * Create a toast message
	 * 
	 * @param message
	 * @param x
	 * @param y
	 */
	public static void makeTopToast(String message) {
		if (message == null) {
			return;
		}
		Toast toast = new Toast(message, Application.getApplication()
				.getWindow().getWidth()
				/ 2 - FontRenderer.arial14.getStringWidth(message) / 2, 0,
				topToasts.size());
		topToasts.add(toast);
		Application.getApplication().getGui().addComponent(toast);

	}

	/**
	 * Create a toast message
	 * 
	 * @param message
	 * @param x
	 * @param y
	 */
	public static void makeToast(String message, int x, int y) {
		if (message == null) {
			return;
		}
		Application.getApplication().getGui()
				.addComponent(new Toast(message, x, y, -1));
	}

	/*
	 * The message of this toast
	 */
	private String message;

	/*
	 * 
	 */
	private int textWidth = 0;

	/*
	 * The life of this component
	 */
	private final int life = 60 * 5;

	/*
	 * The number of ticks this component has to live
	 */
	private int ticks = life; // 5 seconds

	/*
	 * 
	 */
	private int toastIdx;

	/**
	 * Constructs a new Toast
	 * 
	 * @param message
	 * @param y
	 * @param x
	 * 
	 */
	private Toast(String message, int x, int y, int toastIdx) {
		super("Toast_" + Component.getNextId(), x, y, FontRenderer.arial14
				.getStringWidth(message) + 20, 35);
		this.message = message;
		this.textWidth = FontRenderer.arial14.getStringWidth(message);
		this.toastIdx = toastIdx;
	}

	/**
	 * Updates this component
	 */
	public void update() {
		ticks--;
		if (ticks == 0 || ticks < 1) {
			Application.getApplication().getGui().flagDelete(this.getName());
			if (toastIdx != -1) {
				topToasts.remove(this);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#render()
	 */
	public void render() {
		glDisable(GL_TEXTURE_2D);

		float transp = 0.2f + (((float) ticks / (float) life) * 0.8f);

		int drawY = getY();
		if (toastIdx != -1) {
			drawY = getToastY(toastIdx);
		}

		// Draw background part of button
		glBegin(GL_QUADS);
		{
			glColor4f(0.6f, 0.6f, 0.6f, transp);
			drawQuad(getX(), drawY, getWidth(), getHeight());
		}
		glEnd();
		glColor4f(1.0f, 1.0f, 1.0f, transp);
		// Center the text
		FontRenderer.arial14.drawString(message + " " + toastIdx, getX()
				- (textWidth / 2) + (getWidth() / 2), drawY + 10);

		// Reset
		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

}
