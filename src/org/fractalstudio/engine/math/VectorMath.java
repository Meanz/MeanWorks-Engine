package org.fractalstudio.engine.math;

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

	public static boolean intersectsPlane(Ray ray, Vector3f planeNormal,
			Vector3f intersectionPoint) {
		float vdotn = dot(ray.direction, planeNormal);
		if (vdotn == 0)
			return false;
		float t = -dot(ray.origin, planeNormal) / vdotn;
		if (t > 0) {
			intersectionPoint = add(ray.origin, mulLocal(ray.direction, t));
			return true;
		} else
			return false;
	}

	public static boolean intersectsTriangle(Ray ray, Vector3f p1, Vector3f p2,
			Vector3f p3, Vector3f hitPoint) {
		boolean HIT = true;
		boolean MISS = false;
		float epsilon = 0.000001f;
		float det, inv_det;

		Vector3f edge1, edge2, tvec, pvec, qvec;

		edge1 = sub(p2, p1);
		edge2 = sub(p3, p1);

		pvec = cross(ray.direction, edge2);

		det = dot(edge1, pvec);

		if (det < epsilon)
			return MISS;

		tvec = sub(ray.origin, p1);

		hitPoint.x = dot(tvec, pvec);

		if (hitPoint.x < 0.0f || hitPoint.x > det)
			return MISS;

		qvec = cross(tvec, edge1);

		hitPoint.y = dot(ray.direction, qvec);

		if (hitPoint.y < 0.0f || hitPoint.x + hitPoint.y > det)
			return MISS;

		hitPoint.z = dot(edge2, qvec);
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
