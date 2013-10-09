package org.meanworks.render.opengl;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.meanworks.engine.EngineLogger;

public class VertexBuffer {

	public static enum BufferType {

		INDEX_BUFFER(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB),
		ARRAY_BUFFER(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB);

		/*
		 * The buffer type id for use with the opengl library
		 */
		private int bufferType;

		/**
		 * 
		 * @param bufferType
		 */
		private BufferType(int bufferType) {
			this.bufferType = bufferType;
		}

		/**
		 * Get the integer value of this buffer type
		 * 
		 * @return
		 */
		public int getBufferType() {
			return bufferType;
		}

	};

	public static enum BufferUsage {

		DYNAMIC_COPY(ARBVertexBufferObject.GL_DYNAMIC_COPY_ARB), DYNAMIC_DRAW(
				ARBVertexBufferObject.GL_DYNAMIC_DRAW_ARB), DYNAMIC_READ(
				ARBVertexBufferObject.GL_DYNAMIC_READ_ARB), STATIC_COPY(
				ARBVertexBufferObject.GL_STATIC_COPY_ARB), STATIC_DRAW(
				ARBVertexBufferObject.GL_STATIC_DRAW_ARB), STATIC_READ(
				ARBVertexBufferObject.GL_STATIC_READ_ARB);

		/*
		 * The buffer usage int value
		 */
		private int bufferUsage;

		/**
		 * 
		 * @param bufferType
		 */
		private BufferUsage(int bufferUsage) {
			this.bufferUsage = bufferUsage;
		}

		/**
		 * Get the integer value of this buffer type
		 * 
		 * @return
		 */
		public int getBufferUsage() {
			return bufferUsage;
		}
	}

	/*
	 * 
	 */
	private int bufferId;

	/*
	 * 
	 */
	private BufferType bufferType;

	/*
	 * 
	 */
	private BufferUsage bufferUsage;

	/**
	 * 
	 */
	public VertexBuffer(BufferType bufferType, BufferUsage bufferUsage) {
		this.create();
		this.bufferType = bufferType;
		this.bufferUsage = bufferUsage;
	}

	/**
	 * Check whether this is a valid vertex buffer or not
	 * 
	 * @return
	 */
	public boolean isValid() {
		return bufferId > 0;
	}

	/**
	 * Creates a new vertex buffer object
	 * 
	 * @return Whether the operation was successfull or not
	 */
	public boolean create() {
		bufferId = ARBVertexBufferObject.glGenBuffersARB();

		int errorCode = GL11.glGetError();

		if (errorCode != 0) {
			EngineLogger.error("Could not create vertex buffer.");
			return false;
		}
		return true;
	}

	public void delete() {
		ARBVertexBufferObject.glDeleteBuffersARB(bufferId);
	}

	/**
	 * 
	 * @param data
	 */
	public void bufferData(FloatBuffer data) {
		bind();
		ARBVertexBufferObject.glBufferDataARB(bufferType.getBufferType(), data,
				bufferUsage.getBufferUsage());
	}

	/**
	 * 
	 * @param data
	 */
	public void bufferData(IntBuffer data) {
		bind();
		ARBVertexBufferObject.glBufferDataARB(bufferType.getBufferType(), data,
				bufferUsage.getBufferUsage());
	}

	/**
	 * 
	 * @param data
	 */
	public void bufferData(DoubleBuffer data) {
		bind();
		ARBVertexBufferObject.glBufferDataARB(bufferType.getBufferType(), data,
				bufferUsage.getBufferUsage());
	}

	/**
	 * 
	 */
	public void bind() {
		ARBVertexBufferObject.glBindBufferARB(bufferType.getBufferType(),
				bufferId);
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return bufferId;
	}

	/**
	 * We need a way to setup the buffer pointers, And also we should consult
	 * the OpenGL manual, should we pack Verts, Norms and Tex coords in a single
	 * vbo or should we split them into several vbos?
	 * 
	 * Before we design this we need to take that into consideration, We could
	 * however add a build function inside the mesh class.
	 */

}
