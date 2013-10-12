package org.meanworks.testgame.world;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.gui.impl.PerformanceGraph;
import org.meanworks.engine.math.Vec3;
import org.meanworks.render.geometry.Geometry;
import org.meanworks.render.geometry.Vertex;
import org.meanworks.render.geometry.mesh.Mesh;
import org.meanworks.render.geometry.mesh.MeshBuffer;
import org.meanworks.render.geometry.mesh.MeshRenderer;
import org.meanworks.render.geometry.mesh.MeshRenderer.BufferEntry;
import org.meanworks.render.opengl.VertexBuffer;
import org.meanworks.render.opengl.VertexBuffer.BufferType;
import org.meanworks.render.opengl.VertexBuffer.BufferUsage;
import org.meanworks.testgame.TestGame;

/**
 * A class that defines a region in the world
 * 
 * @author meanz
 * 
 */
public class Region {

	/*
	 * Static variables for defining region size
	 */
	public final static float TILE_WIDTH = 1.0f;
	public final static float TILE_LENGTH = 1.0f;
	public final static int REGION_WIDTH = 32;
	public final static int REGION_HEIGHT = 32;

	/*
	 * The array of tiles
	 */
	private Tile[][] tiles;

	/*
	 * Flagged whenever the region's height data has changed
	 */
	private boolean needsUpdate;

	/*
	 * Flagged when the geometry is finished processing
	 */
	private boolean built;

	/*
	 * The world reference
	 */
	private World world;

	/*
	 * The x coordinate of the region
	 */
	private int regionX;
	/*
	 * The y coordinate of the region
	 */
	private int regionY;
	/*
	 * 
	 */
	private Geometry regionGeometry;

	/*
	 * The data buffer
	 */
	private MeshBuffer meshBuffer;
	/*
	 * 
	 */
	private Mesh regionMesh;
	/*
	 * 
	 */
	private MeshBuffer grassBuffer;
	/*
	 * 
	 */
	private Mesh grassMesh;
	/*
	 * 
	 */
	private int lodLevel = 1;

	/**
	 * Construct a new region
	 * 
	 * @param regionX
	 * @param regionY
	 */
	public Region(World world, int regionX, int regionY) {
		this.world = world;
		this.regionX = regionX;
		this.regionY = regionY;

		needsUpdate = true;
		built = false;
		tiles = new Tile[REGION_WIDTH][REGION_HEIGHT];
		for (int x = 0; x < REGION_WIDTH; x++) {
			for (int y = 0; y < REGION_HEIGHT; y++) {

				float p1H = (float) world.getWorldGen()
						.getHeight(regionX * REGION_WIDTH + x,
								regionY * REGION_HEIGHT + y);
				TileType tileType = TileType.GRASS;
				if (p1H < 100.0f) {
					tileType = TileType.MARSH;
				}
				if (p1H > 150.0f) {
					tileType = TileType.ROCK;
				}
				if (p1H > 100.0f && p1H < 110.0f) {
					tileType = TileType.DIRT;
				}

				tiles[x][y] = new Tile(new Vector2f(regionX * REGION_WIDTH + x,
						regionY * REGION_HEIGHT + y), tileType, p1H);
			}
		}
	}

	/**
	 * Get the current lod level of this region
	 * 
	 * @return
	 */
	public int getLodLevel() {
		return lodLevel;
	}

