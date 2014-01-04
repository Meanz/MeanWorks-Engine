package org.meanworks.engine.render.geometry.mesh;

import java.nio.FloatBuffer;

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
 * 
 *         The mesh parser is responsible for parsing meshes before they are
 *         delivered to their last destination, the renderer.
 * 
 *         After the mesh parser is done parsing the mesh, it can be delivered
 *         in several different types of renderers
 */
public class MeshParser {

	/*
	 * These are hardcoded buffers
	 */
	private FloatBuffer positionBuffer;
	private FloatBuffer normalBuffer;
	private FloatBuffer texCoordBuffer;

	/**
	 * Construct a new mesh parser
	 */
	public MeshParser() {

	}

	/**
	 * Set the position buffer
	 * 
	 * @param positionBuffer
	 */
	public void setPositionBuffer(FloatBuffer positionBuffer) {
		this.positionBuffer = positionBuffer;
	}

	/**
	 * Set the normal buffer
	 * 
	 * @param normalBuffer
	 */
	public void setNormalBuffer(FloatBuffer normalBuffer) {
		this.normalBuffer = normalBuffer;
	}

	/**
	 * Set the texture coordinate buffer
	 * 
	 * @param texCoordBuffer
	 */
	public void setTexCoordBuffer(FloatBuffer texCoordBuffer) {
		this.texCoordBuffer = texCoordBuffer;
	}

}
