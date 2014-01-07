package org.meanworks.engine.render.geometry.mesh.render;

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
public enum DrawMode {

	TRIANGLES(GL11.GL_TRIANGLES), QUADS(GL11.GL_QUADS);

	/*
	 * The OpenGL op code
	 */
	private int op;

	/**
	 * Constructor for the DrawMode enum
	 * 
	 * @param op
	 */
	private DrawMode(int op) {
		this.op = op;
	}

	/**
	 * Get the OpenGL opcode
	 * 
	 * @return
	 */
	public int getOp() {
		return op;
	}

}
