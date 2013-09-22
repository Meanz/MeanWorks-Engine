package org.fractalstudio.render.opengl;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.fractalstudio.render.geometry.Vertex;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ImmediateRenderer {

	/**
	 * 
	 */
	public static void setupOrtho(float left, float right, float top,
			float bottom) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GL11.glOrtho(left, right, bottom, top, -1.0f, 1.0f);
		glMatrixMode(GL_MODELVIEW);
	}

	/**
	 * Setup a perspective projection matrix
	 * 
	 * @param width
	 * @param height
	 * @param fov
	 */
	public static void setupPerspective(int width, int height, int fov) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		// glFrustum(-1.0, 1.0, -1.0, 1.0, 1.0f, 100);
		GLU.gluPerspective(fov, width / height, 0.1f, 1000.0f);
		glMatrixMode(GL_MODELVIEW);
	}

	/**
	 * 
	 */
	public static void enableBlending() {
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_BLEND);
	}
	
	/**
	 * 
	 */
	public static void disableBlending() {
		glDisable(GL_BLEND);
	}

	/**
	 * 
	 */
	public static void identityModelView() {
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	/**
	 * Draw a plane from the given four vertices
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param p4
	 */
	public static void drawPlane(Vertex p1, Vertex p2, Vertex p3, Vertex p4) {
		glBegin(GL_TRIANGLES);
		{
			glVertex3f(p1.getPosition().x, p1.getPosition().y,
					p1.getPosition().z);
			glVertex3f(p2.getPosition().x, p2.getPosition().y,
					p2.getPosition().z);
			glVertex3f(p4.getPosition().x, p4.getPosition().y,
					p4.getPosition().z);

			glVertex3f(p2.getPosition().x, p2.getPosition().y,
					p2.getPosition().z);
			glVertex3f(p3.getPosition().x, p3.getPosition().y,
					p3.getPosition().z);
			glVertex3f(p4.getPosition().x, p4.getPosition().y,
					p4.getPosition().z);
		}
		glEnd();
	}

	/**
	 * Draw a plane
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param width
	 * @param length
	 */
	public static void drawPlane(float x, float y, float z, float width,
			float length) {
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0.0f, length * 1.0f);
			glVertex3f(x + width, y, z);
			glTexCoord2f(width * 1.0f, length * 1.0f);
			glVertex3f(x, y, z);
			glTexCoord2f(width * 1.0f, 0.0f);
			glVertex3f(x, y, z + length);
			glTexCoord2f(0.0f, 0.0f);
			glVertex3f(x + width, y, z + length);
		}
		glEnd();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void drawTexturedQuad(float x, float y, float width,
			float height) {
		glBegin(GL_TRIANGLES);
		{
			glTexCoord2f(0.0f, 0.0f);
			glVertex2f(x, y);

			glTexCoord2f(0.0f, 1.0f);
			glVertex2f(x, y + height);

			glTexCoord2f(1.0f, 1.0f);
			glVertex2f(x + width, y + height);

			glTexCoord2f(0.0f, 0.0f);
			glVertex2f(x, y);

			glTexCoord2f(1.0f, 1.0f);
			glVertex2f(x + width, y + height);

			glTexCoord2f(1.0f, 0.0f);
			glVertex2f(x + width, y);

		}
		glEnd();
	}

}
