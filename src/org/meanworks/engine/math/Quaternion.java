package org.meanworks.engine.math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.render.geometry.animation.AnimationNode.QuatKey;

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
public class Quaternion {

	private float x;
	private float y;
	private float z;
	private float w;

	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Quaternion() {
		this(0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	public Quaternion interpolate(Quaternion key1, Quaternion key2, float pFactor) {

		Quaternion pStart = key1;
		Quaternion pEnd = key2;

		float cosom = pStart.x * pEnd.x + pStart.y * pEnd.y + pStart.z * pEnd.z
				+ pStart.w * pEnd.w;

		Quaternion end = pEnd;
		if (cosom < 0.0f) {
			cosom = -cosom;
			end.x = -end.x; // Reverse all signs
			end.y = -end.y;
			end.z = -end.z;
			end.w = -end.w;
		}

		// Calculate coefficients
		float sclp, sclq;
		if ((1.0f - cosom) > (0.01f)) // 0.0001 -> some epsillon
		{
			// Standard case (slerp)
			float omega, sinom;
			omega = (float) Math.acos(cosom); // extract theta from dot
												// product's cos theta
			sinom = (float) Math.sin(omega);
			sclp = (float) Math.sin(((1.0f) - pFactor) * omega) / sinom;
			sclq = (float) Math.sin(pFactor * omega) / sinom;
		} else {
			// Very close, do linear interp (because it's faster)
			sclp = (1.0f) - pFactor;
			sclq = pFactor;
		}
		Quaternion pOut = new Quaternion();
		pOut.x = sclp * pStart.x + sclq * end.x;
		pOut.y = sclp * pStart.y + sclq * end.y;
		pOut.z = sclp * pStart.z + sclq * end.z;
		pOut.w = sclp * pStart.w + sclq * end.w;
		return pOut;
	}	

	public Matrix4f toRotationMatrix(Matrix4f inMatrix) {
		// compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
		// will be used 2-4 times each.
		float s = 2.0f;
		float xs = x * s;
		float ys = y * s;
		float zs = z * s;
		float xx = x * xs;
		float xy = x * ys;
		float xz = x * zs;
		float xw = w * xs;
		float yy = y * ys;
		float yz = y * zs;
		float yw = w * ys;
		float zz = z * zs;
		float zw = w * zs;

		// using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
		inMatrix.m00 = 1 - (yy + zz);
		inMatrix.m01 = (xy - zw);
		inMatrix.m02 = (xz + yw);
		inMatrix.m10 = (xy + zw);
		inMatrix.m11 = 1 - (xx + zz);
		inMatrix.m12 = (yz - xw);
		inMatrix.m20 = (xz - yw);
		inMatrix.m21 = (yz + xw);
		inMatrix.m22 = 1 - (xx + yy);
		return inMatrix;
	}

}
