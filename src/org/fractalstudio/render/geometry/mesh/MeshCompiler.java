package org.fractalstudio.render.geometry.mesh;

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
 */
public class MeshCompiler {

	/*
	 * The buffer containing the positions of the mesh
	 */
	private FloatBuffer positions;
	/*
	 * The buffer containing the normals of the mesh
	 */
	private FloatBuffer normals;
	/*
	 * The buffer containing the texture coordinates of the mesh
	 */
	private FloatBuffer texCoords;
	/*
	 * The mesh
	 */
	private Mesh mesh;
	/*
	 * The mesh renderer
	 */
	private MeshRenderer meshRenderer;

	/**
	 * Construct a new mesh compiler
	 */
	public MeshCompiler() {
		mesh = new Mesh();
		meshRenderer = new MeshRenderer();
	}

	/**
	 * Set the positions of this mesh compiler
	 * 
	 * @param positions
	 */
	public void setPositions(FloatBuffer positions) {
		this.positions = positions;
	}

	/**
	 * Set the normals of this mesh compiler
	 * 
	 * @param normals
	 */
	public void setNormals(FloatBuffer normals) {
		this.normals = normals;
	}

	/**
	 * Set the texture coordinates of this mesh compiler
	 * 
	 * @param texCoords
	 */
	public void setTexCoords(FloatBuffer texCoords) {
		this.texCoords = texCoords;
	}

	/**
	 * Compile
	 */
	public Mesh compileMesh() {

		
		
		return null;
	}

}
