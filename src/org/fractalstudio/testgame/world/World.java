package org.fractalstudio.testgame.world;

import java.util.LinkedList;

import org.fractalstudio.engine.Application;
import org.fractalstudio.engine.EngineLogger;
import org.fractalstudio.engine.math.Ray;
import org.fractalstudio.engine.math.VectorMath;
import org.fractalstudio.render.opengl.shader.ShaderProgram;
import org.fractalstudio.render.texture.Texture;
import org.fractalstudio.render.texture.TextureArray;
import org.fractalstudio.render.texture.TextureLoader;
import org.lwjgl.util.vector.Vector3f;

public class World {

	public final static int VIEW_DISTANCE = 5;

	/*
	 * The world cursors position
	 */
	private int worldX;
	private int worldY;

	// to be able to
	// load further
	private LinkedList<Region> loadedRegions = new LinkedList<>();

	//
	private boolean isListUpdateNeeded = true;

	//
	private Region[] regionList;

	/*
	 * The atlas texture
	 */
	private TextureArray tileAtlas;

	/*
	 * The world gen for testing
	 */
	private WorldGen worldGen;

	/*
	 * The world loader
	 */
	private WorldLoader worldLoader;

	/*
	 * 
	 */
	private ShaderProgram regionShader;

	/*
	 * 
	 */
	private long accumTime = 0;

	/**
	 * Construct a new
	 */
	public World() {

		worldGen = new WorldGen();
		worldGen.setSeed(1337133713);
		worldLoader = new WorldLoader();

		/*
		 * Load the tile atlas
		 */
		tileAtlas = TextureLoader.loadTextureArray(new String[] {

				"./data/images/terrain/grass.jpg",
				"./data/images/terrain/cliff.jpg",
				"./data/images/terrain/rock.jpg",
				"./data/images/terrain/gravel.jpg",
				"./data/images/terrain/cobble.png",
				"./data/images/terrain/slab.jpg",
				"./data/images/terrain/dirt.jpg",
				"./data/images/terrain/packed-dirt.jpg"

		});// Application.getApplication().getAssetManager().loadTexture("./data/images/terrain/atlas.png",
			// true);

		if (tileAtlas == null) {
			System.err.println("Could not load tile atlas :(");
			return;
		}

		/*
		 * Load the region shader
		 */
		regionShader = Application.getApplication().getAssetManager()
				.loadShader(("./data/shaders/terrain"));

	}

	/**
	 * 
	 * @return
	 */
	public int getWorldX() {
		return worldX;
	}

	/**
	 * 
	 * @return
	 */
	public int getWorldY() {
		return worldY;
	}

	/**
	 * 
	 * @param regionX
	 * @param regionY
	 * @return
	 */
	public Region getRegion(int regionX, int regionY) {
		for (Region region : loadedRegions) {
			if (region.getRegionX() == regionX
					&& region.getRegionY() == regionY) {
				return region;
			}
		}
		return null;
	}

	/**
	 * Called to create a new region
	 * 
	 * @param regionX
	 * @param regionY
	 * @return
	 */
	public Region requestRegion(int regionX, int regionY) {
		Region region = new Region(this, regionX, regionY);
		worldLoader.addTask(region);
		loadedRegions.add(region);
		return region;
	}

	/**
	 * Get the interpolated height value from the given x and y positions
	 * 
	 * @param xPos
	 * @param zPos
	 * @return
	 */
	public float getInterpolatedHeight(float xPos, float zPos) {
		// we first get the height of four points of the quad underneath the
		// point
		// Check to make sure this point is not off the map at all
		float scaleFactor = 1.0f;
		int x = (int) (xPos / scaleFactor);
		int z = (int) (zPos / scaleFactor);

		int xPlusOne = x + 1;
		int zPlusOne = z + 1;

		float triZ0 = (getTileHeight(x, z));
		float triZ1 = (getTileHeight(x + 1, z));
		float triZ2 = (getTileHeight(x, z + 1));
		float triZ3 = (getTileHeight(x + 1, z + 1));

		float height = 0.0f;
		float sqX = (xPos / scaleFactor) - x;
		float sqZ = (zPos / scaleFactor) - z;
		if ((sqX + sqZ) < 1) {
			height = triZ0;
			height += (triZ1 - triZ0) * sqX;
			height += (triZ2 - triZ0) * sqZ;
		} else {
			height = triZ3;
			height += (triZ1 - triZ3) * (1.0f - sqZ);
			height += (triZ2 - triZ3) * (1.0f - sqX);
		}
		return height;
	}

	/**
	 * 
	 * @return
	 */
	public WorldGen getWorldGen() {
		return worldGen;
	}

	/**
	 * Get the tile ray intersection
	 * 
	 * @param ray
	 *            The ray to store the result in
	 * @return
	 */
	public Tile pickRay(Ray ray, int maxLength) {
		Vector3f rayIntersection = getRayIntersection(ray, maxLength);
		if (rayIntersection.x == -1) {
			return null;
		}
		return getTile((int) rayIntersection.x, (int) rayIntersection.z);
	}

