package org.fractalstudio.engine.model;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import org.fractalstudio.render.geometry.Geometry;
import org.fractalstudio.render.geometry.animation.Animation;
import org.fractalstudio.render.geometry.animation.AnimationNode;
import org.fractalstudio.render.geometry.animation.AnimationNode.QuatKey;
import org.fractalstudio.render.geometry.animation.AnimationNode.Vec3Key;
import org.fractalstudio.render.geometry.mesh.Mesh;
import org.fractalstudio.render.geometry.mesh.MeshRenderer;
import org.fractalstudio.render.geometry.mesh.MeshRenderer.BufferEntry;
import org.fractalstudio.render.opengl.VertexBuffer;
import org.fractalstudio.render.opengl.VertexBuffer.BufferType;
import org.fractalstudio.render.opengl.VertexBuffer.BufferUsage;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

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
public class MWMLoader {

	public static String getString(ByteBuffer bb) {
		String s = "";
		char c;
		while ((c = (char) bb.get()) != '\0') {
			s += c;
		}
		return s;
	}

	public static Matrix4f getMat4(ByteBuffer bb) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		for (int i = 0; i < 16; i++) {
			fb.put(bb.getFloat());
		}
		fb.flip();
		Matrix4f mat4 = new Matrix4f();
		mat4.load(fb);
		return mat4;
	}

	public static Geometry loadModel(String fileName) {
		Geometry geometry = new Geometry();
		try {

			RandomAccessFile raf = new RandomAccessFile(fileName, "r");
			byte[] data = new byte[(int) raf.length()];
			raf.read(data);
			raf.close();

			ByteBuffer bb = ByteBuffer.wrap(data);

			bb.order(ByteOrder.nativeOrder());

			LinkedList<Mesh> meshes = new LinkedList<>();
			LinkedList<Animation> animations = new LinkedList<>();

			int meshId = 0; // To keep track of meshes
			int version = bb.getInt();
			System.err.println("[Model version: " + version + " size: "
					+ bb.remaining() + "]");
			if (version == 1) {

				while (bb.hasRemaining()) {

					int task = bb.getInt();
					System.err.println("Task: " + task);
					switch (task) {

					// Mesh
					case 10:
						meshId++;
						String meshName = getString(bb);
						int numVertices = bb.getInt();
						boolean hasVertices = bb.getInt() == 1 ? true : false;
						boolean hasNormals = bb.getInt() == 1 ? true : false;
						boolean hasUVs = bb.getInt() == 1 ? true : false;
						String textureFile = getString(bb);
						System.err.println("[Mesh " + "numVertices="
								+ numVertices + "" + " hasVertices="
								+ hasVertices + "" + " hasNormals="
								+ hasNormals + "" + " hasUVs=" + hasUVs + ""
								+ " texture=" + textureFile + "]");

						boolean hasBoneData = bb.getInt() == 1 ? true : false;

						if (hasBoneData) {
							int numBonesPerVertex = bb.getInt();

							for (int i = 0; i < numVertices; i++) {

								for (int j = 0; j < numBonesPerVertex; j++) {

									int id = bb.getInt();
									float weight = bb.getFloat();

								}

							}
						}

						FloatBuffer vertices = BufferUtils
								.createFloatBuffer(numVertices * 3);
						FloatBuffer normals = BufferUtils
								.createFloatBuffer(numVertices * 3);
						FloatBuffer uvs = BufferUtils
								.createFloatBuffer(numVertices * 2);

						if (hasVertices) {
							for (int i = 0; i < numVertices; i++) {
								vertices.put(bb.getFloat()).put(bb.getFloat())
										.put(bb.getFloat());
							}
						}
						if (hasNormals) {
							for (int i = 0; i < numVertices; i++) {
								normals.put(bb.getFloat()).put(bb.getFloat())
										.put(bb.getFloat());
							}
						}
						if (hasUVs) {
							for (int i = 0; i < numVertices; i++) {
								uvs.put(bb.getFloat()).put(bb.getFloat());
							}
						}

						vertices.flip();
						normals.flip();
						uvs.flip();

						// Read indices
						int numIndices = bb.getInt();
						IntBuffer indices = BufferUtils
								.createIntBuffer(numIndices);
						for (int i = 0; i < numIndices; i++) {
							indices.put(bb.getInt());
						}
						indices.flip();

						// Let's build a mesh out of this hehe
						MeshRenderer meshRenderer = new MeshRenderer();
						Mesh mesh = new Mesh();
						mesh.setMeshRenderer(meshRenderer);

						VertexBuffer vbPositions = new VertexBuffer(
								BufferType.ARRAY_BUFFER,
								BufferUsage.STATIC_DRAW);
						VertexBuffer vbNormals = new VertexBuffer(
								BufferType.ARRAY_BUFFER,
								BufferUsage.STATIC_DRAW);
						VertexBuffer vbUVs = new VertexBuffer(
								BufferType.ARRAY_BUFFER,
								BufferUsage.STATIC_DRAW);
						VertexBuffer vbIndices = new VertexBuffer(
								BufferType.INDEX_BUFFER,
								BufferUsage.DYNAMIC_DRAW);

						vbPositions.bufferData(vertices);
						vbNormals.bufferData(normals);
						vbUVs.bufferData(uvs);
						vbIndices.bufferData(indices);

						BufferEntry bePositions = meshRenderer
								.addVertexBuffer(vbPositions);
						bePositions.addAttribute(0, 3, GL11.GL_FLOAT, false,
								3 * 4, 0);

						BufferEntry beNormals = meshRenderer
								.addVertexBuffer(vbNormals);
						beNormals.addAttribute(1, 3, GL11.GL_FLOAT, false,
								3 * 4, 0);

						BufferEntry beUVs = meshRenderer.addVertexBuffer(vbUVs);
						beUVs.addAttribute(2, 2, GL11.GL_FLOAT, false, 2 * 4, 0);

						meshRenderer.setIndexBuffer(vbIndices);
						meshRenderer.setNumIndices(numIndices);

						meshRenderer.compile();
						geometry.addMesh("mesh_" + meshName + "_" + meshId,
								mesh);
						System.err.println("Added mesh: "
								+ ("mesh_" + meshName + "_" + meshId));
						
						meshes.add(mesh);
						break;

					// Read skeleton
					case 11:

						int numNodes = bb.getInt();
						System.err
								.println("[Nodes numNodes: " + numNodes + "]");

						for (int i = 0; i < numNodes; i++) {

							System.err.println("[Node id=" + getString(bb)
									+ " parent=" + getString(bb) + "]");

							Matrix4f localTransform = getMat4(bb);

						}

						break;

					// Animation
					case 12:

						String animationName = getString(bb);
						int numAnimationNodes = bb.getInt();
						double duration = bb.getDouble();
						double ticksPerSecond = bb.getDouble();

						Animation animation = new Animation(animationName,
								numAnimationNodes, duration, ticksPerSecond);

						System.err.println("[Animation name=" + animationName
								+ " numNodes=" + numAnimationNodes
								+ " duration=" + duration + " ticksPerSecond="
								+ ticksPerSecond + "]");

						for (int i = 0; i < numAnimationNodes; i++) {

							String nodeName = getString(bb);
							int numPositionKeys = bb.getInt();
							int numRotationKeys = bb.getInt();
							int numScalingKeys = bb.getInt();

							AnimationNode node = new AnimationNode(nodeName,
									numPositionKeys, numRotationKeys,
									numScalingKeys);

							System.err.println("\t[AnimationNode name="
									+ nodeName + " numPositionKeys="
									+ numPositionKeys + " numRotationKeys="
									+ numRotationKeys + " numScalingKeys="
									+ numScalingKeys + "]");

							for (int j = 0; j < numPositionKeys; j++) {
								double time = bb.getDouble();
								float x = bb.getFloat();
								float y = bb.getFloat();
								float z = bb.getFloat();
								node.setPositionKey(j, new Vec3Key(x, y, z,
										time));
							}

							for (int j = 0; j < numRotationKeys; j++) {
								double time = bb.getDouble();
								float x = bb.getFloat();
								float y = bb.getFloat();
								float z = bb.getFloat();
								float w = bb.getFloat();
								node.setRotationKey(j, new QuatKey(x, y, z, w,
										time));
							}

							for (int j = 0; j < numScalingKeys; j++) {
								double time = bb.getDouble();
								float x = bb.getFloat();
								float y = bb.getFloat();
								float z = bb.getFloat();
								node.setScalingKey(j,
										new Vec3Key(x, y, z, time));
							}

							animation.setAnimationNode(i, node);
						}

						animations.add(animation);
						break;

					default:
						System.err.println("Unknown task " + task);
						break;

					}

				}

			} else {
				System.err.println("Unknown version number " + version);
			}

			raf.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return geometry;
	}

	public static void main(String[] args) {

		MWMLoader
				.loadModel("D:/FractalStudio/DaeToMWM/DaeToMWM/data/stickman_triangles.mwm");

	}
}
