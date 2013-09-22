package org.fractalstudio.testgame.world;

public enum TileType {

	GRASS("Grass", 0),
	DIRT("Dirt", 6),
	ROCK("Rock", 2),
	MARSH("Marsh", 8),
	COBBLE("Cobblestone", 4),
	SLAB("Slab", 5),
	PACKED_DIRT("Packed Dirt", 7)
	;
	
	private String name;
	private int textureIndex;
	
	private TileType(String name, int textureIndex) {
		this.name = name;
		this.textureIndex = textureIndex;
	}
	
	public String getName() {
		return name;
	}
	
	public int getTextureIndex() {
		return textureIndex;
	}
	
}
