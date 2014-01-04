package org.meanworks.engine.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.meanworks.engine.EngineConfig;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.asset.model.ModelLoader;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.fx.ShaderParseException;
import org.meanworks.engine.fx.ShaderParser;
import org.meanworks.engine.render.geometry.Model;
import org.meanworks.engine.render.material.Material;
import org.meanworks.engine.render.opengl.shader.ShaderHelper;
import org.meanworks.engine.render.opengl.shader.ShaderProgram;
import org.meanworks.engine.render.texture.Texture;
import org.meanworks.engine.render.texture.TextureLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Copyright (C) 2013 Steffen Evensen
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Meanz
 */
public class AssetManager implements AssetListener {

	/**
	 * Simple struct for a modified file
	 * 
	 * @author meanz
	 * 
	 */
	private class ModifiedFile {

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

	/*
	 * The list of materials
	 */
	private HashMap<String, Material> materials = new HashMap<>();

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
	 * Internal function for ease
	 * 
	 * @return
	 */
	private static AssetManager getAssetManager() {
		return Application.getApplication().getAssetManager();
	}

	/**
	 * Load a model into the engine, for now it's just a redirection function
	 * 
	 * @param modelPath
	 * @return
	 */
	public static Model loadModel(String modelPath) {
		return ModelLoader.loadModel(modelPath);
	}

	/**
	 * Load a material from the given path
	 * 
	 * @param materialPath
	 * @return
	 */
	public static Material loadMaterial(String materialPath) {

		/*
		 * Read the material XML
		 */
		try {
			File xmlFile = new File(materialPath);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			System.out.println("Root element :"
					+ doc.getDocumentElement().getNodeName());

			/*
			 * Load vertex shaders
			 */
			NodeList vertList = doc.getElementsByTagName("vert");
			for (int temp = 0; temp < vertList.getLength(); temp++) {
				Node vertNode = vertList.item(temp);
				if (vertNode.getNodeType() == Node.ELEMENT_NODE) {
					Element vertElement = (Element) vertNode;
					NodeList shaderList = vertElement
							.getElementsByTagName("shader");
					for (int j = 0; j < shaderList.getLength(); j++) {
						Node shaderNode = shaderList.item(j);
						if (shaderNode.getNodeType() == Node.ELEMENT_NODE) {
							if (shaderNode.getNodeName().equals("shader")) {
								Element shaderElement = (Element) shaderNode;

								String shaderSrc = shaderElement
										.getAttribute("src");

							} else {
								EngineLogger
										.warning("\tMaterial Loader: Expected \"shader\" tag got \""
												+ shaderNode.getNodeName()
												+ "\"");
							}
						}

					}
				}
			}

			/*
			 * Load fragment shaders
			 */
			NodeList fragList = doc.getElementsByTagName("frag");
			for (int i = 0; i < fragList.getLength(); i++) {
				Node fragNode = fragList.item(i);
				if (fragNode.getNodeType() == Node.ELEMENT_NODE) {
					Element vertElement = (Element) fragNode;
					NodeList shaderList = vertElement
							.getElementsByTagName("shader");
					for (int j = 0; j < shaderList.getLength(); j++) {
						Node shaderNode = shaderList.item(j);
						if (shaderNode.getNodeType() == Node.ELEMENT_NODE) {
							if (shaderNode.getNodeName().equals("shader")) {
								Element shaderElement = (Element) shaderNode;

								String shaderSrc = shaderElement
										.getAttribute("src");

							} else {
								EngineLogger
										.warning("\tMaterial Loader: Expected \"shader\" tag got \""
												+ shaderNode.getNodeName()
												+ "\"");
							}
						}

					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			EngineLogger.warning("Could not load material \"" + materialPath
					+ "\"");
		}
		return null;
	}

	/**
	 * Load a shader from the given path
	 * 
	 * @param shaderPath
	 * @return
	 */
	public static ShaderProgram loadShader(String shaderPath) {

		// Don't know how much I like this but okay
		ShaderProgram shaderProgram = getAssetManager().shaders.get(shaderPath.toLowerCase());
		if (shaderProgram != null) {
			return shaderProgram;
		}

		String shaderFile = shaderPath + ".shader";

		shaderProgram = new ShaderProgram();
		if (!shaderProgram.create()) {
			return null;
		}

		try {
			ShaderParser.parseShader(shaderProgram, "./data/shaders/",
					new FileInputStream(new File(shaderFile)));
		} catch (IOException | ShaderParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// Delete the program
			shaderProgram.delete();
			return null;
		}

		// Add the vertex shader file to our watch list
		getAssetManager().assetWatcher.watchFile("shader_" + shaderProgram.getProgramId(),
				shaderFile.toLowerCase());
		// Add the shader program to our store list
		getAssetManager().shaders.put(shaderPath.toLowerCase(), shaderProgram);

		return shaderProgram;
	}

	/**
	 * Load a texture from the given path
	 * 
	 * @param path
	 * @return
	 */
	public static Texture loadTexture(String path) {
		return loadTexture(path, false);
	}

	/**
	 * Load a texture from the given path
	 * 
	 * @param path
	 *            The path to the texture
	 * @param mipMapping
	 *            Whether to create mip maps or not
	 * @return
	 */
	public static Texture loadTexture(String path, boolean mipMapping) {
		Texture outTexture = getAssetManager().textures.get(path.toLowerCase());
		if (outTexture != null) {
			return outTexture;
		}
		outTexture = TextureLoader.loadTexture(path, mipMapping);
		if (outTexture == null) {
			EngineLogger.warning("Could not laod texture " + path);
			return null;
		}

		// Add the texture to our watch list
		getAssetManager().assetWatcher.watchFile("tex_" + outTexture.getId(), path.toLowerCase());

		// Add the texture to our store list
		getAssetManager().textures.put(path.toLowerCase(), outTexture);
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
						int shaderProgramId = Integer
								.parseInt(key.substring(7));

						ShaderProgram program = null;
						for (ShaderProgram shaderProgram : shaders.values()) {
							if (shaderProgram.getProgramId() == shaderProgramId) {
								program = shaderProgram;
								break;
							}
						}
						if (program == null) {
							EngineLogger
									.warning("Could not find stored shader "
											+ key);
						} else {
							ShaderParser.parseShader(program,
									"./data/shaders/", new FileInputStream(
											new File(filePath)));
							EngineLogger.info("Updated shader " + key);
						}
					} catch (NumberFormatException | IOException
							| ShaderParseException e) {
						e.printStackTrace();
					}
				}
			}
			modifiedFiles.clear();
		}
	}
}
