package org.meanworks.engine.math;

import org.lwjgl.util.vector.Vector3f;

public class VectorMath {

	public static void addLocal(Vector3f local, Vector3f ext) {
		local.x += ext.x;
		local.y += ext.y;
		local.z += ext.z;
	}

	public static Vector3f add(Vector3f p1, Vector3f p2) {
		Vector3f out = new Vector3f();
		out.x = p1.x + p2.x;
		out.y = p1.y + p2.y;
		out.z = p1.z + p2.z;
		return out;
	}

	public static void subLocal(Vector3f local, Vector3f ext) {
		local.x -= ext.x;
		local.y -= ext.y;
		local.z -= ext.z;
	}

	public static Vector3f sub(Vector3f p1, Vector3f p2) {
		Vector3f out = new Vector3f();
		out.x = p1.x - p2.x;
		out.y = p1.y - p2.y;
		out.z = p1.z - p2.z;
		return out;
	}

	public static Vector3f mulLocal(Vector3f vec, float scale) {
		vec.x *= scale;
		vec.y *= scale;
		vec.z *= scale;
		return vec;
	}

	public static Vector3f mul(Vector3f p1, float scale) {
		Vector3f out = new Vector3f();
		out.x = p1.x * scale;
		out.y = p1.y * scale;
		out.z = p1.z * scale;
		return out;
	}

	public static float dot(Vector3f p1, Vector3f p2) {
		return Vector3f.dot(p1, p2);
	}

	public static Vector3f cross(Vector3f p1, Vector3f p2) {
		return Vector3f.cross(p1, p2, null);
	}

	public static boolean intersectsPlane(Ray ray, Vec3 planeNormal,
			Vec3 intersectionPoint) {
		float vdotn = ray.direction.dot(planeNormal);
		if (vdotn == 0)
			return false;
		float t = -ray.origin.dot(planeNormal) / vdotn;
		if (t > 0) {
			intersectionPoint = Vec3.add(ray.origin, ray.direction.scale(t));
			return true;
		} else
			return false;
	}

	/**
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param x
	 * @param z
	 * @return
	 */
	public static float getInterpolatedTriangleHeight(Vec3 p1, Vec3 p2, Vec3 p3, float x, float z) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x)
				* (p1.z - p3.z);

		float l1 = ((p2.z - p3.z) * (x - p3.x) + (p3.x - p2.x) * (z - p3.z))
				/ det;
		float l2 = ((p3.z - p1.z) * (x - p3.x) + (p1.x - p3.x) * (z - p3.z))
				/ det;
		float l3 = 1.0f - l1 - l2;

		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	/**
	 * Get the interpolated height value from the given x and y positions
	 * 
	 * @param xPos
	 * @param zPos
	 * @return
	 */
	public static float getInterpolatedQuadHeight(float xPos, float zPos, Vec3 p1,
			Vec3 p2, Vec3 p3, Vec3 p4) {
		float scaleFactor = 1.0f;
		int x = (int) (xPos / scaleFactor);
		int z = (int) (zPos / scaleFactor);
		float triZ0 = (p2.y);
		float triZ1 = (p1.y);
		float triZ2 = (p3.y);
		float triZ3 = (p4.y);
		float height = 0.0f;
		float sqX = (xPos / scaleFactor) - x;
		float sqZ = (zPos / scaleFactor) - z;
		if ((sqX + sqZ) < 1) {
			height = triZ0;
			height += (triZ1 - triZ0) * sqX;
			height += (triZ2 - triZ0) * sqZ;
		} else {
			height = triZ3;
			height += (triZ1 - triZ3) * (1.0f - sqZ);
			height += (triZ2 - triZ3) * (1.0f - sqX);
		}
		return height;
	}

	public static Vec3 intersectsTriangle(Ray ray, Vec3 p1, Vec3 p2, Vec3 p3) {
		Vec3 hitPoint = new Vec3();
		float epsilon = 0.000001f;

		Vec3 edge1 = Vec3.sub(p2, p1);
		Vec3 edge2 = Vec3.sub(p3, p1);

		Vec3 pvec = Vec3.cross(ray.direction, edge2);

		//
		// Check X
		//
		float det = edge1.dot(pvec);

		if (det < epsilon)
			return null;

		Vec3 tvec = Vec3.sub(ray.origin, p1);

		hitPoint.x = tvec.dot(pvec);

		//
		// Check Y
		//
		if (hitPoint.x < 0.0f || hitPoint.x > det)
			return null;

		Vec3 qvec = Vec3.cross(tvec, edge1);

		hitPoint.y = ray.direction.dot(qvec);

		if (hitPoint.y < 0.0f || hitPoint.x + hitPoint.y > det)
			return null;

		//
		// Find Z
		//
		hitPoint.z = edge2.dot(qvec);

		// Calculate hit position
		float inv_det = 1.0f / det;
		hitPoint.x *= inv_det;
		hitPoint.y *= inv_det;
		hitPoint.z *= inv_det;

		return hitPoint;

	}

	/**
	 * Checks whether the given ray intersects the given triangle
	 * 
	 * @param ray
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param hitPoint
	 * @return
	 */
	public static boolean intersectsTriangle(Ray ray, Vec3 p1, Vec3 p2,
			Vec3 p3, Vec3 hitPoint) {
		boolean HIT = true;
		boolean MISS = false;
		float epsilon = 0.000001f;
		float det, inv_det;

		Vec3 edge1, edge2, tvec, pvec, qvec;

		edge1 = Vec3.sub(p2, p1);
		edge2 = Vec3.sub(p3, p1);

		pvec = Vec3.cross(ray.direction, edge2);

		det = edge1.dot(pvec);

		if (det < epsilon)
			return MISS;

		tvec = Vec3.sub(ray.origin, p1);

		hitPoint.x = tvec.dot(pvec);

		if (hitPoint.x < 0.0f || hitPoint.x > det)
			return MISS;

		qvec = Vec3.cross(tvec, edge1);

		hitPoint.y = ray.direction.dot(qvec);

		if (hitPoint.y < 0.0f || hitPoint.x + hitPoint.y > det)
			return MISS;

		hitPoint.z = edge2.dot(qvec);
		inv_det = 1.0f / det;
		hitPoint.x *= inv_det;
		hitPoint.y *= inv_det;
		hitPoint.z *= inv_det;

		return HIT;
	}

	/**
	 * Calc the normal of the given 3 points forming a triangle
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return
	 */
	public static Vector3f calcTriNormal(Vector3f p1, Vector3f p2, Vector3f p3) {
		return cross(sub(p3, p1), sub(p2, p1));
	}

}
