package org.meanworks.engine.render.geometry.mesh.renderers;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.RenderState;
import org.meanworks.engine.render.geometry.Mesh;
import org.meanworks.engine.render.material.Material;

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
public class ImmediateRenderer implements MeshRenderer {

	private Mesh mesh;

	public ImmediateRenderer(Mesh mesh) {

		this.mesh = mesh;

	}

	public void render(Material m) {

		if (mesh.triangles == null) {
			EngineLogger.error("Tried to render a mesh with no data.");
			return;
		}

		glDisable(GL_TEXTURE_2D);
		glColor3f(1.0f, 1.0f, 1.0f);
		
		glPushMatrix();
		{

			glLoadIdentity();
			Matrix4f matrix = RenderState.getTransformMatrix();
			FloatBuffer mat = BufferUtils.createFloatBuffer(16);
			matrix.store(mat);
			mat.flip();
			GL11.glMultMatrix(mat);

			glBegin(GL_TRIANGLES);
			{
				// Draw triangles
				for (int i = 0; i < mesh.triangles.length; i++) {
					int triIdx = mesh.triangles[i];

					glVertex3f(mesh.positions[triIdx * 3],
							mesh.positions[triIdx * 3 + 1],
							mesh.positions[triIdx * 3 + 2]);

				}
			}
			glEnd();

		}
		glPopMatrix();

	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}

}
