package org.meanworks.terrain;

import org.lwjgl.util.vector.Matrix4f;
import org.meanworks.engine.RenderState;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.gui.GuiHandler;
import org.meanworks.engine.math.FrustumResult;
import org.meanworks.engine.math.Transform;
import org.meanworks.engine.math.Vec2i;
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
	private TerrainChunk[] chunks;

	/**
	 * The last update coordinates
	 */
	private Vec2i chunkOrigo;

	/**
	 * Construct a new Terrain
	 */
	public Terrain() {
		viewerPosition = new Vec3();
		viewDistance = 6; // 3 chunks in each direction
		chunkResolution = 32; // 16 cubic tiles for each chunk 16x16
		chunks = new TerrainChunk[(int) Math.pow(viewDistance * 2, 2)]; // Diameter^2
		chunkOrigo = new Vec2i(-viewDistance, -viewDistance);
	}
	
	public Vec2i getChunkOrigo() {
		return chunkOrigo;
	}

	/**
	 * Get the viewer position of this terrain
	 * 
	 * @return
	 */
	public Vec3 getViewerPosition() {
		return viewerPosition;
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
		// The bottom left corner of the chunks
		int chunk0X = chunkOrigo.x - viewDistance;
		int chunk0Z = chunkOrigo.y - viewDistance;

		// Check bounds
		if (chunkX < chunk0X || chunkZ < chunk0Z
				|| chunkX >= chunk0X + (viewDistance * 2)
				|| chunkZ >= chunk0Z + (viewDistance * 2) || chunkX < 0
				|| chunkZ < 0) {

			// If this chunk is out of bounds, return null,
			// Because that means it's not loaded
			return null;

		} else {
			// Return the ch0nk!
			return chunks[(chunkX - chunk0X)
					+ ((chunkZ - chunk0Z) * viewDistance * 2)];
		}
	}

	/**
	 * Check if any chunks needs updates
	 */
	public void checkForUpdates() {

		// Get the current viewer position
		int viewerChunkX = (int) (viewerPosition.x / chunkResolution);
		int viewerChunkZ = (int) (viewerPosition.z / chunkResolution);

		if (viewerChunkX != chunkOrigo.x || viewerChunkZ != chunkOrigo.y) {

			// We need a chunk update!

			// Pos 0 for the chunk cache
			int chunk0X = viewerChunkX - viewDistance;
			int chunk0Z = viewerChunkZ - viewDistance;

			// Populate the chunks
			TerrainChunk[] newChunkCache = new TerrainChunk[(int) Math.pow(
					viewDistance * 2, 2)];

			for (int x = chunk0X; x < chunk0X + (viewDistance * 2); x++) {
				for (int z = chunk0Z; z < chunk0Z + (viewDistance * 2); z++) {

					// can we fetch this chunk from the previous cache
					int arrPos = (x - chunk0X)
							+ ((z - chunk0Z) * viewDistance * 2);

					TerrainChunk chunk = getChunk(x, z);
					if (chunk != null) {
						newChunkCache[arrPos] = chunk;
					} else {
						// Create a new chunk
						newChunkCache[arrPos] = new TerrainChunk(x, z);
					}

				}
			}

			// Move arrays
			chunks = newChunkCache;

			// TODO: Release the graphics card data from the chunks no longer in
			// use

			System.err.println("Updated chunks.");
		}

		chunkOrigo.x = viewerChunkX;
		chunkOrigo.y = viewerChunkZ;

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
		 * Check for updates
		 */
		checkForUpdates();

		/*
		 * Update the render state with this model's transform
		 */
		RenderState.setProjectionViewMatrix(Scene.getCamera()
				.getProjectionViewMatrix());

		GuiHandler.drawString("ViewerPosition: " + viewerPosition.x + " / "
				+ viewerPosition.z, 10, 200);

		GuiHandler.drawString("ChunkPos( " + chunkOrigo.x + " / "
				+ chunkOrigo.y + " )", 10, 250);
		GuiHandler.drawString("View distance: " + viewDistance, 10, 275);

		int drawY = 275;

		// Update chunk cache to match accordingly
		int viewerChunkX = (int) (viewerPosition.x / chunkResolution);
		int viewerChunkZ = (int) (viewerPosition.z / chunkResolution);

		// Pos 0 for the chunk cache
		int chunk0X = viewerChunkX - viewDistance;
		int chunk0Z = viewerChunkZ - viewDistance;

		// Render all chunks
		for (int x = chunk0X; x < chunk0X + (viewDistance * 2); x++) {
			for (int z = chunk0Z; z < chunk0Z + (viewDistance * 2); z++) {
				// For now ignore max boundaries :D
				if (x >= 0 && z >= 0) {

					// Now we need a system to store meshes in
					TerrainChunk chunk = getChunk(x, z);

					// If the chunk is null? bugged? or could not load? Anywho,
					// we can't render it
					// Another process have to clean this up
					if (chunk == null) {
						continue;
					}

					// Set transformation
					RenderState.setTransformMatrix(Transform.fromXYZ(
							(x * chunkResolution), 0.0f, (z * chunkResolution))
							.getTransformMatrix());

					// Get the mesh from the chunk
					Mesh mesh = chunk.getMesh();
					if (mesh == null) {
						// Render without culling,
						// Since this process will create the chunk
						chunk.render(this);
						renderedChunks++;
					} else {
						// Do culling :p
						Matrix4f mat4 = new Matrix4f();
						mat4.m30 = (x * chunkResolution);
						mat4.m31 = 0.0f;
						mat4.m32 = (z * chunkResolution);

						FrustumResult result = Application
								.getApplication()
								.getCamera()
								.getFrustum()
								.cubeInFrustumTranslated(
										mesh.getAABoundingBox().getMin(),
										mesh.getAABoundingBox().getMax(), mat4);
						if (result == FrustumResult.INSIDE
								|| result == FrustumResult.PARTIALLY_INSIDE) {
							chunk.render(this);
							renderedChunks++;

							// Check if we are hovering any chunks for debugging
							// purposes
							if (mesh.castRay().didHit()) {

								//
								GuiHandler.drawString("Hit Chunk: " + x + " / "
										+ z, 10, (drawY += 25));

								int lod = (int) getViewerPosition().dist2D(
										x * chunkResolution,
										z * chunkResolution);

								int lodLevel = chunk.getLOD(lod, this);

								GuiHandler.drawString("Chunk LOD: " + lodLevel, 10, (drawY += 25));
								
							}

						}
					}

				}

			}
		}

		GuiHandler.drawString("ChunksRendered: " + renderedChunks, 10, 225);

	}
}
