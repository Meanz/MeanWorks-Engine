package org.meanworks.engine.render;

import org.meanworks.engine.render.opengl.GLVertexBuffer;
import org.meanworks.engine.render.opengl.GLVertexBuffer.BufferType;
import org.meanworks.engine.render.opengl.GLVertexBuffer.BufferUsage;

/**
 * Copyright (C) 2014 Steffen Evensen
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
public class VertexBuffer {

	/**
	 * 
	 */
	private GLVertexBuffer glVertexBuffer;
	
	/**
	 * 
	 */
	private VertexFormat vertexFormat;

	/**
	 * 
	 */
	public VertexBuffer() {

	}

	/**
	 * Releases this vertex buffer
	 */
	public void release() {

	}

	/**
	 * Compiles this vertex buffer
	 */
	public void compile() {

		if (glVertexBuffer == null) {
			glVertexBuffer = GLVertexBuffer.create(BufferType.ARRAY_BUFFER,
					BufferUsage.STATIC_DRAW);
		}
		
		//Upload the data to the gl vertex buffer

	}
}
