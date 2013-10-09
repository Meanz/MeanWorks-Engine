package org.meanworks.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.meanworks.engine.math.Frustum;
import org.meanworks.engine.math.MatrixHelper;
import org.meanworks.engine.math.Ray;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.math.VectorMath;
import org.meanworks.engine.scene.Node;

public class Camera {

	/*
	 * The view frustum for the camera
	 */
	private Frustum cameraFrustum;

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
	private Vec3 cameraPosition;
	/*
	 * The target that the camera is looking at
	 */
	private Vec3 cameraLookAt;
	/*
	 * Euler representation of the cameras angles
	 */
	private Vec3 rotation;
	/*
	 * 
	 */
	private static Vec3 xVector = new Vec3(1.0f, 0.0f, 0.0f);
	/*
	 * 
	 */
	private static Vec3 zVector = new Vec3(0.0f, 0.0f, 1.0f);
	/*
	 * 
	 */
	private static Vec3 upVector = new Vec3(0.0f, 1.0f, 0.0f);
	/*
	 * 
	 */
	private boolean isFlying = false;
	/*
	 * 
	 */
	private Node followTarget;

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
		this.cameraPosition = new Vec3(0.0f, 0.0f, 0.0f);
		this.cameraLookAt = new Vec3(1.0f, 1.0f, 1.0f);
		this.rotation = new Vec3(0.0f, 0.0f, 0.0f);

		projectionMatrix = perspective(fovY, aspect, 0.0f, 1000.0f,
				new Matrix4f());
		viewMatrix = new Matrix4f();
		projectionViewMatrix = new Matrix4f();
		cameraTranslationMatrix = new Matrix4f();

