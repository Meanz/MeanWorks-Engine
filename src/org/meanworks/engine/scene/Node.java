package org.meanworks.engine.scene;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.lwjgl.util.vector.Matrix4f;
import org.meanworks.engine.bounding.BoundingBox;
import org.meanworks.engine.math.Transform;

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
public abstract class Node {

	/*
	 * The parent node of this node
	 */
	private Node parent;

	/*
	 * The children of this node
	 */
	private LinkedList<Node> children = new LinkedList<>();

	/*
	 * Whether or not this node inherits transforms from it's parent
	 */
	private NodeInheritance nodeInheritance;

	/*
	 * The culling method for this node
	 */
	private CullHint cullHint;
	
	/*
	 * 
	 */
	private BoundingBox cullingBox;

	/*
	 * The transform of this node
	 */
	private Transform transform;

	/**
	 * Construct a new node
	 */
	public Node() {
		cullHint = CullHint.PARENT_CULL;
		transform = new Transform();
		nodeInheritance = NodeInheritance.INHERIT_NONE;
		cullingBox = new BoundingBox();
	}

	/**
	 * Get the children of this node
	 * 
	 * @return
	 */
	public List<Node> getChildren() {
		return children;
	}

	/**
	 * Add a child to this node
	 * 
	 * @param child
	 */
	public void addChild(Node child) {
		if (child != null) {
			child.setParent(this);
			children.add(child);
			System.err.println("Children Size: " + children.size());
		}
	}

	/**
	 * Get the parent node of this node
	 * 
	 * @return
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Set the parent node of this node
	 * 
	 * @param parent
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * Get the transform of this node
	 * 
	 * @return
	 */
	public Transform getTransform() {
		return transform;
	}
	
	/**
	 * 
	 */
	public void updateCullingBox() {
		//Pass the request down the hierarchy to see if updates are needed
		
	}

	/**
	 * Get the transform matrix of this node
	 */
	public Matrix4f getTransformMatrix() {
		Matrix4f transformMatrix = new Matrix4f();
		if (nodeInheritance != NodeInheritance.INHERIT_NONE) {
			Node _parent = parent;
			Stack<Matrix4f> parentStack = new Stack<>();
			while (_parent != null) {
				parentStack.push(_parent.getTransform().getTransformMatrix());
				_parent = _parent.getParent();
			}
			// The root is now at the top of the stack
			if (!parentStack.isEmpty()) {
				while (!parentStack.isEmpty()) {
					transformMatrix = Matrix4f.mul(transformMatrix,
							parentStack.pop(), null);
				}
			}
		}
		transformMatrix = Matrix4f.mul(transformMatrix,
				transform.getTransformMatrix(), null);
		return transformMatrix;
	}

	/**
	 * Update method called from the Application
	 */
	public final void doUpdate() {

		/*
		 * Update transform if needed
		 */
		updateCullingBox();

		/*
		 * Send the update request to the class that extends this node
		 */
		update();
	}

	/**
	 * Render method called from the Application
	 */
	public final void doRender() {

		render();
	}

	/**
	 * Update this node
	 */
	public void update() {

	};

	/**
	 * Render this node
	 */
	public void render() {
	};

}
