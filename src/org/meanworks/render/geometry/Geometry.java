package org.meanworks.render.geometry;

import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Renderable;
import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.scene.Node;
import org.meanworks.render.geometry.mesh.Mesh;
import org.meanworks.render.material.Material;
import org.meanworks.render.opengl.shader.ShaderProgram;

public class Geometry extends Node implements Renderable {

	/*
	 * The meshes for this geometry
	 */
	private HashMap<String, Mesh> meshes = new HashMap<String, Mesh>();

	/*
	 * The material for the geometry object if none is specified it will default
	 * to Material.DEFAULT_MATERIAL
	 */
	private Material material;

	/**
	 * Constructor for Geometry
	 */
	public Geometry() {
		material = Material.DEFAULT_MATERIAL;
	}

	/**
	 * Clear this geometry
	 */
	public void clear() {
		for (Mesh mesh : meshes.values()) {
			mesh.getMeshRenderer().clear();
		}
		meshes.clear();
	}

	/**
	 * Set the material of this geometry object
	 * 
	 * @param material
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	/**
	 * Get the material of this geometry object
	 * 
	 * @return
	 */
	public Material getMaterial() {
		return material;
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
	 * Get the meshes of this geometry
	 * 
	 * @return
	 */
	public Collection<Mesh> getMeshes() {
		return meshes.values();
	}

	/**
	 * 
	 */
	public Geometry instance() {
		Geometry geometry = new Geometry();
		geometry.setMaterial(getMaterial());
		for (String key : meshes.keySet()) {
			geometry.addMesh(key, meshes.get(key));
		}
		return geometry;
	}

	/**
	 * Render this geometry
	 * 
	 * @param camera
	 *            The camera instance so the material can get to know the
	 *            uniform variables
	 */
	@Override
	public void render() {
		if (material != null) {

			getMaterial().setProperty("vAmbientColor",
					new Vector4f(0.5f, 0.5f, 0.5f, 1.0f));
			getMaterial().setProperty("vDiffuseColor",
					new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
			getMaterial().setProperty("fSpecularIntensity", 30.0f);
			getMaterial().setProperty("tColorMap", 0);

			/*
			 * Update viewing matrices
			 */
			material.setProperty("mProjectionView", Application
					.getApplication().getCamera().getProjectionViewMatrix());
			material.setProperty("mModelMatrix", getTransformMatrix());

			/*
			 * Apply material
			 */
			material.apply();
		}
		for (Mesh mesh : meshes.values()) {
			mesh.render(material);
		}
		if (material != null) {
			// TODO: Add better handling here
			ShaderProgram.bindNone();
		}
	}
}
