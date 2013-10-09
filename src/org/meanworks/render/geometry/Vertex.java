package org.meanworks.render.geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.meanworks.engine.math.Vec3;

public class Vertex {

	private Vec3 position;
	private Vec3 normal;
	private Vector2f texCoord;

	/**
	 * 
	 * @param position
	 * @param normal
	 * @param texCoord
	 */
	public Vertex(Vec3 position, Vec3 normal, Vector2f texCoord) {
		this.position = position;
		this.normal = normal;
		this.texCoord = texCoord;
	}

	/**
	 * 
	 * @param position
	 * @param normal
	 */
	public Vertex(Vec3 position, Vec3 normal) {
		this(position, normal, null);
	}

	/**
	 * 
	 * @param position
	 * @param texCoord
	 */
	public Vertex(Vec3 position, Vector2f texCoord) {
		this(position, null, texCoord);
	}

	/**
	 * 
	 * @param position
	 */
	public Vertex(Vec3 position) {
		this(position, null, null);
	}

	/**
	 * Get the position vector for this vertex
	 * 
	 * @return
	 */
	public Vec3 getPosition() {
		return position;
	}

	/**
	 * Get the normal vector for this vertex
	 * 
	 * @return
	 */
	public Vec3 getNormal() {
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
