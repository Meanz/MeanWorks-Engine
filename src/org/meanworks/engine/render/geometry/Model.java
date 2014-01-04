package org.meanworks.engine.render.geometry;

import java.util.HashMap;
import java.util.Map;

import org.meanworks.engine.RenderState;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.scene.Node;

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
 * 
 *         The Model class is responsible for containing the meshes that
 *         composes this model, It also defines some global variables for the
 *         meshes
 */
public class Model extends Node {

	/*
	 * The list of meshes for this geometry
	 */
	private HashMap<String, Mesh> meshes = new HashMap<>();

	/**
	 * Construct a new model object
	 */
	public Model() {

	}

	/**
	 * Construct a new Model with the given meshes
	 * 
	 * @param meshes
	 */
	public Model(Map<String, Mesh> meshes) {
		setMeshes(meshes);
	}

	/**
	 * Allows you to copy this model
	 */
	public Model shallowCopy() {
		return new Model(getMeshes());
	}

	/**
	 * Clear the meshes contained in this model
	 */
	public void clearMeshes() {
		/*
		 * TODO: Improve mesh destroying
		 */
		for (Mesh mesh : meshes.values()) {
			mesh.getMeshRenderer().clear();
		}
		meshes.clear();
	}

	/**
	 * Set the meshes for this model
	 * 
	 * @param meshes
	 */
	public void setMeshes(Map<String, Mesh> meshes) {
		this.meshes.clear();
		this.meshes.putAll(meshes);
	}

	/**
	 * Get the list of meshes
	 * 
	 * @return
	 */
	public HashMap<String, Mesh> getMeshes() {
		return meshes;
	}

	/**
	 * Get the mesh by the given name
	 * 
	 * @param meshName
	 * @return
	 */
	public Mesh getMesh(String meshName) {
		return meshes.get(meshName);
	}

	/**
	 * Add a mesh to this model
	 * 
	 * @param meshName
	 * @param mesh
	 */
	public void addMesh(String meshName, Mesh mesh) {
		meshes.put(meshName, mesh);
	}

	/**
	 * Render this model
	 */
	public void render() {
		/*
		 * Update the render state with this model's transform
		 */
		RenderState.setProjectionMatrix(Application.getApplication()
				.getCamera().getProjectionViewMatrix());
		RenderState.setTransformMatrix(getGlobalTransform());
		/*
		 * Send the draw call to the meshes
		 */
		for (Mesh mesh : meshes.values()) {
			mesh.render();
		}
	}

}
