package org.fractalstudio.render.geometry.mesh;

import org.fractalstudio.render.material.Material;

public class Mesh {

	/*
	 * The renderer for this mesh
	 */
	private MeshRenderer meshRenderer;
	
	/*
	 * The material for the mesh
	 * if none is specified it will default to Material.DEFAULT_MATERIAL
	 */
	private Material meshMaterial;
	
	/**
	 * Constructor
	 */
	public Mesh() {

	}
	
	/**
	 * 
	 */
	public void setMeshRenderer(MeshRenderer meshRenderer) {
		this.meshRenderer = meshRenderer;
	}
	
	/**
	 * Render this mesh
	 */
	public void render() {
		
		if(meshRenderer != null) {
			
			//Dispatch the render call to the mesh renderer
			meshRenderer.render();
		}
		
	}
}
