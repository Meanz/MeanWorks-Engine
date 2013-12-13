package org.meanworks.render.geometry.mesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.math.Vec2;
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
public class MeshBuffer {

	private FloatBuffer buffer;

	private IntBuffer indices;

	private int numIndices;

	public MeshBuffer(int size, int numIndices) {
		this.numIndices = numIndices;
		buffer = BufferUtils.createFloatBuffer(size);
		indices = BufferUtils.createIntBuffer(numIndices);
	}

	public int getNumIndices() {
		return numIndices;
	}

	public void addIndex(int i) {
		indices.put(i);
	}

	public void addVec4(Vector4f vec) {
		addVec4(vec.x, vec.y, vec.z, vec.w);
	}

	public void addVec4(float x, float y, float z, float w) {
		buffer.put(x);
		buffer.put(y);
		buffer.put(z);
		buffer.put(w);
	}

	public void addVec2(Vec2 vec) {
		addVec2(vec.x, vec.y);
	}

	public void addVec2(float x, float y) {
		buffer.put(x);
		buffer.put(y);
	}

	public void addVec3(Vec3 vec) {
		addVec3(vec.x, vec.y, vec.z);
	}

	public void addVec3(float x, float y, float z) {
		buffer.put(x);
		buffer.put(y);
		buffer.put(z);
	}

	public IntBuffer getFlippedIntBuffer() {
		return (IntBuffer) indices.flip();
	}

	public FloatBuffer getFlippedFloatBuffer() {
		return (FloatBuffer) buffer.flip();
	}

}
