package org.meanworks.engine.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;

import java.util.LinkedList;
import java.util.List;

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
public class Checkbox extends Component {

	private float opacity = 0.2f;

	private Vec3 backgroundColor = new Vec3(0.6f, 0.6f, 0.6f);

	private Vec3 borderColor = new Vec3(0.8f, 0.8f, 0.8f);

	private Vec3 hoverColor = new Vec3(0.7f, 0.7f, 0.7f);

	private boolean selected = false;

	private boolean isHovered = false;

	private String tooltip = null;

	public Checkbox(int x, int y, boolean selected) {
		super("checkbox" + getNextId(), x, y, 16, 16);
		setSelected(selected);
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		onSelection();
	}

	@Override
	public boolean onMouseDown(int button, int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		if (isInside(mouseX, mouseY)) {
			this.setSelected(!isSelected());
			return true;
		}
		return false;
	}

	@Override
	public boolean onMouseMove(int mouseX, int mouseY, int mouseDeltaX,
			int mouseDeltaY) {
		// TODO Auto-generated method stub
		if (isInside(mouseX, mouseY)) {
			isHovered = true;
			return true;
		} else {
			if (isHovered) {
				isHovered = false;
			}
		}
		return false;
	}

	/**
	 * Called when this checkbox get's a selection update
	 */
	public void onSelection() {
	};

	@Override
	public void render() {

		glDisable(GL_TEXTURE_2D);

		// Draw background part of button
		glBegin(GL_QUADS);
		{
			// Make border
			glColor4f(borderColor.x, borderColor.y, borderColor.z,
					1.0f - opacity);
			drawQuad(getX(), getY(), getWidth(), getHeight());
			if (isHovered) {
				glColor4f(hoverColor.x, hoverColor.y, hoverColor.z,
						1.0f - opacity);
			} else {
				glColor4f(backgroundColor.x, backgroundColor.y,
						backgroundColor.z, 1.0f - opacity);
			}
			drawQuad(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2);
		}
		glEnd();
		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		if (isSelected()) {
			FontRenderer.arial14_white.drawString("X", getX() + 3, getY());
		}

		/*
		 * Render tooltip if hovered
		 */
		if (isHovered && tooltip != null) {
			paintTooltip(tooltip, getMouseX(), getMouseY());
		}
	}

}