	/**
	 * 
	 * @param ray
	 * @param ox
	 * @param oz
	 * @param radius
	 * @return
	 */
	public Vector3f getRayIntersection(Ray ray, int radius) {
		// Just search all tiles and look for an intersection hehe

		int ox = (int) ray.getOrigin().x;
		int oz = (int) ray.getOrigin().z;

		for (int x = ox - radius; x < ox + radius; x++) {
			for (int z = oz - radius; z < oz + radius; z++) {

				if (x < 0 || z < 0) {
					continue;
				}

				float p1H = getTileHeight(x + 1, z);
				float p2H = getTileHeight(x, z);
				float p3H = getTileHeight(x, z + 1);
				float p4H = getTileHeight(x + 1, z + 1);

				Vector3f p1 = new Vector3f((x * Region.TILE_WIDTH)
						+ Region.TILE_WIDTH, p1H, (z * Region.TILE_LENGTH));
				Vector3f p2 = new Vector3f((x * Region.TILE_WIDTH), p2H,
						(z * Region.TILE_LENGTH));
				Vector3f p3 = new Vector3f((x * Region.TILE_WIDTH), p3H,
						(z * Region.TILE_LENGTH) + Region.TILE_LENGTH);
				Vector3f p4 = new Vector3f((x * Region.TILE_WIDTH)
						+ Region.TILE_WIDTH, p4H, (z * Region.TILE_LENGTH)
						+ Region.TILE_LENGTH);

				Vector3f t1_p1 = p1;
				Vector3f t1_p2 = p2;
				Vector3f t1_p3 = p4;

				Vector3f t2_p1 = p2;
				Vector3f t2_p2 = p3;
				Vector3f t2_p3 = p4;

				Vector3f intersectionPoint = new Vector3f();
				if (VectorMath.intersectsTriangle(ray, t1_p1, t1_p2, t1_p3,
						intersectionPoint)) {
					// System.err.println("Found tile1 at [" + x + ", " + z
					// + "] \n");
					return new Vector3f(x, 0.0f, z);
				}
				if (VectorMath.intersectsTriangle(ray, t2_p1, t2_p2, t2_p3,
						intersectionPoint)) {
					// System.err.println("Found tile2 at [" + x + ", " + z
					// + "] \n");
					return new Vector3f(x, 0.0f, z);
				}
			}
		}
		return new Vector3f(-1, -1, -1);
	}

	/**
	 * Get the tile atlas texture of this world
	 * 
	 * @return
	 */
	public Texture getTileAtlas() {
		return tileAtlas;
	}

	/**
	 * Get the tile height at the given location
	 * 
	 * @param absX
	 * @param absY
	 * @return
	 */
	public float getTileHeight(int absX, int absY) {
		Tile tile = getTile(absX, absY);
		if (tile != null) {
			return tile.getTileHeight();
		} else {
			return 0.0f;
		}
	}

	/**
	 * Get the tile at the given location
	 * 
	 * @param absX
	 * @param absY
	 * @return
	 */
	public Tile getTile(int absX, int absY) {
		synchronized (loadedRegions) {
			int regionX = absX / Region.REGION_WIDTH;
			int regionY = absY / Region.REGION_HEIGHT;

			Region region = getRegion(regionX, regionY);
			if (region != null) {
				return region.getTile(absX - (regionX * Region.REGION_WIDTH),
						absY - (regionY * Region.REGION_HEIGHT));
			} else {
				return null;
			}
		}
	}

	/**
	 * Update the world
	 */
	public void update(int middleRegionX, int middleRegionY) {
		if (middleRegionX != worldX || middleRegionY != worldY) {
			isListUpdateNeeded = true;
		}
		this.worldX = middleRegionX;
		this.worldY = middleRegionY;
		if (isListUpdateNeeded) {
			regionList = new Region[(VIEW_DISTANCE * 2) * (VIEW_DISTANCE * 2)];
			int off = 0;
			for (int x = worldX - VIEW_DISTANCE; x < worldX + VIEW_DISTANCE; x++) {
				for (int y = worldY - VIEW_DISTANCE; y < worldY + VIEW_DISTANCE; y++) {
					if (x < 0 || y < 0) {
						continue;
					}
					Region currRegion = getRegion(x, y);
					if (currRegion == null) {
						currRegion = requestRegion(x, y);
						if (currRegion == null) {
							EngineLogger.info("Could not load region (" + x
									+ ", " + y + ")");
							continue;
						}
					}
					regionList[off++] = currRegion;
				}
			}
			EngineLogger.info("Updated list.");
			isListUpdateNeeded = false;
		}
		int off = 0;
		for (int x = worldX - VIEW_DISTANCE; x < worldX + VIEW_DISTANCE; x++) {
			for (int y = worldY - VIEW_DISTANCE; y < worldY + VIEW_DISTANCE; y++) {
				if (x < 0 || y < 0) {
					continue;
				}
				Region currRegion = regionList[off++];
				if (currRegion != null) {
					currRegion.update();
				}
			}
		}
	}

	/**
	 * Render the world
	 */
	public void render() {

		regionShader.use();

		// Bind the tile atlas
		getTileAtlas().bind2DArray();
		// Render all active regions
		regionShader.setTextureLocation("tColorMap", 0);
		regionShader.setProjectionViewMatrix(Application.getApplication()
				.getCamera().getProjectionViewMatrix());
		regionShader.setModelMatrix(Application.getApplication().getCamera()
				.getModelMatrix());

		if (regionList != null) {
			int off = 0;
			for (int x = worldX - VIEW_DISTANCE; x < worldX + VIEW_DISTANCE; x++) {
				for (int y = worldY - VIEW_DISTANCE; y < worldY + VIEW_DISTANCE; y++) {
					if (x < 0 || y < 0) {
						continue;
					}
					Region currRegion = regionList[off++];
					if (currRegion != null) {
						currRegion.render();
					}
				}
			}
		}

		getTileAtlas().unbind2DArray();

		regionShader.useNone();
	}
}
