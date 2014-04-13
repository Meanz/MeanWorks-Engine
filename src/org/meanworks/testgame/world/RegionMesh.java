package org.meanworks.testgame.world;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.gui.impl.PerformanceGraph;
import org.meanworks.engine.math.Vec2;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.math.Vertex;
import org.meanworks.engine.render.geometry.Mesh;
import org.meanworks.engine.render.geometry.Model;
import org.meanworks.engine.render.geometry.mesh.MeshBuffer;
import org.meanworks.engine.render.geometry.mesh.renderers.VAOMeshRenderer;
import org.meanworks.engine.render.geometry.mesh.renderers.VAOMeshRenderer.BufferEntry;
import org.meanworks.engine.render.opengl.GLVertexBuffer;
import org.meanworks.engine.render.opengl.GLVertexBuffer.BufferType;
import org.meanworks.engine.render.opengl.GLVertexBuffer.BufferUsage;

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
 * @author Meanz
 */
public class RegionMesh {

	/*
	 * Reference to the region that owns this region mesh
	 */
	private final Region region;

	/*
	 * The reference to the world
	 */
	private final World world;

	/*
	 * 
	 */
	private Model regionGeometry;

	/*
	 * The data buffer
	 */
	private MeshBuffer meshBuffer;
	/*
	 * 
	 */
	private MeshBuffer grassBuffer;

	/*
	 * Flagged whenever the region's height data has changed
	 */
	private boolean needsUpdate;

	/*
	 * Flagged when the geometry is finished processing
	 */
	private boolean built;

