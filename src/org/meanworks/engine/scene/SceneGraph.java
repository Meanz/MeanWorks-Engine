package org.meanworks.engine.scene;

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
public class SceneGraph {

	/*
	 * The root node of the scene graph
	 */
	private SpatialNode rootNode;

	/**
	 * Construct the scene graph
	 */
	public SceneGraph() {
		rootNode = new SpatialNode();
	}

	/**
	 * Get the root node of this scene graph
	 * 
	 * @return
	 */
	public SpatialNode getRootNode() {
		return rootNode;
	}

	/**
	 * Recursive updating function
	 * 
	 * @param node
	 */
	public void doUpdate(Node node) {
		node.update();
		for (Node child : node.getChildren()) {
			doUpdate(child);
		}
	}

	/**
	 * Recursive rendering function
	 * 
	 * @param node
	 */
	private void doRender(Node node) {
		node.render();
		for (Node child : node.getChildren()) {
			doRender(child);
		}
	}

	/**
	 * 
	 */
	public void update() {
		doUpdate(rootNode);
	}

	/**
	 * 
	 */
	public void render() {
		doRender(rootNode);
	}

}
