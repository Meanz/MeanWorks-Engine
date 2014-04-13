package org.meanworks.engine.asset.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import org.meanworks.engine.render.texture.Texture;

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

	/**
	 * 
	 * @author Meanz
	 * 
	 */
	public static class OBJFile {

		/**
		 * The thingies use a shared vertex information system
		 */
		public List<Vec3> vertices = new LinkedList<Vec3>();
		public List<Vec3> normals = new LinkedList<Vec3>();
		public List<Vec2> uvs = new LinkedList<Vec2>();

		/**
		 * 
		 */
		public List<OBJMesh> meshes = new LinkedList<OBJMesh>();

		/**
		 * 
		 */
		public List<OBJMaterial> materials = new LinkedList<OBJMaterial>();

		/**
		 * 
		 */
		private OBJFile() {

		}

		/**
		 * 
		 * @param materialName
		 * @return
		 */
		public OBJMaterial getMaterial(String materialName) {
			for (OBJMaterial mat : materials) {
				if (mat.name != null) {
					if (mat.name.equals(materialName)) {
						return mat;
					}
				}
			}
			return null;
		}

		/**
		 * 
		 * @param file
		 * @return
		 * @throws IOException
		 */
		public static OBJFile read(File file) throws IOException {
			List<String> lines = new LinkedList<String>();
			BufferedReader br = new BufferedReader(new FileReader(file));

			String inLine = null;
			while ((inLine = br.readLine()) != null) {
				lines.add(inLine);
			}
			br.close();

			OBJFile objfile = new OBJFile();
			objfile.parse(lines, file.getAbsolutePath());
			return objfile;
		}

		/**
		 * 
		 * @param materialFile
		 * @return
		 */
		private List<OBJMaterial> readMaterialFile(String materialFile) {
			try {
				List<String> lines = new LinkedList<String>();
				BufferedReader br = new BufferedReader(new FileReader(
						materialFile));

				String inLine = null;
				while ((inLine = br.readLine()) != null) {
					lines.add(inLine);
				}
				br.close();
				OBJMaterial currMat = null;
				// Parse the lines
				for (String line : lines) {
					if (line.startsWith("newmtl")) {
						// Create a new material
						currMat = new OBJMaterial();
						materials.add(currMat);
						// Get the material name
						currMat.name = line.split("newmtl ")[1];
					} else if (line.startsWith("Ns")) {
						currMat.specular = parseFloat(line.split("Ns ")[1]);
					} else if (line.startsWith("Ka")) {
						currMat.ambientColor = parseVec3(line.split("Ka ")[1]);
					} else if (line.startsWith("Kd")) {
						currMat.diffuseColor = parseVec3(line.split("Kd ")[1]);
					} else if (line.startsWith("Ks")) {
						currMat.specularColor = parseVec3(line.split("Ks ")[1]);
					} else if (line.startsWith("Ni")) {
						currMat.Ni = parseFloat(line.split("Ni ")[1]);
					} else if (line.startsWith("d")) {
						currMat.d = parseFloat(line.split("d ")[1]);
					} else if (line.startsWith("illum")) {
						currMat.illum = parseInt(line.split("illum ")[1]);
					} else if (line.startsWith("map_Kd")) {
						currMat.map_Kd = line.split("map_Kd ")[1];
					}
				}
				return materials;
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return new LinkedList<OBJMaterial>();

		}

		/**
		 * Parses the given set of lines
		 * 
		 * @param lines
		 */
		private void parse(List<String> lines, String filePath) {
			OBJMesh currentMesh = new OBJMesh();
			// Parse the file
			for (String line : lines) {

				if (line.startsWith("mtllib")) {
					// Read material lib
					String materialFile = line.split("mtllib ")[1];
					String fullPath = new File(filePath).getParent() + "/"
							+ materialFile;
					System.err.println("mtllib: " + materialFile);
					if (new File(materialFile).exists()) {
						materials = readMaterialFile(materialFile);
					}
				} else if (line.startsWith("o")) {
					currentMesh = new OBJMesh();
					currentMesh.name = line.split("o ")[1];
					System.err.println("OBJ o " + currentMesh.name);
					meshes.add(currentMesh);
				} else if (line.startsWith("usemtl")) {
					currentMesh.material = line.split("usemtl ")[1];
				} else if (line.startsWith("v ")) {
					// Parse vertex
					vertices.add(parseVec3(line));
				} else if (line.startsWith("vn ")) {
					normals.add(parseVec3(line));
				} else if (line.startsWith("vt ")) {
					uvs.add(parseVec2(line));
				} else if (line.startsWith("f ")) {
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

					currentMesh.faces.add(face);
				}
			}

			if (meshes.size() == 0) {
				meshes.add(currentMesh);
			}
		}
	}

	/**
	 * 
	 * @author Meanz
	 * 
	 */
	public static class OBJMesh {
		String name;
		String material;
		List<Face> faces = new LinkedList<Face>();
	}

	/**
	 * 
	 * @author Meanz
	 * 
	 */
	public static class OBJMaterial {
		String name;
		float specular = 0;
		Vec3 ambientColor = new Vec3();
		Vec3 diffuseColor = new Vec3();
		Vec3 specularColor = new Vec3();
		float Ni = 0; // Illumination?
		float d = 0; // Diffuse intensity?
		int illum = 0; // Illum
		String map_Kd = null; // Diffuse map
	}

	/**
	 * 
	 * @author Meanz
	 * 
	 */
	public static class OBJVertex {
		Vec3 position;
		Vec3 normal;
		Vec2 uv;
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
			OBJFile file = OBJFile.read(new File(modelFile));
			Model model = new Model();
			/*
			 * Post processing
			 */
			int cnt = 0;
			for (OBJMesh mesh : file.meshes) {
				boolean hasNormals = file.normals.size() > 0;
				boolean hasUVs = file.uvs.size() > 0;

				List<OBJVertex> vertices = new LinkedList<OBJVertex>();

				ArrayList<Vec3> _pos = new ArrayList<Vec3>(vertices.size() * 3);
				ArrayList<Vec3> _nor = new ArrayList<Vec3>(vertices.size() * 3);
				ArrayList<Vec2> _uvs = new ArrayList<Vec2>(vertices.size() * 2);
				
				// For each face create 3 vertex thingies
				for (Face face : mesh.faces) {

					OBJVertex v1 = new OBJVertex();
					v1.position = file.vertices.get(face.v1 - 1);
					if (hasNormals) {
						v1.normal = file.normals.get(face.n1 - 1);
					}
					if (hasUVs) {
						v1.uv = file.uvs.get(face.t1 - 1);
					}
					OBJVertex v2 = new OBJVertex();
					v2.position = file.vertices.get(face.v2 - 1);
					if (hasNormals) {
						v2.normal = file.normals.get(face.n2 - 1);
					}
					if (hasUVs) {
						v2.uv = file.uvs.get(face.t2 - 1);
					}
					OBJVertex v3 = new OBJVertex();
					v3.position = file.vertices.get(face.v3 - 1);
					if (hasNormals) {
						v3.normal = file.normals.get(face.n3 - 1);
					}
					if (hasUVs) {
						v3.uv = file.uvs.get(face.t3 - 1);
					}
					
					_pos.add(v1.position);
					_pos.add(v2.position);
					_pos.add(v3.position);
					if(hasNormals) {
						_nor.add(v1.normal);
						_nor.add(v2.normal);
						_nor.add(v3.normal);
					}
					if(hasUVs) {
						_uvs.add(v1.uv);
						_uvs.add(v2.uv);
						_uvs.add(v3.uv);
					}
				}
				
				//We got the data, now fill it into a mesh
				
				
				Mesh _mesh = new Mesh();
				_mesh.setMaterial(new Material("DEFAULT_MATERIAL", AssetManager
						.loadShader("./data/shaders/colorShader")));
				
				//Make float arrays of our data
				_mesh.positions = fListToArray3(_pos);
				_mesh.normals = hasNormals ? fListToArray3(_nor) : null;
				_mesh.uvs = hasUVs ? fListToArray2(_uvs) : null;
				
				//Create our index buffer, it's just 0-n
				//so is ezy
				int[] indices = new int[_pos.size()];
				for(int i=0; i < indices.length; i++) {
					indices[i] = i;
				}
				_mesh.triangles = indices;
				
				if(false) {

				MeshBuffer mb = new MeshBuffer(file.vertices.size() * 8,
						mesh.faces.size() * 3);

				// Make sure all of the vertices has normals
				ArrayList<Vec3> fixedVertices = new ArrayList<Vec3>(
						file.vertices.size());
				ArrayList<Vec3> fixedNormals = new ArrayList<Vec3>(
						file.vertices.size());
				ArrayList<Vec2> fixedUVs = new ArrayList<Vec2>(
						file.vertices.size());
				for (Face face : mesh.faces) {
					if (hasNormals) {
						fixedNormals
								.add(face.n1 - 1, file.normals.get(face.n1));
						fixedNormals
								.add(face.n2 - 1, file.normals.get(face.n2));
						fixedNormals
								.add(face.n3 - 1, file.normals.get(face.n3));
					}
					if (hasUVs) {
						fixedUVs.add(face.t1 - 1, file.uvs.get(face.t1));
						fixedUVs.add(face.t2 - 1, file.uvs.get(face.t2));
						fixedUVs.add(face.t3 - 1, file.uvs.get(face.t3));
					}
					mb.addIndex(face.v3 - 1);
					mb.addIndex(face.v2 - 1);
					mb.addIndex(face.v1 - 1);
				}

				for (int i = 0; i < file.vertices.size(); i++) {
					mb.addVec3(file.vertices.get(i));
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

				FloatBuffer meshBuffer = mb.getFlippedFloatBuffer();
				IntBuffer meshIdxBuffer = mb.getFlippedIntBuffer();

				// Fill Tri's
				int[] tris = new int[meshIdxBuffer.capacity()];
				meshIdxBuffer.get(tris);

				FloatBuffer posBuffer = BufferUtils
						.createFloatBuffer(file.vertices.size() * 3);
				FloatBuffer norBuffer = hasNormals ? BufferUtils
						.createFloatBuffer(file.vertices.size() * 3) : null;
				FloatBuffer uvsBuffer = hasUVs ? BufferUtils
						.createFloatBuffer(file.vertices.size() * 2) : null;

				for (int i = 0; i < file.vertices.size(); i++) {

					Vec3 vertex = file.vertices.get(i);
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

				float[] posF = new float[file.vertices.size() * 3];
				float[] norF = new float[file.vertices.size() * 3];
				float[] uvsF = new float[file.vertices.size() * 2];

				posBuffer.get(posF);
				if (hasNormals)
					norBuffer.get(norF);
				if (hasUVs)
					uvsBuffer.get(uvsF);

				// If there are no normals or uvs, fake them
				if (!hasNormals) {
					// Ignore, there be no need for normals they are all inited
					// at 0
					// now
				}
				if (!hasUVs) {
					// Also ignore, there be no need.
				}

				_mesh.setMaterial(new Material("DEFAULT_MATERIAL", AssetManager
						.loadShader("./data/shaders/colorShader")));
				_mesh.positions = posF;
				_mesh.normals = norF;
				_mesh.uvs = uvsF;
				_mesh.triangles = tris;
				
				}

				/**
				 * Set some material values
				 */
				OBJMaterial mat = file.getMaterial(mesh.material);
				if (mat != null) {
					// Check if the material has a texture
					if (mat.map_Kd != null) {

						// It does have a texture, try to find it
						Texture texture = AssetManager.loadTexture(new File(
								modelFile).getParent() + "/" + mat.map_Kd);
						if (texture != null) {
							_mesh.getMaterial().setTexture(texture);
						} else {
							// Try again
							texture = AssetManager.loadTexture(mat.map_Kd);
							if (texture != null) {
								_mesh.getMaterial().setTexture(texture);
							} else {
								System.err.println("Could not find texture "
										+ mat.map_Kd);
							}
						}
					}
				}

				if (!_mesh.compile()) {
					EngineLogger.error("Could not compile OBJ model.");
				} else {
					EngineLogger.info("OBJ Model seemingly compiled.");
				}

				model.addMesh("modMesh_" + (cnt++), _mesh);
			}

			return model;

		} catch (Exception ex) {
			EngineLogger.error("Could not load model " + modelFile);
			ex.printStackTrace();
			return null;
		}

	}
	
	private static float[] fListToArray2(List<Vec2> list) {
		int off = 0;
		float[] fl = new float[list.size() * 2];
		for(Vec2 v : list) {
			fl[off++] = v.x;
			fl[off++] = v.y;
		}
		return fl;
	}

	private static float[] fListToArray3(List<Vec3> list) {
		int off = 0;
		float[] fl = new float[list.size() * 3];
		for(Vec3 v : list) {
			fl[off++] = v.x;
			fl[off++] = v.y;
			fl[off++] = v.z;
		}
		return fl;
	}
	
	/**
	 * 
	 * @param line
	 * @return
	 */
	private static float parseFloat(String line) {
		try {
			return Float.parseFloat(line);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * @param line
	 * @return
	 */
	private static int parseInt(String line) {
		try {
			return Integer.parseInt(line);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
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
