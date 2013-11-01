package org.meanworks.engine.scene;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.Renderer;
import org.meanworks.engine.core.Application;
import org.meanworks.render.material.Material;
import org.meanworks.render.opengl.shader.ShaderProgram;

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
public class Geometry {

	/*
	 * The list of meshes for this geometry
	 */
	private HashMap<String, Mesh> meshes = new HashMap<>();

	/*
	 * The material for this geometry
	 */
	private Material material;

	/*
	 * 
	 */
	public Geometry() {
		material = Material.DEFAULT_MATERIAL;
	}

	/**
	 * Clear the meshes contained in this geometry
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
	 * Add a mesh to this geometry
	 * 
	 * @param meshName
	 * @param mesh
	 */
	public void addMesh(String meshName, Mesh mesh) {
		meshes.put(meshName, mesh);
	}

	/**
	 * Set the material of this geometry
	 * 
	 * @param material
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	/**
	 * Get the material of this geometry
	 * 
	 * @return
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * 
	 */
	public void render() {
		if (material != null) {

			getMaterial().setProperty("vAmbientColor",
					new Vector4f(0.5f, 0.5f, 0.5f, 1.0f));
			getMaterial().setProperty("vDiffuseColor",
					new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
			getMaterial().setProperty("fSpecularIntensity", 30.0f);
			getMaterial().setProperty("tColorMap", 0);

			/*
			 * Apply material
			 */
			material.apply();
		}
		/*
		 * Send the draw call to the meshes
		 */
		for (Mesh mesh : meshes.values()) {
			mesh.render(material);
		}

		/*
		 * Only disable if our material actually did something here.
		 */
		if (material != null) {
			// Clear state
			Renderer.clearState();
			ShaderProgram.bindNone();
		}
	}

}
