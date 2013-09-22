package org.fractalstudio.render.material;

import java.util.HashMap;

import org.fractalstudio.engine.EngineLogger;
import org.fractalstudio.render.opengl.shader.Property;
import org.fractalstudio.render.opengl.shader.ShaderProgram;

public class Material {

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
				EngineLogger.warning("Uniform: " + string
						+ " was not found in shader.");
			}
		}

	}
}
