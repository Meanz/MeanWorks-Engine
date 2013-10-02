package org.meanworks.engine.model.daetofe;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import org.fractalstudio.flatmath.Matrix4x4;
import org.fractalstudio.jcollada.ColladaDocument;
import org.fractalstudio.jcollada.JCollada;
import org.fractalstudio.jcollada.controllers.Controller;
import org.fractalstudio.jcollada.controllers.Skin;
import org.fractalstudio.jcollada.dataflow.DataSource;
import org.fractalstudio.jcollada.dataflow.datatype.FloatArray;
import org.fractalstudio.jcollada.geometry.Geometry;
import org.fractalstudio.jcollada.geometry.Mesh;
import org.fractalstudio.jcollada.geometry.primitives.TrianglesArray;
import org.fractalstudio.jcollada.visualscene.Node;
import org.fractalstudio.jcollada.visualscene.VisualScene;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.meanworks.engine.EngineLogger;
import org.meanworks.render.geometry.mesh.MeshRenderer;
import org.meanworks.render.geometry.mesh.MeshRenderer.BufferEntry;
import org.meanworks.render.opengl.VertexBuffer;
import org.meanworks.render.opengl.VertexBuffer.BufferType;
import org.meanworks.render.opengl.VertexBuffer.BufferUsage;

