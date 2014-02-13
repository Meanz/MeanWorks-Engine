package org.meanworks.engine.math;

import org.lwjgl.opengl.GL11;

/**
 * A class to hold encapsulate a triangle, also provides some helper functions
 * 
 * @author meanz
 * 
 */
public class Triangle {

	/*
	 * The three vertices forming a triangle
	 */
	private Vertex p1;
	private Vertex p2;
	private Vertex p3;

	/**
	 * Construct a new triangle
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public Triangle(Vertex p1, Vertex p2, Vertex p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}

	/**
	 * Get the positions of this triangle as floats
	 * 
	 * @return
	 */
	public float[] positionArray() {
		if (p1.getPosition() == null) {
			return null;
		}
		float[] floats = new float[9]; // 9 floats

		floats[0] = p1.getPosition().x;
		floats[1] = p1.getPosition().y;
		floats[2] = p1.getPosition().z;

		floats[3] = p2.getPosition().x;
		floats[4] = p2.getPosition().y;
		floats[5] = p2.getPosition().z;

		floats[6] = p3.getPosition().x;
		floats[7] = p3.getPosition().y;
		floats[8] = p3.getPosition().z;

		return floats;
	}

	/**
	 * Get the normals of this triangle as floats
	 * 
	 * @return
	 */
	public float[] normalArray() {
		if (p1.getNormal() == null) {
			return null;
		}
		float[] floats = new float[9]; // 9 floats

		floats[0] = p1.getNormal().x;
		floats[1] = p1.getNormal().y;
		floats[2] = p1.getNormal().z;

		floats[3] = p2.getNormal().x;
		floats[4] = p2.getNormal().y;
		floats[5] = p2.getNormal().z;

		floats[6] = p3.getNormal().x;
		floats[7] = p3.getNormal().y;
		floats[8] = p3.getNormal().z;

		return floats;
	}

	/**
	 * Get the texture coordinates of this triangle as floats
	 * 
	 * @return
	 */
	public float[] texCoordArray() {
		if (p1.getNormal() == null) {
			return null;
		}
		float[] floats = new float[6]; // 9 floats

		floats[0] = p1.getTexCoord().x;
		floats[1] = p1.getTexCoord().y;

		floats[2] = p2.getTexCoord().x;
		floats[3] = p2.getTexCoord().y;

		floats[4] = p3.getTexCoord().x;
		floats[5] = p3.getTexCoord().y;

		return floats;
	}

	/**
	 * Get the first point of this triangle
	 * 
	 * @return
	 */
	public Vertex getP1() {
		return p1;
	}

	/**
	 * Get the second point of this triangle
	 * 
	 * @return
	 */
	public Vertex getP2() {
		return p2;
	}

	/**
	 * Get the third point of this triangle
	 * 
	 * @return
	 */
	public Vertex getP3() {
		return p3;
	}

	/**
	 * A simple helper function for drawing the triangle in immediate mode
	 */
	public void drawImmediate() {
		if (p1.getTexCoord() != null)
			GL11.glTexCoord2f(p1.getTexCoord().x, p1.getTexCoord().y);
		if (p1.getNormal() != null)
			GL11.glNormal3f(p1.getNormal().x, p1.getNormal().y,
					p1.getNormal().z);
		GL11.glVertex3f(p1.getPosition().x, p1.getPosition().y,
				p1.getPosition().z);

		if (p2.getTexCoord() != null)
			GL11.glTexCoord2f(p2.getTexCoord().x, p2.getTexCoord().y);
		if (p2.getNormal() != null)
			GL11.glNormal3f(p2.getNormal().x, p2.getNormal().y,
					p2.getNormal().z);
		GL11.glVertex3f(p2.getPosition().x, p2.getPosition().y,
				p2.getPosition().z);

		if (p3.getTexCoord() != null)
			GL11.glTexCoord2f(p3.getTexCoord().x, p3.getTexCoord().y);
		if (p3.getNormal() != null)
			GL11.glNormal3f(p3.getNormal().x, p3.getNormal().y,
					p3.getNormal().z);
		GL11.glVertex3f(p3.getPosition().x, p3.getPosition().y,
				p3.getPosition().z);
	}
}
