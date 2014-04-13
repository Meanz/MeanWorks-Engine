package org.meanworks.terrain;

import org.meanworks.engine.math.Vec2i;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.math.VectorMath;
import org.meanworks.engine.render.geometry.Mesh;
import org.meanworks.engine.util.PerlinNoise;

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
public class TerrainChunk {

	/**
	 * The mesh for this terrain chunk
	 */
	private Mesh terrainMesh;

	/**
	 * The coordinates for this chunk
	 */
	private int chunkX, chunkZ;

	/**
	 * The current level of lod for this chunk
	 */
	private int lod;

	/**
	 * 
	 * @param chunkX
	 * @param chunkZ
	 */
	public TerrainChunk(int chunkX, int chunkZ) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	/**
	 * Get the terrain mesh
	 * 
	 * @return
	 */
	public Mesh getMesh() {
		return terrainMesh;
	}

	/**
	 * 
	 * @return
	 */
	public int getChunkX() {
		return chunkX;
	}

	/**
	 * 
	 * @return
	 */
	public int getChunkZ() {
		return chunkZ;
	}

	/**
	 * Render this chunk
	 * 
	 * @param terrain
	 *            The terrain that owns this chunk
	 */
	public void render(final Terrain terrain) {
		if (terrainMesh == null) {
			generate(terrain);
		} else {

			// Chunk origo
			Vec2i origo = terrain.getChunkOrigo();

			// Get lod levels
			int lodLevel = getLOD(origo.dist(chunkX, chunkZ), terrain);

			if (lodLevel != lod) {
				generate(terrain);
			}

			terrainMesh.render();
		}
	}

	/**
	 * Generates the chunk
	 * 
	 * @param terrain
	 */
	private void generate(final Terrain terrain) {
		if (terrainMesh != null) {
			terrainMesh.delete();
		}
		terrainMesh = new Mesh();
		// We need a generator, request one from the terrain
		TerrainMeshGenerator tmg = new TerrainMeshGenerator(this, terrainMesh,
				new TerrainHeightProvider() {

					private int cX;
					private int cY;
					private PerlinNoise pn;

					@Override
					public float getHeight(int x, int y) {
						if (pn == null) {
							this.cX = chunkX;
							this.cY = chunkZ;
							pn = new PerlinNoise();
							pn.setSeed(777777);
							pn.amplitude = 20;
						}
						float height = (float) pn.getHeight((double) (cX
								* terrain.chunkResolution + x), (double) (cY
								* terrain.chunkResolution + y));

						return (float) pn.clamp(height, 0f, 32f);
					}

					@Override
					public float getInterpolatedHeight(float x, float y,
							int unitSize) {
						int intX = (int) x - unitSize;
						int intY = (int) y - unitSize;
						Vec3 p1 = new Vec3(intX, getHeight(intX, intY), intY);
						Vec3 p2 = new Vec3(intX + unitSize, getHeight(intX
								+ unitSize, intY), intY);
						Vec3 p3 = new Vec3(intX + unitSize, getHeight(intX
								+ unitSize, intY + unitSize), intY + unitSize);
						Vec3 p4 = new Vec3(intX, getHeight(intX, intY
								+ unitSize), intY + unitSize);
						return VectorMath.getInterpolatedQuadHeight(x, y, p1,
								p2, p3, p4);
					}
				});
		tmg.setChunkSize(terrain.chunkResolution, terrain.chunkResolution);

		// Chunk origo
		Vec2i origo = terrain.getChunkOrigo();

		// Get lod levels
		int lodLevel = getLOD(origo.dist(chunkX, chunkZ), terrain);
		ChunkInfo ci = new ChunkInfo();
		ci.right = getLOD(origo.dist(chunkX - 1, chunkZ), terrain);
		ci.top = getLOD(origo.dist(chunkX, chunkZ + 1), terrain);
		ci.bottom = getLOD(origo.dist(chunkX, chunkZ - 1), terrain);
		ci.left = getLOD(origo.dist(chunkX + 1, chunkZ), terrain);

		lod = lodLevel;

		if (!tmg.generate(ci, lodLevel)) {
			throw new RuntimeException("Could not compile terrain mesh["
					+ chunkX + ", " + chunkZ + "]");
		}
	}

	/**
	 * Get the lod for the given distance
	 * 
	 * @param dist
	 * @param terrain
	 * @return
	 */
	public int getLOD(double dist, Terrain terrain) {
		int lodLevel = 1;
		if (dist > 2) {
			lodLevel = 2;
		}
		if (dist > 4) {
			lodLevel = 4;
		}
		if (dist > 6) {
			lodLevel = 8;
		}
		if (lodLevel > terrain.getMaxLod()) {
			lodLevel = terrain.getMaxLod();
		}
		return lodLevel;
	}

}
