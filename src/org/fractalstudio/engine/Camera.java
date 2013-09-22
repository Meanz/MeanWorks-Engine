package org.fractalstudio.engine;

import org.fractalstudio.engine.math.MatrixHelper;
import org.fractalstudio.engine.math.Ray;
import org.fractalstudio.engine.math.VectorMath;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	/*
	 * The view matrix for the camera
	 */
	private Matrix4f viewMatrix;
	/*
	 * The priojection matrix for the camera
	 */
	private Matrix4f projectionMatrix;
	/*
	 * 
	 */
	private Matrix4f cameraTranslationMatrix;
	/*
	 * 
	 */
	private Matrix4f projectionViewMatrix;
	/*
	 * Attributes that the camera needs to know
	 */
	private int windowWidth;
	private int windowHeight;
	private float fovY;
	private float aspect;
	/*
	 * Euclidean representation of the cameras position
	 */
	private Vector3f cameraPosition;
	/*
	 * The target that the camera is looking at
	 */
	private Vector3f cameraLookAt;
	/*
	 * Euler representation of the cameras angles
	 */
	private Vector3f rotation;
	/*
	 * 
	 */
	private static Vector3f xVector = new Vector3f(1.0f, 0.0f, 0.0f);
	/*
	 * 
	 */
	private static Vector3f zVector = new Vector3f(0.0f, 0.0f, 1.0f);
	/*
	 * 
	 */
	private static Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);

	/**
	 * Construct a new camera
	 * 
	 * @param windowWidth
	 * @param windowHeight
	 * @param fovY
	 * @param aspect
	 */
	public Camera(int windowWidth, int windowHeight, float fovY, float aspect) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.fovY = fovY;
		this.aspect = aspect;
		this.cameraPosition = new Vector3f(0.0f, 0.0f, 0.0f);
		this.cameraLookAt = new Vector3f(1.0f, 1.0f, 1.0f);
		this.rotation = new Vector3f(0.0f, 0.0f, 0.0f);

		projectionMatrix = perspective(fovY, aspect, 0.0f, 1000.0f);
		viewMatrix = new Matrix4f();
		projectionViewMatrix = new Matrix4f();
		cameraTranslationMatrix = new Matrix4f();
	}
	
	public Matrix4f getProjectionViewMatrix() {
		return projectionMatrix;
	}
	
	public Matrix4f getModelMatrix() {
		return viewMatrix;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void translate(float x, float y, float z) {
		cameraPosition.translate(x, y, z);
	}

	/**
	 * Set the position of the camera
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}

	/**
	 * Set the position of the camera
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(float x, float y, float z) {
		cameraPosition.x = x;
		cameraPosition.y = y;
		cameraPosition.z = z;
	}

	/**
	 * Get the camera position
	 * 
	 * @return
	 */
	public Vector3f getPosition() {
		return cameraPosition;
	}

	/**
	 * 
	 * @return
	 */
	public float getYaw() {
		return rotation.y;
	}

	/**
	 * 
	 * @param yaw
	 */
	public void yaw(float yaw) {
		rotation.y += yaw;
	}

	/**
	 * 
	 * @return
	 */
	public float getPitch() {
		return rotation.x;
	}

	/**
	 * 
	 * @param pitch
	 */
	public void pitch(float pitch) {
		rotation.x += pitch;
	}

	/**
	 * Bugged function
	 * 
	 * @param position
	 * @param orientation
	 * @return
	 */
	public Vector3f forward(Vector3f position, Vector3f orientation) {
		return Vector3f.add(position, new Vector3f(orientation.x * 2.0f,
				orientation.y * 2.0f, orientation.z * 2.0f), null);
	}

	/**
	 * Updates the camera matrices
	 */
	public void updateCamera() {
		/*
		 * viewMatrix = lookAt(cameraPosition, forward(cameraPosition,
		 * rotation)); projectionViewMatrix = Matrix4f.mul(projectionMatrix,
		 * viewMatrix, null);
		 * 
		 * cameraTranslationMatrix = Matrix4f.translate(cameraPosition,
		 * cameraTranslationMatrix, null); //Matrix4f.rotate(angle, axis, src,
		 * dest) MatrixHelper.setupMatrices(projectionMatrix, viewMatrix);
		 */

		projectionMatrix = perspective(fovY, aspect, 1.0f, 1000.0f);
		// viewMatrix = lookAt(cameraPosition, forward(cameraPosition,
		// rotation));
		viewMatrix.setIdentity();
		viewMatrix.rotate((float) Math.toRadians(rotation.x), xVector);
		viewMatrix.rotate((float) Math.toRadians(rotation.y), upVector);
		viewMatrix.translate(cameraPosition.negate(null), viewMatrix);

		// Translation
		MatrixHelper.setupMatrices(projectionMatrix, viewMatrix);

	}

	/**
	 * 
	 */
	public Matrix4f lookAt(Vector3f eye, Vector3f target) {
		Matrix4f persp = new Matrix4f();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		{
			GL11.glPushMatrix();
			{
				GL11.glLoadIdentity();
				GLU.gluLookAt(eye.x, eye.y, eye.z, target.x, target.y,
						target.z, upVector.x, upVector.y, upVector.z);
				MatrixHelper.getMatrix(GL11.GL_MODELVIEW_MATRIX, persp);
			}
			GL11.glPopMatrix();
		}
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		return persp;
		/*
		 * Vector3f zaxis = (Vector3f) Vector3f.sub(eye, target,
		 * null).normalise(); Vector3f xaxis = (Vector3f)
		 * Vector3f.cross(upVector, zaxis, null) .normalise(); Vector3f yaxis =
		 * Vector3f.cross(zaxis, xaxis, null);
		 * 
		 * Matrix4f orientation = new Matrix4f(); Matrix4f translation = new
		 * Matrix4f();
		 * 
		 * FloatBuffer tempBuffer = BufferUtils.createFloatBuffer(16);
		 * tempBuffer.put(new float[] { xaxis.x, yaxis.x, zaxis.x, 0, xaxis.y,
		 * yaxis.y, zaxis.y, 0, xaxis.z, yaxis.z, zaxis.z, 0, 0, 0, 0, 1 });
		 * tempBuffer.flip(); orientation.load(tempBuffer); tempBuffer =
		 * BufferUtils.createFloatBuffer(16); tempBuffer.put(new float[] { 1, 0,
		 * 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, -eye.x, -eye.y, -eye.z, 1 });
		 * tempBuffer.flip(); translation.load(tempBuffer);
		 * 
		 * viewMatrix = Matrix4f.mul(orientation, translation, null);
		 */
	}

	/**
	 * Temporary function for setting up the projection matrix
	 */
	private Matrix4f perspective(float fovY, float aspect, float nearPlane,
			float farPlane) {
		Matrix4f persp = new Matrix4f();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		{
			GL11.glPushMatrix();
			{
				GL11.glLoadIdentity();
				GLU.gluPerspective(fovY, aspect, nearPlane, farPlane);
				MatrixHelper.getMatrix(GL11.GL_PROJECTION_MATRIX, persp);
			}
			GL11.glPopMatrix();
		}
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		return persp;
	}

	/**
	 * 
	 */
	public void immediateCameraSetup() {
		// Setup camera
		GL11.glRotatef(rotation.x, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(rotation.y, 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(-cameraPosition.x, -cameraPosition.y,
				-cameraPosition.z);
	}

	/**
	 * Get the pick ray of this camera This be messy but good code, TODO: Use
	 * less operations
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public Ray getPickRay(int mouseX, int mouseY) {

		// Get the point on the near plane we clicked!
		// First we need to normalize the mouse coordinates
		// To do that we need to know the height of the viewport
		float fovy = fovY;
		float fovx = 60.0f;
		float mx = (float) mouseX;
		float my = (float) mouseY;
		float ww = (float) windowWidth;
		float wh = (float) windowHeight;
		float nmx = (mx - (ww / 2.0f)) / (ww / 2.0f);
		float nmy = -((wh / 2.0f) - my) / (wh / 2.0f);
		float alpha = (float) Math.tan(Math.toRadians(fovx / 2.0f)) * nmx;
		float beta = (float) Math.tan(Math.toRadians(fovy / 2.0f)) * nmy;
		Vector3f eye = cameraPosition;
		// Create a forward vector
		float factor = (float) Math.cos(Math.toRadians(rotation.x));
		Vector3f forward = new Vector3f();
		forward.x = (float) Math.sin(Math.toRadians(rotation.y)) * factor;
		forward.y = (float) Math.sin(Math.toRadians(-rotation.x));
		forward.z = (float) -Math.cos(Math.toRadians(rotation.y)) * factor;
		VectorMath.mulLocal(forward, 100);
		Vector3f center = new Vector3f(cameraPosition.x + forward.x,
				cameraPosition.y + forward.y, cameraPosition.z + forward.z);
		Vector3f a = (Vector3f) VectorMath.sub(eye, center).negate();
		Vector3f zaxis = a.normalise(null);
		Vector3f xaxis = VectorMath.cross(zaxis, upVector).normalise(null);
		Vector3f yaxis = VectorMath.cross(xaxis, zaxis);

		Vector3f dirVec = VectorMath.add(
				VectorMath.add(VectorMath.mul(xaxis, alpha),
						VectorMath.mul(yaxis, beta)), zaxis);

		float absLength = (float) Math.abs(dirVec.length());
		Vector3f rayDirection = new Vector3f(dirVec.x / absLength, dirVec.y
				/ absLength, dirVec.z / absLength);

		return new Ray(new Vector3f(eye.x, eye.y, eye.z), rayDirection);
	}
}
