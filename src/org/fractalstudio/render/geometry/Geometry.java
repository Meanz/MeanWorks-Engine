package org.fractalstudio.render.geometry;

import java.util.HashMap;

import org.fractalstudio.engine.Camera;
import org.fractalstudio.render.material.Material;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Renderable;

public class Geometry implements Renderable {

	/*
	 * The shader program that handles this geometry object
	 */
	private Material material = null;

	/*
	 * The meshes for this geometry
	 */
	private HashMap<String, Mesh> meshes = new HashMap<String, Mesh>();

	/**
	 * Constructor for Geometry
	 */
	public Geometry() {

	}

	/**
	 * Dull function, we haven't really finished mesh things yet
	 * 
	 * @param Material
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	/**
	 * Add a mesh to this geometry
	 * 
	 * @param name
	 * @param mesh
	 */
	public void addMesh(String name, Mesh mesh) {
		meshes.put(name, mesh);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Mesh getMesh(String name) {
		return meshes.get(name);
	}

	/**
	 * Render this geometry
	 * 
	 * @param camera
	 *            The camera instance so the material can get to know the
	 *            uniform variables
	 */
	public void render() {
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		if (material != null) {
			material.apply();
		}
		for (Mesh mesh : meshes.values()) {
			mesh.prepareMesh(); // Updates the mesh if needed
			mesh.render();
		}
	}

}
