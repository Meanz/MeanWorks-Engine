package org.fractalstudio.render.geometry;

import java.nio.FloatBuffer;

import org.fractalstudio.engine.EngineLogger;
import org.fractalstudio.render.opengl.VertexBuffer;
import org.fractalstudio.render.opengl.VertexBufferArray;
import org.fractalstudio.render.opengl.VertexBuffer.BufferType;
import org.fractalstudio.render.opengl.VertexBuffer.BufferUsage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class MeshBuilder {

	/**
	 * 
	 * @param data
	 * @param numVerts
	 * @param positions
	 * @param normals
	 * @param texCoords
	 */
	public static Mesh compileSingleVBO(FloatBuffer data, int numVerts,
			boolean positions, boolean normals, boolean texCoords) {

		Mesh mesh = new Mesh();
		mesh.setNumVertices(numVerts);

		// Create our vertex array
		VertexBufferArray vertexBufferArray = new VertexBufferArray();
		if (!vertexBufferArray.isValid()) {
			EngineLogger.warning("Invalid Vertex Buffer Array");
			return null;
		}

		// Buffer the data
		VertexBuffer vbData = new VertexBuffer(BufferType.ARRAY_BUFFER,
				BufferUsage.STATIC_DRAW);

		vbData.bufferData(data);

		vertexBufferArray.bind();
		vbData.bind();

		mesh.setVertexBufferArray(vertexBufferArray);
		mesh.addVertexBuffer(vbData);

		// int index, int size, int type, boolean normalized, int stride, long
		// buffer_buffer_offset
		// Setup pointers
		int stride = (positions ? 3 * 4 : 0) + (normals ? 3 * 4 : 0)
				+ (texCoords ? 3 * 4 : 0) + 16;

		int offset = 0;

		if (positions) {
			GL20.glEnableVertexAttribArray(0);
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, stride,
					offset);

			offset += 12;
		}
		if (normals) {
			GL20.glEnableVertexAttribArray(1);
			GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, stride,
					offset);

			offset += 12;
		}
		if (texCoords) {
			GL20.glEnableVertexAttribArray(2);
			GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, stride,
					offset);

			offset += 12;
		}

		GL20.glEnableVertexAttribArray(3);
		GL20.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, stride, offset);
		offset += 16;

		vertexBufferArray.unbind();

		// data.clear();

		return mesh;
	}

}
