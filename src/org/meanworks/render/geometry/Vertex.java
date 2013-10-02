package org.meanworks.render.geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Vertex {

	private Vector3f position;
	private Vector3f normal;
	private Vector2f texCoord;

	/**
	 * 
	 * @param position
	 * @param normal
	 * @param texCoord
	 */
	public Vertex(Vector3f position, Vector3f normal, Vector2f texCoord) {
		this.position = position;
		this.normal = normal;
		this.texCoord = texCoord;
	}

	/**
	 * 
	 * @param position
	 * @param normal
	 */
	public Vertex(Vector3f position, Vector3f normal) {
		this(position, normal, null);
	}

	/**
	 * 
	 * @param position
	 * @param texCoord
	 */
	public Vertex(Vector3f position, Vector2f texCoord) {
		this(position, null, texCoord);
	}

	/**
	 * 
	 * @param position
	 */
	public Vertex(Vector3f position) {
		this(position, null, null);
	}

	/**
	 * Get the position vector for this vertex
	 * 
	 * @return
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * Get the normal vector for this vertex
	 * 
	 * @return
	 */
	public Vector3f getNormal() {
		return normal;
	}

	/**
	 * Get the uv vecotr for this vertex
	 * 
	 * @return
	 */
	public Vector2f getTexCoord() {
		return texCoord;
	}

}
