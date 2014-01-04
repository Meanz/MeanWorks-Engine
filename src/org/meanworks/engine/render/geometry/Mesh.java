package org.meanworks.engine.render.geometry;

import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.RenderState;
import org.meanworks.engine.render.geometry.mesh.renderers.MeshRenderer;
import org.meanworks.engine.render.material.Material;
import org.meanworks.engine.render.texture.Texture;

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
 * @author meanz
 * 
 *         The Mesh class describes a mesh and it's associated rendering values,
 *         among those values are texture and material.
 * 
 *         The Mesh is also responsible for describing how to render the data it
 *         contains
 */
public class Mesh {

	/*
	 * The renderer for this mesh
	 */
	private MeshRenderer meshRenderer;

	/*
	 * The material for this mesh
	 */
	private Material meshMaterial;

	/*
	 * The texture for this mesh Temporary solution
	 */
	private Texture meshTexture;

	/**
	 * Constructor
	 */
	public Mesh() {
		setMaterial(null);
		setTexture(null);
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
		newMesh.setTexture(meshTexture);
		newMesh.setMaterial(meshMaterial);
		return newMesh;
	}

	/**
	 * Get the material of this mesh
	 * 
	 * @return
	 */
	public Material getMaterial() {
		return meshMaterial;
	}

	/**
	 * Set the material of this mesh
	 * 
	 * @param material
	 */
	public void setMaterial(Material material) {
		if (material == null) {
			meshMaterial = Material.DEFAULT_MATERIAL;
		} else {
			meshMaterial = material;
		}
	}

	/**
	 * Set the texture of this mesh
	 * 
	 * @param texture
	 */
	public void setTexture(Texture texture) {
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
	public void render() {
		if (meshMaterial != null && meshRenderer != null) {

			/*
			 * TODO: Fix to suit the new material solution
			 */
			getMaterial().setProperty("vAmbientColor",
					new Vector4f(0.5f, 0.5f, 0.5f, 1.0f));
			getMaterial().setProperty("vDiffuseColor",
					new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
			getMaterial().setProperty("fSpecularIntensity", 30.0f);
			getMaterial().setProperty("tColorMap", 0);

			/*
			 * Apply material
			 */
			meshMaterial.apply();
			if (meshTexture != null) {
				meshMaterial.setProperty("tColorMap", 0);
				RenderState.activeTexture(0);
				meshTexture.bind();
			}
			// We can apply custom material properties here
			// Dispatch the render call to the mesh renderer
			meshRenderer.render(meshMaterial);
		} else {
			EngineLogger
					.warning("[Mesh].render() - "
							+ (meshMaterial == null ? " Material = null " : "")
							+ (meshRenderer == null ? (meshMaterial == null ? ", Mesh Renderer = null"
									: " Mesh Renderer = null")
									: ""));
		}
	}
}
