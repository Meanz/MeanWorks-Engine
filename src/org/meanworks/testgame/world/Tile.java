package org.meanworks.testgame.world;

import org.lwjgl.util.vector.Vector2f;

public class Tile {

	/*
	 * The tile type for this tile
	 */
	private TileType tileType;
	/*
	 * The height of the tile
	 */
	private float tileHeight;
	/*
	 * A vector describing the position of the tile
	 */
	private Vector2f tilePosition;

	/**
	 * Construct a new tile
	 * 
	 * @param tileType
	 * @param tileHeights
	 */
	public Tile(Vector2f tilePosition, TileType tileType, float tileHeight) {
		this.tilePosition = tilePosition;
		this.tileType = tileType;
		this.tileHeight = tileHeight;
	}

	/**
	 * Get the vector representation of this tile's position
	 * 
	 * @return The vector of this tile's position
	 */
	public Vector2f getTilePosition() {
		return tilePosition;
	}

	/**
	 * Get the tile height of this tile
	 * 
	 * @return The tile height of this tile
	 */
	public float getTileHeight() {
		return tileHeight;
	}

	/**
	 * Set the tile height for this tile
	 * 
	 * @param tileHeight
	 *            The new tile height for this tile
	 */
	public void setTileHeight(float tileHeight) {
		this.tileHeight = tileHeight;
	}

	/**
	 * Get the tile type of this tile
	 * 
	 * @return The tile type of this tile
	 */
	public TileType getTileType() {
		return tileType;
	}

	/**
	 * Set the tile type of this tile
	 * 
	 * @param tileType
	 *            The new tile type of this tile
	 */
	public void setTileType(TileType tileType) {
		this.tileType = tileType;
	}

}
