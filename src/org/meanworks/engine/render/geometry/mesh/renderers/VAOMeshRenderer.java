package org.meanworks.engine.render.geometry.mesh.renderers;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;
import org.meanworks.engine.EngineConfig;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.RenderState;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.render.material.Material;
import org.meanworks.engine.render.opengl.GLImmediate;
import org.meanworks.engine.render.opengl.VertexBuffer;
import org.meanworks.engine.render.opengl.VertexBuffer.BufferType;
import org.meanworks.engine.render.opengl.VertexBuffer.BufferUsage;
import org.meanworks.engine.render.opengl.VertexBufferArray;
import org.meanworks.engine.scene.Scene;

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
public class VAOMeshRenderer implements MeshRenderer {

	/**
	 * Helper
	 * 
	 * @author Meanz
	 * 
	 */
	public enum BufferEntryType {

		POSITION(0, 3, GL11.GL_FLOAT),
		NORMAL(1, 3, GL11.GL_FLOAT),
		UV(2, 2, GL11.GL_FLOAT),
		TANGENT(3, 3, GL11.GL_FLOAT),
		BI_TANGENT(4, 3, GL11.GL_FLOAT)

		;

		private int shaderLocation;
		private int defaultSize;
		private int defaultType;

		private BufferEntryType(int shaderLocation, int defaultsize,
				int defaultType) {
			this.shaderLocation = shaderLocation;
			this.defaultSize = defaultsize;
			this.defaultType = defaultType;
		}

		public int getShaderLocation() {
			return shaderLocation;
		}

		public int getDefaultSize() {
			return defaultSize;
		}

		public int getDefaultType() {
			return defaultType;
		}

	}

	/**
	 * Holds information about a buffer entry
	 * 
	 * @author meanz
	 * 
	 */
	@SuppressWarnings("all")
	public class BufferEntry {

		/*
		 * The buffer of this entry
		 */
		VertexBuffer buffer;
		/*
		 * The attributes for this buffer
		 */
		LinkedList<BufferAttribute> bufferAttributes = new LinkedList<>();

		/**
		 * Add the given buffer attribute to this buffer entry
		 * 
		 * @param ba
		 */
		public void addAttribute(BufferAttribute ba) {
			bufferAttributes.add(ba);
		}

		/**
		 * Add an attribute to this buffer entry
		 * 
		 * @param index
		 * @param size
		 * @param type
		 * @param normalized
		 * @param stride
		 * @param bufferOffset
		 */
		public void addAttribute(int index, int size, int type,
				boolean normalized, int stride, int bufferOffset) {
			BufferAttribute bufferAttribute = new BufferAttribute();
			bufferAttribute.index = index;
			bufferAttribute.size = size;
			bufferAttribute.type = type;
			bufferAttribute.normalized = normalized;
			bufferAttribute.stride = stride;
			bufferAttribute.bufferOffset = bufferOffset;
			addAttribute(bufferAttribute);
		}

	}

	/**
	 * Structure for a buffer attribute
	 * 
	 * @author meanz
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
		BufferEntryType bet;

		public BufferAttribute copy() {
			BufferAttribute ba = new BufferAttribute();
			ba.index = index;
			ba.size = size;
			ba.type = type;
			ba.normalized = normalized;
			ba.stride = stride;
			ba.bufferOffset = bufferOffset;
			ba.bet = bet;
			return ba;
		}
	}

	/*
	 * The list of vertex buffers
	 */
	private LinkedList<BufferEntry> vertexBuffers = new LinkedList<>();

	/*
	 * The index buffer for this mesh renderer
	 */
	private VertexBuffer indexBuffer = null;

	/*
	 * We have the ability to array up the mesh
	 */
	private VertexBufferArray vertexBufferArray;

	/*
	 * The number of vertices stored in this mesh renderer
	 */
	private int numVertices;

	/*
	 * The number of indices stored in this mesh renderer
	 */
	private int numIndices;

