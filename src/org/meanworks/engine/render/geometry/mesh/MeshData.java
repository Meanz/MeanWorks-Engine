package org.meanworks.engine.render.geometry.mesh;

import org.lwjgl.util.vector.Vector2f;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.math.Vec2;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.render.geometry.Triangle;
import org.meanworks.engine.render.geometry.Vertex;

/**
 * A class you can feed to a mesh and it will build a mesh out of the data
 * provided here
 * 
 * @author meanz
 * 
 */
public class MeshData {

	/*
	 * Raw positions
	 */
	private float[] positions;

	/*
	 * Raw normals
	 */
	private float[] normals;

	/*
	 * Raw tex coords
	 */
	private float[] texCoords;

	/*
	 * 
	 */
	private int[] indices;

	/**
	 * Constructor for a mesh data object
	 */
	public MeshData() {
		positions = null;
		normals = null;
		texCoords = null;
		indices = null;
	}

	/**
	 * 
	 * @return
	 */
	public int[] getIndices() {
		return indices;
	}

	/**
	 * Get the positions of this mesh data
	 * 
	 * @return
	 */
	public float[] getPositions() {
		return positions;
	}

	/**
	 * Get the normals of this mesh data
	 * 
	 * @return
	 */
	public float[] getNormals() {
		return normals;
	}

	/**
	 * Get the raw texture coordinates of this mesh data
	 * 
	 * @return
	 */
	public float[] getTexCoords() {
		return texCoords;
	}

	/**
	 * Add the given indices to this mesh data
	 * 
	 * @param inIndices
	 */
	public void putIndices(int[] inIndices) {
		if (inIndices == null) {
			return;
		}
		if (indices == null) {
			indices = inIndices;
		} else {
			//
			int[] _indices = indices;
			indices = new int[indices.length + inIndices.length];
			System.arraycopy(_indices, 0, indices, 0, _indices.length);
			System.arraycopy(inIndices, 0, indices, _indices.length,
					inIndices.length);
		}
	}

	/**
	 * Add the given vertices to this mesh data
	 * 
	 * @param vertices
	 */
	public void putPositions(float[] vertices) {
		if (vertices == null) {
			return;
		}
		if (positions == null) {
			positions = vertices;
		} else {
			//
			float[] _positions = positions;
			positions = new float[positions.length + vertices.length];
			System.arraycopy(_positions, 0, positions, 0, _positions.length);
			System.arraycopy(vertices, 0, positions, _positions.length,
					vertices.length);
		}
	}

	/**
	 * 
	 */
	public void putNormals(float[] inNormals) {
		if (inNormals == null) {
			return;
		}
		if (normals == null) {
			normals = inNormals;
		} else {
			//
			float[] _normals = normals;
			normals = new float[normals.length + inNormals.length];
			System.arraycopy(_normals, 0, normals, 0, _normals.length);
			System.arraycopy(inNormals, 0, normals, _normals.length,
					inNormals.length);
		}
	}

	/**
	 * 
	 */
	public void putTexCoords(float[] inTexCoords) {
		if (inTexCoords == null) {
			return;
		}
		if (texCoords == null) {
			texCoords = inTexCoords;
		} else {
			//
			float[] _texCoords = texCoords;
			texCoords = new float[texCoords.length + inTexCoords.length];
			System.arraycopy(_texCoords, 0, texCoords, 0, _texCoords.length);
			System.arraycopy(inTexCoords, 0, texCoords, _texCoords.length,
					inTexCoords.length);
		}
	}

	/**
	 * Construct triangle objects from the given data
	 * 
	 * @return
	 */
	public Triangle[] getTriangles() {

		boolean hasPositions = positions != null;
		boolean hasNormals = normals != null;
		boolean hasTexCoords = texCoords != null;

		if (hasPositions) {
			Triangle[] triangles = new Triangle[(positions.length / 3) / 3];

			for (int i = 0; i < triangles.length; i++) {
				int offset = i * (3 * 3);
				int toffset = i * (3 * 2);
				Vertex p1, p2, p3;
				p1 = p2 = p3 = null;
				if (hasNormals && !hasTexCoords) {
					p1 = new Vertex(new Vec3(positions[offset],
							positions[offset + 1], positions[offset + 2]),
							new Vec3(normals[offset], normals[offset + 1],
									normals[offset + 2]));
					p2 = new Vertex(new Vec3(positions[offset + 3],
							positions[offset + 4], positions[offset + 5]),
							new Vec3(normals[offset + 3],
									normals[offset + 4], normals[offset + 5]));
					p3 = new Vertex(new Vec3(positions[offset + 6],
							positions[offset + 7], positions[offset + 8]),
							new Vec3(normals[offset + 6],
									normals[offset + 7], normals[offset + 8]));
				} else if (!hasNormals && hasTexCoords) {
					p1 = new Vertex(new Vec3(positions[offset],
							positions[offset + 1], positions[offset + 2]),
							new Vec2(texCoords[toffset],
									texCoords[toffset + 1]));
					p2 = new Vertex(new Vec3(positions[offset + 3],
							positions[offset + 4], positions[offset + 5]),
							new Vec2(texCoords[toffset + 2],
									texCoords[toffset + 3]));
					p3 = new Vertex(new Vec3(positions[offset + 6],
							positions[offset + 7], positions[offset + 8]),
							new Vec2(texCoords[toffset + 4],
									texCoords[toffset + 5]));
				} else if (hasNormals && hasTexCoords) {
					p1 = new Vertex(new Vec3(positions[offset],
							positions[offset + 1], positions[offset + 2]),
							new Vec3(normals[offset], normals[offset + 1],
									normals[offset + 2]), new Vec2(
									texCoords[toffset], texCoords[toffset + 1]));
					p2 = new Vertex(new Vec3(positions[offset + 3],
							positions[offset + 4], positions[offset + 5]),
							new Vec3(normals[offset + 3],
									normals[offset + 4], normals[offset + 5]),
							new Vec2(texCoords[toffset + 2],
									texCoords[toffset + 3]));
					p3 = new Vertex(new Vec3(positions[offset + 6],
							positions[offset + 7], positions[offset + 8]),
							new Vec3(normals[offset + 6],
									normals[offset + 7], normals[offset + 8]),
							new Vec2(texCoords[toffset + 4],
									texCoords[toffset + 5]));
				} else if (!hasNormals && !hasTexCoords) {
					p1 = new Vertex(new Vec3(positions[offset],
							positions[offset + 1], positions[offset + 2]));
					p2 = new Vertex(new Vec3(positions[offset + 3],
							positions[offset + 4], positions[offset + 5]));
					p3 = new Vertex(new Vec3(positions[offset + 6],
							positions[offset + 7], positions[offset + 8]));
				} else {
					EngineLogger
							.error("Unknown combination of vertices in mesh data ("
									+ (positions != null)
									+ ", "
									+ (normals != null)
									+ ", "
									+ (texCoords != null) + ")");
				}
				triangles[i] = new Triangle(p1, p2, p3);
			}

			return triangles;

		} else {
			return null;
		}

	}

}
