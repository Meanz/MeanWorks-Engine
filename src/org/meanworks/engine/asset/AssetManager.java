package org.meanworks.engine.asset;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.meanworks.engine.EngineConfig;
import org.meanworks.engine.EngineLogger;
import org.meanworks.render.opengl.shader.Shader;
import org.meanworks.render.opengl.shader.ShaderHelper;
import org.meanworks.render.opengl.shader.ShaderProgram;
import org.meanworks.render.opengl.shader.Shader.ShaderType;
import org.meanworks.render.texture.Texture;
import org.meanworks.render.texture.TextureLoader;

public class AssetManager implements AssetListener {

	/**
	 * Simple struct for a modified file
	 * 
	 * @author meanz
	 * 
	 */
	class ModifiedFile {

		String key;
		String path;

		ModifiedFile(String key, String path) {
			this.key = key;
			this.path = path;
		}
	}

	/*
	 * The asset watcher of this asset manager
	 */
	private AssetWatcher assetWatcher;

	/*
	 * The list of textures
	 */
	private HashMap<String, Texture> textures = new HashMap<>();

	/*
	 * The list of shaders
	 */
	private HashMap<String, ShaderProgram> shaders = new HashMap<>();

	/*
	 * The list of modified actions
	 */
	private LinkedList<ModifiedFile> modifiedFiles = new LinkedList<>();

	/**
	 * Construct a new AssetManager
	 */
	public AssetManager() {

		// Create our asset watcher
		assetWatcher = new AssetWatcher();
		if (EngineConfig.debug) {
			assetWatcher.addListener(this);
		}

	}

	/**
	 * Load a shader from the given path
	 * 
	 * @param shaderPath
	 * @return
	 */
	public ShaderProgram loadShader(String shaderPath) {

		ShaderProgram shaderProgram = shaders.get(shaderPath.toLowerCase());
		if (shaderProgram != null) {
			return shaderProgram;
		}

		String vertexShaderFile = shaderPath.toLowerCase() + ".vert";
		String fragmentShaderFile = shaderPath.toLowerCase() + ".frag";
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
			shaderProgram = new ShaderProgram();
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

			// Add the vertex shader file to our watch list
			assetWatcher.watchFile(
					"shader_vert_" + shaderProgram.getProgramId(),
					vertexShaderFile.toLowerCase());

			// Add the fragment shader file to our watch list
			assetWatcher.watchFile(
					"shader_frag_" + shaderProgram.getProgramId(),
					fragmentShaderFile.toLowerCase());

			// Add the shader program to our store list
			shaders.put(shaderPath.toLowerCase(), shaderProgram);

			return shaderProgram;
		} catch (IOException iex) {
			iex.printStackTrace();
		}
		return null;
	}

	/**
	 * Load a texture from the given path
	 * 
	 * @param path
	 * @return
	 */
	public Texture loadTexture(String path) {
		return loadTexture(path, false);
	}
	
	/**
	 * Load a texture from the given path
	 * 
	 * @param path The path to the texture
	 * @param mipMapping Whether to create mip maps or not
	 * @return
	 */
	public Texture loadTexture(String path, boolean mipMapping) {
		Texture outTexture = textures.get(path.toLowerCase());
		if (outTexture != null) {
			return outTexture;
		}
		outTexture = TextureLoader.loadTexture(path, mipMapping);
		if (outTexture == null) {
			EngineLogger.warning("Could not laod texture " + path);
			return null;
		}

		// Add the texture to our watch list
		assetWatcher.watchFile("tex_" + outTexture.getId(), path.toLowerCase());

		// Add the texture to our store list
		textures.put(path.toLowerCase(), outTexture);
		return outTexture;
	}
	

	/**
	 * Loads a texture from the given path without storing it for later use
	 * 
	 * @param path
	 * @return
	 */
	public Texture forceLoadTexture(String path) {
		return TextureLoader.loadTexture(path);
	}
	
	/**
	 * Loads a shader from the given path without storing it for later use
	 * 
	 * @param path
	 * @return
	 */
	public ShaderProgram forceLoadShaderProgram(String path) {
		return ShaderHelper.loadShader(path + ".vert", path + ".frag");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fractalstudio.engine.asset.AssetListener#fileModified(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public void fileModified(String key, String filePath) {
		synchronized (modifiedFiles) {
			modifiedFiles.add(new ModifiedFile(key, filePath));
		}
	}

	/**
	 * Called to update the asset manager
	 */
	public void update() {
		if (modifiedFiles.size() > 0) {
			for (ModifiedFile file : modifiedFiles) {
				String key = file.key;
				String filePath = file.path;

				EngineLogger.info("File[" + key + "] : " + filePath
						+ " Modified");

				if (key.startsWith("tex_")) {
					// A texture has been modified
					try {
						int texId = Integer.parseInt(key.substring(4));
						boolean found = false;
						for (Texture texture : textures.values()) {
							if (texture.getId() == texId) {

								// Reload the texture
								TextureLoader.loadTexture(texture, filePath);

								found = true;
								break;
							}
						}
						if (!found) {
							EngineLogger
									.warning("Could not find stored texture "
											+ key);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						EngineLogger
								.error("Could not load texture id from key "
										+ key + " [Unable to update texture]");
					}
				} else if (key.startsWith("shader_")) {

					try {
						int shaderProgramId = Integer.parseInt(key
								.substring(12));

						boolean found = false;
						for (ShaderProgram shaderProgram : shaders.values()) {
							if (shaderProgram.getProgramId() == shaderProgramId) {

								String shaderPath = key
										.startsWith("shader_vert") ? filePath
										.toLowerCase().split(".vert")[0]
										: filePath.toLowerCase().split(".frag")[0];
								String vertexShaderFile = shaderPath + ".vert";
								String fragmentShaderFile = shaderPath
										+ ".frag";

								/*
								 * Create vertex shader
								 */
								Shader vertexShader = new Shader(
										ShaderType.VERTEX_SHADER);
								if (!vertexShader.create()) {
									break;
								}
								vertexShader.readShaderData(new File(
										vertexShaderFile));

								/*
								 * Create fragment shader
								 */
								Shader fragmentShader = new Shader(
										ShaderType.FRAGMENT_SHADER);
								if (!fragmentShader.create()) {
									vertexShader.delete();
									break;
								}
								fragmentShader.readShaderData(new File(
										fragmentShaderFile));

								// If we have come this far let's go on to
								// fixing up the shader
								LinkedList<Shader> shaders = shaderProgram
										.detatchAll();

								shaderProgram.attach(vertexShader);
								shaderProgram.attach(fragmentShader);

								if (!shaderProgram.compile()) {
									vertexShader.delete();
									fragmentShader.delete();
									shaderProgram.detatchAll();
									for (Shader shader : shaders) {
										shaderProgram.attach(shader);
									}
									if (!shaderProgram.compile()) {
										EngineLogger
												.error("Something odd happened here, what now ?");
									}
								} else {
									// Delete the old shaders
									for (Shader shader : shaders) {
										shader.delete();
									}
								}
								found = true;
								break;
							}
						}
						if (!found) {
							EngineLogger
									.warning("Could not find stored shader "
											+ key);
						}

					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			}
			modifiedFiles.clear();
		}
	}
}
