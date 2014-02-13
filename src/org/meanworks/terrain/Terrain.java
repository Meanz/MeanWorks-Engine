package org.meanworks.terrain;

import java.util.LinkedList;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.meanworks.engine.RenderState;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.gui.GuiHandler;
import org.meanworks.engine.math.FrustumResult;
import org.meanworks.engine.math.Transform;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.render.geometry.Mesh;
import org.meanworks.engine.scene.Node;
import org.meanworks.engine.scene.Scene;

/**
 * Copyright (C) 2014 Steffen Evensen
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
public class Terrain extends Node {

	/**
	 * The viewer position
	 */
	private Vec3 viewerPosition;

	/**
	 * View distance
	 */
	private int viewDistance;

	/**
	 * The chunk resolution, must be cubic
	 */
	public int chunkResolution;

	/**
	 * The number of rendered chunks
	 */
	private int renderedChunks;

	/**
	 * The chunk cache
	 */
	private LinkedList<TerrainChunk> chunkCache = new LinkedList<TerrainChunk>();

	/**
	 * 
	 */
	public Terrain() {
		viewerPosition = new Vec3();
		viewDistance = 6; // 3 chunks in each direction
		chunkResolution = 64; // 16 cubic tiles for each chunk 16x16
	}

	/**
	 * Set the viewer position
	 * 
	 * @param position
	 */
	public void setViewerPosition(Vec3 position) {
		this.viewerPosition = position;
	}

	/**
	 * Get the chunk at the given chunk coordinates
	 * 
	 * @param chunkX
	 * @param chunkZ
	 * @return
	 */
	private TerrainChunk getChunk(int chunkX, int chunkZ) {

		// Access the chunk cache
		for (TerrainChunk chunk : chunkCache) {
			if (chunk != null) {
				if (chunk.getChunkX() == chunkX && chunk.getChunkZ() == chunkZ) {
					return chunk;
				}
			}
		}

		// This means that no chunk was found, create a new one.
		TerrainChunk chunk = new TerrainChunk(chunkX, chunkZ);
		chunkCache.add(chunk);
		return chunk;
	}

	/**
	 * Renders the terrain
	 */
	public void render() {

		/*
		 * Reset render count
		 */
		renderedChunks = 0;

		/*
		 * Update the render state with this model's transform
		 */
		RenderState.setProjectionViewMatrix(Scene.getCamera()
				.getProjectionViewMatrix());

		GuiHandler.drawString("ViewerPosition: " + viewerPosition.x + " / "
				+ viewerPosition.z, 10, 200);

		for (int x = -viewDistance; x < viewDistance; x++) {
			for (int y = -viewDistance; y < viewDistance; y++) {

				// Calculate chunk
				int chunkX = (int) ((viewerPosition.x / chunkResolution) + x);
				int chunkZ = (int) ((viewerPosition.z / chunkResolution) + y);

				// For now ignore max boundaries :D
				if (chunkX >= 0 && chunkZ >= 0) {

					// Now we need a system to store meshes in
					TerrainChunk chunk = getChunk(chunkX, chunkZ);

					RenderState.setTransformMatrix(Transform.fromXYZ(
							(chunkX * chunkResolution), 0.0f,
							(chunkZ * chunkResolution)).getTransformMatrix());

					if (chunk == null) {
						continue;
					}
					
					Mesh mesh = chunk.getMesh();
					if(mesh == null) {
						chunk.render(this);
						renderedChunks++;
					} else {
						// Do culling :p
						Matrix4f mat4 = new Matrix4f();
						mat4.m30 = (chunkX * chunkResolution);
						mat4.m31 = 0.0f;
						mat4.m32 = (chunkZ * chunkResolution);
								
						
						FrustumResult result = Application
								.getApplication()
								.getCamera()
								.getFrustum()
								.cubeInFrustumTranslated(
										mesh.getAABoundingBox().getMin(),
										mesh.getAABoundingBox().getMax(),
										mat4
										);
						if (result == FrustumResult.INSIDE
								|| result == FrustumResult.PARTIALLY_INSIDE) {
							chunk.render(this);
							renderedChunks++;
						}
					}

				}

			}
		}

		GuiHandler.drawString("ChunksRendered: " + renderedChunks, 10, 225);

	}
}