	/**
	 * Set the lod level of this region
	 * 
	 * @param lodLevel
	 */
	public void setLodLevel(int lodLevel) {
		this.lodLevel = lodLevel;
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
	 * Get the x coordinate of this region
	 * 
	 * @return The x coordinate of this region
	 */
	public int getRegionX() {
		return regionX;
	}

	/**
	 * Get the y coordinate of this region
	 * 
	 * @return The y coordinate of this region
	 */
	public int getRegionY() {
		return regionY;
	}

	/**
	 * Get the tile at the given local coordinate
	 * 
	 * @param localX
	 * @param localY
	 * @return
	 */
	public Tile getTile(int localX, int localY) {
		if (localX >= 0 && localY >= 0 && localX < REGION_WIDTH
				&& localY < REGION_HEIGHT) {
			return tiles[localX][localY];
		}
		return null;
	}

	/**
	 * Set the tile type of the tile at the given local coordinates
	 * 
	 * @param localX
	 * @param localY
	 * @param tileType
	 */
	public void setTile(int localX, int localY, TileType tileType) {
		if (localX >= 0 && localY >= 0 && localX < REGION_WIDTH
				&& localY < REGION_HEIGHT) {
			tiles[localX][localY].setTileType(tileType);
			flagUpdate(); // Flag the region for update
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

	public void buildGrass() {
		if (lodLevel != 1) {
			return;
		}
		/* We need to iterate EVERY tile */
		int numGrassTiles = 0;
		for (int x = 0; x < REGION_WIDTH; x++) {
			for (int y = 0; y < REGION_HEIGHT; y++) {
				if (getTile(x, y).getTileType() == TileType.GRASS) {
					numGrassTiles++;
				}
			}
		}

		int grassDensity = 16;

		grassBuffer = new MeshBuffer(numGrassTiles * (grassDensity * 4) * 6
				* 4, numGrassTiles * (grassDensity * 12));

		int index = 0;
		float grassHeight = 2.0f;
		for (int x = 0; x < REGION_WIDTH; x++) {
			for (int y = 0; y < REGION_HEIGHT; y++) {
				if (getTile(x, y).getTileType() == TileType.GRASS) {

					int _x = regionX * REGION_WIDTH + x;
					int _y = regionY * REGION_HEIGHT + y;

					int lodOffset = 1;
					float p1H = (float) world.getWorldGen().getHeight(
							_x + lodOffset, _y);
					float p2H = tiles[x][y].getTileHeight();
					float p3H = (float) world.getWorldGen().getHeight(_x,
							_y + lodOffset);
					float p4H = (float) world.getWorldGen().getHeight(
							_x + lodOffset, _y + lodOffset);

					Random random = new Random(getRegionX() * getRegionY() + _x * _y);

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
								height2, z2), 1.5f + (random.nextFloat() * 0.2f));
						index += 4;
					}
				}
			}
		}
	}

