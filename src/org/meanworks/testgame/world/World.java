package org.meanworks.testgame.world;

import java.util.LinkedList;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.gui.impl.PerformanceGraph;
import org.meanworks.engine.gui.impl.Toast;
import org.meanworks.engine.math.FrustumResult;
import org.meanworks.engine.math.Ray;
import org.meanworks.engine.math.VectorMath;
import org.meanworks.engine.scene.Geometry;
import org.meanworks.engine.scene.GeometryNode;
import org.meanworks.engine.util.PerlinNoise;
import org.meanworks.render.material.Material;
import org.meanworks.render.texture.Texture;
import org.meanworks.render.texture.TextureArray;
import org.meanworks.render.texture.TextureLoader;

public class World {

	/*
	 * Debug counter for how many regions were rendered
	 */
	public static int renderedRegions = 0;

	/*
	 * The view distance of this world
	 */
	public final static int VIEW_DISTANCE = 8;

	/*
	 * The world cursors position
	 */
	private int worldX;
	private int worldY;

	/*
	 * Whether the region list needs to be updated
	 */
	private boolean isListUpdateNeeded = true;

	/*
	 * The list of active regions
	 */
	private Region[] regionList;

	/*
	 * The atlas texture
	 */
	private TextureArray tileAtlas;

	/*
	 * The world gen for testing
	 */
	private PerlinNoise worldGen;

	/*
	 * The world loader
	 */
	private WorldLoader worldLoader;

	/*
	 * 
	 */
	private Material material;

	/*
	 * 
	 */
	private GeometryNode treeModel;

	/*
	 * 
	 */
	private Texture treeTexture;

	/*
	 * 
	 */
	private LinkedList<Geometry> trees = new LinkedList<Geometry>();

