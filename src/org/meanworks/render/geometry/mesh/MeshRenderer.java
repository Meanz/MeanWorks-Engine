package org.meanworks.render.geometry.mesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.meanworks.engine.EngineLogger;
import org.meanworks.render.opengl.VertexBuffer;
import org.meanworks.render.opengl.VertexBuffer.BufferType;
import org.meanworks.render.opengl.VertexBuffer.BufferUsage;
import org.meanworks.render.opengl.VertexBufferArray;

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
public class MeshRenderer {
	/**
	 * Holds information about a buffer entry
	 * 
	 * @author meanz
	 * 
	 */
	@SuppressWarnings("all")
	public class BufferEntry {

		VertexBuffer buffer;
		LinkedList<BufferAttribute> bufferAttributes = new LinkedList<>();

		public void addAttribute(int index, int size, int type,
				boolean normalized, int stride, int bufferOffset) {
			BufferAttribute bufferAttribute = new BufferAttribute();
			bufferAttribute.index = index;
			bufferAttribute.size = size;
			bufferAttribute.type = type;
			bufferAttribute.normalized = normalized;
			bufferAttribute.stride = stride;
			bufferAttribute.bufferOffset = bufferOffset;
			bufferAttributes.add(bufferAttribute);
		}

	}

	/*
	 * 
	 */
	@SuppressWarnings("all")
	private class BufferAttribute {
		int index;
		int size;
		int type;
		boolean normalized;
		int stride;
		int bufferOffset;
	}

	// int index, int size, int type, boolean normalized, int stride, long
	// buffer_buffer_offset

	/*
	 * The list of vertex buffers
	 */
	private LinkedList<BufferEntry> vertexBuffers = new LinkedList<>();

	/*
	 * 
	 */
	private VertexBuffer indexBuffer = null;

	/*
	 * We have the ability to array up the mesh
	 */
	private VertexBufferArray vertexBufferArray;

	/*
	 * 
	 */
	private int numVertices;

	/*
	 * 
	 */
	private int numIndices;

	/**
	 * Constructor
	 */
	public MeshRenderer() {

	}

	/**
	 * 
	 * @param buffer
	 * @param index
	 * @param size
	 */
	public void addIndex(IntBuffer buffer, int numIndices) {
		VertexBuffer vbIndices = new VertexBuffer(BufferType.INDEX_BUFFER,
				BufferUsage.DYNAMIC_DRAW);
		vbIndices.bufferData(buffer);
		setIndexBuffer(vbIndices);
		setNumIndices(numIndices);
	}

	/**
	 * 
	 * @param buffer
	 * @param index
	 * @param size
	 */
	public void addBuffer(IntBuffer buffer, int index, int size) {
		VertexBuffer vb = new VertexBuffer(BufferType.ARRAY_BUFFER,
				BufferUsage.STATIC_DRAW);
		vb.bufferData(buffer);
		BufferEntry be = addVertexBuffer(vb);
		be.addAttribute(index, size, GL11.GL_FLOAT, false, size * 4, 0);
	}

	/**
	 * 
	 * @param buffer
	 * @param index
	 * @param size
	 */
	public void addBuffer(FloatBuffer buffer, int index, int size) {

		VertexBuffer vb = new VertexBuffer(BufferType.ARRAY_BUFFER,
				BufferUsage.STATIC_DRAW);
		vb.bufferData(buffer);

		BufferEntry be = addVertexBuffer(vb);
		be.addAttribute(index, size, GL11.GL_FLOAT, false, size * 4, 0);

	}

	/**
	 * Set the number of indices for this mesh
	 * 
	 * @param numIndices
	 */
	public void setNumIndices(int numIndices) {
		this.numIndices = numIndices;
	}

	/**
	 * Get the number of indices for this mesh
	 * 
	 * @return
	 */
	public int getNumIndices() {
		return numIndices;
	}

	/**
	 * Set the index buffer of this mesh
	 * 
	 * @param vertexBuffer
	 */
	public void setIndexBuffer(VertexBuffer vertexBuffer) {
		this.indexBuffer = vertexBuffer;
	}

	/**
	 * Get the index buffer of this mesh if any
	 * 
	 * @return
	 */
	public VertexBuffer getIndexBuffer() {
		return indexBuffer;
	}

	/**
	 * Get the number of vertices this mesh has
	 * 
	 * @return
	 */
	public int getNumVertices() {
		return numVertices;
	}

	/**
	 * Add a vertex buffer to this mesh
	 * 
	 * @param vertexBuffer
	 */
	public BufferEntry addVertexBuffer(VertexBuffer vertexBuffer) {
		if (vertexBuffer == null) {
			return null;
		}
		BufferEntry bufferEntry = new BufferEntry();
		bufferEntry.buffer = vertexBuffer;
		vertexBuffers.add(bufferEntry);
		return bufferEntry;
	}

	/**
	 * Set the number of vertices for this mesh
	 * 
	 * @param numVertices
	 */
	public void setNumVertices(int numVertices) {
		this.numVertices = numVertices;
	}

	/**
	 * Get the vertex buffer array of this mesh
	 * 
	 * @return
	 */
	public VertexBufferArray getVertexBufferArray() {
		return vertexBufferArray;
	}

	/**
	 * Set the vertex buffer array of this mesh
	 * 
	 * @param vertexBufferArray
	 */
	public void setVertexBufferArray(VertexBufferArray vertexBufferArray) {
		this.vertexBufferArray = vertexBufferArray;
	}

	/**
	 * Compiles this mesh and makes sure all attributes are set up correctly.
	 */
	public void compile() {

		// Preliminary check
		for (BufferEntry bufferEntry : vertexBuffers) {
			if (bufferEntry == null || bufferEntry.buffer == null
					|| bufferEntry.bufferAttributes.size() == 0) {
				EngineLogger
						.error("Could not compile Mesh, invalid buffer entry.");
				return;
			}
		}

		if (vertexBufferArray == null) {
			vertexBufferArray = new VertexBufferArray();
		}

		vertexBufferArray.bind();
		{
			for (BufferEntry bufferEntry : vertexBuffers) {
				bufferEntry.buffer.bind();
				for (BufferAttribute bufferAttribute : bufferEntry.bufferAttributes) {
					GL20.glEnableVertexAttribArray(bufferAttribute.index);
					GL20.glVertexAttribPointer(bufferAttribute.index,
							bufferAttribute.size, bufferAttribute.type,
							bufferAttribute.normalized, bufferAttribute.stride,
							bufferAttribute.bufferOffset);
				}
			}

			if (indexBuffer != null) {
				indexBuffer.bind();
			}
		}
		vertexBufferArray.unbind();

	}

	/**
	 * Renders the mesh
	 */
	public void render() {
		if (vertexBufferArray != null) {
			vertexBufferArray.bind();
			{
				if (indexBuffer != null) {
					GL11.glDrawElements(GL11.GL_TRIANGLES, numIndices,
							GL11.GL_UNSIGNED_INT, 0);
				} else {
					GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.numVertices);
				}
			}
			vertexBufferArray.unbind();
		}
	}
}
