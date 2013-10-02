package org.meanworks.render.material;

import java.util.HashMap;

import org.meanworks.engine.EngineLogger;
import org.meanworks.render.opengl.shader.Property;
import org.meanworks.render.opengl.shader.ShaderProgram;

public class Material {

	/*
	 * The default material
	 */
	public static Material DEFAULT_MATERIAL;

	/*
	 * The program for this material
	 */
	private ShaderProgram shaderProgram = null;

	/*
	 * The name of the material
	 */
	private String name = "Unnamed";

	/*
	 * The properties for this material
	 */
	private HashMap<String, Property> properties = new HashMap<String, Property>();

	/**
	 * Constructor
	 */
	public Material(String name, ShaderProgram shaderProgram) {
		this.name = name;
		this.shaderProgram = shaderProgram;
	}

	/**
	 * Get the shader program of this material
	 * 
	 * @return
	 */
	public ShaderProgram getShaderProgram() {
		return shaderProgram;
	}

	/**
	 * Set a property to this material
	 * 
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, Object value) {
		properties.put(name, new Property(value));
	}

	/**
	 * Get a property from this material
	 * 
	 * @param name
	 * @return
	 */
	public Object getProperty(String name) {
		return properties.get(name);
	}

	/**
	 * 
	 */
	public void apply() {
		if (shaderProgram == null) {
			EngineLogger.warning("Material(" + name + ") has a null shader.");
			return;
		}
		shaderProgram.use();
		for (String string : properties.keySet()) {
			int location = shaderProgram.getUniformLocation(string);
			if (location != -1) {
				properties.get(string).sendToShader(location, shaderProgram);
			} else {
				//EngineLogger.warning("Uniform: " + string
				//		+ " was not found in shader.");
			}
		}
	}
}
