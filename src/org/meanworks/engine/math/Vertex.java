package org.meanworks.engine.math;

import org.lwjgl.util.vector.Vector2f;

public class Vertex {

	private Vec3 position;
	private Vec3 normal;
	private Vec2 texCoord;

	/**
	 * 
	 * @param position
	 * @param normal
	 * @param texCoord
	 */
	public Vertex(Vec3 position, Vec3 normal, Vec2 texCoord) {
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
	public Vertex(Vec3 position, Vec2 texCoord) {
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
	public Vec2 getTexCoord() {
		return texCoord;
	}

}
