package org.fractalstudio.render.geometry;

import java.nio.FloatBuffer;
import java.util.LinkedList;

import org.fractalstudio.engine.EngineLogger;
import org.fractalstudio.render.opengl.DisplayList;
import org.fractalstudio.render.opengl.VertexBuffer;
import org.fractalstudio.render.opengl.VertexBufferArray;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Mesh {

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
	 * If we are using a display list
	 */
	protected DisplayList displayList = null;

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
	public Mesh() {

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
	 * Process the given mesh data into a set of vertex buffers
	 */
	private void makeVBOs(MeshData meshData) {
		if (meshData == null) {
			EngineLogger.warning("MeshData is null :'(");
			return;
		}
		// Inspect the mesh data
		// Check what kinds of data the triangles has
		float[] positions = meshData.getPositions();
		float[] normals = meshData.getNormals();
		float[] texCoords = meshData.getTexCoords();

		// Sequence the data
		int numFloats = (positions != null ? positions.length : 0)
				+ (normals != null ? normals.length : 0)
				+ (texCoords != null ? texCoords.length : 0);
		FloatBuffer data = BufferUtils.createFloatBuffer(numFloats);

		int numVerts = positions.length / 3;

		for (int vertIdx = 0; vertIdx < numVerts; vertIdx++) {
			int positionIdx = vertIdx * 3;
			int texIdx = vertIdx * 2;
			if (positions != null) {
				data.put(positions[positionIdx])
						.put(positions[positionIdx + 1])
						.put(positions[positionIdx + 2]);
			}
			if (normals != null) {
				data.put(normals[positionIdx]).put(normals[positionIdx + 1])
						.put(normals[positionIdx + 2]);
			}
			if (texCoords != null) {
				data.put(texCoords[texIdx]).put(texCoords[texIdx + 1]);
			}
		}
		data.flip();
	}

	/**
	 * Processes the given mesh data into a display list
	 */
	public void makeDisplayList(MeshData meshData) {
		if (displayList == null) {
			displayList = new DisplayList();
			displayList.create();

			/** Put all triangles inside **/
			displayList.newList();
			GL11.glBegin(GL11.GL_TRIANGLES);
			for (Triangle triangle : meshData.getTriangles()) {
				triangle.drawImmediate();
			}
			GL11.glEnd();
			displayList.endList();
		}
	}

	/**
	 * This is called from the main draw loop
	 */
	public void prepareMesh() {

	}

	/**
	 * Renders the mesh
	 */
	public void render() {
		// Render display list
		if (displayList != null) {
			displayList.callList();
		}
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
