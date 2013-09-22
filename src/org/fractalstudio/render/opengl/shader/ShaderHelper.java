package org.fractalstudio.render.opengl.shader;

import java.io.File;
import java.io.IOException;

import org.fractalstudio.render.opengl.shader.Shader.ShaderType;

public class ShaderHelper {

	/**
	 * Loads a shader program from the given vertex and fragment shaders
	 * 
	 * @param vertexShaderFile
	 * @param fragmentShaderFile
	 * @return
	 */
	public static ShaderProgram loadShader(String vertexShaderFile,
			String fragmentShaderFile) {

		try {
			/*
			 * Create vertex shader
			 */
			Shader vertexShader = new Shader(ShaderType.VERTEX_SHADER);
			if (!vertexShader.create()) {
				return null;
			}
			vertexShader.readShaderData(new File(vertexShaderFile));

			/*
			 * Create fragment shader
			 */
			Shader fragmentShader = new Shader(ShaderType.FRAGMENT_SHADER);
			if (!fragmentShader.create()) {
				vertexShader.delete();
				return null;
			}
			fragmentShader.readShaderData(new File(fragmentShaderFile));

			/*
			 * Create shader program
			 */
			ShaderProgram shaderProgram = new ShaderProgram();
			if (!shaderProgram.create()) {
				vertexShader.delete();
				fragmentShader.delete();
				return null;
			}
			shaderProgram.attach(vertexShader);
			shaderProgram.attach(fragmentShader);
			if (!shaderProgram.compile()) {
				return null;
			}

			return shaderProgram;
		} catch (IOException iex) {
			iex.printStackTrace();
		}
		return null;

	}

}
