package org.fractalstudio.render.opengl.shader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.fractalstudio.engine.EngineLogger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBGeometryShader4;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

public class Shader {

	/**
	 * An enum describing the available shader types
	 * 
	 * @author meanz
	 * 
	 */
	public static enum ShaderType {
		FRAGMENT_SHADER(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB),
		VERTEX_SHADER(ARBVertexShader.GL_VERTEX_SHADER_ARB),
		GEOMETRY_SHADER(ARBGeometryShader4.GL_GEOMETRY_SHADER_ARB);
		/*
		 * 
		 */
		private int shaderType;

		/**
		 * 
		 * @param shaderType
		 *            The integer value for this shader type
		 */
		private ShaderType(int shaderType) {
			this.shaderType = shaderType;
		}

		/**
		 * Get the integer value for this shader type
		 * 
		 * @return
		 */
		public int getShaderType() {
			return shaderType;
		}
	}

	/*
	 * The shader code
	 */
	private String shaderData = "";

	/*
	 * The shader type
	 */
	private ShaderType shaderType = null;

	/*
	 * The id of the shader
	 */
	private int shaderId;

	/**
	 * 
	 */
	public Shader(ShaderType shaderType) {
		this.shaderType = shaderType;
	}

	/**
	 * Read the shader data from the input stream
	 * 
	 * @param is
	 * @throws IOException
	 */
	public void readShaderData(InputStream input) throws IOException {
		readShaderData(input, false);
	}

	/**
	 * Read the shader data from the input stream
	 * 
	 * @param is
	 * @throws IOException
	 */
	public void readShaderData(InputStream input, boolean append)
			throws IOException {
		byte[] cbuf = new byte[(int) input.available()];
		input.read(cbuf);
		if (input != null) {
			input.close();
		}
		if (append) {
			addShaderData(new String(cbuf) + "\n");
		} else {
			setShaderData(new String(cbuf) + "\n");
		}
	}

	/**
	 * Read shader data from a file
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void readShaderData(File file) throws IOException {
		readShaderData(file, false);
	}

	/**
	 * Read shader data from a file
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void readShaderData(File file, boolean append) throws IOException {
		FileInputStream input = new FileInputStream(file);
		readShaderData(input, append);
	}

	/**
	 * Add shader data to this shader
	 * 
	 * @param shaderData
	 */
	public void addShaderData(String shaderData) {
		this.shaderData += "\r\n" + shaderData;
	}

	/**
	 * Set the shader data of this shader
	 * 
	 * @param shaderData
	 */
	public void setShaderData(String shaderData) {
		this.shaderData = shaderData;
	}

	/**
	 * Compile the shader object
	 */
	public boolean compile() {
		ARBShaderObjects.glShaderSourceARB(shaderId, shaderData);
		ARBShaderObjects.glCompileShaderARB(shaderId);
		if (ARBShaderObjects.glGetObjectParameteriARB(shaderId,
				ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
			EngineLogger.error("[ERROR COMPILING SHADER(" + shaderId + ")]");
			printLogInfo(shaderId);
			return false;
		}
		return true;
	}

	/**
	 * Create the shader object
	 * 
	 * @return
	 */
	public boolean create() {
		shaderId = ARBShaderObjects.glCreateShaderObjectARB(shaderType
				.getShaderType());

		int errorCode = GL11.glGetError();
		if (errorCode != 0) {
			EngineLogger.error("[ERROR CREATING SHADER]: " + errorCode);
			return false;
		}
		return true;
	}

	/**
	 * Delete the shader object
	 * 
	 * @return
	 */
	public boolean delete() {
		ARBShaderObjects.glDeleteObjectARB(shaderId);

		int errorCode = GL11.glGetError();
		if (errorCode != 0) {
			EngineLogger.error("[ERROR DELETING SHADER(" + shaderId + ")]");
			return false;
		}
		return true;
	}

	/**
	 * Get the id of the shader
	 * 
	 * @return
	 */
	public int getId() {
		return shaderId;
	}

	/**
	 * Print log info about an object
	 * 
	 * @param obj
	 * @return
	 */
	private static boolean printLogInfo(int obj) {
		IntBuffer iVal = BufferUtils.createIntBuffer(1);
		ARBShaderObjects.glGetObjectParameterARB(obj,
				ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);

		int length = iVal.get();
		if (length > 1) {
			// We have some info we need to output.
			ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
			iVal.flip();
			ARBShaderObjects.glGetInfoLogARB(obj, iVal, infoLog);
			byte[] infoBytes = new byte[length];
			infoLog.get(infoBytes);
			String out = new String(infoBytes);
			EngineLogger.info("Info log:\n" + out);
		} else {
			return true;
		}
		return false;
	}
}