	/**
	 * Construct a new
	 */
	public World() {

		worldGen = new PerlinNoise();
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
				"./data/images/terrain/packed-dirt.jpg",
				"./data/images/terrain/grasssprite_spring.png",
				"./data/images/terrain/marsh.png",
				"./data/images/terrain/grasssprite_spring2.png",

		});// Application.getApplication().getAssetManager().loadTexture("./data/images/terrain/atlas.png",
			// true);

		if (tileAtlas == null) {
			System.err.println("Could not load tile atlas :(");
			return;
		}

		/*
		 * Load the region shader
		 */
		material = new Material("worldMaterial", Application.getApplication()
				.getAssetManager().loadShader(("./data/shaders/terrain")));

		/*
		 * Load the tree
		 */
		/*
		 * treeModel = null;//
		 * DaeToFeConverter.convertModel("./data/models/pineTree.dae");
		 * treeTexture = Application.getApplication().getAssetManager()
		 * .loadTexture("./data/models/pinetex.png"); for (int x = 4900; x <
		 * 5100; x++) { for (int y = 4900; y < 5100; y++) { int should = (int)
		 * (Math.random() * 15f); if (should == 5) {
		 * System.err.println("Added tree at " + x + " / " + y); Geometry tree =
		 * treeModel.instance(); trees.add(tree);
		 * tree.getTransform().setPosition((float) x, 110, (float) y); } } }
		 */

		regionList = new Region[(VIEW_DISTANCE * 2) * (VIEW_DISTANCE * 2)];

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

		int minX = worldX - VIEW_DISTANCE;
		int maxX = worldX + VIEW_DISTANCE;
		int minY = worldY - VIEW_DISTANCE;
		int maxY = worldY + VIEW_DISTANCE;

		if (regionX < minX || regionX >= maxX || regionY < minY
				|| regionY >= maxY || regionX < 0 || regionY < 0) {
			return null;
		}
		// width=VIEW_DISTANCE * 2
		// pos = x * width + y
		if (regionList == null) {
			System.err.println("REGION LIST = NULL");
			return null;
		}
		return regionList[(regionX - minX) * (VIEW_DISTANCE * 2)
				+ (regionY - minY)];
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
		float scaleFactor = 1.0f;
		int x = (int) (xPos / scaleFactor);
		int z = (int) (zPos / scaleFactor);
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
	public PerlinNoise getWorldGen() {
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
					// System.err.println("Intersection point: " +
					// intersectionPoint.toString());
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
		int regionX = absX / Region.REGION_WIDTH;
		int regionY = absY / Region.REGION_HEIGHT;

		Region region = getRegion(regionX, regionY);
		if (region != null) {
			return region.getTile(absX - (regionX * Region.REGION_WIDTH), absY
					- (regionY * Region.REGION_HEIGHT));
		} else {
			return null;
		}
	}

	/**
	 * Update the world
	 */
	public void update(int middleRegionX, int middleRegionY) {
		if (middleRegionX != worldX || middleRegionY != worldY) {
			isListUpdateNeeded = true;
		}
		if (isListUpdateNeeded) {
			Region[] tempList = new Region[(VIEW_DISTANCE * 2)
					* (VIEW_DISTANCE * 2)];
			LinkedList<Region> toBeBuilt = new LinkedList<>();
			int off = 0;
			for (int x = middleRegionX - VIEW_DISTANCE; x < middleRegionX
					+ VIEW_DISTANCE; x++) {
				for (int y = middleRegionY - VIEW_DISTANCE; y < middleRegionY
						+ VIEW_DISTANCE; y++) {
					if (x < 0 || y < 0) {
						continue;
					}
					Region currRegion = getRegion(x, y);

					int oldLod = currRegion == null ? -1 : currRegion
							.getLodLevel();

					if (currRegion == null) {
						currRegion = new Region(this, x, y);
						toBeBuilt.add(currRegion);
					}
					// Distance from middle
					int dx = Math.abs(x - middleRegionX);
					int dy = Math.abs(y - middleRegionY);
					double dist = Math.sqrt(dx * dx + dy * dy);
					dist = (int) dist;
					if (dist <= 1) {
						currRegion.setLodLevel(1);
					}
					if (dist > 1) {
						currRegion.setLodLevel(2);
					}
					if (dist > 3) {
						currRegion.setLodLevel(4);
					}
					if (dist > 5) {
						currRegion.setLodLevel(8);
					}
					if (dist > 7) {
						currRegion.setLodLevel(16);
					}
					if (dist > 9) {
						currRegion.setLodLevel(32);
					}
					if (oldLod != currRegion.getLodLevel() && oldLod != -1) {
						updateRegion(currRegion);
					}
					tempList[off++] = currRegion;
				}
			}
			regionList = tempList;
			for (Region region : toBeBuilt) {
				worldLoader.addTask(region);
			}

			// We can either clear it, or leave it up to the GC to remove the
			// used memory
			toBeBuilt.clear();

			Toast.makeTopToast("Updated world.");
			System.gc();
			isListUpdateNeeded = false;
		}
		this.worldX = middleRegionX;
		this.worldY = middleRegionY;
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
	 * Send this region for update
	 * 
	 * @param region
	 */
	public void updateRegion(Region region) {
		region.getRegionMesh().flagUpdate();
		worldLoader.addTask(region);
	}

	/**
	 * Render the world
	 */
	public void render() {

		/*
		 * TODO: Add a proper way to handle geometries such as the terrain using
		 * the geometry system provided by the engine
		 */

		// Bind the tile atlas
		getTileAtlas().bind2DArray();
		// Render all active regions
		material.setProperty("time", (int) System.currentTimeMillis());
		material.setProperty("tColorMap", 0);
		material.setProperty("mProjectionView", Application.getApplication()
				.getCamera().getProjectionViewMatrix());
		material.setProperty("mModelMatrix", new Matrix4f());
		material.apply();

		if (regionList != null) {
			int off = 0;
			renderedRegions = 0;
			for (int x = worldX - VIEW_DISTANCE; x < worldX + VIEW_DISTANCE; x++) {
				for (int y = worldY - VIEW_DISTANCE; y < worldY + VIEW_DISTANCE; y++) {
					if (x < 0 || y < 0) {
						continue;
					}
					Region currRegion = regionList[off++];
					if (currRegion != null) {
						FrustumResult result = Application
								.getApplication()
								.getCamera()
								.getFrustum()
								.cubeInFrustum(
										new Vector3f(currRegion.getRegionX()
												* Region.REGION_WIDTH, 50.0f,
												currRegion.getRegionY()
														* Region.REGION_HEIGHT),
										new Vector3f(currRegion.getRegionX()
												* Region.REGION_WIDTH
												+ Region.REGION_WIDTH, 200.0f,
												currRegion.getRegionY()
														* Region.REGION_HEIGHT
														+ Region.REGION_HEIGHT));
						if (result == FrustumResult.INSIDE
								|| result == FrustumResult.PARTIALLY_INSIDE) {
							currRegion.render();
							renderedRegions++;
						}
					}
				}
			}
		}

		getTileAtlas().unbind2DArray();
		material.getShaderProgram().useNone();
	}
}
