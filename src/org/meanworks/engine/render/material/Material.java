package org.meanworks.engine.render.material;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.meanworks.engine.EngineConfig;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.RenderState;
import org.meanworks.engine.render.opengl.shader.ShaderProgram;
import org.meanworks.engine.render.texture.Texture;

/**
 * A material is a description and a shader program The same shader program can
 * be run on different materials, The material only provide values for the
 * shader
 * 
 * @author Meanz
 * 
 */
public class Material {

	/**
	 * The default material
	 */
	public static Material DEFAULT_MATERIAL;

	/**
	 * The program for this material
	 */
	private ShaderProgram shaderProgram = null;

	/**
	 * The textures for this material
	 */
	private Texture[] textures = new Texture[EngineConfig.MAX_TEXTURE_UNITS];

	/**
	 * The name of the material
	 */
	private String name = "Unnamed";

	/**
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
	 * Set the texture at the 0 slot, Since most materials only use one texture
	 * this is an okay solution
	 * 
	 * @param texture
	 */
	public void setTexture(Texture texture) {
		setTexture(0, texture);
	}

	/**
	 * Set the texture at the given texture slot
	 * 
	 * @param slot
	 * @param texture
	 */
	public void setTexture(int slot, Texture texture) {
		if (slot > textures.length || slot < 0) {
			throw new RuntimeException(
					"Material.setTexture(slot, texture) slot out of bounds "
							+ slot);
		}
		textures[slot] = texture;
	}

	/**
	 * Clear the properties
	 */
	public void clearProperties() {
		properties.clear();
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

		for (int i = 0; i < textures.length; i++) {
			if (textures[i] != null) {
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				RenderState.activeTexture(i);
				textures[i].bind();
			}
		}

		for (String string : properties.keySet()) {
			int location = shaderProgram.getUniformLocation(string);
			if (location != -1) {
				properties.get(string).sendToShader(location, shaderProgram);
			} else {
				// EngineLogger.warning("Uniform: " + string
				// + " was not found in shader.");
			}
		}
	}
}
