package org.meanworks.engine.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

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
public class Frustum {

	private float[][] frustum = new float[6][4];

	public void createFrustrum(Matrix4f projectionMatrix, Matrix4f viewMatrix) {

		float t = 0;
		Matrix4f clipMatrix = Matrix4f.mul(projectionMatrix, viewMatrix, null);

		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		clipMatrix.store(floatBuffer);
		floatBuffer.flip();

		float[] clip = new float[16];
		floatBuffer.get(clip);

		/* Extract the numbers for the RIGHT plane */
		frustum[0][0] = clip[3] - clip[0];
		frustum[0][1] = clip[7] - clip[4];
		frustum[0][2] = clip[11] - clip[8];
		frustum[0][3] = clip[15] - clip[12];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[0][0] * frustum[0][0] + frustum[0][1]
				* frustum[0][1] + frustum[0][2] * frustum[0][2]);
		frustum[0][0] /= t;
		frustum[0][1] /= t;
		frustum[0][2] /= t;
		frustum[0][3] /= t;

		/* Extract the numbers for the LEFT plane */
		frustum[1][0] = clip[3] + clip[0];
		frustum[1][1] = clip[7] + clip[4];
		frustum[1][2] = clip[11] + clip[8];
		frustum[1][3] = clip[15] + clip[12];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[1][0] * frustum[1][0] + frustum[1][1]
				* frustum[1][1] + frustum[1][2] * frustum[1][2]);
		frustum[1][0] /= t;
		frustum[1][1] /= t;
		frustum[1][2] /= t;
		frustum[1][3] /= t;

		/* Extract the BOTTOM plane */
		frustum[2][0] = clip[3] + clip[1];
		frustum[2][1] = clip[7] + clip[5];
		frustum[2][2] = clip[11] + clip[9];
		frustum[2][3] = clip[15] + clip[13];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[2][0] * frustum[2][0] + frustum[2][1]
				* frustum[2][1] + frustum[2][2] * frustum[2][2]);
		frustum[2][0] /= t;
		frustum[2][1] /= t;
		frustum[2][2] /= t;
		frustum[2][3] /= t;

		/* Extract the TOP plane */
		frustum[3][0] = clip[3] - clip[1];
		frustum[3][1] = clip[7] - clip[5];
		frustum[3][2] = clip[11] - clip[9];
		frustum[3][3] = clip[15] - clip[13];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[3][0] * frustum[3][0] + frustum[3][1]
				* frustum[3][1] + frustum[3][2] * frustum[3][2]);
		frustum[3][0] /= t;
		frustum[3][1] /= t;
		frustum[3][2] /= t;
		frustum[3][3] /= t;

		/* Extract the FAR plane */
		frustum[4][0] = clip[3] - clip[2];
		frustum[4][1] = clip[7] - clip[6];
		frustum[4][2] = clip[11] - clip[10];
		frustum[4][3] = clip[15] - clip[14];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[4][0] * frustum[4][0] + frustum[4][1]
				* frustum[4][1] + frustum[4][2] * frustum[4][2]);
		frustum[4][0] /= t;
		frustum[4][1] /= t;
		frustum[4][2] /= t;
		frustum[4][3] /= t;

		/* Extract the NEAR plane */
		frustum[5][0] = clip[3] + clip[2];
		frustum[5][1] = clip[7] + clip[6];
		frustum[5][2] = clip[11] + clip[10];
		frustum[5][3] = clip[15] + clip[14];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[5][0] * frustum[5][0] + frustum[5][1]
				* frustum[5][1] + frustum[5][2] * frustum[5][2]);
		frustum[5][0] /= t;
		frustum[5][1] /= t;
		frustum[5][2] /= t;
		frustum[5][3] /= t;
	}

	/**
	 * Check to see if the sphere defined by (vec.x, vec.y, vec.z) rad(vec.w) is
	 * in the frustum
	 * 
	 * @param vec
	 * @return The distance to the sphere or 0 if not inside
	 */
	public float sphereInFrustum(Vector4f vec) {
		return sphereInFrustum(vec.x, vec.y, vec.z, vec.w);
	}

	/**
	 * Check to see if the sphere defined by (vec.x, vec.y, vec.z) rad radius is
	 * in the frustum
	 * 
	 * @param vec
	 * @param radius
	 * @return The distance to the sphere or 0 if not inside
	 */
	public float sphereInFrustum(Vector3f vec, float radius) {
		return sphereInFrustum(vec.x, vec.y, vec.z, radius);
	}

	/**
	 * Check to see if the sphere given by x, y, z, radius is in the frustum
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param radius
	 * @return The distance to the sphere or 0 if not inside
	 */
	public float sphereInFrustum(float x, float y, float z, float radius) {
		float d = 0;
		for (int p = 0; p < 6; p++) {
			d = frustum[p][0] * x + frustum[p][1] * y + frustum[p][2] * z
					+ frustum[p][3];
			if (d <= -radius)
				return 0;
		}
		return d + radius;
	}

	/**
	 * Check to see if the given point vec3 is in the frustum
	 * 
	 * @param vec
	 * @return
	 */
	public boolean pointInFrustum(Vector3f vec) {
		return pointInFrustum(vec.x, vec.y, vec.z);
	}

	/**
	 * Check to see if the given point x, y, z is in the frustum
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean pointInFrustum(float x, float y, float z) {
		int p;

		for (p = 0; p < 6; p++)
			if (frustum[p][0] * x + frustum[p][1] * y + frustum[p][2] * z
					+ frustum[p][3] <= 0)
				return false;
		return true;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param size
	 * @return
	 */
	public FrustumResult cubeInFrustum(Vector3f min, Vector3f max) {
		int p;
		int c;
		int c2 = 0;
		for (p = 0; p < 6; p++) {
			c = 0;
			if (frustum[p][0] * (min.x) + frustum[p][1] * (min.y)
					+ frustum[p][2] * (min.z) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (max.x) + frustum[p][1] * (min.y)
					+ frustum[p][2] * (min.z) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (min.x) + frustum[p][1] * (max.y)
					+ frustum[p][2] * (min.z) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (max.x) + frustum[p][1] * (max.y)
					+ frustum[p][2] * (min.z) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (min.x) + frustum[p][1] * (min.y)
					+ frustum[p][2] * (max.z) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (max.x) + frustum[p][1] * (min.y)
					+ frustum[p][2] * (max.z) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (min.x) + frustum[p][1] * (max.y)
					+ frustum[p][2] * (max.z) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (max.x) + frustum[p][1] * (max.y)
					+ frustum[p][2] * (max.z) + frustum[p][3] > 0)
				c++;
			if (c == 0)
				return FrustumResult.OUTSIDE;
			if (c == 8)
				c2++;
		}
		return (c2 == 6) ? FrustumResult.INSIDE
				: FrustumResult.PARTIALLY_INSIDE;
	}
	
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param size
	 * @return
	 */
	public FrustumResult cubeInFrustum(float x, float y, float z, float size) {
		int p;
		int c;
		int c2 = 0;
		for (p = 0; p < 6; p++) {
			c = 0;
			if (frustum[p][0] * (x - size) + frustum[p][1] * (y - size)
					+ frustum[p][2] * (z - size) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (x + size) + frustum[p][1] * (y - size)
					+ frustum[p][2] * (z - size) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (x - size) + frustum[p][1] * (y + size)
					+ frustum[p][2] * (z - size) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (x + size) + frustum[p][1] * (y + size)
					+ frustum[p][2] * (z - size) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (x - size) + frustum[p][1] * (y - size)
					+ frustum[p][2] * (z + size) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (x + size) + frustum[p][1] * (y - size)
					+ frustum[p][2] * (z + size) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (x - size) + frustum[p][1] * (y + size)
					+ frustum[p][2] * (z + size) + frustum[p][3] > 0)
				c++;
			if (frustum[p][0] * (x + size) + frustum[p][1] * (y + size)
					+ frustum[p][2] * (z + size) + frustum[p][3] > 0)
				c++;
			if (c == 0)
				return FrustumResult.OUTSIDE;
			if (c == 8)
				c2++;
		}
		return (c2 == 6) ? FrustumResult.INSIDE
				: FrustumResult.PARTIALLY_INSIDE;
	}

}
