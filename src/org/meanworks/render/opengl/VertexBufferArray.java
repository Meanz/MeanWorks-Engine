package org.meanworks.render.opengl;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.meanworks.engine.EngineLogger;

public class VertexBufferArray {

	/*
	 * The id of the Vertex Array Object
	 */
	private int bufferArrayId;

	/**
	 * Constructor for this vertex buffer array
	 */
	public VertexBufferArray() {
		this.create();
	}

	/**
	 * Check whether this vertex buffer array is valid
	 * 
	 * @return
	 */
	public boolean isValid() {
		return bufferArrayId > 0;
	}

	/**
	 * Bind this vertex buffer array
	 */
	public void bind() {
		ARBVertexArrayObject.glBindVertexArray(bufferArrayId);
	}

	/**
	 * Unbind this vertex buffer array
	 */
	public void unbind() {
		ARBVertexArrayObject.glBindVertexArray(0);
	}

	/**
	 * Create this vertex buffer array
	 * 
	 * @return
	 */
	public boolean create() {

		bufferArrayId = ARBVertexArrayObject.glGenVertexArrays();

		int errorCode = GL11.glGetError();
		if (errorCode != 0) {
			EngineLogger
					.error("Could not create vertex array object (VertexBufferArray) " + GLU.gluErrorString(errorCode));
			return false;
		}
		return true;
	}

	/**
	 * Delete this vertex buffer array
	 * 
	 * @return
	 */
	public boolean delete() {

		ARBVertexArrayObject.glDeleteVertexArrays(bufferArrayId);

		int errorCode = GL11.glGetError();
		if (errorCode != 0) {
			EngineLogger
					.error("Could not delete vertex array object (VertexBufferArray("
							+ bufferArrayId + "))");
			return false;
		}
		return true;
	}

}
