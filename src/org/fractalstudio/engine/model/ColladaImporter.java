package org.fractalstudio.engine.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;

import org.fractalstudio.engine.EngineLogger;
import org.fractalstudio.jcollada.ColladaDocument;
import org.fractalstudio.jcollada.JCollada;
import org.fractalstudio.jcollada.dataflow.DataSource;
import org.fractalstudio.jcollada.dataflow.datatype.FloatArray;
import org.fractalstudio.jcollada.geometry.primitives.TrianglesArray;
import org.fractalstudio.render.geometry.Geometry;
import org.fractalstudio.render.geometry.Mesh;
import org.fractalstudio.render.geometry.Mesh.BufferEntry;
import org.fractalstudio.render.opengl.VertexBuffer;
import org.fractalstudio.render.opengl.VertexBuffer.BufferType;
import org.fractalstudio.render.opengl.VertexBuffer.BufferUsage;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ColladaImporter {

	public static Geometry loadModel(String modelName) {

		try {
			ColladaDocument document = JCollada.parseDocument(modelName);

			if (document == null) {
				EngineLogger.error("Could not load model " + modelName);
				return null;
			}

			Geometry _retGeometry = new Geometry();

			// Create all the meshes
			for (org.fractalstudio.jcollada.geometry.Geometry geometry : document
					.getGeometryLibrary().getGeometries()) {

				_retGeometry.addMesh("mesh_" + geometry.getId(),
						createTriangleMesh(document, geometry));

			}

			return _retGeometry;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Mesh createTriangleMesh(ColladaDocument document,
			org.fractalstudio.jcollada.geometry.Geometry geometry) {

		// Get the mesh of the geometry
		org.fractalstudio.jcollada.geometry.Mesh mesh = geometry.getMesh();

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

		// Put the float data into float buffers
		FloatBuffer positions = BufferUtils.createFloatBuffer(faPositions
				.getCount());

		positions.put(faPositions.getFloats());
		positions.flip();

		FloatBuffer normals = faNormals != null ? BufferUtils
				.createFloatBuffer(faNormals.getCount()) : null;
		if (normals != null) {
			normals.put(faNormals.getFloats());
			normals.flip();
		}

		FloatBuffer texCoords = faTexCoords != null ? BufferUtils
				.createFloatBuffer(faTexCoords.getCount()) : null;
		if (texCoords != null) {
			texCoords.put(faTexCoords.getFloats());
			texCoords.flip();
		}

		IntBuffer indices = BufferUtils
				.createIntBuffer(triangles.getP().length);
		indices.put(triangles.getP());
		indices.flip();

		// Create and compile the vertex buffers
		Mesh _mesh = new Mesh();

		VertexBuffer vbPositions = new VertexBuffer(BufferType.ARRAY_BUFFER,
				BufferUsage.STATIC_DRAW);
		VertexBuffer vbNormals = normals != null ? new VertexBuffer(
				BufferType.ARRAY_BUFFER, BufferUsage.STATIC_DRAW) : null;
		VertexBuffer vbTexCoords = texCoords != null ? new VertexBuffer(
				BufferType.ARRAY_BUFFER, BufferUsage.STATIC_DRAW) : null;
		VertexBuffer vbIndices = new VertexBuffer(BufferType.INDEX_BUFFER,
				BufferUsage.DYNAMIC_DRAW);

		BufferEntry bePositions = _mesh.addVertexBuffer(vbPositions);
		BufferEntry beNormals = _mesh.addVertexBuffer(vbNormals);
		BufferEntry beTexCoords = _mesh.addVertexBuffer(vbTexCoords);

		vbIndices.bufferData(indices);
		_mesh.setIndexBuffer(vbIndices);
		_mesh.setNumIndices(triangles.getP().length);

		vbPositions.bufferData(positions);
		bePositions.addAttribute(0, 3, GL11.GL_FLOAT, false, 0, 0);

		if (vbNormals != null) {
			vbNormals.bufferData(normals);
			beNormals.addAttribute(1, 3, GL11.GL_FLOAT, false, 0, 0);
		}
		if (vbTexCoords != null) {
			vbTexCoords.bufferData(texCoords);
			beTexCoords.addAttribute(2, 2, GL11.GL_FLOAT, false, 0, 0);
		}

		_mesh.compile();

		return _mesh;
	}
}
