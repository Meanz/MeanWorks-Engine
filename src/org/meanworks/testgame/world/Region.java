package org.meanworks.testgame.world;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.gui.impl.PerformanceGraph;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.scene.Geometry;
import org.meanworks.engine.scene.GeometryNode;
import org.meanworks.engine.scene.Mesh;
import org.meanworks.render.geometry.Vertex;
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
public class Region extends GeometryNode {

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
	private int lodLevel = 1;
	/*
	 * 
	 */
	private RegionMesh regionMesh;

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

		regionMesh = new RegionMesh(this, world);
		regionMesh.flagUpdate();

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
	 * Get the mesh of this region
	 * 
	 * @return
	 */
	public RegionMesh getRegionMesh() {
		return regionMesh;
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
			if (regionMesh != null) {
				regionMesh.flagUpdate(); // Flag the region for update
			}
		}
	}

	/**
	 * Update the region
	 */
	public void update() {
		if (regionMesh != null) {
			regionMesh.update();
		}
	}

	/**
	 * Render this region
	 */
	public void render() {
		if (regionMesh != null) {
			regionMesh.render();
		}
	}
}