		cameraFrustum = new Frustum();
	}

	/**
	 * Make the camera follow the given node
	 * 
	 * @param target
	 */
	public void follow(Node target) {
		this.followTarget = target;
	}

	/**
	 * Check whether we are flying or not
	 * 
	 * @return
	 */
	public boolean isFlying() {
		return isFlying;
	}

	/**
	 * Get the window ray
	 * 
	 * @return
	 */
	public Ray getWindowRay() {
		float factor = (float) Math.cos(Math.toRadians(getPitch()));
		Vector3f forward = new Vector3f();
		forward.x = (float) Math.sin(Math.toRadians(getYaw())) * factor;
		forward.y = (float) Math.sin(Math.toRadians(-getPitch()));
		forward.z = (float) -Math.cos(Math.toRadians(getYaw())) * factor;
		forward.normalise();
		return new Ray(new Vector3f(getPosition().x, getPosition().y,
				getPosition().z), forward);
	}

	/**
	 * Get the camera frustum
	 * 
	 * @return
	 */
	public Frustum getFrustum() {
		return cameraFrustum;
	}

	/**
	 * 
	 * @return
	 */
	public Matrix4f getProjectionViewMatrix() {
		return projectionViewMatrix;
	}

	/**
	 * 
	 * @return
	 */
	public Matrix4f getModelMatrix() {
		return cameraTranslationMatrix;
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
	public void setPosition(Vec3 position) {
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
	public Vec3 getPosition() {
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

		perspective(fovY, aspect, 1.0f, 1000.0f, projectionMatrix);
		// viewMatrix = lookAt(cameraPosition, forward(cameraPosition,
		// rotation));

		if (isFlying()) {
			viewMatrix.setIdentity();
			viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(
					xVector.x, xVector.y, xVector.z));
			viewMatrix.rotate((float) Math.toRadians(rotation.y), new Vector3f(
					upVector.x, upVector.y, upVector.z));
			viewMatrix.translate(new Vector3f(cameraPosition.x,
					cameraPosition.y, cameraPosition.z).negate(null),
					viewMatrix);
		} else if (followTarget != null) {
			/*
			 * viewMatrix = lookAt(cameraPosition, followTarget.getTransform()
			 * .getPosition());
			 */
			viewMatrix.setIdentity();
			viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(
					xVector.x, xVector.y, xVector.z));
			viewMatrix.rotate((float) Math.toRadians(rotation.y + 180),
					new Vector3f(upVector.x, upVector.y, upVector.z));
			viewMatrix.translate(new Vector3f(cameraPosition.x,
					cameraPosition.y, cameraPosition.z).negate(null),
					viewMatrix);
		} else {
			viewMatrix.setIdentity();

			viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(
					xVector.x, xVector.y, xVector.z));
			viewMatrix.rotate((float) Math.toRadians(rotation.y), new Vector3f(
					upVector.x, upVector.y, upVector.z));
			viewMatrix.translate(new Vector3f(cameraPosition.x,
					cameraPosition.y, cameraPosition.z).negate(null),
					viewMatrix);
		}
		// Translation
		MatrixHelper.setupMatrices(projectionMatrix, viewMatrix);

		// Calculate matrices
		Matrix4f.mul(projectionMatrix, viewMatrix, projectionViewMatrix);

		cameraFrustum.createFrustrum(projectionMatrix, viewMatrix);
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
			float farPlane, Matrix4f storeIn) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		{
			GL11.glPushMatrix();
			{
				GL11.glLoadIdentity();
				GLU.gluPerspective(fovY, aspect, nearPlane, farPlane);
				MatrixHelper.getMatrix(GL11.GL_PROJECTION_MATRIX, storeIn);
			}
			GL11.glPopMatrix();
		}
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		return storeIn;
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
		Vec3 eye = cameraPosition;
		// Create a forward vector
		float factor = (float) Math.cos(Math.toRadians(rotation.x));
		Vec3 forward = new Vec3((float) Math.sin(Math
				.toRadians(rotation.y + 180.0f)) * factor,
				(float) Math.sin(Math.toRadians(-rotation.x)),
				(float) -Math.cos(Math.toRadians(rotation.y + 180.0f)) * factor);
		forward.scale(100);
		Vec3 center = new Vec3(cameraPosition.x + forward.x, cameraPosition.y
				+ forward.y, cameraPosition.z + forward.z);
		Vec3 a = Vec3.sub(eye, center).negate();
		Vec3 zaxis = a.normalize();
		Vec3 xaxis = Vec3.cross(zaxis, upVector).normalize();
		Vec3 yaxis = Vec3.cross(xaxis, zaxis);

		Vec3 dirVec = Vec3.add(Vec3.scale(xaxis, alpha), Vec3.scale(yaxis, beta));
		dirVec.add(zaxis);
		
		float absLength = (float) Math.abs(dirVec.getLength());
		Vector3f rayDirection = new Vector3f(dirVec.x / absLength, dirVec.y
				/ absLength, dirVec.z / absLength);

		return new Ray(new Vector3f(eye.x, eye.y, eye.z), rayDirection);
	}

	public void thirdPersonCamera() {
		//
		if (Mouse.isButtonDown(1)) { // RMB
			float mouseRatio = 0.2f;

			float yincr = Application.getApplication().getInputHandler()
					.getDX()
					* mouseRatio;
			float pincr = -Application.getApplication().getInputHandler()
					.getDY()
					* mouseRatio;
			yaw(yincr);
			// pitch(pincr);
		}

		// Set our position to the players position
		this.setPosition(followTarget.getTransform().getPosition());

		// The distance from the camera
		float distance = 10;

		// Find the yaw of our camera
		float yaw = (float) Math.toRadians(getYaw());
		float pitch = (float) Math.toRadians(getPitch());

		float factor = (float) Math.cos(Math.toRadians(rotation.x));
		Vector3f forward = new Vector3f();
		forward.x = (float) Math.sin(Math.toRadians(rotation.y)) * factor;
		forward.y = (float) Math.sin(Math.toRadians(-rotation.x));
		forward.z = (float) -Math.cos(Math.toRadians(rotation.y)) * factor;

		translate((float) forward.x * distance, (float) forward.y * distance,
				(float) forward.z * distance);
	}

	public void update() {
		float moveSpeed = 0.2f;
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			Application.getApplication().stop();
		}

		if (followTarget != null) {

			if (Mouse.isButtonDown(1)) { // RMB
				float mouseRatio = 0.2f;

				float yincr = Application.getApplication().getInputHandler()
						.getDX()
						* mouseRatio;
				float pincr = Application.getApplication().getInputHandler()
						.getDY()
						* mouseRatio;
				yaw(yincr);
				pitch(pincr);
			}

			// Set our position to the players position
			this.setPosition(followTarget.getTransform().getPosition());

			this.translate(0.0f, 2.5f, 0.0f);

		} else if (isFlying()) {
			if (Mouse.isButtonDown(1)) { // RMB
				float mouseRatio = 0.2f;

				float yincr = Application.getApplication().getInputHandler()
						.getDX()
						* mouseRatio;
				float pincr = -Application.getApplication().getInputHandler()
						.getDY()
						* mouseRatio;
				yaw(yincr);
				pitch(pincr);
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				moveSpeed = 1.0f;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				translate(
						moveSpeed * (float) Math.sin(Math.toRadians(getYaw())),
						0.0f,
						-moveSpeed * (float) Math.cos(Math.toRadians(getYaw())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				translate(
						moveSpeed
								* (float) Math.sin(Math
										.toRadians(getYaw() + 90)),
						0.0f,
						-moveSpeed
								* (float) Math.cos(Math
										.toRadians(getYaw() + 90)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				translate(
						moveSpeed
								* (float) Math.sin(Math
										.toRadians(getYaw() - 90)),
						0.0f,
						-moveSpeed
								* (float) Math.cos(Math
										.toRadians(getYaw() - 90)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				translate(
						-moveSpeed * (float) Math.sin(Math.toRadians(getYaw())),
						0.0f,
						moveSpeed * (float) Math.cos(Math.toRadians(getYaw())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				translate(0.0f, -0.5f * moveSpeed, 0.0f);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				translate(0.0f, 0.5f * moveSpeed, 0.0f);
			}
		} else {
		}
	}
}
