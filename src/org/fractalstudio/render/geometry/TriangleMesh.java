package org.fractalstudio.render.geometry;

import java.util.LinkedList;

import org.fractalstudio.engine.EngineConfig;
import org.fractalstudio.render.opengl.DisplayList;
import org.lwjgl.opengl.GL11;

public class TriangleMesh extends Mesh {

	/*
	 * The list of triangles for this mesh
	 */
	private LinkedList<Triangle> triangles = new LinkedList<Triangle>();

	/**
	 * Construct a new triangle mesh
	 */
	public TriangleMesh() {
	}

	/**
	 * Add a triangle to this triangle mesh
	 * 
	 * @param triangle
	 */
	public void addTriangle(Triangle triangle) {
		triangles.add(triangle);
	}

	/**
	 * Clear the triangles in this triangle mesh
	 */
	public void clearTriangles() {
		triangles.clear();
	}

	/**
	 * Overriden function that compiles the data to the graphics card
	 */
	@Override
	public void prepareMesh() {

		if (triangles.size() == 0) {
			return; // Nothing to do here
		}

		// As tedious as it sounds we have to convert the triangles into
		// float data
		MeshData meshData = new MeshData();

		float[] positions = triangles.get(0).getP1().getPosition() != null ? new float[triangles
				.size() * 9] : null;
		float[] normals = triangles.get(0).getP1().getNormal() != null ? new float[triangles
				.size() * 9] : null;
		float[] texCoords = triangles.get(0).getP1().getTexCoord() != null ? new float[triangles
				.size() * 6] : null;

		int positionIdx = 0;
		int normalIdx = 0;
		int texCoordIdx = 0;

		for (Triangle triangle : triangles) {
			float[] buffer = null;
			if (positions != null) {
				buffer = triangle.positionArray();
				for (int i = 0; i < buffer.length; i++) {
					positions[positionIdx++] = buffer[i];
				}
			}
			if (normals != null) {
				buffer = triangle.normalArray();
				for (int i = 0; i < buffer.length; i++) {
					normals[normalIdx++] = buffer[i];
				}
			}
			if (texCoords != null) {
				buffer = triangle.texCoordArray();
				for (int i = 0; i < buffer.length; i++) {
					texCoords[texCoordIdx++] = buffer[i];
				}
			}
		}

		meshData.putPositions(positions);
		meshData.putNormals(normals);
		meshData.putTexCoords(texCoords);

		// Are we using display lists or not?
		// EngineConfig.usingModernOpenGL
		makeDisplayList(meshData);

		clearTriangles();
	}
}
