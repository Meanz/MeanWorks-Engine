package org.meanworks.render.opengl;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.meanworks.engine.EngineLogger;

public class VertexBuffer {
	/**
	 * An enum describing what type of buffer this is
	 * 
	 * @author meanz
	 * 
	 */
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

	/**
	 * An enum describing how this buffer will be used
	 * 
	 * @author meanz
	 * 
	 */
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
	 * For storing the data
	 */
	private FloatBuffer floatBuffer;
	/*
	 * For storing the data
	 */
	private IntBuffer intBuffer;
	/*
	 * For storing the data
	 */
	private DoubleBuffer doubleBuffer;
	/*
	 * The id of this buffer
	 */
	private int bufferId;
	/*
	 * The type of buffer this is
	 */
	private BufferType bufferType;
	/*
	 * The usage of this buffer
	 */
	private BufferUsage bufferUsage;

	/**
	 * Constructs a new VertexBuffer
	 * 
	 * @param bufferType
	 * @param bufferUsage
	 */
	public VertexBuffer(BufferType bufferType, BufferUsage bufferUsage) {
		this.create();
		this.bufferType = bufferType;
		this.bufferUsage = bufferUsage;
	}

	/**
	 * Get the type of buffer
	 * 
	 * @return
	 */
	public BufferType getType() {
		return bufferType;
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
			EngineLogger.error("Could not create vertex buffer. " + GLU.gluErrorString(errorCode));
			return false;
		}
		return true;
	}

	/**
	 * Delete this buffer
	 */
	public void delete() {
		ARBVertexBufferObject.glDeleteBuffersARB(bufferId);
	}

	/**
	 * 
	 * @param data
	 */
	public void bufferData(FloatBuffer data) {
		floatBuffer = data;
		intBuffer = null;
		doubleBuffer = null;
		bind();
		ARBVertexBufferObject.glBufferDataARB(bufferType.getBufferType(), data,
				bufferUsage.getBufferUsage());
	}

	/**
	 * 
	 * @param data
	 */
	public void bufferData(IntBuffer data) {
		floatBuffer = null;
		intBuffer = data;
		doubleBuffer = null;
		bind();
		ARBVertexBufferObject.glBufferDataARB(bufferType.getBufferType(), data,
				bufferUsage.getBufferUsage());
	}

	/**
	 * 
	 * @param data
	 */
	public void bufferData(DoubleBuffer data) {
		floatBuffer = null;
		intBuffer = null;
		doubleBuffer = data;
		bind();
		ARBVertexBufferObject.glBufferDataARB(bufferType.getBufferType(), data,
				bufferUsage.getBufferUsage());
	}

	/**
	 * Bind this VertexBuffer
	 */
	public void bind() {
		ARBVertexBufferObject.glBindBufferARB(bufferType.getBufferType(),
				bufferId);
	}

	/**
	 * Get the float buffer for this vertex buffer
	 * 
	 * @return
	 */
	public FloatBuffer getFloatBuffer() {
		return floatBuffer;
	}

	/**
	 * Get the OpenGL ID of this Vertexbuffer
	 * 
	 * @return
	 */
	public int getId() {
		return bufferId;
	}

	/**
	 * Deep copies this VertexBuffer
	 * 
	 * @return The deep copied instance of this VertexBuffer
	 */
	public VertexBuffer deepCopy() {
		VertexBuffer vb = new VertexBuffer(bufferType, bufferUsage);
		if (!vb.isValid()) {
			EngineLogger
					.error("[VertexBuffer] Could not deepCopy VertexBuffer");
			return null;
		}
		if (floatBuffer != null) {
			vb.bufferData(floatBuffer);
			return vb;
		}
		if (intBuffer != null) {
			vb.bufferData(intBuffer);
			return vb;
		}
		if (doubleBuffer != null) {
			vb.bufferData(doubleBuffer);
			return vb;
		}
		// Err?
		EngineLogger.warning("[VertexBuffer] Deep copy on empty VertexBuffer");
		return vb;
	}

}
