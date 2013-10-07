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
import org.meanworks.render.geometry.mesh.MeshBuffer;
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
	private MeshBuffer buffer;

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

	public Vector4f getTileTransition(int _x, int _y) {
		Vector4f tileTransitions = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
		Tile thisTile = world.getTile(_x, _y);
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

	public void buildTerrain(int lodLevel) {
	}

	/**
	 * Build the terrain
	 */
	public void buildTerrain() {
		// Recompile
		int numTiles = REGION_WIDTH * REGION_HEIGHT;
		int FLOATS_IN_VEC = 3;
		buffer = new MeshBuffer((numTiles * 4 * FLOATS_IN_VEC * 2)
				+ (numTiles * 4 * FLOATS_IN_VEC) + (numTiles * 4 * 4),
				(numTiles * 6));

		/*
		 * Build new vertices
		 */
		int index = 0;
		for (int x = 0; x < REGION_WIDTH; x++) {
			for (int y = 0; y < REGION_HEIGHT; y++) {

				int _x = regionX * REGION_WIDTH + x;
				int _y = regionY * REGION_HEIGHT + y;

				float p1H = (float) world.getWorldGen().getHeight(_x + 1, _y);
				float p2H = tiles[x][y].getTileHeight();
				float p3H = (float) world.getWorldGen().getHeight(_x, _y + 1);
				float p4H = (float) world.getWorldGen().getHeight(_x + 1,
						_y + 1);
				
				int myTexId = tiles[x][y].getTileType().getTextureIndex();
				float minTexX = 0.0f;
				float maxTexX = 1.0f;
				float minTexY = 0.0f;
				float maxTexY = 1.0f;

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

				Vector3f texp1 = new Vector3f(minTexX, maxTexY, myTexId);
				Vector3f texp2 = new Vector3f(maxTexX, maxTexY, myTexId);
				Vector3f texp3 = new Vector3f(maxTexX, minTexY, myTexId);
				Vector3f texp4 = new Vector3f(minTexX, minTexY, myTexId);

				Vector4f tileTransitions = getTileTransition(_x, _y);

				buffer.addVec3(p1.getPosition());
				buffer.addVec3(p1.getNormal());
				buffer.addVec3(texp1);
				buffer.addVec4(tileTransitions);

				index++;

				buffer.addVec3(p2.getPosition());
				buffer.addVec3(p2.getNormal());
				buffer.addVec3(texp2);
				buffer.addVec4(tileTransitions);

				index++;

				buffer.addVec3(p3.getPosition());
				buffer.addVec3(p3.getNormal());
				buffer.addVec3(texp3);
				buffer.addVec4(tileTransitions);

				index++;

				buffer.addVec3(p4.getPosition());
				buffer.addVec3(p4.getNormal());
				buffer.addVec3(texp4);
				buffer.addVec4(tileTransitions);

				index++;

				buffer.addIndex(index - 4);
				buffer.addIndex(index - 3);
				buffer.addIndex(index - 1);
				buffer.addIndex(index - 3);
				buffer.addIndex(index - 2);
				buffer.addIndex(index - 1);
			}
		}
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
				MeshRenderer meshRenderer = new MeshRenderer();

				regionMesh = new Mesh();
				meshRenderer.addIndex(buffer.getFlippedIntBuffer(),
						buffer.getNumIndices());

				VertexBuffer vbData = new VertexBuffer(BufferType.ARRAY_BUFFER,
						BufferUsage.STATIC_DRAW);

				vbData.bind();
				vbData.bufferData(buffer.getFlippedFloatBuffer());

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
