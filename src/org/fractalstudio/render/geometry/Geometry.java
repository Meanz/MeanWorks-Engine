package org.fractalstudio.render.geometry;

import java.util.HashMap;

import org.fractalstudio.engine.Camera;
import org.fractalstudio.render.geometry.mesh.Mesh;
import org.fractalstudio.render.material.Material;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Renderable;

public class Geometry implements Renderable {

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
		for (Mesh mesh : meshes.values()) {
			mesh.render();
		}
	}

}
