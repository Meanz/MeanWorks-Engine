package org.meanworks.engine.asset.model;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.render.geometry.Mesh;
import org.meanworks.engine.render.geometry.Model;
import org.meanworks.engine.render.geometry.SkinnedModel;
import org.meanworks.engine.render.geometry.animation.Animation;
import org.meanworks.engine.render.geometry.animation.AnimationNode;
import org.meanworks.engine.render.geometry.animation.AnimationNode.QuatKey;
import org.meanworks.engine.render.geometry.animation.AnimationNode.Vec3Key;
import org.meanworks.engine.render.geometry.animation.Bone;
import org.meanworks.engine.render.geometry.animation.Skeleton;
import org.meanworks.engine.render.geometry.mesh.renderers.VAOMeshRenderer;

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

	static class VertexBoneInformation {

		int[] ids;
		float[] weights;

		public VertexBoneInformation(int[] ids, float[] weights) {
			this.ids = ids;
			this.weights = weights;
		}

	}

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

	public static Matrix4f getMat4Transpose(ByteBuffer bb) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		for (int i = 0; i < 16; i++) {
			fb.put(bb.getFloat());
		}
		fb.flip();
		Matrix4f mat4 = new Matrix4f();
		mat4.loadTranspose(fb);
		return mat4;
	}

	public static Model loadModel(String fileName) {
		return loadModel(new Model(), fileName);
	}

	public static SkinnedModel loadAnimatedModel(String fileName) {
		return (SkinnedModel) loadModel(new SkinnedModel(new Model()), fileName);
	}

	/**
	 * Load a MeanWorks Model
	 * 
	 * @param fileName
	 * @return
	 */
	public static Model loadModel(Model geometry, String fileName) {
		try {
			RandomAccessFile raf = new RandomAccessFile(fileName, "r");
			byte[] data = new byte[(int) raf.length()];
			raf.read(data);
			raf.close();

			ByteBuffer bb = ByteBuffer.wrap(data);

			bb.order(ByteOrder.nativeOrder());

			LinkedList<Animation> animations = new LinkedList<>();
			Skeleton skeleton = null;

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
						int numBonesPerVertex = 0;
						String textureFile = getString(bb);

						boolean hasBoneData = bb.getInt() == 1 ? true : false;

						System.err.println("[Mesh " + "numVertices="
								+ numVertices + "" + " hasVertices="
								+ hasVertices + "" + " hasNormals="
								+ hasNormals + "" + " hasUVs=" + hasUVs + ""
								+ " texture=" + textureFile + ""
								+ " hasBoneData=" + hasBoneData + "]");

						VertexBoneInformation[] vbi = null;

						if (hasBoneData) {
							vbi = new VertexBoneInformation[numVertices];
							numBonesPerVertex = bb.getInt();
							System.err.println("NUM BONES PER VERTEX: "
									+ numBonesPerVertex);

							for (int i = 0; i < numVertices; i++) {

								int[] ids = new int[numBonesPerVertex];
								float[] weights = new float[numBonesPerVertex];

								for (int j = 0; j < numBonesPerVertex; j++) {

									ids[j] = bb.getInt();
									weights[j] = bb.getFloat();

								}

								vbi[i] = new VertexBoneInformation(ids, weights);

							}
						}

						FloatBuffer vertices = BufferUtils
								.createFloatBuffer(numVertices * 3);
						FloatBuffer normals = BufferUtils
								.createFloatBuffer(numVertices * 3);
						FloatBuffer uvs = BufferUtils
								.createFloatBuffer(numVertices * 2);

						IntBuffer ids = null;
						FloatBuffer weights = null;

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

						if (hasBoneData) {
							ids = BufferUtils.createIntBuffer(numVertices
									* numBonesPerVertex);
							weights = BufferUtils.createFloatBuffer(numVertices
									* numBonesPerVertex);

							// fill these
							for (int i = 0; i < numVertices; i++) {
								for (int j = 0; j < numBonesPerVertex; j++) {
									ids.put(vbi[i].ids[j]);
									weights.put(vbi[i].weights[j]);
								}
							}

							ids.flip();
							weights.flip();
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
						VAOMeshRenderer meshRenderer = new VAOMeshRenderer();
						Mesh mesh = new Mesh();
						mesh.setMeshRenderer(meshRenderer);

						meshRenderer.addIndex(indices, numIndices);
						meshRenderer.addBuffer(vertices, 0, 3);
						meshRenderer.addBuffer(normals, 1, 3);
						meshRenderer.addBuffer(uvs, 2, 2);

						if (hasBoneData) {
							meshRenderer.addBuffer(ids, 5, 4);
							meshRenderer.addBuffer(weights, 6, 4);
						}

						meshRenderer.compile();
						geometry.addMesh("mesh_" + meshName + "_" + meshId,
								mesh);
						System.err.println("Added mesh: "
								+ ("mesh_" + meshName + "_" + meshId));

						if (!textureFile.equals("null")) {
							// Find the root dir
							System.err.println("Loaded texture " + textureFile);
							File modelFile = new File(fileName);
							mesh.setTexture(Application
									.getApplication()
									.getAssetManager()
									.loadTexture(
											modelFile.getParentFile()
													.toString()
													+ "/"
													+ textureFile));
						}
						break;

					// Read skeleton
					case 11:

						skeleton = new Skeleton();

						int numNodes = bb.getInt();

						Bone[] bones = new Bone[numNodes];
						String[] boneParents = new String[numNodes];
						System.err
								.println("[Nodes numNodes: " + numNodes + "]");

						for (int i = 0; i < numNodes; i++) {

							String nodeId = getString(bb);
							String parent = getString(bb);

							System.err.println("[Node id=" + nodeId
									+ " parent=" + parent + "]");

							Matrix4f localTransform = getMat4(bb);
							Matrix4f offsetMatrix = getMat4(bb);

							boneParents[i] = parent;
							bones[i] = new Bone(nodeId);
							bones[i].setLocalTransform(localTransform);
							bones[i].setOffsetMatrix(offsetMatrix);

						}

						// Iterate through shit and setup bone hierarchy
						for (int i = 0; i < boneParents.length; i++) {

							// Find parent
							String parentName = boneParents[i];
							Bone parent = null;
							for (Bone bone : bones) {
								if (bone.getBoneName().equals(parentName)) {
									parent = bone;
									break;
								}
							}
							bones[i].setParent(parent);
						}

						skeleton.setBones(bones);

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

			if (geometry instanceof SkinnedModel) {

				SkinnedModel model = (SkinnedModel) geometry;

				model.setAvailableAnimations(animations);
				model.setSkeleton(skeleton);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return geometry;
	}

}
