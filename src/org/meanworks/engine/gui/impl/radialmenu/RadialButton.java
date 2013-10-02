package org.meanworks.engine.gui.impl.radialmenu;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;

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
public class RadialButton extends Component {

	/*
	 * The text of this radial button
	 */
	private String text;
	/*
	 * The text width of this radial button
	 */
	private int textWidth;
	/*
	 * 
	 */
	private boolean gray = false;

	/**
	 * Construct a new radial button
	 * 
	 * @param text
	 */
	public RadialButton(String text) {
		super("RadialButton_" + Component.getNextId(), 0, 0, 110, 35);
		setText(text);
	}

	/**
	 * Set the text of this radial button
	 * 
	 * @param text
	 */
	public void setText(String text) {
		if (text == null) {
			return;
		}
		this.text = text;
		textWidth = FontRenderer.arial14.getStringWidth(text);
	}

	@Override
	public boolean onMouseUp(int button, int mouseX, int mouseY) {
		if(button == 2) {
			System.err.println("Clicked radial button " + getName());
			onButtonClick();
			return false; //Don't want to consume it
		}
		return false;
	}
	
	/**
	 * Called when the button is clicked
	 */
	public void onButtonClick() {

	}

	/**
	 * Render this radial button
	 */
	public void render() {
		glDisable(GL_TEXTURE_2D);

		// Draw background part of button
		glBegin(GL_QUADS);
		{
			// Make border
			if (isHovered()) {
				glColor4f(0.9f, 0.9f, 0.9f, 0.8f);
			} else {
				glColor4f(0.6f, 0.6f, 0.6f, 0.8f);
			}
			Component.drawQuad(getX(), getY(), getWidth(), getHeight());
		}
		glEnd();
		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		// Draw the font
		glColor3f(0.0f, 0.0f, 0.0f);

		// Center the text
		FontRenderer.arial14.drawString(text, getX() - (textWidth / 2)
				+ (getWidth() / 2), getY() + 10);
	}

}
