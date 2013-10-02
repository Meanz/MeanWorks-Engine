package org.meanworks.testgame.world;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.gui.impl.PerformanceGraph;
import org.meanworks.engine.math.VectorMath;
import org.meanworks.render.geometry.Geometry;
import org.meanworks.render.geometry.Vertex;
import org.meanworks.render.geometry.mesh.Mesh;
import org.meanworks.render.geometry.mesh.MeshRenderer;
import org.meanworks.render.geometry.mesh.MeshRenderer.BufferEntry;
import org.meanworks.render.opengl.VertexBuffer;
import org.meanworks.render.opengl.VertexBuffer.BufferType;
import org.meanworks.render.opengl.VertexBuffer.BufferUsage;

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
	public final static int REGION_WIDTH = 64;
	public final static int REGION_HEIGHT = 64;

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
	 * The data buffer
	 */
	private FloatBuffer dataBuffer;

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
	 * 
	 */
	private Mesh regionMesh;

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
	 * Calc the normal of a given point
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector3f calcNormal(int x, int y) {
		Vector3f p1 = new Vector3f((float) x, (float) world.getWorldGen()
				.getHeight(x, y), (float) y);
		Vector3f p2 = new Vector3f((float) x, (float) world.getWorldGen()
				.getHeight(x, y + 1), (float) y + 1);
		Vector3f p3 = new Vector3f((float) x + 1, (float) world.getWorldGen()
				.getHeight(x + 1, y + 1), (float) y + 1);
		Vector3f p4 = new Vector3f((float) x + 1, (float) world.getWorldGen()
				.getHeight(x + 1, y), (float) y);
		Vector3f p5 = new Vector3f((float) x, (float) world.getWorldGen()
				.getHeight(x, y - 1), (float) y - 1);
		Vector3f p6 = new Vector3f((float) x - 1, (float) world.getWorldGen()
				.getHeight(x - 1, y - 1), (float) y - 1);
		Vector3f p7 = new Vector3f((float) x - 1, (float) world.getWorldGen()
				.getHeight(x - 1, y), (float) y);

		Vector3f norm1 = VectorMath.calcTriNormal(p3, p4, p1);
		Vector3f norm2 = VectorMath.calcTriNormal(p1, p2, p3);
		Vector3f norm3 = VectorMath.calcTriNormal(p1, p7, p2);
		Vector3f norm4 = VectorMath.calcTriNormal(p1, p6, p6);
		Vector3f norm5 = VectorMath.calcTriNormal(p5, p6, p1);
		Vector3f norm6 = VectorMath.calcTriNormal(p5, p1, p4);
		Vector3f norm7 = VectorMath.calcTriNormal(p4, p1, p3);

		float endX = norm1.x + norm2.x + norm3.x + norm4.x + norm5.x + norm6.x
				+ norm7.x;
		float endY = norm1.y + norm2.y + norm3.y + norm4.y + norm5.y + norm6.y
				+ norm7.y;
		float endZ = norm1.z + norm2.z + norm3.z + norm4.z + norm5.z + norm6.z
				+ norm7.z;
		return new Vector3f(endX / 7.0f, endY / 7.0f, endZ / 7.0f);
	}

	/**
	 * Build the terrain
	 */
	public void buildTerrain() {
		// Recompile
		int numTris = REGION_WIDTH * REGION_HEIGHT * 2;
		int numVertices = numTris * 3;
		dataBuffer = BufferUtils.createFloatBuffer((numVertices * 2 * 3)
				+ (numVertices * 3) + (numVertices * 4));

		/*
		 * Build new vertices
		 */
		for (int x = 0; x < REGION_WIDTH; x++) {
			for (int y = 0; y < REGION_HEIGHT; y++) {

				int _x = regionX * REGION_WIDTH + x;
				int _y = regionY * REGION_HEIGHT + y;

				float p1H = (float) world.getWorldGen().getHeight(_x + 1, _y);
				float p2H = tiles[x][y].getTileHeight();
				float p3H = (float) world.getWorldGen().getHeight(_x, _y + 1);
				float p4H = (float) world.getWorldGen().getHeight(_x + 1,
						_y + 1);

				int numWTextures = 4;
				int numHTextures = 4;

				int myTexId = tiles[x][y].getTileType().getTextureIndex();

				float perWUnit = (1.0f / (float) numWTextures);
				float perHUnit = (1.0f / (float) numHTextures);
				float minTexX = perWUnit
						* (myTexId - ((myTexId / numWTextures) * numWTextures));
				float minTexY = perHUnit * (myTexId / numWTextures);
				float maxTexX = minTexX + perWUnit;
				float maxTexY = minTexY + perHUnit;

				minTexX = 0.0f;
				maxTexX = 1.0f;
				minTexY = 0.0f;
				maxTexY = 1.0f;

				// p4----p3
				// |_____|
				// |_____|
				// p1----p2
				Vertex p1 = new Vertex(new Vector3f((float) _x + TILE_WIDTH,
						p1H, (float) _y), calcNormal(_x + 1, _y), new Vector2f(
						minTexX, maxTexY));

				Vertex p2 = new Vertex(
						new Vector3f((float) _x, p2H, (float) _y), calcNormal(
								_x, _y), new Vector2f(maxTexX, maxTexY));

				Vertex p3 = new Vertex(new Vector3f((float) _x, p3H, (float) _y
						+ TILE_LENGTH), calcNormal(_x, _y + 1), new Vector2f(
						maxTexX, minTexY));

				Vertex p4 = new Vertex(new Vector3f((float) _x + TILE_WIDTH,
						p4H, (float) (float) _y + TILE_LENGTH), calcNormal(
						_x + 1, _y + 1), new Vector2f(minTexX, minTexY));

				float val = myTexId;

				Vector3f texp1 = new Vector3f(minTexX, maxTexY, val);
				Vector3f texp2 = new Vector3f(maxTexX, maxTexY, val);
				Vector3f texp3 = new Vector3f(maxTexX, minTexY, val);
				Vector3f texp4 = new Vector3f(minTexX, minTexY, val);

				Vector4f tileTransitions = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);

				// TRBL

				Tile thisTile = world.getTile(_x, _y);
				// Check for top transition
				Tile tile = world.getTile(_x, _y + 1);
				if (tile == null) {
					tileTransitions.x = 0f;
				} else {
					if (tile.getTileType() != thisTile.getTileType()) {
						tileTransitions.x = tile.getTileType()
								.getTextureIndex() + 1f;
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
						tileTransitions.y = tile.getTileType()
								.getTextureIndex() + 1f;
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
						tileTransitions.z = tile.getTileType()
								.getTextureIndex() + 1f;
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
						tileTransitions.w = tile.getTileType()
								.getTextureIndex() + 1f;
					} else {
						tileTransitions.w = 0f;
					}
				}

				dataBuffer.put(p1.getPosition().x).put(p1.getPosition().y)
						.put(p1.getPosition().z);
				dataBuffer.put(p1.getNormal().x).put(p1.getNormal().y)
						.put(p1.getNormal().z);
				dataBuffer.put(texp1.x).put(texp1.y).put(texp1.z);
				dataBuffer.put(tileTransitions.x).put(tileTransitions.y)
						.put(tileTransitions.z).put(tileTransitions.w);

				dataBuffer.put(p2.getPosition().x).put(p2.getPosition().y)
						.put(p2.getPosition().z);
				dataBuffer.put(p2.getNormal().x).put(p2.getNormal().y)
						.put(p2.getNormal().z);
				dataBuffer.put(texp2.x).put(texp2.y).put(texp2.z);
				dataBuffer.put(tileTransitions.x).put(tileTransitions.y)
						.put(tileTransitions.z).put(tileTransitions.w);

				dataBuffer.put(p4.getPosition().x).put(p4.getPosition().y)
						.put(p4.getPosition().z);
				dataBuffer.put(p4.getNormal().x).put(p4.getNormal().y)
						.put(p4.getNormal().z);
				dataBuffer.put(texp4.x).put(texp4.y).put(texp4.z);
				dataBuffer.put(tileTransitions.x).put(tileTransitions.y)
						.put(tileTransitions.z).put(tileTransitions.w);

				dataBuffer.put(p2.getPosition().x).put(p2.getPosition().y)
						.put(p2.getPosition().z);
				dataBuffer.put(p2.getNormal().x).put(p2.getNormal().y)
						.put(p2.getNormal().z);
				dataBuffer.put(texp2.x).put(texp2.y).put(texp2.z);
				dataBuffer.put(tileTransitions.x).put(tileTransitions.y)
						.put(tileTransitions.z).put(tileTransitions.w);

				dataBuffer.put(p3.getPosition().x).put(p3.getPosition().y)
						.put(p3.getPosition().z);
				dataBuffer.put(p3.getNormal().x).put(p3.getNormal().y)
						.put(p3.getNormal().z);
				dataBuffer.put(texp3.x).put(texp3.y).put(texp3.z);
				dataBuffer.put(tileTransitions.x).put(tileTransitions.y)
						.put(tileTransitions.z).put(tileTransitions.w);

				dataBuffer.put(p4.getPosition().x).put(p4.getPosition().y)
						.put(p4.getPosition().z);
				dataBuffer.put(p4.getNormal().x).put(p4.getNormal().y)
						.put(p4.getNormal().z);
				dataBuffer.put(texp4.x).put(texp4.y).put(texp4.z);
				dataBuffer.put(tileTransitions.x).put(tileTransitions.y)
						.put(tileTransitions.z).put(tileTransitions.w);

				// Triangle t1 = new Triangle(p1, p2, p4);
				// Triangle t2 = new Triangle(p2, p3, p4);
			}
		}
		dataBuffer.flip();
		built = true;
	}

	/**
	 * Update the region
	 */
	public void update() {
		if (needsUpdate()) {
			if (isBuilt()) {
				long time = System.nanoTime();
				if (regionGeometry == null) {
					regionGeometry = new Geometry();
					regionGeometry.setMaterial(null);
				}
				int numTris = REGION_WIDTH * REGION_HEIGHT * 2;
				int numVertices = numTris * 3;

				MeshRenderer meshRenderer = new MeshRenderer();

				regionMesh = new Mesh();
				meshRenderer.setNumVertices(numVertices);

				VertexBuffer vbData = new VertexBuffer(BufferType.ARRAY_BUFFER,
						BufferUsage.STATIC_DRAW);

				vbData.bind();
				vbData.bufferData(dataBuffer);

				// Some vars we need to know
				int stride = 13 * 4; // 13 floats
				BufferEntry entry = meshRenderer.addVertexBuffer(vbData);
				entry.addAttribute(0, 3, GL11.GL_FLOAT, false, stride, 0); // positions
				entry.addAttribute(1, 3, GL11.GL_FLOAT, false, stride, 12); // normals
				entry.addAttribute(2, 3, GL11.GL_FLOAT, false, stride, 24); // tex
																			// coords
				entry.addAttribute(3, 4, GL11.GL_FLOAT, false, stride, 36); // tile
																			// transition
																			// coords

				meshRenderer.compile();
				regionMesh.setMeshRenderer(meshRenderer);

				regionGeometry.addMesh("region_" + regionX + "_" + regionY,
						regionMesh);
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
