package org.meanworks.engine.asset.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.asset.AssetManager;
import org.meanworks.engine.math.Vec2;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.render.geometry.Mesh;
import org.meanworks.engine.render.geometry.Model;
import org.meanworks.engine.render.geometry.mesh.MeshBuffer;
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
public class OBJLoader {

	private static void readMaterialFile(String materialFile) {

	}

	/**
	 * Loads an obj model
	 * 
	 * @param modelFile
	 * @return
	 */
	public static Model loadModel(String modelFile) {

		/**
		 * Read the file.
		 */

		try {

			List<Vec3> vertices = new LinkedList<Vec3>();
			List<Vec3> normals = new LinkedList<Vec3>();
			List<Vec2> uvs = new LinkedList<Vec2>();
			List<Face> faces = new LinkedList<Face>();

			List<String> lines = new LinkedList<String>();
			BufferedReader br = new BufferedReader(new FileReader(modelFile));

			String inLine = null;
			while ((inLine = br.readLine()) != null) {
				lines.add(inLine);
			}

			// Parse the file
			for (String line : lines) {

				if (line.startsWith("v")) {
					// Parse vertex
					vertices.add(parseVec3(line));
					continue;
				} else if (line.startsWith("vn")) {
					normals.add(parseVec3(line));
				} else if (line.startsWith("f")) {
					// We only support triangles
					// Parse a triangle
					String[] groupSplits = line.split(" ");

					Face face = new Face();

					// Parse face 1
					String[] slashSplits = groupSplits[1].split("/");
					face.v1 = Integer.parseInt(slashSplits[0]);
					if (!slashSplits[1].equals(""))
						face.t1 = Integer.parseInt(slashSplits[1]);
					if (!slashSplits[2].equals(""))
						face.n1 = Integer.parseInt(slashSplits[2]);

					// Parse face 2
					slashSplits = groupSplits[2].split("/");
					face.v2 = Integer.parseInt(slashSplits[0]);
					if (!slashSplits[1].equals(""))
						face.t2 = Integer.parseInt(slashSplits[1]);
					if (!slashSplits[2].equals(""))
						face.n2 = Integer.parseInt(slashSplits[2]);

					// Parse face 3
					slashSplits = groupSplits[3].split("/");
					face.v3 = Integer.parseInt(slashSplits[0]);
					if (!slashSplits[1].equals(""))
						face.t3 = Integer.parseInt(slashSplits[1]);
					if (!slashSplits[2].equals(""))
						face.n3 = Integer.parseInt(slashSplits[2]);

					faces.add(face);
				}

			}

			/*
			 * Post processing
			 */
			boolean hasNormals = normals.size() > 0;
			boolean hasUVs = uvs.size() > 0;

			MeshBuffer mb = new MeshBuffer(vertices.size() * 8,
					faces.size() * 3);

			// Make sure all of the vertices has normals
			ArrayList<Vec3> fixedVertices = new ArrayList<Vec3>(vertices.size());
			ArrayList<Vec3> fixedNormals = new ArrayList<Vec3>(vertices.size());
			ArrayList<Vec2> fixedUVs = new ArrayList<Vec2>(vertices.size());
			for (Face face : faces) {
				if (hasNormals) {
					fixedNormals.add(face.v1 - 1, normals.get(face.n1));
					fixedNormals.add(face.v2 - 1, normals.get(face.n2));
					fixedNormals.add(face.v3 - 1, normals.get(face.n3));
				}
				if (hasUVs) {
					fixedUVs.add(face.v1 - 1, uvs.get(face.t1));
					fixedUVs.add(face.v2 - 1, uvs.get(face.t2));
					fixedUVs.add(face.v3 - 1, uvs.get(face.t3));
				}
				mb.addIndex(face.v3 - 1);
				mb.addIndex(face.v2 - 1);
				mb.addIndex(face.v1 - 1);
			}

			for (int i = 0; i < vertices.size(); i++) {
				mb.addVec3(vertices.get(i));
				if (hasNormals) {
					mb.addVec3(fixedNormals.get(i));
				} else {
					mb.addVec3(new Vec3(0.0f, 1.0f, 0.0f));
				}
				if (hasUVs) {
					mb.addVec2(fixedUVs.get(i));
				} else {
					mb.addVec2(new Vec2(0.0f, 0.0f));
				}
			}

			Model model = new Model();

			Mesh mesh = new Mesh();

			FloatBuffer meshBuffer = mb.getFlippedFloatBuffer();
			IntBuffer meshIdxBuffer = mb.getFlippedIntBuffer();

			// Fill Tri's
			int[] tris = new int[meshIdxBuffer.capacity()];
			meshIdxBuffer.get(tris);

			FloatBuffer posBuffer = BufferUtils.createFloatBuffer(vertices
					.size() * 3);
			FloatBuffer norBuffer = hasNormals ? BufferUtils
					.createFloatBuffer(vertices.size() * 3) : null;
			FloatBuffer uvsBuffer = hasUVs ? BufferUtils
					.createFloatBuffer(vertices.size() * 2) : null;

			for (int i = 0; i < vertices.size(); i++) {

				Vec3 vertex = vertices.get(i);
				posBuffer.put(new float[] {
						vertex.x, vertex.y, vertex.z
				});

				if (hasNormals) {
					Vec3 normal = fixedNormals.get(i);
					norBuffer.put(new float[] {
							normal.x, normal.y, normal.z
					});
				}
				if (hasUVs) {
					Vec2 uv = fixedUVs.get(i);
					uvsBuffer.put(new float[] {
							uv.x, uv.y,
					});
				}
			}

			posBuffer.flip();
			if (norBuffer != null)
				norBuffer.flip();
			if (uvsBuffer != null)
				uvsBuffer.flip();

			float[] posF = new float[vertices.size() * 3];
			float[] norF = new float[vertices.size() * 3];
			float[] uvsF = new float[vertices.size() * 2];

			posBuffer.get(posF);
			if (hasNormals)
				norBuffer.get(norF);
			if (hasUVs)
				uvsBuffer.get(uvsF);

			// If there are no normals or uvs, fake them
			if (!hasNormals) {
				// Ignore, there be no need for normals they are all inited at 0
				// now
			}
			if (!hasUVs) {
				// Also ignore, there be no need.
			}

			mesh.setMaterial(new Material("DEFAULT_MATERIAL", AssetManager
					.loadShader("./data/shaders/colorShader")));
			mesh.positions = posF;
			mesh.normals = norF;
			mesh.uvs = uvsF;
			mesh.triangles = tris;
			if (!mesh.compile()) {
				EngineLogger.error("Could not compile OBJ model.");
			} else {
				EngineLogger.info("OBJ Model seemingly compiled.");
			}

			model.addMesh("modMesh_0", mesh);

			/*
			 * MeshRenderer mr = new MeshRenderer(); Mesh mesh = new Mesh();
			 * mesh.setMeshRenderer(mr); geo.addMesh("mesh_0", mesh);
			 * 
			 * mesh.setMaterial(new Material("DEFAULT_MATERIAL", Application
			 * .getApplication().getAssetManager()
			 * .loadShader("./data/shaders/colorShader")));
			 * 
			 * mr.addIndex(mb.getFlippedIntBuffer(), mb.getNumIndices());
			 * 
			 * VertexBuffer vb = new VertexBuffer(BufferType.ARRAY_BUFFER,
			 * BufferUsage.STATIC_DRAW);
			 * 
			 * vb.bufferData(mb.getFlippedFloatBuffer()); BufferEntry be =
			 * mr.addVertexBuffer(vb); int stride = 8 * 4; be.addAttribute(0, 3,
			 * GL11.GL_FLOAT, false, stride, 0); be.addAttribute(1, 3,
			 * GL11.GL_FLOAT, false, stride, 12); be.addAttribute(2, 2,
			 * GL11.GL_FLOAT, false, stride, 24);
			 * 
			 * mr.compile();
			 */
			return model;

		} catch (Exception ex) {
			EngineLogger.error("Could not load model " + modelFile);
			ex.printStackTrace();
			return null;
		}

	}

	/**
	 * Parse a vec3 from the given line
	 * 
	 * @param line
	 * @return
	 */
	private static Vec3 parseVec3(String line) {
		String[] splits = line.split(" "); // A simple space is the seperator
											// used by blender
		float[] floats = new float[3];
		for (int i = 1; i < 4; i++) {
			floats[i - 1] = Float.parseFloat(splits[i]);
		}
		return new Vec3(floats[0], floats[1], floats[2]);
	}

	/**
	 * Parse a vec2 from the given line
	 * 
	 * @param line
	 * @return
	 */
	private static Vec2 parseVec2(String line) {
		String[] splits = line.split(" "); // A simple space is the seperator
		// used by blender
		float[] floats = new float[2];
		for (int i = 1; i < 3; i++) {
			floats[i - 1] = Float.parseFloat(splits[i]);
		}
		return new Vec2(floats[0], floats[1]);
	}

	/**
	 * Simple container for a face
	 * 
	 * @author Meanz
	 * 
	 */
	private static class Face {
		int v1 = -1;
		int t1 = -1;
		int n1 = -1;

		int v2 = -1;
		int t2 = -1;
		int n2 = -1;

		int v3 = -1;
		int t3 = -1;
		int n3 = -1;
	}

}
