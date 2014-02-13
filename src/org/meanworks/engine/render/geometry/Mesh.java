package org.meanworks.engine.render.geometry;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.math.BigDecimal;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.RenderState;
import org.meanworks.engine.bounding.AABoundingBox;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.math.Ray;
import org.meanworks.engine.math.RayResult;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.math.VectorMath;
import org.meanworks.engine.render.geometry.mesh.renderers.ImmediateRenderer;
import org.meanworks.engine.render.geometry.mesh.renderers.MeshRenderer;
import org.meanworks.engine.render.geometry.mesh.renderers.VAOMeshRenderer;
import org.meanworks.engine.render.geometry.mesh.renderers.VAOMeshRenderer.BufferEntryType;
import org.meanworks.engine.render.material.Material;
import org.meanworks.engine.render.opengl.GLImmediate;
import org.meanworks.engine.render.opengl.shader.ShaderProgram;
import org.meanworks.engine.scene.Scene;

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
	 * The axis aligned bounding box of this mesh
	 */
	private AABoundingBox aaBoundingBox;

	/**
	 * Constructor
	 */
	public Mesh() {
		setMaterial(null);
	}

	/**
	 * Get the bounding box for this mesh
	 * 
	 * @return
	 */
	public AABoundingBox getAABoundingBox() {
		return aaBoundingBox;
	}

	/**
	 * Calculate the bounding box of this Mesh
	 */
	public void calculateBoundingBox() {

		Vec3 min = null;
		Vec3 max = null;

		for (int i = 0; i < positions.length / 3; i++) {

			float x = positions[i * 3];
			float y = positions[i * 3 + 1];
			float z = positions[i * 3 + 2];

			if (min == null || max == null) {

				min = new Vec3(x, y, z);
				max = new Vec3(x, y, z);

				continue;
			}

			if (x < min.x) {
				min.x = x;
			}
			if (y < min.y) {
				min.y = y;
			}
			if (z < min.z) {
				min.z = z;
			}

			if (x > max.x) {
				max.x = x;
			}
			if (y > max.y) {
				max.y = y;
			}
			if (z > max.z) {
				max.z = z;
			}
		}

		aaBoundingBox = new AABoundingBox(min, max);

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
		Vec3 hitPoint = new Vec3();

		// We need to construct the geometry here
		if (ray != null) {
			// if (aaBoundingBox != null) {

			// if (AABoundingBox.intersects(aaBoundingBox, ray)) {
			// Perform geometry tests
			if (triangles != null) {

				for (int i = 0; i < triangles.length; i += 3) {
					// Construct triangles

					//
					int idx = triangles[i] * 3;

					Vec3 p1 = new Vec3(positions[idx], positions[idx + 1],
							positions[idx + 2]);

					//
					idx = triangles[i + 1] * 3;

					Vec3 p2 = new Vec3(positions[idx], positions[idx + 1],
							positions[idx + 2]);

					//
					idx = triangles[i + 2] * 3;

					Vec3 p3 = new Vec3(positions[idx], positions[idx + 1],
							positions[idx + 2]);

					Matrix4f thisTransform = RenderState.getTransformMatrix();
					if (thisTransform != null) {
						p1.translate(thisTransform);
						p2.translate(thisTransform);
						p3.translate(thisTransform);
					}

					Matrix4f cameraTransform = Scene.getCamera()
							.getModelMatrix();

					p1.translateN(cameraTransform);
					p2.translateN(cameraTransform);
					p3.translateN(cameraTransform);

					Vec3 hitPos = VectorMath
							.intersectsTriangle(ray, p1, p2, p3);
					if (hitPos != null) {
						didHit = true;
						hitPoint.x = ray.origin.x
								+ (ray.direction.x * hitPos.z);
						hitPoint.y = ray.origin.y
								+ (ray.direction.y * hitPos.z);
						hitPoint.z = ray.origin.z
								+ (ray.direction.z * hitPos.z);
						break;
					}

				}

			}
			// }
			// }
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

		calculateBoundingBox();

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
	 * Round to certain number of decimals
	 * 
	 * @param d
	 * @param decimalPlace
	 * @return
	 */
	public static float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

	/**
	 * 
	 */
	public void renderBoundingBox() {
		if (aaBoundingBox == null) {
			System.err.println("AABB null");
			return;
		}

		glDisable(GL11.GL_BLEND);
		glDisable(GL11.GL_ALPHA_TEST);
		glDisable(GL_TEXTURE_2D);
		glColor3f(1.0f, 1.0f, 1.0f);

		// Unbind any possible shaders
		RenderState.clearState();

		glPushMatrix();
		{

			// Get the pick ray
			Ray pickRay = Application.getApplication().getCamera()
					.getPickRay(Mouse.getX(), Mouse.getY());

			RayResult rr = castRay(pickRay);

			if (rr.didHit()) {
				glColor3f(1.0f, 0.0f, 0.0f);

				Vec3 hitPoint = new Vec3(pickRay.origin.x
						+ (pickRay.direction.x + rr.hitPoint.z),
						pickRay.origin.y
								+ (pickRay.direction.y + rr.hitPoint.z),
						pickRay.origin.y
								+ (pickRay.direction.z + rr.hitPoint.z)

				);

				GLImmediate.drawPlane(rr.hitPoint.x - 0.05f,
						rr.hitPoint.y - 0.05f, rr.hitPoint.z - 0.05f, 0.1f,
						0.1f);
			}

			// Translate
			Matrix4f mat = RenderState.getTransformMatrix();
			if (mat != null) {
				GL11.glTranslatef(mat.m30, mat.m31, mat.m32);
			}
			Vec3 min = aaBoundingBox.getMin();
			Vec3 max = aaBoundingBox.getMax();

			GLImmediate.lineBox(min, max);
		}
		glPopMatrix();
	}

	/**
	 * Render this mesh
	 */
	public void render() {

		// Immediate render the bounding box!!
		renderBoundingBox();

		if (meshMaterial != null && meshRenderer != null) {
			/*
			 * Apply material
			 */
			getMaterial().apply();

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
