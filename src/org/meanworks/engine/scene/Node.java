package org.meanworks.engine.scene;

import java.util.LinkedList;
import java.util.List;

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
	 * The transform of this node
	 */
	private Transform transform;

	/**
	 * Construct a new node
	 */
	public Node() {
		transform = new Transform();
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
			children.add(child);
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
	 * Debug function for drawing node
	 */
	public void drawNodeBox() {

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