	/**
	 * Construct a new region mesh
	 * 
	 * @param region
	 * @param world
	 */
	public RegionMesh(Region region, World world) {
		this.region = region;
		this.world = world;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isBuilt() {
		return built;
	}

	/**
	 * Flag this region as needing an update
	 */
	public void flagUpdate() {
		this.needsUpdate = true;
		this.built = false;
	}

	/**
	 * Check whether this region needs an update or not
	 * 
	 * @return Whether this region needs an update or not
	 */
	public boolean needsUpdate() {
		return needsUpdate;
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
	public Vec3 calcNormal(int x, int y) {
		Vec3 p1 = new Vec3((float) x, (float) world.getWorldGen().getHeight(x,
				y), (float) y);
		Vec3 p2 = new Vec3((float) x, (float) world.getWorldGen().getHeight(x,
				y + 1), (float) y + 1);
		Vec3 p3 = new Vec3((float) x + 1, (float) world.getWorldGen()
				.getHeight(x + 1, y + 1), (float) y + 1);
		Vec3 p4 = new Vec3((float) x + 1, (float) world.getWorldGen()
				.getHeight(x + 1, y), (float) y);
		Vec3 p5 = new Vec3((float) x, (float) world.getWorldGen().getHeight(x,
				y - 1), (float) y - 1);
		Vec3 p6 = new Vec3((float) x - 1, (float) world.getWorldGen()
				.getHeight(x - 1, y - 1), (float) y - 1);
		Vec3 p7 = new Vec3((float) x - 1, (float) world.getWorldGen()
				.getHeight(x - 1, y), (float) y);

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

	/**
	 * Get the tile transition values for the given tile coordinates
	 * 
	 * @param _x
	 * @param _y
	 * @return
	 */
	public Vector4f getTileTransition(int _x, int _y) {
		Vector4f tileTransitions = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
		Tile thisTile = world.getTile(_x, _y);
		if (thisTile == null) {
			// System.err.println("This tile = null");
			return new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
		}
		// Check for top transition
		Tile tile = world.getTile(_x, _y + 1);
		if (tile == null) {
			tileTransitions.x = 0f;
		} else {
			if (tile.getTileType() != thisTile.getTileType()) {
				tileTransitions.x = tile.getTileType().getTextureIndex() + 1f;
			} else {
				tileTransitions.x = 0f;
			}
		}
		// Check for right transition
		tile = world.getTile(_x - 1, _y);
		if (tile == null) {
			tileTransitions.y = 0f;
		} else {
			if (tile.getTileType() != thisTile.getTileType()) {
				tileTransitions.y = tile.getTileType().getTextureIndex() + 1f;
			} else {
				tileTransitions.y = 0f;
			}
		}
		// Check for bottom transition
		tile = world.getTile(_x, _y - 1);
		if (tile == null) {
			tileTransitions.z = 0f;
		} else {
			if (tile.getTileType() != thisTile.getTileType()) {
				tileTransitions.z = tile.getTileType().getTextureIndex() + 1f;
			} else {
				tileTransitions.z = 0f;
			}
		}
		// Check for left transition
		tile = world.getTile(_x + 1, _y);
		if (tile == null) {
			tileTransitions.w = 0f;
		} else {
			if (tile.getTileType() != thisTile.getTileType()) {
				tileTransitions.w = tile.getTileType().getTextureIndex() + 1f;
			} else {
				tileTransitions.w = 0f;
			}
		}
		return tileTransitions;
	}

	/**
	 * Add a grass to the grass buffer
	 * 
	 * @param index
	 * @param in1
	 * @param in2
	 * @param height
	 */
	public void addGrass(int index, Vec3 in1, Vec3 in2, float height) {

		Vec3 p1 = new Vec3(in1.x, in1.y, in1.z);
		Vec3 p2 = new Vec3(in2.x, in2.y, in2.z);
		Vec3 p3 = new Vec3(in2.x, in2.y + height, in2.z);
		Vec3 p4 = new Vec3(in1.x, in1.y + height, in1.z);

		float minTexX = 0f;
		float minTexY = 0f;
		float maxTexX = 0.5f;
		float maxTexY = 0.5f;

		int texType = (int) (Math.random() * 5d);
		Vec3 texp1 = new Vec3(minTexX, maxTexY, texType == 2 ? 10 : 8);
		Vec3 texp2 = new Vec3(maxTexX, maxTexY, texType == 2 ? 10 : 8);
		Vec3 texp3 = new Vec3(maxTexX, minTexY, texType == 2 ? 10 : 8);
		Vec3 texp4 = new Vec3(minTexX, minTexY, texType == 2 ? 10 : 8);

		grassBuffer.addVec3(p1);
		grassBuffer.addVec3(texp1);
		index++;
		grassBuffer.addVec3(p2);
		grassBuffer.addVec3(texp2);
		index++;
		grassBuffer.addVec3(p3);
		grassBuffer.addVec3(texp3);
		index++;
		grassBuffer.addVec3(p4);
		grassBuffer.addVec3(texp4);
		index++;

		// p1 p2 p4
		grassBuffer.addIndex(index - 4);
		grassBuffer.addIndex(index - 3);
		grassBuffer.addIndex(index - 1);
		// p4 p2 p3
		grassBuffer.addIndex(index - 1);
		grassBuffer.addIndex(index - 3);
		grassBuffer.addIndex(index - 2);

		// p4 p2 p1
		grassBuffer.addIndex(index - 1);
		grassBuffer.addIndex(index - 3);
		grassBuffer.addIndex(index - 4);

		// p3 p2 p4
		grassBuffer.addIndex(index - 2);
		grassBuffer.addIndex(index - 3);
		grassBuffer.addIndex(index - 1);
	}

	/**
	 * Build the grass of this region mesh
	 */
	public void buildGrass() {
		if (region.getLodLevel() != 1) {
			return;
		}
		/* We need to iterate EVERY tile */
		int numGrassTiles = 0;
		for (int x = 0; x < Region.REGION_WIDTH; x++) {
			for (int y = 0; y < Region.REGION_HEIGHT; y++) {
				if (region.getTile(x, y).getTileType() == TileType.GRASS) {
					numGrassTiles++;
				}
			}
		}

		int grassDensity = 16;

		grassBuffer = new MeshBuffer(
				numGrassTiles * (grassDensity * 4) * 6 * 4, numGrassTiles
						* (grassDensity * 12));

		int index = 0;
		float grassHeight = 2.0f;
		for (int x = 0; x < Region.REGION_WIDTH; x++) {
			for (int y = 0; y < Region.REGION_HEIGHT; y++) {
				if (region.getTile(x, y).getTileType() == TileType.GRASS) {

					int _x = region.getRegionX() * Region.REGION_WIDTH + x;
					int _y = region.getRegionY() * Region.REGION_HEIGHT + y;

					int lodOffset = 1;
					float p1H = (float) world.getWorldGen().getHeight(
							_x + lodOffset, _y);
					float p2H = region.getTile(x, y).getTileHeight();
					float p3H = (float) world.getWorldGen().getHeight(_x,
							_y + lodOffset);
					float p4H = (float) world.getWorldGen().getHeight(
							_x + lodOffset, _y + lodOffset);

					Random random = new Random(region.getRegionX()
							* region.getRegionY() + _x * _y);

					// Add all four vertices

					// First point x, lowestHeight, z
					// Second point x, highestHeight, z
					for (int j = 0; j < grassDensity; j++) {
						float r1 = random.nextFloat() + 0.1f;
						float r2 = random.nextFloat() + 0.1f;
						float r3 = random.nextFloat() + 0.1f;
						float r4 = random.nextFloat() + 0.1f;
						float x1 = _x + r1;
						float z1 = _y + r2;
						float x2 = _x + r3 + r1;
						float z2 = _y + r4 + r2;

						float height1 = world.getInterpolatedHeight(x1, z1);
						float height2 = world.getInterpolatedHeight(x2, z2);

						addGrass(index, new Vec3(x1, height1, z1), new Vec3(x2,
								height2, z2),
								1.5f + (random.nextFloat() * 0.2f));
						index += 4;
					}
				}
			}
		}
	}

	/**
	 * Build the terrain of this region mesh
	 */
	public void buildTerrain() {
		if (region.getLodLevel() == 0 || region.getLodLevel() < 0) {
			System.err.println("region.getLodLevel() CANT BE ZERO OR LESS");
			return;
		}
		int numTiles = (Region.REGION_WIDTH * Region.REGION_HEIGHT)
				/ region.getLodLevel();
		int FLOATS_IN_VEC = 3;
		meshBuffer = new MeshBuffer((numTiles * 4 * FLOATS_IN_VEC * 2)
				+ (numTiles * 4 * FLOATS_IN_VEC) + (numTiles * 4 * 4),
				(numTiles * 6));
		int index = 0;
		for (int x = 0; x < Region.REGION_WIDTH; x += region.getLodLevel()) {
			for (int y = 0; y < Region.REGION_HEIGHT; y += region.getLodLevel()) {

				// First is 0 next is 2
				// The only thing we know for certain here is the origo point
				// Derive the other points from the lod int

				int _x = region.getRegionX() * Region.REGION_WIDTH + x;
				int _y = region.getRegionY() * Region.REGION_HEIGHT + y;

				int lodOffset = (region.getLodLevel());

				float p1H = (float) world.getWorldGen().getHeight(
						_x + lodOffset, _y);
				float p2H = region.getTile(x, y).getTileHeight();
				float p3H = (float) world.getWorldGen().getHeight(_x,
						_y + lodOffset);
				float p4H = (float) world.getWorldGen().getHeight(
						_x + lodOffset, _y + lodOffset);

				int myTexId = region.getTile(x, y).getTileType()
						.getTextureIndex();
				float minTexX = 0.0f;
				float maxTexX = (float) 1.0f;
				float minTexY = 0.0f;
				float maxTexY = (float) 1.0f;

				Vertex p1 = new Vertex(new Vec3((float) _x + Region.TILE_WIDTH
						* (lodOffset), p1H, (float) _y), calcNormal(_x
						+ lodOffset, _y), new Vec2(minTexX, maxTexY));

				Vertex p2 = new Vertex(new Vec3((float) _x, p2H, (float) _y),
						calcNormal(_x, _y), new Vec2(maxTexX, maxTexY));

				Vertex p3 = new Vertex(new Vec3((float) _x, p3H, (float) _y
						+ Region.TILE_LENGTH * (lodOffset)), calcNormal(_x, _y
						+ lodOffset), new Vec2(maxTexX, minTexY));

				Vertex p4 = new Vertex(new Vec3((float) _x + Region.TILE_WIDTH
						* (lodOffset), p4H, (float) (float) _y
						+ Region.TILE_LENGTH * (lodOffset)), calcNormal(_x
						+ lodOffset, _y + lodOffset), new Vec2(minTexX,
						minTexY));

				Vec3 texp1 = new Vec3(minTexX, maxTexY, myTexId);
				Vec3 texp2 = new Vec3(maxTexX, maxTexY, myTexId);
				Vec3 texp3 = new Vec3(maxTexX, minTexY, myTexId);
				Vec3 texp4 = new Vec3(minTexX, minTexY, myTexId);

				// Ignore this for now
				Vector4f tileTransitions = getTileTransition(_x, _y);

				meshBuffer.addVec3(p1.getPosition());
				meshBuffer.addVec3(p1.getNormal());
				meshBuffer.addVec3(texp1);
				meshBuffer.addVec4(tileTransitions);

				index++;

				meshBuffer.addVec3(p2.getPosition());
				meshBuffer.addVec3(p2.getNormal());
				meshBuffer.addVec3(texp2);
				meshBuffer.addVec4(tileTransitions);

				index++;

				meshBuffer.addVec3(p3.getPosition());
				meshBuffer.addVec3(p3.getNormal());
				meshBuffer.addVec3(texp3);
				meshBuffer.addVec4(tileTransitions);

				index++;

				meshBuffer.addVec3(p4.getPosition());
				meshBuffer.addVec3(p4.getNormal());
				meshBuffer.addVec3(texp4);
				meshBuffer.addVec4(tileTransitions);

				index++;

				meshBuffer.addIndex(index - 4);
				meshBuffer.addIndex(index - 3);
				meshBuffer.addIndex(index - 1);
				meshBuffer.addIndex(index - 3);
				meshBuffer.addIndex(index - 2);
				meshBuffer.addIndex(index - 1);
			}
		}
		buildGrass();
		built = true;
	}

	/**
	 * Buffer the data of this region mesh
	 */
	public void bufferData() {
		if (regionGeometry == null) {
			regionGeometry = new Model();
		}
		regionGeometry.clearMeshes();

		/*
		 * Setup the terrain mesh
		 */
		VAOMeshRenderer meshRenderer = new VAOMeshRenderer();
		Mesh regionMesh = new Mesh();

		/*
		 * Upload terrain mesh
		 */
		meshRenderer.addIndex(meshBuffer.getFlippedIntBuffer(),
				meshBuffer.getNumIndices());
		GLVertexBuffer vbData = new GLVertexBuffer(BufferType.ARRAY_BUFFER,
				BufferUsage.STATIC_DRAW);
		vbData.bind();
		vbData.bufferData(meshBuffer.getFlippedFloatBuffer());
		int stride = 13 * 4; // 13 floats
		BufferEntry entry = meshRenderer.addVertexBuffer(vbData);
		entry.addAttribute(0, 3, GL11.GL_FLOAT, false, stride, 0); // positions
		entry.addAttribute(1, 3, GL11.GL_FLOAT, false, stride, 12); // normals
		entry.addAttribute(2, 3, GL11.GL_FLOAT, false, stride, 24); // texcoords
		entry.addAttribute(3, 4, GL11.GL_FLOAT, false, stride, 36); // tile
																	// transition
																	// coords
		meshRenderer.compile();
		regionMesh.setMeshRenderer(meshRenderer);
		regionGeometry.addMesh(
				"region_" + region.getRegionX() + "_" + region.getRegionY(),
				regionMesh);

		/*
		 * Setup grass mesh
		 */
		if (region.getLodLevel() == 1) {
			/*
			 * meshRenderer = new MeshRenderer(); Mesh grassMesh = new Mesh();
			 * 
			 * meshRenderer.addIndex(grassBuffer.getFlippedIntBuffer(),
			 * grassBuffer.getNumIndices());
			 * 
			 * vbData = new VertexBuffer(BufferType.ARRAY_BUFFER,
			 * BufferUsage.STATIC_DRAW);
			 * 
			 * vbData.bind();
			 * vbData.bufferData(grassBuffer.getFlippedFloatBuffer());
			 * 
			 * stride = 6 * 4; // 3 floats so far entry =
			 * meshRenderer.addVertexBuffer(vbData); entry.addAttribute(0, 3,
			 * GL11.GL_FLOAT, false, stride, 0); entry.addAttribute(2, 3,
			 * GL11.GL_FLOAT, false, stride, 12);
			 * 
			 * meshRenderer.compile();
			 * 
			 * grassMesh.setMeshRenderer(meshRenderer);
			 * regionGeometry.addMesh("region_grass_" + region.getRegionX() +
			 * "_" + region.getRegionY(), grassMesh);
			 */
		}
	}

	/**
	 * Update this region mesh
	 */
	public void update() {
		if (needsUpdate()) {
			if (isBuilt()) {
				long time = System.nanoTime();
				bufferData();
				needsUpdate = false;
				time = System.nanoTime() - time;
				PerformanceGraph.tick(0, (int) time);
			}
		}
	}

	/**
	 * Render this region mesh
	 */
	public void render() {
		if (regionGeometry != null) {
			regionGeometry.render();
		}
	}
}
