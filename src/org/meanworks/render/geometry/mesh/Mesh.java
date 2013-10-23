package org.meanworks.render.geometry.mesh;

import org.meanworks.render.material.Material;
import org.meanworks.render.texture.Texture;

public class Mesh {

	/*
	 * The renderer for this mesh
	 */
	private MeshRenderer meshRenderer;

	/*
	 * The texture for this mesh Temporary solution
	 */
	private Texture meshTexture;

	/**
	 * Constructor
	 */
	public Mesh() {
	}

	/**
	 * Deep copy this mesh
	 * 
	 * @return
	 */
	public Mesh deepCopy() {
		Mesh newMesh = new Mesh();
		MeshRenderer newMeshRenderer = meshRenderer.deepCopy();
		newMesh.setMeshRenderer(newMeshRenderer);
		newMesh.setMeshTexture(meshTexture);
		return newMesh;
	}

	/**
	 * Set the texture of this mesh
	 * 
	 * @param texture
	 */
	public void setMeshTexture(Texture texture) {
		this.meshTexture = texture;
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
	public void render(Material material) {
		if (material != null) {
			if (meshTexture != null) {
				material.setProperty("tColorMap", 0);
				meshTexture.bind();
			}
			// We can apply custom material properties here
		}
		if (meshRenderer != null) {
			// Dispatch the render call to the mesh renderer
			meshRenderer.render();
		}
	}
}
