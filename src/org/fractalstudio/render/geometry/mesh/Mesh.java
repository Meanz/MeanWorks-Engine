package org.fractalstudio.render.geometry.mesh;

import java.nio.FloatBuffer;

import org.fractalstudio.render.material.Material;

public class Mesh {
	
	private FloatBuffer positions;
	private FloatBuffer normals;
	private FloatBuffer texCoords;
	
	/*
	 * The renderer for this mesh
	 */
	private MeshRenderer meshRenderer;

	/*
	 * The material for the mesh if none is specified it will default to
	 * Material.DEFAULT_MATERIAL
	 */
	private Material material;

	/**
	 * Constructor
	 */
	public Mesh() {
		material = Material.DEFAULT_MATERIAL;
	}

	/**
	 * Set the material of this mesh
	 * 
	 * @param material
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	/**
	 * Get the material of this mesh
	 * 
	 * @return
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Set the mesh renderer for this mesh
	 * 
	 * @param meshRenderer
	 */
	public void setMeshRenderer(MeshRenderer meshRenderer) {
		this.meshRenderer = meshRenderer;
	}

	/**
	 * Get the mesh renderer for this mesh
	 * 
	 * @return
	 */
	public MeshRenderer getMeshRenderer() {
		return meshRenderer;
	}

	/**
	 * Render this mesh
	 */
	public void render() {

		if (meshRenderer != null) {
			//Apply the material
			if(material != null) {
				material.apply();
			}
			// Dispatch the render call to the mesh renderer
			meshRenderer.render();
		}

	}
}