	public void buildTerrain() {
		if (lodLevel == 0 || lodLevel < 0) {
			System.err.println("LODLEVEL CANT BE ZERO OR LESS");
			return;
		}
		int numTiles = (REGION_WIDTH * REGION_HEIGHT) / lodLevel;
		int FLOATS_IN_VEC = 3;
		meshBuffer = new MeshBuffer((numTiles * 4 * FLOATS_IN_VEC * 2)
				+ (numTiles * 4 * FLOATS_IN_VEC) + (numTiles * 4 * 4),
				(numTiles * 6));
		int index = 0;
		for (int x = 0; x < REGION_WIDTH; x += lodLevel) {
			for (int y = 0; y < REGION_HEIGHT; y += lodLevel) {

				// First is 0 next is 2
				// The only thing we know for certain here is the origo point
				// Derive the other points from the lod int

				int _x = regionX * REGION_WIDTH + x;
				int _y = regionY * REGION_HEIGHT + y;

				int lodOffset = (lodLevel);

				float p1H = (float) world.getWorldGen().getHeight(
						_x + lodOffset, _y);
				float p2H = tiles[x][y].getTileHeight();
				float p3H = (float) world.getWorldGen().getHeight(_x,
						_y + lodOffset);
				float p4H = (float) world.getWorldGen().getHeight(
						_x + lodOffset, _y + lodOffset);

				int myTexId = tiles[x][y].getTileType().getTextureIndex();
				float minTexX = 0.0f;
				float maxTexX = (float) 1.0f;
				float minTexY = 0.0f;
				float maxTexY = (float) 1.0f;

				Vertex p1 = new Vertex(new Vec3((float) _x + TILE_WIDTH
						* (lodOffset), p1H, (float) _y), calcNormal(_x
						+ lodOffset, _y), new Vector2f(minTexX, maxTexY));

				Vertex p2 = new Vertex(new Vec3((float) _x, p2H, (float) _y),
						calcNormal(_x, _y), new Vector2f(maxTexX, maxTexY));

				Vertex p3 = new Vertex(new Vec3((float) _x, p3H, (float) _y
						+ TILE_LENGTH * (lodOffset)), calcNormal(_x, _y
						+ lodOffset), new Vector2f(maxTexX, minTexY));

				Vertex p4 = new Vertex(new Vec3((float) _x + TILE_WIDTH
						* (lodOffset), p4H, (float) (float) _y + TILE_LENGTH
						* (lodOffset)), calcNormal(_x + lodOffset, _y
						+ lodOffset), new Vector2f(minTexX, minTexY));

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
	 * Update the region
	 */
	public void update() {
		if (Keyboard.isKeyDown(Keyboard.KEY_1) && built) {
			lodLevel = 1;
			TestGame.getWorld().updateRegion(this);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_2) && built) {
			lodLevel = 2;
			TestGame.getWorld().updateRegion(this);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_3) && built) {
			lodLevel = 4;
			TestGame.getWorld().updateRegion(this);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_4) && built) {
			lodLevel = 8;
			TestGame.getWorld().updateRegion(this);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_5) && built) {
			lodLevel = 16;
			TestGame.getWorld().updateRegion(this);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_6) && built) {
			lodLevel = 32;
			TestGame.getWorld().updateRegion(this);
		}
		if (needsUpdate()) {
			if (isBuilt()) {
				long time = System.nanoTime();
				if (regionGeometry == null) {
					regionGeometry = new Geometry();
					regionGeometry.setMaterial(null);
				}
				regionGeometry.clear();

				/*
				 * Setup the terrain mesh
				 */
				MeshRenderer meshRenderer = new MeshRenderer();
				regionMesh = new Mesh();
				meshRenderer.addIndex(meshBuffer.getFlippedIntBuffer(),
						meshBuffer.getNumIndices());

				VertexBuffer vbData = new VertexBuffer(BufferType.ARRAY_BUFFER,
						BufferUsage.STATIC_DRAW);

				vbData.bind();
				vbData.bufferData(meshBuffer.getFlippedFloatBuffer());

				// Some vars we need to know
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
				regionGeometry.addMesh("region_" + regionX + "_" + regionY,
						regionMesh);

				/*
				 * Setup grass mesh
				 */
				if (lodLevel == 1) {
					meshRenderer = new MeshRenderer();
					grassMesh = new Mesh();
					meshRenderer.addIndex(grassBuffer.getFlippedIntBuffer(),
							grassBuffer.getNumIndices());

					vbData = new VertexBuffer(BufferType.ARRAY_BUFFER,
							BufferUsage.STATIC_DRAW);

					vbData.bind();
					vbData.bufferData(grassBuffer.getFlippedFloatBuffer());

					stride = 6 * 4; // 3 floats so far
					entry = meshRenderer.addVertexBuffer(vbData);
					entry.addAttribute(0, 3, GL11.GL_FLOAT, false, stride, 0);
					entry.addAttribute(2, 3, GL11.GL_FLOAT, false, stride, 12);

					meshRenderer.compile();

					grassMesh.setMeshRenderer(meshRenderer);
					regionGeometry.addMesh("region_grass_" + regionX + "_"
							+ regionY, grassMesh);
				}
				needsUpdate = false;
				time = System.nanoTime() - time;
				PerformanceGraph.feedTick2((int) time);
			}
		}
	}

	/**
	 * Render this region
	 */
	public void render() {
		if (regionGeometry != null) {
			regionGeometry.render();
		}
	}
}
