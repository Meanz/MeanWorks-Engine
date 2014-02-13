package org.meanworks.terrain;

import org.meanworks.engine.asset.AssetManager;
import org.meanworks.engine.math.Vec2;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.render.geometry.Mesh;
import org.meanworks.engine.render.material.Material;

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
public class TerrainMeshGenerator {

	/**
	 * The mesh to put the data in
	 */
	private Mesh mesh;

	/**
	 * The height provider for this generator
	 */
	private TerrainHeightProvider heightProvider;

	/**
	 * Whether to compile the mesh after generation or not
	 */
	private boolean compileAfterGen;

	/**
	 * The number of tiles in the X direction
	 */
	private int chunkSizeX = 16;

	/**
	 * The number of tiles in the Y (Z) direction
	 */
	private int chunkSizeY = 16;

	/**
	 * Construct a new TerrainMeshGenerator
	 * 
	 * @param store
	 * @param heightProvider
	 */
	public TerrainMeshGenerator(Mesh store,
			TerrainHeightProvider heightProvider, boolean compileAfterGen) {
		this.mesh = store;
		this.heightProvider = heightProvider;
		this.compileAfterGen = compileAfterGen;
	}

	/**
	 * Construct a new TerrainMeshGenerator
	 * 
	 * @param store
	 * @param heightProvider
	 */
	public TerrainMeshGenerator(Mesh store, TerrainHeightProvider heightProvider) {
		this(store, heightProvider, true);
	}

	/**
	 * Set the tile count in the X,Y directions
	 * 
	 * @param x
	 * @param y
	 */
	public void setChunkSize(int x, int y) {
		this.chunkSizeX = x;
		this.chunkSizeY = y;
	}

	/**
	 * 
	 * @return
	 */
	public Mesh getMesh() {
		return mesh;
	}

	/**
	 * Generates the mesh
	 * 
	 * @return
	 */
	public boolean generate() {

		if (mesh == null || heightProvider == null) {
			return false;
		}

		// Create float mesh buffer
		FloatMeshBuffer fmb = new FloatMeshBuffer((chunkSizeX * chunkSizeY * 4 * 6)
				+ (chunkSizeX * chunkSizeY * 2 * 4), chunkSizeX * chunkSizeY * 6);

		// Create vertices
		int idx = 0;
		for (int x = 0; x < chunkSizeX; x++) {
			for (int z = 0; z < chunkSizeY; z++) {

				Vec3 normP1 = calcNormal(heightProvider, x + 1, z);
				Vec3 normP2 = calcNormal(heightProvider, x, z);
				Vec3 normP3 = calcNormal(heightProvider, x, z + 1);
				Vec3 normP4 = calcNormal(heightProvider, x + 1, z + 1);

				float minTexX = 0.0f;
				float maxTexX = (float) 1.0f;
				float minTexY = 0.0f;
				float maxTexY = (float) 1.0f;

				Vec2 texp1 = new Vec2(minTexX, maxTexY);
				Vec2 texp2 = new Vec2(maxTexX, maxTexY);
				Vec2 texp3 = new Vec2(maxTexX, minTexY);
				Vec2 texp4 = new Vec2(minTexX, minTexY);

				fmb.addPosition(x + 1, heightProvider.getHeight(x + 1, z), z);
				fmb.addNormal(normP1);
				fmb.addUV(texp1);

				fmb.addPosition(x, heightProvider.getHeight(x, z), z);
				fmb.addNormal(normP2);
				fmb.addUV(texp2);

				fmb.addPosition(x, heightProvider.getHeight(x, z + 1), z + 1);
				fmb.addNormal(normP3);
				fmb.addUV(texp3);

				fmb.addPosition(x + 1, heightProvider.getHeight(x + 1, z + 1),
						z + 1);
				fmb.addNormal(normP4);
				fmb.addUV(texp4);

				// Add indicices
				// 1 - 2 - 4
				// 2 - 3- 4

				fmb.addIndex(idx);
				fmb.addIndex(idx + 1);
				fmb.addIndex(idx + 3);
				fmb.addIndex(idx + 1);
				fmb.addIndex(idx + 2);
				fmb.addIndex(idx + 3);

				/*
				 * fmb.addIndex(idx + 3); fmb.addIndex(idx); fmb.addIndex(idx +
				 * 2); fmb.addIndex(idx + 1); fmb.addIndex(idx + 2);
				 * fmb.addIndex(idx);
				 */

				idx += 4;
			}
		}

		mesh.positions = fmb.positions;
		mesh.normals = fmb.normals;
		mesh.uvs = fmb.uvs;
		mesh.triangles = fmb.triangles;

		mesh.setMaterial(new Material("terrainMat", AssetManager
				.loadShader("./data/shaders/wnrTerrain")));
		mesh.getMaterial().setTexture(
				AssetManager.loadTexture("./data/images/terrain/grass.jpg"));

		if (compileAfterGen) {
			return mesh.compile();
		} else {
			return true;
		}
	}

	/**
	 * Calc the normal of the given 3 points forming a triangle
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return
	 */
	public static Vec3 calcTriNormal(Vec3 p1, Vec3 p2, Vec3 p3) {
		return Vec3.cross(Vec3.sub(p3, p1), Vec3.sub(p2, p1));
	}

	/**
	 * Calc the normal of a given point
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Vec3 calcNormal(TerrainHeightProvider provider, int x, int y) {
		Vec3 p1 = new Vec3((float) x, (float) provider.getHeight(x, y),
				(float) y);
		Vec3 p2 = new Vec3((float) x, (float) provider.getHeight(x, y + 1),
				(float) y + 1);
		Vec3 p3 = new Vec3((float) x + 1, (float) provider.getHeight(x + 1,
				y + 1), (float) y + 1);
		Vec3 p4 = new Vec3((float) x + 1, (float) provider.getHeight(x + 1, y),
				(float) y);
		Vec3 p5 = new Vec3((float) x, (float) provider.getHeight(x, y - 1),
				(float) y - 1);
		Vec3 p6 = new Vec3((float) x - 1, (float) provider.getHeight(x - 1,
				y - 1), (float) y - 1);
		Vec3 p7 = new Vec3((float) x - 1, (float) provider.getHeight(x - 1, y),
				(float) y);

		Vec3 norm1 = calcTriNormal(p3, p4, p1);
		Vec3 norm2 = calcTriNormal(p1, p2, p3);
		Vec3 norm3 = calcTriNormal(p1, p7, p2);
		Vec3 norm4 = calcTriNormal(p1, p6, p6);
		Vec3 norm5 = calcTriNormal(p5, p6, p1);
		Vec3 norm6 = calcTriNormal(p5, p1, p4);
		Vec3 norm7 = calcTriNormal(p4, p1, p3);

		float endX = norm1.x + norm2.x + norm3.x + norm4.x + norm5.x + norm6.x
				+ norm7.x;
		float endY = norm1.y + norm2.y + norm3.y + norm4.y + norm5.y + norm6.y
				+ norm7.y;
		float endZ = norm1.z + norm2.z + norm3.z + norm4.z + norm5.z + norm6.z
				+ norm7.z;
		return new Vec3(endX / 7.0f, endY / 7.0f, endZ / 7.0f);
	}

}
