package org.meanworks.terrain;

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
			terrainMesh = new Mesh();
			// We need a generator, request one from the terrain
			TerrainMeshGenerator tmg = new TerrainMeshGenerator(terrainMesh,
					new TerrainHeightProvider() {

						private int cX;
						private int cY;
						private PerlinNoise pn;

						@Override
						public float getHeight(int x, int y) {
							if(pn == null) {
								this.cX = chunkX;
								this.cY = chunkZ;
								pn = new PerlinNoise();
								pn.setSeed(5555);
							}
							return (float)pn.getHeight((double)(cX * terrain.chunkResolution + x), (double)(cY * terrain.chunkResolution + y)) - 100f;
						}
					});
			tmg.setChunkSize(terrain.chunkResolution, terrain.chunkResolution);
			if (!tmg.generate()) {
				throw new RuntimeException("Could not compile terrain mesh["
						+ chunkX + ", " + chunkZ + "]");
			}
		} else {
			terrainMesh.render();
		}
	}

}
