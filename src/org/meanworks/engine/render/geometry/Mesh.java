package org.meanworks.engine.render.geometry;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.RenderState;
import org.meanworks.engine.bounding.AABoundingBox;
import org.meanworks.engine.math.Ray;
import org.meanworks.engine.math.RayResult;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.render.geometry.mesh.renderers.ImmediateRenderer;
import org.meanworks.engine.render.geometry.mesh.renderers.MeshRenderer;
import org.meanworks.engine.render.geometry.mesh.renderers.VAOMeshRenderer;
import org.meanworks.engine.render.geometry.mesh.renderers.VAOMeshRenderer.BufferEntryType;
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
	 * The mesh data
	 */
	public float[] positions;
	public float[] normals;
	public float[] uvs;
	public float[] tangents;
	public float[] bitangents;
	public int[] triangles;

	/*
	 * The material for this mesh
	 */
	private Material meshMaterial;

	/*
	 * The texture for this mesh Temporary solution
	 */
	private Texture meshTexture;

	/*
	 * The axis aligned bounding box of this mesh
	 */
	private AABoundingBox aaBoundingBox;

	/**
	 * Constructor
	 */
	public Mesh() {
		setMaterial(null);
		setTexture(null);
	}

	/**
	 * Set the mesh renderer of this mesh
	 * 
	 * @param meshRenderer
	 */
	public void setMeshRenderer(VAOMeshRenderer meshRenderer) {
		this.meshRenderer = meshRenderer;
	}

	/**
	 * Delete this mesh
	 */
	public void delete() {
		meshRenderer.delete();
	}

	/**
	 * Cast a ray on this mesh
	 * 
	 * @param castPosition
	 * @param castDirection
	 */
	public RayResult castRay(Ray ray) {
		// Just search all tiles and look for an intersection hehe
		boolean didHit = false;
		Vec3 hitPoint = null;

		// We need to construct the geometry here
		if (ray != null) {
			if (aaBoundingBox != null) {
				if (AABoundingBox.intersects(aaBoundingBox, ray)) {
					// Perform geometry tests
				}
			}
		}
		return new RayResult(didHit, hitPoint);
	}

	/**
	 * Deep copy this mesh
	 * 
	 * @return
	 */
	public Mesh deepCopy() {
		Mesh newMesh = new Mesh();
		if (meshRenderer instanceof VAOMeshRenderer) {
			VAOMeshRenderer newMeshRenderer = ((VAOMeshRenderer) meshRenderer)
					.deepCopy();
			newMesh.meshRenderer = newMeshRenderer;
		}
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
	 * This function uploads the data to the graphics card
	 */
	public boolean compile() {

		/*
		 * Let's create an immediate renderer for giggles
		 */
		if (false) {
			meshRenderer = new ImmediateRenderer(this);
			return true;
		}

		/*
		 * Check if the mesh has any triangles at all
		 */
		if (triangles == null || triangles.length == 0) {
			EngineLogger.error("Tried to compile Mesh with no triangles.");
			return false;
		}

		// Create the mesh renderer
		VAOMeshRenderer renderer = new VAOMeshRenderer();
		meshRenderer = renderer;
		
		if (positions != null) {
			FloatBuffer fb = BufferUtils.createFloatBuffer(positions.length);
			fb.put(positions);
			fb.flip();
			renderer.addBuffer(fb, BufferEntryType.POSITION);
		}
		if (normals != null) {
			FloatBuffer fb = BufferUtils.createFloatBuffer(normals.length);
			fb.put(normals);
			fb.flip();
			renderer.addBuffer(fb, BufferEntryType.NORMAL);
		}
		if (uvs != null) {
			FloatBuffer fb = BufferUtils.createFloatBuffer(uvs.length);
			fb.put(uvs);
			fb.flip();
			renderer.addBuffer(fb, BufferEntryType.UV);
		}

		IntBuffer ib = BufferUtils.createIntBuffer(triangles.length);
		ib.put(triangles);
		ib.flip();
		renderer.addIndex(ib, triangles.length);
		
		return renderer.compile();
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
