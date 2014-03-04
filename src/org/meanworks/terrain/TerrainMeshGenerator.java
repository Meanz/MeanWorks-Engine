package org.meanworks.terrain;

import java.util.LinkedList;

import org.meanworks.engine.asset.AssetManager;
import org.meanworks.engine.math.Vec2;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.math.VectorMath;
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
	 * The float mesh buffer for this generator
	 */
	private FloatMeshBuffer fmb;

	/**
	 * The idx of the position buffer we are in!
	 */
	private int idx;

	/**
	 * Indices for the tile points
	 */
	private int p1idx;
	private int p2idx;
	private int p3idx;
	private int p4idx;
	private int p5idx;

	float minTexX = 0.0f;
	float maxTexX = (float) 1.0f;
	float minTexY = 0.0f;
	float maxTexY = (float) 1.0f;

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
	 * The chunk that owns this generator
	 */
	private TerrainChunk chunk;

	/**
	 * Construct a new TerrainMeshGenerator
	 * 
	 * @param store
	 * @param heightProvider
	 */
	public TerrainMeshGenerator(TerrainChunk chunk, Mesh store,
			TerrainHeightProvider heightProvider, boolean compileAfterGen) {
		this.chunk = chunk;
		this.mesh = store;
		this.heightProvider = heightProvider;
		this.compileAfterGen = compileAfterGen;
		idx = 0;
	}

	/**
	 * Construct a new TerrainMeshGenerator
	 * 
	 * @param store
	 * @param heightProvider
	 */
	public TerrainMeshGenerator(TerrainChunk chunk, Mesh store,
			TerrainHeightProvider heightProvider) {
		this(chunk, store, heightProvider, true);
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

	public void addVertex(FloatMeshBuffer fmb, Vec3 pos, Vec3 norm, Vec2 uv) {
		fmb.addPosition(pos.x, pos.y, pos.z);
		fmb.addNormal(norm.x, norm.y, norm.z);
		fmb.addUV(uv.x, uv.y);
	}

	public void addIndex(FloatMeshBuffer fmb, int idx, int index) {
		fmb.addIndex(idx + (index - 1));
	}

	public void addIndexS(FloatMeshBuffer fmb, int idx) {
		fmb.addIndex(idx);
	}

	public Vec3 makePos(int x, int z) {
		return new Vec3(x, heightProvider.getHeight(x, z), z);
	}

	public float getHeight(int x, int z) {
		return heightProvider.getHeight(x, z);
	}

	public Vec3 makeMiddlePos(int x, int z, int lod) {

		Vec3 middlePos = new Vec3(x + lod / 2, 0.0f, z + lod / 2);

		int halfLod = lod / 2;

		Vec3 p1 = new Vec3(x, getHeight(x, z), z);
		Vec3 p2 = new Vec3(x + halfLod, getHeight(x + halfLod, z), z);
		Vec3 p3 = new Vec3(x + halfLod, getHeight(x + halfLod, z + halfLod), z
				+ halfLod);
		Vec3 p4 = new Vec3(x, getHeight(x, z + halfLod), z + halfLod);

		middlePos.y = VectorMath.getInterpolatedQuadHeight(middlePos.x,
				middlePos.y, p1, p2, p3, p4);

		return middlePos;
	}

	public void makeP5(int x, int z, int lod) {
		// This one is only needed if we have a center
		Vec3 p5 = makeMiddlePos(x, z, lod);
		addVertex(fmb, p5, calcNormal(heightProvider, x, z + lod), new Vec2(
				maxTexX / 2, minTexY / 2));
		idx++;
	}

	public void generateTile(int cursorX, int cursorZ, int _x, int _z,
			ChunkInfo ci, int lod) {

		boolean br = (cursorX + cursorZ) % 2 == 0;

		idx += 4;

		float nLeftPoints = (float) lod / ci.left;
		float nRightPoints = (float) lod / ci.right;
		float nTopPoints = (float) lod / ci.top;
		float nBottomPoints = (float) lod / ci.bottom;

		// Is this a negative corner?
		if (cursorX != 0) {
			nRightPoints = 1.0f;
		}
		if (!(cursorX >= chunkSizeX)) {
			nLeftPoints = 1.0f;
		}
		if (cursorZ != 0) {
			nBottomPoints = 1.0f;
		}
		if (!(cursorZ >= chunkSizeY)) {
			nTopPoints = 1.0f;
		}
		if (nLeftPoints != 1f || nRightPoints != 1f || nTopPoints != 1f
				|| nBottomPoints != 1f) {
			makeP5(_x, _z, lod);
		}

		if (chunk.getChunkX() == 2 && chunk.getChunkZ() == 0) {
			System.err.println("We are here what is the probs?");
			System.err.println("CI: " + ci.left + " / " + ci.right + " / "
					+ ci.top + " / " + ci.bottom);
		}
		computeTile(_x, _z, br, nLeftPoints, nTopPoints, nRightPoints,
				nBottomPoints, lod);

	}

	public void computeTile(int _x, int _z, boolean br, float nLeftPoints,
			float nTopPoints, float nRightPoints, float nBottomPoints, int lod) {

		int tileWidth = lod;
		int tileLength = lod;

		if (br) {
			// TR side
			if (nRightPoints > 1.0f || nTopPoints > 1.0f || nLeftPoints > 1.0f
					|| nBottomPoints > 1.0f) {
				if (nLeftPoints > 1.0f) {
					// Do left points!
					float perUnit = tileLength / nLeftPoints;

					int lastIdx = p3idx;
					for (int i = (int) nLeftPoints; i > 0; i--) {

						int __z = (int) (_z + ((i + 1) * perUnit));

						Vec3 p = new Vec3(_x,
								heightProvider.getHeight(_x, __z), __z);

						addVertex(
								fmb,
								p,
								calcNormal(heightProvider, _x, _z + tileLength),
								new Vec2(maxTexX, maxTexY
										- (maxTexY / nLeftPoints) * (i + 1)));

						// Add first point
						addIndexS(fmb, lastIdx);
						addIndexS(fmb, idx);
						addIndexS(fmb, p5idx);
						lastIdx = idx;
						idx++;
					}
				} else {
					// Attach normal left tri
					addIndexS(fmb, p3idx);
					addIndexS(fmb, p2idx);
					addIndexS(fmb, p5idx);
				}
				if (nBottomPoints > 1.0f) {
					// Do bottom points
					float perUnit = tileWidth / nBottomPoints;

					int lastIdx = p2idx;
					for (int i = (int) nBottomPoints; i > 0; i--) {

						int __x = (int) (_x + ((i - 1) * perUnit));

						Vec3 p = new Vec3(__x,
								heightProvider.getHeight(__x, _z), _z);

						addVertex(
								fmb,
								p,
								calcNormal(heightProvider, _x, _z + tileLength),
								new Vec2(maxTexX, maxTexY
										- (maxTexY / nBottomPoints) * (i + 1)));

						// Add first point
						addIndexS(fmb, lastIdx);
						addIndexS(fmb, idx);
						addIndexS(fmb, p5idx);
						lastIdx = idx;
						idx++;
					}
				} else {
					// Attach normal bottom tri
					addIndexS(fmb, p2idx);
					addIndexS(fmb, p1idx);
					addIndexS(fmb, p5idx);
				}
				if (nRightPoints > 1.0f) {
					// Do right points

					if (chunk.getChunkX() == 2 && chunk.getChunkZ() == 0) {
						System.err.println("We are here what is the probs?");
					}

					float perUnit = tileLength / nRightPoints;
					int lastIdx = p1idx;
					for (int i = 0; i < (int) nRightPoints; i++) {

						int __z = (int) (_z + ((i + 1) * perUnit));

						Vec3 p = new Vec3(_x,
								heightProvider.getHeight(_x, __z), __z);

						addVertex(
								fmb,
								p,
								calcNormal(heightProvider, _x, _z + tileLength),
								new Vec2(maxTexX, maxTexY
										- (maxTexY / nBottomPoints) * (i + 1)));

						// Add first point
						addIndexS(fmb, lastIdx);
						addIndexS(fmb, idx);
						addIndexS(fmb, p5idx);
						lastIdx = idx;
						idx++;
					}
				} else {
					// Add normal right tri
					addIndexS(fmb, p1idx);
					addIndexS(fmb, p4idx);
					addIndexS(fmb, p5idx);
				}
				if (nTopPoints > 1.0f) {
					// Do top points
					float perUnit = tileWidth / nTopPoints;

					int lastIdx = p4idx;
					for (int i = 0; i > nTopPoints; i++) {

						int __x = (int) (_x + ((i + 1) * perUnit));

						Vec3 p = new Vec3(__x,
								heightProvider.getHeight(__x, _z), _z);

						addVertex(
								fmb,
								p,
								calcNormal(heightProvider, _x, _z + tileLength),
								new Vec2(maxTexX, maxTexY
										- (maxTexY / nBottomPoints) * (i + 1)));

						// Add first point
						addIndexS(fmb, lastIdx);
						addIndexS(fmb, idx);
						addIndexS(fmb, p5idx);
						lastIdx = idx;
						idx++;
					}
				} else {
					addIndexS(fmb, p4idx);
					addIndexS(fmb, p3idx);
					addIndexS(fmb, p5idx);
				}
			} else {
				// Add normal trbr tri
				addIndexS(fmb, p1idx);
				addIndexS(fmb, p4idx);
				addIndexS(fmb, p3idx);

				// Add normal bltl tri
				addIndexS(fmb, p2idx);
				addIndexS(fmb, p1idx);
				addIndexS(fmb, p3idx);
			}
		} else {
			// TR side
			if (nRightPoints > 1.0f || nBottomPoints > 1.0f
					|| nLeftPoints > 1.0f || nTopPoints > 1.0f) {
				if (nLeftPoints > 1.0f) {
					// Do left points!
					float perUnit = tileLength / nLeftPoints;

					int lastIdx = p3idx;
					for (int i = (int) nLeftPoints; i > 0; i++) {

						int __z = (int) (_z + ((i + 1) * perUnit));

						Vec3 p = new Vec3(_x,
								heightProvider.getHeight(_x, __z), __z);

						addVertex(
								fmb,
								p,
								calcNormal(heightProvider, _x, _z + tileLength),
								new Vec2(maxTexX, maxTexY
										- (maxTexY / nLeftPoints) * (i + 1)));

						// Add first point
						addIndexS(fmb, lastIdx);
						addIndexS(fmb, idx);
						addIndexS(fmb, p5idx);
						lastIdx = idx;
						idx++;
					}
				} else {
					// Attach normal left tri
					addIndexS(fmb, p3idx);
					addIndexS(fmb, p2idx);
					addIndexS(fmb, p5idx);
				}
				if (nTopPoints > 1.0f) {
					// Do top points
					float perUnit = tileWidth / nTopPoints;

					int lastIdx = p4idx;
					for (int i = 0; i < nTopPoints; i++) {

						int __x = (int) (_x + ((i + 1) * perUnit));

						Vec3 p = new Vec3(__x,
								heightProvider.getHeight(__x, _z), _z);

						addVertex(
								fmb,
								p,
								calcNormal(heightProvider, _x, _z + tileLength),
								new Vec2(maxTexX, maxTexY
										- (maxTexY / nTopPoints) * (i + 1)));

						// Add first point
						addIndexS(fmb, lastIdx);
						addIndexS(fmb, idx);
						addIndexS(fmb, p5idx);
						lastIdx = idx;
						idx++;
					}
				} else {
					// Attach normal top tri
					addIndexS(fmb, p4idx);
					addIndexS(fmb, p3idx);
					addIndexS(fmb, p5idx);
				}
				if (nRightPoints > 1.0f) {
					// Do right points
					float perUnit = tileLength / nRightPoints;

					int lastIdx = p1idx;
					for (int i = 0; i < nRightPoints; i++) {

						int __z = (int) (_z + ((i + 1) * perUnit));

						Vec3 p = new Vec3(_x,
								heightProvider.getHeight(_x, __z), __z);

						addVertex(
								fmb,
								p,
								calcNormal(heightProvider, _x, _z + tileLength),
								new Vec2(maxTexX, maxTexY
										- (maxTexY / nRightPoints) * (i + 1)));

						// Add first point
						addIndexS(fmb, lastIdx);
						addIndexS(fmb, idx);
						addIndexS(fmb, p5idx);
						lastIdx = idx;
						idx++;
					}
				} else {
					// Add normal right tri
					addIndexS(fmb, p1idx);
					addIndexS(fmb, p4idx);
					addIndexS(fmb, p5idx);
				}
				if (nBottomPoints > 1.0f) {
					// Do bottom points
					float perUnit = tileWidth / nBottomPoints;

					int lastIdx = p2idx;
					for (int i = (int) nBottomPoints; i > 0; i--) {

						int __x = (int) (_x + ((i - 1) * perUnit));

						Vec3 p = new Vec3(__x,
								heightProvider.getHeight(__x, _z), _z);

						addVertex(
								fmb,
								p,
								calcNormal(heightProvider, _x, _z + tileLength),
								new Vec2(maxTexX, maxTexY
										- (maxTexY / nBottomPoints) * (i + 1)));

						// Add first point
						addIndexS(fmb, lastIdx);
						addIndexS(fmb, idx);
						addIndexS(fmb, p5idx);
						lastIdx = idx;
						idx++;
					}
				} else {
					addIndexS(fmb, p2idx);
					addIndexS(fmb, p1idx);
					addIndexS(fmb, p5idx);
				}
			} else {
				// Add normal bltr
				addIndexS(fmb, p2idx);
				addIndexS(fmb, p1idx);
				addIndexS(fmb, p4idx);
				// Add normal bltl tri
				addIndexS(fmb, p3idx);
				addIndexS(fmb, p2idx);
				addIndexS(fmb, p4idx);
			}
		}
	}

	/**
	 * Control point
	 */
	class CP {
		int x;
		int y;
		float h;
	}

	class Quad {

		// This information is needed?
		int patchId;
		int patchX;
		int patchY;
		int patchSize;

		// Quad def
		// TODO: This might be shortened to p1 and p3 if all patches
		// Are supposed to be quadratic
		Vec3 p1;
		Vec3 p2;
		Vec3 p3;
		Vec3 p4;

		//
		//
		//
		LinkedList<CP> leftCP = new LinkedList<>();
		LinkedList<CP> bottomCP = new LinkedList<>();
		LinkedList<CP> rightCP = new LinkedList<>();
		LinkedList<CP> topCP = new LinkedList<>();
	}

	public boolean generateLOD2(ChunkInfo ci, int lod) {

		// Create our heightmap
		// heightmap = new float[chunkSizeX][chunkSizeY];

		// Populate the height map
		for (int x = 0; x < chunkSizeX; x++) {
			for (int y = 0; y < chunkSizeY; y++) {
				// heightmap[x][y] = heightProvider.getHeight(x, y);
			}
		}

		// Create our patchmap
		int[][] patchMap = new int[chunkSizeX][chunkSizeY];

		// Create our quadlist
		LinkedList<Quad> quadList = new LinkedList<Quad>();

		// Adjust this for lod ?
		float tolerance = 0.2f + (0.2f * lod); // If the slope from tile1
												// exceeds the tolerance
		// create a new patch

		// Our patch incrementor
		int patchId = 1;

		// Calculate patches
		for (int x = 0; x < chunkSizeX; x++) {
			for (int y = 0; y < chunkSizeY; y++) {

				// Is there already a patch here?
				if (patchMap[x][y] != 0) {
					continue; // Nothing to do here.
				}

				patchMap[x][y] = patchId++;

				float tXSlope = getHeight(x + 1, y) - getHeight(x, y);
				float tYSlope = getHeight(x, y + 1) - getHeight(x, y);

				int endRadius = 1;

				// For now only generate larger quads
				if (x != 0 && y != 0 && x != chunkSizeX - 1
						&& y != chunkSizeY - 1)
					a: for (int radius = 1; radius < chunkSizeX; radius++) {

						// Search quadratic in x+ and z+
						b: for (int _x = x; _x < radius + x; _x++) {
							for (int _y = y; _y < radius + y; _y++) {

								// Calculate X Slope
								if (_x + 1 >= chunkSizeX)
									break a;

								if (_y + 1 >= chunkSizeY)
									break a;

								if (_x == 0 || _y == 0)
									continue;

								float xSlope = getHeight(_x + 1, _y)
										- getHeight(_x, _y);
								float ySlope = getHeight(_x, _y + 1)
										- getHeight(_x, _y);

								if (Math.abs(tXSlope - xSlope) > tolerance) {
									break a;
								}
								if (Math.abs(tYSlope - ySlope) > tolerance) {
									break a;
								}
							}
						}

						// If a was not broken, assign the patch id to the new
						// patches
						for (int _x = x; _x < radius + x; _x++) {
							for (int _y = y; _y < radius + y; _y++) {
								patchMap[_x][_y] = patchId - 1;
							}
						}
						endRadius = radius;
					}

				float h1 = getHeight(x, y);
				float h2 = getHeight(x + endRadius, y);
				float h3 = getHeight(x + endRadius, y + endRadius);
				float h4 = getHeight(x, y + endRadius);

				// Create our quad based on end radius
				Quad quad = new Quad();

				quad.patchX = x;
				quad.patchY = y;
				quad.patchSize = endRadius;
				quad.patchId = patchId - 1;

				quad.p1 = new Vec3(x, h1, y);
				quad.p2 = new Vec3(x + endRadius, h2, y);
				quad.p3 = new Vec3(x + endRadius, h3, y + endRadius);
				quad.p4 = new Vec3(x, h4, y + endRadius);

				quadList.add(quad);

			}
		}

		// Count subdivisions
		for (Quad quad : quadList) {

			// L T R B

			// Check left side
			if (quad.patchX + (quad.patchSize + 1) < chunkSizeX) {
				LinkedList<Integer> storedIds = new LinkedList<Integer>();
				for (int i = 0; i < quad.patchSize; i++) {

					int _x = quad.patchX + quad.patchSize + 1;
					int _y = quad.patchY + i;

					if (_y >= chunkSizeY)
						break;

					boolean found = false;
					int targetPatchId = patchMap[_x][_y];
					for (Integer p : storedIds) {
						if (p.equals(targetPatchId)) {
							found = true;
						}
					}
					if (!found) {
						storedIds.add(patchMap[_x][_y]);

						System.err.println("Patch [ " + quad.patchX + ", "
								+ quad.patchY + " ] Add CP Left [ " + (_x)
								+ ", " + (_y));

						CP cp = new CP();

						cp.x = _x - 1;
						cp.y = _y;

						quad.leftCP.add(cp);
					}
				}
			}

			// Check bottom side
			if (quad.patchY - 1 >= 0) {
				LinkedList<Integer> storedIds = new LinkedList<Integer>();
				for (int i = 0; i < quad.patchSize; i++) {

					int _x = quad.patchX + i;
					int _y = quad.patchY - 1;

					if (_x >= chunkSizeX)
						break;

					boolean found = false;
					int targetPatchId = patchMap[_x][_y];
					for (Integer p : storedIds) {
						if (p.equals(targetPatchId)) {
							found = true;
						}
					}
					if (!found) {
						storedIds.add(patchMap[_x][_y]);

						System.err.println("Patch [ " + quad.patchX + ", "
								+ quad.patchY + " ] Add CP Bottom [ " + (_x)
								+ ", " + (_y));

						CP cp = new CP();

						cp.x = _x;
						cp.y = _y + 1;

						quad.bottomCP.add(cp);
					}
				}
			}

			// Check right side
			if (quad.patchX - 1 >= 0) {
				LinkedList<Integer> storedIds = new LinkedList<Integer>();
				for (int i = 0; i < quad.patchSize; i++) {

					int _x = quad.patchX - 1;
					int _y = quad.patchY + i;

					if (_y >= chunkSizeY)
						break;

					boolean found = false;
					int targetPatchId = patchMap[_x][_y];
					for (Integer p : storedIds) {
						if (p.equals(targetPatchId)) {
							found = true;
						}
					}
					if (!found) {
						storedIds.add(patchMap[_x][_y]);

						System.err.println("Patch [ " + quad.patchX + ", "
								+ quad.patchY + " ] Add CP Right [ " + (_x)
								+ ", " + (_y));

						CP cp = new CP();

						cp.x = _x + 1;
						cp.y = _y;

						quad.rightCP.add(cp);
					}
				}
			}

			// Check top side
			if (quad.patchY + (quad.patchSize + 1) < chunkSizeY) {
				LinkedList<Integer> storedIds = new LinkedList<Integer>();
				for (int i = 0; i < quad.patchSize; i++) {

					int _x = quad.patchX + i;
					int _y = quad.patchY + (quad.patchSize + 1);

					if (_x >= chunkSizeX)
						break;

					boolean found = false;
					int targetPatchId = patchMap[_x][_y];
					for (Integer p : storedIds) {
						if (p.equals(targetPatchId)) {
							found = true;
						}
					}
					if (!found) {
						storedIds.add(patchMap[_x][_y]);

						System.err.println("Patch [ " + quad.patchX + ", "
								+ quad.patchY + " ] Add CP Top [ " + (_x)
								+ ", " + (_y));

						CP cp = new CP();

						cp.x = _x;
						cp.y = _y - 1;

						quad.topCP.add(cp);
					}
				}
			}

		}

		// Make our quads
		// Create float mesh buffer
		// (4 points * (3 pos + 3 norm + 2 uv)) * noPoints

		patchId += 500; // HACKS!!

		fmb = new FloatMeshBuffer((4 * (3 + 3 + 2)) * patchId,
		// 6 indices per quad?
				patchId * 6);

		// Index!
		int idx = 0;

		// Generate our quads
		for (Quad quad : quadList) {

			float maxTexX = quad.p2.x - quad.p1.x;
			float maxTexY = quad.p4.z - quad.p1.z;

			addVertex(
					fmb,
					quad.p1,
					calcNormal(heightProvider, (int) quad.p1.x, (int) quad.p1.z),
					new Vec2(maxTexX, maxTexY));
			addVertex(
					fmb,
					quad.p2,
					calcNormal(heightProvider, (int) quad.p2.x, (int) quad.p2.z),
					new Vec2(minTexX, maxTexY));
			addVertex(
					fmb,
					quad.p3,
					calcNormal(heightProvider, (int) quad.p3.x, (int) quad.p3.z),
					new Vec2(minTexX, minTexY));
			addVertex(
					fmb,
					quad.p4,
					calcNormal(heightProvider, (int) quad.p4.x, (int) quad.p4.z),
					new Vec2(maxTexX, minTexY));

			int p1 = idx;
			int p2 = idx + 1;
			int p3 = idx + 2;
			int p4 = idx + 3;
			idx += 4;

			// Need middle vertex?
			int p5 = -1;
			if (quad.leftCP.size() > 1 || quad.bottomCP.size() > 1
					|| quad.rightCP.size() > 1 || quad.topCP.size() > 1) {

				// Make p5!
				float __x = quad.p2.x - ((quad.p2.x - quad.p1.x) / 2f);
				float __y = quad.p4.z - ((quad.p4.z - quad.p1.z) / 2f);
				float _h = VectorMath.getInterpolatedQuadHeight(0.5f, 0.5f,
						quad.p1, quad.p2, quad.p3, quad.p4);

				Vec3 vp5 = new Vec3(__x, _h, __y);

				addVertex(fmb, vp5,
						calcNormal(heightProvider, (int) __x, (int) __y),
						new Vec2(0.5f, 0.5f));

				p5 = idx;
				idx++;
			}

			// Build left side
			if (quad.leftCP.size() > 1 || quad.bottomCP.size() > 1) {

				if (quad.leftCP.size() > 1) {

					int lastIdx = p2;
					boolean first = true;
					for (CP cp : quad.leftCP) {
						if (first) {
							first = false;
							continue;
						}
						int _x = (int) cp.x;
						int _y = (int) cp.y;

						// Find the offset from the quad
						// Max range
						float mb = _y - quad.p2.z; // 1
						float texY = mb / maxTexY; // 1 / 2 = 0.5f

						Vec3 pi = new Vec3(_x,
								heightProvider.getHeight(_x, _y), _y);

						addVertex(fmb, pi,
								calcNormal(heightProvider, (int) _x, (int) _y),
								new Vec2(minTexX, texY));

						addIndexS(fmb, idx);
						addIndexS(fmb, lastIdx);
						addIndexS(fmb, p5);
						lastIdx = idx;
						idx++;
					}

					// Add the last point
					addIndexS(fmb, lastIdx);
					addIndexS(fmb, p5);
					addIndexS(fmb, p3);

				} else {
					addIndexS(fmb, p3);
					addIndexS(fmb, p2);
					addIndexS(fmb, p5);
				}

				if (quad.bottomCP.size() > 1) {

					int lastIdx = p1;
					boolean first = true;
					for (CP cp : quad.bottomCP) {
						if (first) {
							first = false;
							continue;
						}
						int _x = (int) cp.x;
						int _y = (int) cp.y;

						// Find the offset from the quad
						// Max range
						float mb = _x - quad.p1.x; // 1
						float texX = mb / maxTexX; // 1 / 2 = 0.5f

						Vec3 pi = new Vec3(_x,
								heightProvider.getHeight(_x, _y), _y);

						addVertex(fmb, pi,
								calcNormal(heightProvider, (int) _x, (int) _y),
								new Vec2(texX, maxTexY));

						addIndexS(fmb, idx);
						addIndexS(fmb, lastIdx);
						addIndexS(fmb, p5);
						lastIdx = idx;
						idx++;

					}

					// Add the last point
					addIndexS(fmb, p2);
					addIndexS(fmb, lastIdx);
					addIndexS(fmb, p5);

				} else {
					addIndexS(fmb, p2);
					addIndexS(fmb, p1);
					addIndexS(fmb, p5);
				}

			} else {

				// Build normally
				addIndexS(fmb, p2);
				addIndexS(fmb, p1);
				addIndexS(fmb, p3);

			}

			// Build right side
			if (quad.rightCP.size() > 1 || quad.topCP.size() > 1) {

				if (quad.rightCP.size() > 1) {

					int lastIdx = p1;
					boolean first = true;
					for (CP cp : quad.rightCP) {
						if (first) {
							first = false;
							continue;
						}
						int _x = (int) cp.x;
						int _y = (int) cp.y;

						// Find the offset from the quad
						// Max range
						float ma = quad.p4.z - quad.p1.z; // 2
						float mb = _y - quad.p1.z; // 1
						float texY = mb / ma; // 1 / 2 = 0.5f

						Vec3 pi = new Vec3(_x,
								heightProvider.getHeight(_x, _y), _y);

						addVertex(fmb, pi,
								calcNormal(heightProvider, (int) _x, (int) _y),
								new Vec2(maxTexX, texY));

						addIndexS(fmb, lastIdx);
						addIndexS(fmb, idx);
						addIndexS(fmb, p5);
						lastIdx = idx;
						idx++;

					}

					// Add the last point
					addIndexS(fmb, lastIdx);
					addIndexS(fmb, p4);
					addIndexS(fmb, p5);

				} else {
					addIndexS(fmb, p1);
					addIndexS(fmb, p4);
					addIndexS(fmb, p5);
				}

				if (quad.topCP.size() > 1) {

					int lastIdx = p4;
					boolean first = true;
					for (CP cp : quad.topCP) {
						if (first) {
							first = false;
							continue;
						}
						int _x = (int) cp.x;
						int _y = (int) cp.y;

						// Find the offset from the quad
						// Max range
						float ma = quad.p3.x - quad.p4.x; // 2
						float mb = _x - quad.p4.x; // 1
						float texX = mb / ma; // 1 / 2 = 0.5f

						Vec3 pi = new Vec3(_x,
								heightProvider.getHeight(_x, _y), _y);

						addVertex(fmb, pi,
								calcNormal(heightProvider, (int) _x, (int) _y),
								new Vec2(texX, minTexY));

						addIndexS(fmb, lastIdx);
						addIndexS(fmb, idx);
						addIndexS(fmb, p5);
						lastIdx = idx;
						idx++;

					}

					// Add the last point
					addIndexS(fmb, lastIdx);
					addIndexS(fmb, p3);
					addIndexS(fmb, p5);

				} else {
					addIndexS(fmb, p4);
					addIndexS(fmb, p3);
					addIndexS(fmb, p5);
				}

			} else {

				// Build normally
				addIndexS(fmb, p1);
				addIndexS(fmb, p4);
				addIndexS(fmb, p3);
			}

		}

		if (true) {
			mesh.positions = fmb.positions;
			mesh.normals = fmb.normals;
			mesh.uvs = fmb.uvs;
			mesh.triangles = fmb.triangles;

			mesh.setMaterial(new Material("terrainMat", AssetManager
					.loadShader("./data/shaders/wnrTerrain")));
			mesh.getMaterial()
					.setTexture(
							AssetManager
									.loadTexture("./data/images/terrain/grass.jpg"));

			if (compileAfterGen) {
				return mesh.compile();
			} else {
				return true;
			}
		}

		// Create float mesh buffer
		fmb = new FloatMeshBuffer((chunkSizeX * chunkSizeY * 4 * 6)
				+ (chunkSizeX * chunkSizeY * 2 * 4), chunkSizeX * chunkSizeY
				* 9);
		//
		for (int cursorX = 0; cursorX < chunkSizeX / lod; cursorX++) {
			for (int cursorZ = 0; cursorZ < chunkSizeY / lod; cursorZ++) {

				// Draw a tile
				// Tiles are diamonds
				// So they can hae two shapes
				// Find out our lod of neighbouring tiles
				int tileWidth = lod;
				int tileLength = lod;
				int _x = cursorX * lod;
				int _z = cursorZ * lod;

				Vec3 p1 = makePos(_x, _z);
				Vec3 p2 = makePos(_x + tileWidth, _z);
				Vec3 p3 = makePos(_x + tileWidth, _z + tileLength);
				Vec3 p4 = makePos(_x, _z + tileLength);

				p1idx = idx;
				p2idx = idx + 1;
				p3idx = idx + 2;
				p4idx = idx + 3;
				p5idx = idx + 4;

				// Add all these four guranteed vertices
				addVertex(fmb, p1, calcNormal(heightProvider, _x, _z),
						new Vec2(maxTexX, maxTexY));
				addVertex(fmb, p2,
						calcNormal(heightProvider, _x + tileWidth, _z),
						new Vec2(minTexX, maxTexY));
				addVertex(
						fmb,
						p3,
						calcNormal(heightProvider, _x + tileWidth, _z
								+ tileLength), new Vec2(minTexX, minTexY));
				addVertex(fmb, p4,
						calcNormal(heightProvider, _x, _z + tileLength),
						new Vec2(maxTexX, minTexY));

				// Do all corner attaching!
				generateTile(cursorX, cursorZ, _x, _z, ci, lod);
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
	 * Generates the mesh
	 * 
	 * @return
	 */
	public boolean generate(ChunkInfo ci, int lod) {

		if (mesh == null || heightProvider == null) {
			return false;
		}

		if (true) {
			return generateLOD2(ci, lod);
		}

		// Create float mesh buffer
		FloatMeshBuffer fmb = new FloatMeshBuffer(
				(chunkSizeX * chunkSizeY * 4 * 6)
						+ (chunkSizeX * chunkSizeY * 2 * 4), chunkSizeX
						* chunkSizeY * 6);

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
