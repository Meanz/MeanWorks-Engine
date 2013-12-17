package org.meanworks.engine.scene;

import org.meanworks.engine.core.Application;

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
public class GeometryNode extends Node {

	private Geometry geometry;

	/**
	 * Construct a new geometry node
	 */
	public GeometryNode(Geometry geometry) {
		super("geometryNode_" + SceneGraph.getNextNodeId());
		this.geometry = geometry;
	}

	/**
	 * Construct a new empty geometry node
	 */
	public GeometryNode() {
		this(null);
	}

	/**
	 * Get the geometry of this node
	 * 
	 * @return
	 */
	public Geometry getGeometry() {
		return geometry;
	}

	/**
	 * Set the geometry of this node
	 * 
	 * @param geometry
	 */
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	/**
	 * Forward rendering call to the geometry
	 */
	@Override
	public void render() {
		if (geometry != null) {
			if (geometry.getMaterial() != null) {
				/*
				 * Update matrices
				 */
				geometry.getMaterial().setProperty(
						"mProjectionView",
						Application.getApplication().getScene().getCamera()
								.getProjectionViewMatrix());
				geometry.getMaterial().setProperty("mModelMatrix",
						getGlobalTransform());
			}
			/*
			 * Forward rendering call
			 */
			geometry.render();
		}
	}

}