	/**
	 * Deep copy this mesh renderer
	 * 
	 * @return
	 */
	public VAOMeshRenderer deepCopy() {

		VAOMeshRenderer mr = new VAOMeshRenderer();

		mr.setNumIndices(numIndices);
		mr.setNumVertices(numVertices);

		/*
		 * Check if we have an index buffer to copy
		 */
		if (indexBuffer != null) {
			mr.setIndexBuffer(indexBuffer.deepCopy());
		}

		/*
		 * Copy the rest of the buffers
		 */
		for (BufferEntry be : vertexBuffers) {
			VertexBuffer vb = be.buffer.deepCopy();
			BufferEntry newBe = mr.addVertexBuffer(vb);
			for (BufferAttribute ba : be.bufferAttributes) {
				newBe.addAttribute(ba.copy());
			}
		}

		return mr;
	}

	/**
	 * Deletes all buffers associated with this mesh renderer
	 */
	public void delete() {
		if (indexBuffer != null) {
			indexBuffer.delete();
		}
		if (vertexBufferArray != null) {
			vertexBufferArray.delete();
		}
		for (BufferEntry vb : vertexBuffers) {
			vb.buffer.delete();
		}
	}

	/**
	 * 
	 * @param buffer
	 * @param index
	 * @param size
	 */
	public void addIndex(IntBuffer buffer, int numIndices) {
		VertexBuffer vbIndices = new VertexBuffer(BufferType.INDEX_BUFFER,
				BufferUsage.STATIC_DRAW);
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
	public void addBuffer(FloatBuffer buffer, int index, int size) {
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
	public void addBuffer(FloatBuffer buffer, BufferEntryType bufferEntryType) {
		VertexBuffer vb = new VertexBuffer(BufferType.ARRAY_BUFFER,
				BufferUsage.STATIC_DRAW);
		vb.bufferData(buffer);
		BufferEntry be = addVertexBuffer(vb);
		be.addAttribute(bufferEntryType.getShaderLocation(), // Index
				bufferEntryType.getDefaultSize(), // Size
				bufferEntryType.getDefaultType(), // Type
				false, // Normalized
				bufferEntryType.getDefaultSize() * 4, // Stride
				0 // Offset
		);
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
		if (vertexBuffer.getType() == VertexBuffer.BufferType.ARRAY_BUFFER) {

			// This has to be a float buffer
			// Let's assume every vertex has 3 elements
			if (vertexBuffer.getFloatBuffer() != null) {
				numVertices += vertexBuffer.getFloatBuffer().capacity() / 3;
			}
		}
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
	public boolean compile() {

		// Preliminary check
		for (BufferEntry bufferEntry : vertexBuffers) {
			if (bufferEntry == null || bufferEntry.buffer == null
					|| bufferEntry.bufferAttributes.size() == 0) {
				EngineLogger
						.error("Could not compile Mesh, invalid buffer entry.");
				return false;
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

		int error = GL11.glGetError();
		if (error != 0) {
			EngineLogger.error("Could not compile Mesh, "
					+ GLU.gluErrorString(error));

			return false;
		}

		return true;
	}

	/**
	 * Renders the mesh
	 */
	public void render(Material material) {
		if (vertexBufferArray != null) {

			/*
			 * Update matrices
			 */
			material.setProperty("mProjectionView",
					RenderState.getProjectionMatrix());
			material.setProperty("mModelMatrix",
					RenderState.getTransformMatrix());

			/*
			 * Upload material data to the graphics card
			 */
			material.apply();
			
			vertexBufferArray.bind();
			{
				if (indexBuffer != null) {
					if (EngineConfig.usingModernOpenGL) {
						GL11.glDrawElements(GL11.GL_TRIANGLES, numIndices,
								GL11.GL_UNSIGNED_INT, 0);
					} else {
						EngineLogger
								.error("Tried to render using a VertexBufferRenderer while the graphics card does not support it.");
					}
				} else {
					GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.numVertices);
				}
				RenderState.addRenderedVertices(this.numVertices);
			}
			vertexBufferArray.unbind();
		} else {
			EngineLogger.error("Could not render model... VAO = null");
		}
	}
}
