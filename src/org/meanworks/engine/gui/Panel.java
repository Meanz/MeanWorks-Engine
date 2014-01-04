package org.meanworks.engine.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;

import org.meanworks.engine.math.Vec3;

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
public class Panel extends Component {

	private Vec3 color;

	private float opacity;

	public Panel(int x, int y, int width, int height) {
		super("panel", x, y, width, height);
		color = new Vec3(0.6f, 0.6f, 0.6f);
		opacity = 0.2f;
	}

	/**
	 * Set the color of this panel RGB Vec3 0-1f
	 * 
	 * @param color
	 */
	public void setColor(Vec3 color) {
		if (color == null) {
			color = new Vec3(0.6f, 0.6f, 0.6f);
		}
		this.color = color;
	}

	/**
	 * Set the opacity of this panel, 0f = visible, 1f = transparent
	 * 
	 * @param opacity
	 */
	public void setOpacity(float opacity) {
		if (opacity < 0.0f) {
			opacity = 0.0f;
		} else if (opacity > 1.0f) {
			opacity = 1.0f;
		}
		this.opacity = opacity;
	}

	/**
	 * Render this panel
	 */
	@Override
	public void render() {
		// Draw background part of button
		glBegin(GL_QUADS);
		{
			glColor4f(color.x, color.y, color.z, 1.0f - opacity);
			drawQuad(getX(), getY(), getWidth(), getHeight());
		}
		glEnd();
	}

}
