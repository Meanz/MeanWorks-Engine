package org.meanworks.render.geometry;

import java.util.HashMap;

import org.meanworks.engine.scene.Geometry;
import org.meanworks.engine.scene.GeometryNode;
import org.meanworks.render.geometry.mesh.Mesh;

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
public class Model extends GeometryNode {

	public Model(Geometry geometry) {
		super(geometry);
	}

	/**
	 * Shallow copies this model Used for instancing. The model will not be
	 * added to the scene automatically
	 * 
	 * @return
	 */
	public Model shallowCopy() {
		Model model = new Model(this.getGeometry());
		return model;
	}

	/**
	 * Copies this model and it's entire data structure. The model will not be
	 * added to the scene automatically
	 * 
	 * @return
	 */
	public Model deepCopy() {
		/*
		 * Check if there is actually something to copy
		 */
		if (getGeometry() == null) {
			return null;
		}
		Geometry geometry = new Geometry();
		// We use the same material here
		geometry.setMaterial(this.getGeometry().getMaterial());

		HashMap<String, Mesh> meshes = getGeometry().getMeshes();
		for (String key : meshes.keySet()) {
			geometry.addMesh(key, meshes.get(key).deepCopy());
		}

		Model model = new Model(geometry);
		return model;
	}

}
