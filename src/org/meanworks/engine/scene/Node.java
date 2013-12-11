package org.meanworks.engine.scene;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glColor3f;

import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glPopMatrix;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.meanworks.engine.bounding.BoundingBox;
import org.meanworks.engine.math.Transform;
import org.meanworks.render.opengl.ImmediateRenderer;

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
public abstract class Node {

	/*
	 * The parent node of this node
	 */
	private Node parent;

	/*
	 * The children of this node
	 */
	private LinkedList<Node> children = new LinkedList<>();

	/*
	 * Whether or not this node inherits transforms from it's parent
	 */
	private NodeInheritance nodeInheritance;

	/*
	 * The culling method for this node
	 */
	private CullHint cullHint;

	/*
	 * The culling box for this node
	 */
	private BoundingBox cullingBox;

	/*
	 * The transform of this node
	 */
	private Transform transform;

	/*
	 * The global transform matrix of this node
	 */
	private Matrix4f globalTransform;

	/*
	 * Whether something changed with this node, Calls to update all other sub
	 * nodes.
	 */
	private boolean needsUpdate;

	/**
	 * Construct a new node
	 */
	public Node() {
		cullHint = CullHint.PARENT_CULL;
		transform = new Transform();
		nodeInheritance = NodeInheritance.INHERIT_NONE;
		needsUpdate = true;
		cullingBox = new BoundingBox();
		globalTransform = new Matrix4f();
	}

	/**
	 * Get the children of this node
	 * 
	 * @return
	 */
	public List<Node> getChildren() {
		return children;
	}

	/**
	 * Add a child to this node
	 * 
	 * @param child
	 */
	public void addChild(Node child) {
		if (child != null) {
			child.setParent(this);
			children.add(child);
			System.err.println("Children Size: " + children.size());
		}
	}

	/**
	 * Get the parent node of this node
	 * 
	 * @return
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Set the parent node of this node
	 * 
	 * @param parent
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * Get the transform of this node
	 * 
	 * @return
	 */
	public Transform getTransform() {
		return transform;
	}

	/**
	 * 
	 */
	public void updateCullingBox() {
		// Pass the request down the hierarchy to see if updates are needed

	}

	/**
	 * Get the global transform matrix for this node
	 * 
	 * @return
	 */
	public Matrix4f getGlobalTransform() {
		return globalTransform;
	}

	/**
	 * Get the transform matrix of this node
	 */
	public void calculateTransformMatrix() {
		if (nodeInheritance == NodeInheritance.INHERIT_NONE) {
			if (needsUpdate) {
				globalTransform = transform.getTransformMatrix();
			}
		} else if (nodeInheritance == NodeInheritance.INHERIT_TRANSFORM) {
			final Node _parent = parent;
			if (_parent != null) {
				if (_parent.needsUpdate || needsUpdate) {
					globalTransform = Matrix4f.mul(
							_parent.getGlobalTransform(),
							transform.getTransformMatrix(), null);
					needsUpdate = true;
				}
			} else {
				globalTransform = transform.getTransformMatrix();
			}
		} else {
			throw new UnsupportedOperationException(
					"Can not handle such node inheritance yet.");
		}
	}

	/**
	 * Update method called from the Application
	 */
	public final void doUpdate() {

		/*
		 * Update transform if needed
		 */
		updateCullingBox();

		/*
		 * Check if our transform has updated
		 */
		if (transform.isChanged()) {
			needsUpdate = true;
			transform.unflagChange();
		}

		/*
		 * Send the transform update
		 */
		calculateTransformMatrix();

		/*
		 * Send the update request to the class that extends this node
		 */
		update();
	}

	/**
	 * Called after the update method
	 */
	public final void doPostUpdate() {
		// Unflag update flag
		if (needsUpdate) {
			needsUpdate = false;
		}
	}

	/**
	 * Render method called from the Application
	 */
	public final void doRender() {
		renderNodeBox();
		render();
	}

	/**
	 * Update this node
	 */
	public void update() {

	};

	/**
	 * Render this node
	 */
	public void render() {
		
	};

	/**
	 * 
	 */
	public void renderNodeBox() {
		glDisable(GL_TEXTURE_2D);

		// ImmediateRenderer.setModelMatrix(getTransformMatrix());
		glPushMatrix();

		final Matrix4f transf = getGlobalTransform();
		GL11.glTranslatef(transf.m30, transf.m31, transf.m32);

		glColor3f(1.0f, 0.0f, 0.0f);
		glBegin(GL_QUADS);
		{
			drawCube(0.5f, 0.5f, 0.5f);
		}
		glEnd();
		glColor3f(1.0f, 1.0f, 1.0f);

		glPopMatrix();
	}

	public static void drawCube(float w, float h, float l) {
		final float minx = -(w / 2);
		final float miny = -(h / 2);
		final float minz = -(l / 2);
		final float maxx = (w / 2);
		final float maxy = (h / 2);
		final float maxz = (l / 2);
		// Quad 1
		GL11.glNormal3f(1, 0, 0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(maxx, maxy, maxz); // V2
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(maxx, miny, maxz); // V1
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(maxx, miny, minz); // V3
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(maxx, maxy, minz); // V4
		// Quad 2
		GL11.glNormal3f(0, 0, -1);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(maxx, maxy, minz); // V4
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(maxx, miny, minz); // V3
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(minx, miny, minz); // V5
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(minx, maxy, minz); // V6
		// Quad 3
		GL11.glNormal3f(-1, 0, 0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(minx, maxy, minz); // V6
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(minx, miny, minz); // V5
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(minx, miny, maxz); // V7
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(minx, maxy, maxz); // V8
		// Quad 4
		GL11.glNormal3f(0, 0, 1);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(minx, maxy, maxz); // V8
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(minx, miny, maxz); // V7
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(maxx, miny, maxz); // V1
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(maxx, maxy, maxz); // V2
		// Quad 5
		GL11.glNormal3f(0, 1, 0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(minx, maxy, minz); // V6
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(minx, maxy, maxz); // V8
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(maxx, maxy, maxz); // V2
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(maxx, maxy, minz); // V4
		// Quad 6
		GL11.glNormal3f(0, -1, 0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(minx, miny, maxz); // V7
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(minx, miny, minz); // V5
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(maxx, miny, minz); // V3
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(maxx, miny, maxz); // V1
	}

}
