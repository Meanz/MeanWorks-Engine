package org.meanworks.terrain;

import org.meanworks.engine.math.Vec2;
import org.meanworks.engine.math.Vec3;

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
public class FloatMeshBuffer {

	public float[] positions;
	public float[] normals;
	public float[] uvs;
	public int[] triangles;

	public int posOffset = 0;
	public int norOffset = 0;
	public int uvsOffset = 0;
	public int idxOffset = 0;

	/**
	 * Construct a new float mesh buffer
	 * 
	 * @param numVertices
	 * @param numIndices
	 */
	public FloatMeshBuffer(int numVertices, int numIndices) {
		positions = new float[numVertices * 3];
		normals = new float[numVertices * 3];
		uvs = new float[numVertices * 2];
		triangles = new int[numIndices];
	}

	/**
	 * Add a position vector to the buffer
	 * 
	 * @param position
	 */
	public void addPosition(Vec3 position) {
		addPosition(position.x, position.y, position.z);
	}

	/**
	 * Add the given position floats to the buffer
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addPosition(float x, float y, float z) {
		positions[posOffset++] = x;
		positions[posOffset++] = y;
		positions[posOffset++] = z;
	}

	/**
	 * Add a normal vector to the buffer
	 * 
	 * @param normal
	 */
	public void addNormal(Vec3 normal) {
		addNormal(normal.x, normal.y, normal.z);
	}

	/**
	 * Add the given normal floats to the buffer
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addNormal(float x, float y, float z) {
		normals[norOffset++] = x;
		normals[norOffset++] = y;
		normals[norOffset++] = z;
	}

	/**
	 * Add the given UV vector to the buffer
	 * 
	 * @param uv
	 */
	public void addUV(Vec2 uv) {
		addUV(uv.x, uv.y);
	}

	/**
	 * Add the given UV floats to the buffer
	 * 
	 * @param u
	 * @param v
	 */
	public void addUV(float u, float v) {
		uvs[uvsOffset++] = u;
		uvs[uvsOffset++] = v;
	}

	/**
	 * Add the given indice to the buffer
	 * 
	 * @param index
	 */
	public void addIndex(int index) {
		triangles[idxOffset++] = index;
	}

	/**
	 * Add the given indices to the buffer
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public void addTriangle(int p1, int p2, int p3) {
		addIndex(p1);
		addIndex(p2);
		addIndex(p3);
	}

}