/**
 * Copyright (C) 2013 Steffen Evensen
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
public class DaeToFeConverter {

	/**
	 * Container for the mesh
	 * 
	 * @author meanz
	 * 
	 */
	static class FEMesh {

		String meshName;
		float[] positions;
		float[] normals;
		float[] uvs;
		int[] indices;

	}

	static class SkinnedFEMesh extends FEMesh {

		Matrix4f bindShapeMatrix;
		FENode[] joints;
		Matrix4f[] bindPoses;
		FEWeight[] weights;
	}

	static class FEWeight {

		float weightValue;

	}

	/**
	 * A node container
	 * 
	 * @author meanz
	 * 
	 */
	static class FENode {

		/* The id of this node */
		String id;

		/* The parent of this node */
		FEBone parent;

		/* The children of this node */
		LinkedList<FEBone> children = new LinkedList<>();

		/* The node matrix */
		Matrix4f nodeMatrix = new Matrix4f();

		/* The node matrix */
		Matrix4f transform = new Matrix4f();
	}

	/**
	 * A bone container
	 */
	static class FEBone extends FENode {

		FEWeight[] weights;
		int numWeights;

	}

	/**
	 * A animation container
	 */
	static class FEAnimation {

	}

	/**
	 * 
	 */

	/**
	 * Convert the given model
	 * 
	 * @param inputFile
	 */
	public static org.meanworks.render.geometry.Geometry convertModel(
			String inputFile) {

		try {
			ColladaDocument document = JCollada.parseDocument(inputFile);

			if (document == null) {
				EngineLogger.error("Could not convert model " + inputFile);
				return null;
			}

			LinkedList<FEMesh> meshes = new LinkedList<>();
			LinkedList<FENode> nodes = new LinkedList<>();

			/*
			 * Convert
			 */

			// Geometries
			for (Geometry geometry : document.getGeometryLibrary()
					.getGeometries()) {
				meshes.add(convertTriangleMesh(document, geometry));
			}

			// Check if the model has bones
			VisualScene vsScene = document.getVisualSceneLibrary()
					.getVisualScenes().getFirst();

			LinkedList<Node> vsNodes = vsScene.getAllNodes();
			LinkedList<Node> rootNodes = new LinkedList<>();

			/*
			 * Discover root nodes
			 */
			for (Node node : vsNodes) {
				if (node.getParent() == null) {
					rootNodes.add(node);
					// System.err.println("Added root node: " + node.getId());
				}
			}

			/*
			 * Build node hierarchies
			 */
			for (Node node : rootNodes) {
				nodes.add(buildNodeHierarchy(null, node));
			}

			/*
			 * Read Skinning
			 */
			LinkedList<Controller> controllers = document
					.getControllersLibrary().getControllers();

			for (Controller controller : controllers) {

				for (Skin skin : controller.getSkins()) {

					SkinnedFEMesh skinnedMesh = new SkinnedFEMesh();

					// Find the mesh for the controller
					for (FEMesh mesh : meshes) {
						if (skin.getSource().substring(1).equals(mesh.meshName)) {

						}
					}

				}

			}

			/*
			 * Write to output file
			 */
			org.meanworks.render.geometry.Geometry geometry = new org.meanworks.render.geometry.Geometry();

			for (FEMesh feMesh : meshes) {

				org.meanworks.render.geometry.mesh.Mesh mesh = new org.meanworks.render.geometry.mesh.Mesh();
				MeshRenderer meshRenderer = new MeshRenderer();
				mesh.setMeshRenderer(meshRenderer);
				
				/*
				 * Reorganize data
				 */

				if (feMesh.positions != null) {
					VertexBuffer vbPositions = new VertexBuffer(
							BufferType.ARRAY_BUFFER, BufferUsage.STATIC_DRAW);
					FloatBuffer fbPositions = BufferUtils
							.createFloatBuffer(feMesh.positions.length);
					fbPositions.put(feMesh.positions);
					fbPositions.flip();
					vbPositions.bufferData(fbPositions);

					BufferEntry be = meshRenderer.addVertexBuffer(vbPositions);

					be.addAttribute(0, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
				}

				if (feMesh.positions != null) {
					VertexBuffer vbPositions = new VertexBuffer(
							BufferType.ARRAY_BUFFER, BufferUsage.STATIC_DRAW);
					FloatBuffer fbPositions = BufferUtils
							.createFloatBuffer(feMesh.positions.length);
					fbPositions.put(feMesh.positions);
					fbPositions.flip();
					vbPositions.bufferData(fbPositions);

					BufferEntry be = meshRenderer.addVertexBuffer(vbPositions);

					be.addAttribute(0, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
				}

				if (feMesh.normals != null) {
					VertexBuffer vbNormals = new VertexBuffer(
							BufferType.ARRAY_BUFFER, BufferUsage.STATIC_DRAW);
					FloatBuffer fbNormals = BufferUtils
							.createFloatBuffer(feMesh.normals.length);
					fbNormals.put(feMesh.normals);
					fbNormals.flip();
					vbNormals.bufferData(fbNormals);

					BufferEntry be = meshRenderer.addVertexBuffer(vbNormals);

					be.addAttribute(1, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
				}

				if (feMesh.uvs != null) {
					VertexBuffer vbTexCoords = new VertexBuffer(
							BufferType.ARRAY_BUFFER, BufferUsage.STATIC_DRAW);
					FloatBuffer fbTexCoords = BufferUtils
							.createFloatBuffer(feMesh.uvs.length);
					fbTexCoords.put(feMesh.uvs);
					fbTexCoords.flip();
					vbTexCoords.bufferData(fbTexCoords);

					BufferEntry be = meshRenderer.addVertexBuffer(vbTexCoords);

					be.addAttribute(2, 2, GL11.GL_FLOAT, false, 2 * 4, 0);
				}

				if (feMesh.indices == null) {
					System.err.println("No indices! Panick!");
				} else {

					VertexBuffer vbIndices = new VertexBuffer(
							BufferType.INDEX_BUFFER, BufferUsage.DYNAMIC_DRAW);
					IntBuffer ibIndices = BufferUtils
							.createIntBuffer(feMesh.indices.length);
					ibIndices.put(feMesh.indices);
					ibIndices.flip();
					vbIndices.bufferData(ibIndices);

					meshRenderer.setIndexBuffer(vbIndices);
					meshRenderer.setNumIndices(feMesh.indices.length);

				}

				if (feMesh.meshName.contains("bounding")
						|| feMesh.meshName.contains("picking")) {
					continue;
				}

				geometry.addMesh("mesh_" + feMesh.meshName, mesh);
				System.err
						.println("Added mesh: " + ("mesh_" + feMesh.meshName));

				meshRenderer.compile();

			}

			return geometry;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Convert a collada matrix to a matrix4f
	 * 
	 * @param inMatrix
	 * @return
	 */
	public static Matrix4f toMatrix4f(Matrix4x4 inMatrix) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		inMatrix.store(buffer);
		buffer.flip();
		Matrix4f out = new Matrix4f();
		out.load(buffer);
		return out;
	}

	/*
	 * Debug depth
	 */
	private static int depth = 0;

	/**
	 * Helper function for debugging
	 * 
	 * @param name
	 */
	public static void printName(String name) {
		String output = "";
		for (int i = 0; i < depth; i++) {
			output += "-";
		}
		System.err.println(output + name);
	}

	/*
	 * Function for building node hierarchies
	 */
	public static FEBone buildNodeHierarchy(FEBone parent, Node node) {

		FEBone bone = new FEBone();
		bone.parent = parent;
		bone.id = node.getId();
		bone.nodeMatrix = toMatrix4f(node.getTransform().getMatrix());
		bone.transform = Matrix4f.mul(parent != null ? parent.transform
				: new Matrix4f(), toMatrix4f(node.getTransform().getMatrix()),
				null);

		// printName(node.getId());

		for (Node child : node.getChildren()) {
			depth++;
			bone.children.add(buildNodeHierarchy(bone, child));
			depth--;
		}

		return bone;
	}

	public static FEMesh flipUVs(FEMesh mesh) {
		if (mesh.uvs == null) {
			return mesh;
		}
		for (int i = 0; i < mesh.uvs.length / 2; i++) {
			mesh.uvs[i * 2 + 1] = 1.0f - mesh.uvs[i * 2 + 1];
		}
		return mesh;
	}

	public static FEMesh deIndex(FEMesh mesh, int vertOff, int normOff,
			int texOff) {

		boolean hasVerts = vertOff == -1 ? false : true;
		boolean hasNorms = normOff == -1 ? false : true;
		boolean hasUVs = texOff == -1 ? false : true;

		int count = (hasVerts ? 1 : 0) + (hasNorms ? 1 : 0) + (hasUVs ? 1 : 0);
		float[] newVerts, newNorms, newTexs;

		newVerts = new float[(mesh.indices.length / count) * 3];
		newNorms = hasNorms ? new float[(mesh.indices.length / count) * 3]
				: null;
		newTexs = hasUVs ? new float[(mesh.indices.length / count) * 2] : null;

		/*
		 * Perform conversion
		 */
		for (int i = 0; i < mesh.indices.length / count; i++) {

			int vert, norm, tex;
			vert = norm = tex = 0;

			vert = mesh.indices[i * count + vertOff];
			if (hasNorms)
				norm = mesh.indices[i * count + normOff];
			if (hasUVs)
				tex = mesh.indices[i * count + texOff];

			newVerts[i * 3] = mesh.positions[vert * 3];
			newVerts[i * 3 + 1] = mesh.positions[vert * 3 + 1];
			newVerts[i * 3 + 2] = mesh.positions[vert * 3 + 2];

			if (hasNorms) {
				newNorms[i * 3] = mesh.normals[norm * 3];
				newNorms[i * 3 + 1] = mesh.normals[norm * 3 + 1];
				newNorms[i * 3 + 2] = mesh.normals[norm * 3 + 2];
			}
			if (hasUVs) {
				newTexs[i * 2] = mesh.uvs[tex * 2];
				newTexs[i * 2 + 1] = mesh.uvs[tex * 2 + 1];
			}
		}

		// Assign newly created data
		mesh.positions = newVerts;
		mesh.normals = newNorms;
		mesh.uvs = newTexs;

		// Fixup indices
		int[] newIndices = new int[mesh.indices.length / count];
		for (int i = 0; i < newIndices.length; i++) {
			newIndices[i] = i;
		}
		mesh.indices = newIndices;
		EngineLogger.info("[DeIndexer] Complete.");
		return mesh;
	}

	/**
	 * Convert a triangle mesh to a FEMesh
	 * 
	 * @param document
	 * @param geometry
	 * @return
	 */
	public static FEMesh convertTriangleMesh(ColladaDocument document,
			Geometry geometry) {

		// Get the mesh of the geometry
		Mesh mesh = geometry.getMesh();

		// Get the triangle description from this mesh
		TrianglesArray triangles = mesh.getTriangles().getFirst();
		if (triangles == null) {
			return null;
		}

		// Get url's for the sources of the vertex data
		String positionSource = mesh.getVertices().getInputPipe("POSITION") != null ? mesh
				.getVertices().getInputPipe("POSITION").getSource()
				: null;
		String normalSource = triangles.getInputPipe("NORMAL") != null ? triangles
				.getInputPipe("NORMAL").getSource() : null;
		String texcoordSource = triangles.getInputPipe("TEXCOORD") != null ? triangles
				.getInputPipe("TEXCOORD").getSource() : null;

		// Get the data sources
		DataSource srcPositions = mesh
				.getDataSource(positionSource != null ? positionSource
						.substring(1) : null);
		DataSource srcNormals = mesh
				.getDataSource(normalSource != null ? normalSource.substring(1)
						: null);
		DataSource srcTexCoords = mesh
				.getDataSource(texcoordSource != null ? texcoordSource
						.substring(1) : null);

		FloatArray faPositions = srcPositions != null ? srcPositions
				.asFloatArray() : null;
		FloatArray faNormals = srcNormals != null ? srcNormals.asFloatArray()
				: null;
		FloatArray faTexCoords = srcTexCoords != null ? srcTexCoords
				.asFloatArray() : null;

		// Create our FEMesh
		FEMesh feMesh = new FEMesh();
		feMesh.meshName = geometry.getId();
		feMesh.positions = faPositions != null ? faPositions.getFloats() : null;
		feMesh.normals = faNormals != null ? faNormals.getFloats() : null;
		feMesh.uvs = faTexCoords != null ? faTexCoords.getFloats() : null;
		feMesh.indices = triangles.getP();

		int vertOff = triangles.getInputPipe("VERTEX").getOffset();
		int normOff = triangles.getInputPipe("NORMAL") != null ? triangles
				.getInputPipe("NORMAL").getOffset() : -1;
		int texCoordOff = triangles.getInputPipe("TEXCOORD") != null ? triangles
				.getInputPipe("TEXCOORD").getOffset() : -1;

		// Check if we need to deindex
		if (feMesh.indices.length > triangles.getCount() * 3) {
			System.err.println("DeIndexing!");
			deIndex(feMesh, vertOff, normOff, texCoordOff);
			flipUVs(feMesh);
		}

		return feMesh;
	}

}
