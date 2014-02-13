package org.meanworks.engine.scene;

import org.lwjgl.opengl.GL11;
import org.meanworks.engine.EngineConfig;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.camera.Camera;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.math.FrustumResult;

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
public class Scene {

	/*
	 * The scene node number increment
	 */
	private static int sceneIncr = 0;

	/**
	 * Get the next node id for the scene graph!
	 * 
	 * @return
	 */
	public static int getNextNodeId() {
		if (Integer.MAX_VALUE == (sceneIncr + 1)) {
			sceneIncr = 0;
			return sceneIncr;
		} else {
			return sceneIncr++;
		}
	}

	/*
	 * The camera in the scene
	 */
	private Camera camera;

	/*
	 * The root node of the scene graph
	 */
	private Node rootNode;

	/**
	 * Construct the scene graph
	 */
	public Scene() {
		rootNode = new Node("rootNode");
	}

	/**
	 * Internal function for getting the scene graph reference
	 * 
	 * @return
	 */
	private static Scene getScene() {
		return Application.getApplication().getScene();
	}

	/**
	 * Add a child to the scene graph
	 * 
	 * @param node
	 */
	public static void add(Node node) {
		getScene().getRootNode().addChild(node);
	}

	/**
	 * Set the camera of this scene
	 * 
	 * @param camera
	 */
	public static void setCamera(Camera camera) {
		getScene().camera = camera;
	}

	/**
	 * Get the camera of this scene
	 * 
	 * @return
	 */
	public static Camera getCamera() {
		return getScene().camera;
	}

	/**
	 * Recursive search function for findNode(string)
	 * 
	 * @param searchName
	 * @param parentNode
	 * @return
	 */
	private Node recursiveFindNode(String searchName, Node parentNode) {
		if (parentNode.getName().equals(searchName)) {
			return parentNode;
		}
		for (Node child : parentNode.getChildren()) {
			Node found = recursiveFindNode(searchName, child);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	/**
	 * This is a super expensive search that looks for a node with the given
	 * name. And nodes can also have similar names, so it's not a guaranteed
	 * function!
	 * 
	 * TODO: Update description
	 * 
	 * @param nodeName
	 * @return
	 */
	public Node findNode(String nodeName) {
		return recursiveFindNode(nodeName, getRootNode());
	}

	/**
	 * Get the root node of this scene graph
	 * 
	 * @return
	 */
	public Node getRootNode() {
		return rootNode;
	}

	/**
	 * Recursive updating function
	 * 
	 * @param node
	 */
	public void doUpdate(Node node) {
		node.doUpdate();
		for (Node child : node.getChildren()) {
			doUpdate(child);
		}
		// Will be called after all childs are updated
		// This will be used to unflag flags that we wanted to pass down the
		// hierarchy.
		// Example is the update flag.
		// If node1 needs to be updated, all it's children will be notified of
		// this and so
		// The children updates while each of the children copies the
		// needsUpdate flag so it's children
		// Will also be updated
		// And when each child has finished the update on itself and all it's
		// children
		// The post update function is used to unflag the update flag.
		node.doPostUpdate();
	}

	/**
	 * Recursive rendering function
	 * 
	 * @param node
	 */
	private void doRender(Node node) {
		/*
		 * Determine if this object should be displayed, test culling
		 */
		boolean didCull = false;
		FrustumResult result = Application
				.getApplication()
				.getCamera()
				.getFrustum()
				.cubeInFrustumTranslated(node.getCullingBox().getMin(),
						node.getCullingBox().getMax(), node.getGlobalTransform());
		if (result == FrustumResult.INSIDE
				|| result == FrustumResult.PARTIALLY_INSIDE) {
			didCull = false;
		}
		if (!didCull) {
			node.doRender();
		}
		for (Node child : node.getChildren()) {
			if (child.getCullHint() == CullHint.PARENT_CULL && didCull) {
				continue;
			} else if (child.getCullHint() == CullHint.ALWAYS_CULL) {
				doRender(child);
			} else {
				doRender(child);
			}

		}
	}

	/**
	 * Update all the nodes in this scene graph
	 */
	public void update() {
		if (camera != null) {
			camera.update();
		} else {
			EngineLogger.error("Scene: Camera is null");
		}
		doUpdate(rootNode);
	}

	/**
	 * Render all the nodes in this scene graph
	 */
	public void render() {
		if (camera != null) {
			camera.updateMatrices();
		} else {
			EngineLogger.error("Scene: Camera is null");
		}
		if (EngineConfig.wireframeScene) {
			GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
			GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_LINE);
		} else {
			GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
			GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_FILL);
		}
		doRender(rootNode);
		if (EngineConfig.wireframeScene) {
			GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
			GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_FILL);
		}
	}

}
