package org.fractalstudio.render.opengl.shader;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import org.fractalstudio.engine.EngineLogger;
import org.fractalstudio.engine.math.MatrixHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderProgram {

	/*
	 * The list of attached shaders
	 */
	private LinkedList<Shader> attachedShaders = new LinkedList<Shader>();

	/*
	 * Whether this program is compiled or not
	 */
	private boolean isCompiled = false;

	/*
	 * The id of the shader program
	 */
	private int programId = 0;

	/**
	 * Attach a shader to this shader program
	 * 
	 * @param shader
	 */
	public void attach(Shader shader) {
		attachedShaders.add(shader);
		ARBShaderObjects.glAttachObjectARB(programId, shader.getId());
		isCompiled = false;
	}

	/**
	 * Detatch a shader from this shader program
	 * 
	 * @param shader
	 */
	public void detatch(Shader shader) {
		attachedShaders.remove(shader);
		ARBShaderObjects.glDetachObjectARB(programId, shader.getId());
		isCompiled = false;
	}
	
	/**
	 * 
	 */
	public LinkedList<Shader> detatchAll() {
		LinkedList<Shader> retList = new LinkedList<>();
		for(Shader shader : attachedShaders) {
			retList.add(shader);
			ARBShaderObjects.glDetachObjectARB(programId, shader.getId());
		}
		attachedShaders.clear();
		isCompiled = false;
		return retList;
	}

	/**
	 * Create the shader program
	 * 
	 * @return
	 */
	public boolean create() {
		programId = ARBShaderObjects.glCreateProgramObjectARB();

		int errorCode = GL11.glGetError();
		if (errorCode != 0) {
			EngineLogger.error("[ERROR CREATING PROGRAM]");
			return false;
		}
		return true;
	}

	/**
	 * Delete the shader program
	 * 
	 * @return
	 */
	public boolean delete() {
		ARBShaderObjects.glDeleteObjectARB(programId);

		int errorCode = GL11.glGetError();
		if (errorCode != 0) {
			EngineLogger.error("[ERROR DELETING PROGRAM]");
			return false;
		}
		return true;
	}

	/**
	 * Compile this shader program
	 * 
	 * @return
	 */
	public boolean compile() {
		isCompiled = false;
		if (programId <= 0) {
			EngineLogger.error("[ERROR COMPILING PROGRAM(UNCREATED PROGRAM)]");
			return false;
		}
		for (Shader shader : attachedShaders) {
			if (!shader.compile()) {
				EngineLogger.error("[ERROR COMPILING PROGRAM(" + programId
						+ ")]");
				return false;
			}
			shader.compile();
		}
		if (!link()) {
			EngineLogger.error("[ERROR LINKING PROGRAM(" + programId + ")]");
			return false;
		}
		if (!validate()) {
			EngineLogger.error("[ERROR VALIDATING PROGRAM(" + programId + ")]");
			return false;
		}
		isCompiled = true;
		return true;
	}

	/**
	 * Link a shader program
	 * 
	 * @return
	 */
	public boolean link() {
		ARBShaderObjects.glLinkProgramARB(programId);
		if (ARBShaderObjects.glGetObjectParameteriARB(programId,
				ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
			printLogInfo(programId);
			return false;
		}
		return true;
	}

	/**
	 * Validate a shader program
	 * 
	 * @return
	 */
	public boolean validate() {
		ARBShaderObjects.glValidateProgramARB(programId);
		if (ARBShaderObjects.glGetObjectParameteriARB(programId,
				ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
			printLogInfo(programId);
			return false;
		}
		return true;
	}

	/**
	 * Use this shader program
	 */
	public void use() {
		if (!isCompiled) {
			EngineLogger
					.error("[ERROR TRIED TO USE UNCOMPILED SHADER PROGRAM]");
			return;
		}
		ARBShaderObjects.glUseProgramObjectARB(programId);
	}

	/**
	 * Use no shader programs
	 */
	public void useNone() {
		ARBShaderObjects.glUseProgramObjectARB(0);
	}

	/**
	 * Get the id of the program
	 * 
	 * @return
	 */
	public int getProgramId() {
		return programId;
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

	/**
	 * Temporary helper function for setting texture units in the shader
	 * 
	 * @param attrName
	 * @param texUnit
	 */
	public void setTextureLocation(String attrName, int texUnit) {
		int loc = getUniformLocation(attrName);
		if (loc != -1) {
			ARBShaderObjects.glUniform1iARB(loc, texUnit);
		}
	}

	/**
	 * Send the projection view matrix to the shader
	 * 
	 * @param matrix
	 */
	public void setProjectionViewMatrix(Matrix4f matrix) {
		int loc = getUniformLocation("mProjectionView");
		if (loc != -1) {
			ARBShaderObjects.glUniformMatrix4ARB(loc, false,
					MatrixHelper.storeMatrix(matrix));
		}
	}

	/**
	 * Send the model matrix to the shader
	 * 
	 * @param matrix
	 */
	public void setModelMatrix(Matrix4f matrix) {
		int loc = getUniformLocation("mModelMatrix");
		if (loc != -1) {
			ARBShaderObjects.glUniformMatrix4ARB(loc, false,
					MatrixHelper.storeMatrix(matrix));
		}
	}

	/**
	 * Useless function
	 * @param loc
	 * @param name
	 */
	public void bindAttribLocation(int loc, String name) {
		GL20.glBindAttribLocation(programId, 0, name);
	}

	/**
	 * Get the uniform location of the supplied uniform name
	 * 
	 * @param uniformName
	 * @return
	 */
	public int getUniformLocation(String uniformName) {
		int loc = ARBShaderObjects.glGetUniformLocationARB(programId,
				uniformName);
		if (loc == -1) {
			//EngineLogger.info("Can not find uniform " + uniformName);
		}

		return loc;
	}
}
