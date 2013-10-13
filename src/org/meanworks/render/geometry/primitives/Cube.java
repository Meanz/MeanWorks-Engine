package org.meanworks.render.geometry.primitives;

import org.meanworks.engine.scene.Geometry;

public class Cube extends Geometry {

	float[] positions = null;
	float[] normals = null;
	float[] texCoords = null;
	int posOff = 0;
	int normOff = 0;
	int texOff = 0;

	public Cube(float w, float h, float l) {
		final float minx = -(w / 2);
		final float miny = -(h / 2);
		final float minz = -(l / 2);
		final float maxx = (w / 2);
		final float maxy = (h / 2);
		final float maxz = (l / 2);

		int numVerts = 6 * 6;
		positions = new float[numVerts * 3];
		normals = new float[numVerts * 3];
		texCoords = new float[numVerts * 2];

		// V2 - V1 - V3 - V4
		// V2 - V1 - V4
		// V1 - V3 - V4

		// V4 - V3 - V5 - V6
		// V4 - V3 - V6
		// V3 - V5 - V6
		
		//V6 - V5 - V7 - V8
		//V6 - V5 - V8
		//V5 - V7 - V8

		// Quad 1
		addNormal(1, 0, 0);
		addNormal(1, 0, 0);
		addNormal(1, 0, 0);
		addNormal(1, 0, 0);
		addNormal(1, 0, 0);
		addNormal(1, 0, 0);
		
		addTexCoord(0, 1);
		addVertex(maxx, maxy, maxz); // V2
		addTexCoord(0, 0);
		addVertex(maxx, miny, maxz); // V1
		addTexCoord(1, 1);
		addVertex(maxx, maxy, minz); // V4
		addTexCoord(0, 0);
		addVertex(maxx, miny, maxz); // V1
		addTexCoord(1, 0);
		addVertex(maxx, miny, minz); // V3
		addTexCoord(1, 1);
		addVertex(maxx, maxy, minz); // V4

		// Quad 2
		addNormal(0, 0, -1);
		addNormal(0, 0, -1);
		addNormal(0, 0, -1);
		addNormal(0, 0, -1);
		addNormal(0, 0, -1);
		addNormal(0, 0, -1);
		addTexCoord(0, 1);
		addVertex(maxx, maxy, minz); // V4
		addTexCoord(0, 0);
		addVertex(maxx, miny, minz); // V3
		addTexCoord(1, 1);
		addVertex(minx, maxy, minz); // V6
		addTexCoord(0, 0);
		addVertex(maxx, miny, minz); // V3
		addTexCoord(1, 0);
		addVertex(minx, miny, minz); // V5
		addTexCoord(1, 1);
		addVertex(minx, maxy, minz); // V6

		// Quad 3
		addNormal(-1, 0, 0);
		addNormal(-1, 0, 0);
		addNormal(-1, 0, 0);
		addNormal(-1, 0, 0);
		addNormal(-1, 0, 0);
		addNormal(-1, 0, 0);
		addTexCoord(0, 1);
		addVertex(minx, maxy, minz); // V6
		addTexCoord(0, 0);
		addVertex(minx, miny, minz); // V5
		addTexCoord(1, 1);
		addVertex(minx, maxy, maxz); // V8
		addTexCoord(0, 0);
		addVertex(minx, miny, minz); // V5
		addTexCoord(1, 0);
		addVertex(minx, miny, maxz); // V7
		addTexCoord(1, 1);
		addVertex(minx, maxy, maxz); // V8
		// Quad 4
		addNormal(0, 0, 1);
		addNormal(0, 0, 1);
		addNormal(0, 0, 1);
		addNormal(0, 0, 1);
		addNormal(0, 0, 1);
		addNormal(0, 0, 1);
		addTexCoord(0, 1);
		addVertex(minx, maxy, maxz); // V8
		addTexCoord(0, 0);
		addVertex(minx, miny, maxz); // V7
		addTexCoord(1, 1);
		addVertex(maxx, maxy, maxz); // V2
		addTexCoord(0, 0);
		addVertex(minx, miny, maxz); // V7
		addTexCoord(1, 0);
		addVertex(maxx, miny, maxz); // V1
		addTexCoord(1, 1);
		addVertex(maxx, maxy, maxz); // V2
		// Quad 5
		addNormal(0, 1, 0);
		addNormal(0, 1, 0);
		addNormal(0, 1, 0);
		addNormal(0, 1, 0);
		addNormal(0, 1, 0);
		addNormal(0, 1, 0);
		addTexCoord(0, 1);
		addVertex(minx, maxy, minz); // V6
		addTexCoord(0, 0);
		addVertex(minx, maxy, maxz); // V8
		addTexCoord(1, 1);
		addVertex(maxx, maxy, minz); // V4
		addTexCoord(0, 0);
		addVertex(minx, maxy, maxz); // V8
		addTexCoord(1, 0);
		addVertex(maxx, maxy, maxz); // V2
		addTexCoord(1, 1);
		addVertex(maxx, maxy, minz); // V4
		// Quad 6
		addNormal(0, -1, 0);
		addNormal(0, -1, 0);
		addNormal(0, -1, 0);
		addNormal(0, -1, 0);
		addNormal(0, -1, 0);
		addNormal(0, -1, 0);
		addTexCoord(0, 1);
		addVertex(minx, miny, maxz); // V7
		addTexCoord(0, 0);
		addVertex(minx, miny, minz); // V5
		addTexCoord(1, 1);
		addVertex(maxx, miny, maxz); // V1
		addTexCoord(1, 0);
		addVertex(maxx, miny, minz); // V3
		addTexCoord(1, 1);
		addVertex(maxx, miny, maxz); // V1
		addTexCoord(0, 0);
		addVertex(minx, miny, minz); // V5

		//
		System.err.println("Can't use cube, it's broken :D");
	}

	public void addVertex(float x, float y, float z) {
		positions[posOff++] = x;
		positions[posOff++] = y;
		positions[posOff++] = z;
	}

	public void addNormal(float x, float y, float z) {
		normals[normOff++] = x;
		normals[normOff++] = y;
		normals[normOff++] = z;
	}

	public void addTexCoord(float u, float v) {
		texCoords[texOff++] = u;
		texCoords[texOff++] = v;
	}

}