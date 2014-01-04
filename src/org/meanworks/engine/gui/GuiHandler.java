package org.meanworks.engine.gui;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;

import java.util.HashMap;
import java.util.LinkedList;

import org.meanworks.engine.EngineConfig;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.RenderState;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.core.KeyListener;
import org.meanworks.engine.core.MouseListener;
import org.meanworks.engine.render.FontRenderer;
import org.meanworks.engine.render.opengl.ImmediateRenderer;
import org.meanworks.engine.util.NumberFormatter;
import org.meanworks.testgame.world.World;

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
public class GuiHandler implements KeyListener, MouseListener {

	/*
	 * Reference to the application controlling this gui handler
	 */
	private Application application;

	/*
	 * The font renderer
	 */
	private FontRenderer fontRenderer;

	/*
	 * The list of gui components
	 */
	private HashMap<String, Component> components = new HashMap<String, Component>();

	/*
	 * The list of gui components flagged for deletion
	 */
	private LinkedList<String> deleteQueue = new LinkedList<String>();

	/*
	 * Whether the input was consumed or not if the input was consumed no other
	 * components would receive events.
	 */
	private boolean consumedInput = false;

	/*
	 * The root component
	 */
	private Component rootComponent;

	/**
	 * Constructor
	 * 
	 * @param application
	 */
	public GuiHandler(Application application) {
		this.application = application;
		fontRenderer = new FontRenderer("./data/fonts/arial.ttf", 14);

		/*
		 * We add components to this root component
		 */
		rootComponent = new Component("RootComponent", 0, 0, application
				.getWindow().getWidth(), application.getWindow().getHeight()) {

			@Override
			public void render() {
			}
		};
	}

	/**
	 * Flags a component for deletion (Does not work on sub components)
	 */
	public void flagDelete(String componentId) {
		if (componentId == null) {
			return;
		}
		deleteQueue.add(componentId);
	}

	/**
	 * Get whether the input was consumed or not
	 * 
	 * @return
	 */
	public boolean didConsumeInput() {
		return consumedInput;
	}

	/**
	 * Update the components 60times/second
	 */
	public void update() {
		rootComponent.fireUpdate();
	}

	/**
	 * Add a component to the gui
	 * 
	 * @param component
	 * @return
	 */
	public boolean addComponent(Component component) {
		if (component == null) {
			EngineLogger.warning("Tried to add null component.");
			return false;
		}
		if (components.containsKey(component.getName())) {
			EngineLogger
					.warning("Tried adding duplicate component ( or at least duplicate name ) : "
							+ component.getName());
			return false;
		}
		components.put(component.getName(), component);
		rootComponent.add(component);
		return true;
	}

	/**
	 * Remove the given component
	 * 
	 * @param componentId
	 * @return
	 */
	public boolean removeComponent(String componentId) {
		if (componentId == null) {
			return false;
		}
		if (components.containsKey(componentId)) {
			rootComponent.remove(components.get(componentId));
			components.remove(componentId);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The render function for the gui handler
	 */
	public void render() {

		ImmediateRenderer.setupOrtho(0, application.getWindow().getWidth(), 0,
				application.getWindow().getHeight());

		glLoadIdentity();
		glDisable(GL_DEPTH_TEST);
		{

			glEnable(GL_BLEND);
			{
				glEnable(GL_TEXTURE_2D);
				{
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
					{
						fontRenderer.drawString("FractalEngine v "
								+ EngineConfig.MW_VERSION + " Alpha [DEBUG]",
								10, 10);
						fontRenderer.drawString("FPS: " + application.getFps(),
								10, 25);
						fontRenderer.drawString("Cam("
								+ application.getScene().getCamera()
										.getPosition().x
								+ ", "
								+ application.getScene().getCamera()
										.getPosition().y
								+ ", "
								+ application.getScene().getCamera()
										.getPosition().z + ")", 10, 40);

						fontRenderer
								.drawString(
										"Memory usage: "
												+ ((Runtime.getRuntime()
														.totalMemory() - Runtime
														.getRuntime()
														.freeMemory()) / 1000 / 1000)
												+ "mb", 10, 55);

						fontRenderer
								.drawString(
										"Rendered Vertices: "
												+ NumberFormatter
														.formatNumber(RenderState
																.getRenderedVertices()),
										10, 70);

						fontRenderer.drawString("Top Component: "
								+ rootComponent.getComponents().getFirst()
										.getName(), 10, 85);

						rootComponent.fireRender();
					}
				}
				glDisable(GL_TEXTURE_2D);
			}
			glDisable(GL_BLEND);
		}
		glEnable(GL_DEPTH_TEST);
	}

	/**
	 * Check whether the given coordinates is inside a component
	 * 
	 * @param component
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isInsideComponent(Component component, int x, int y) {
		if ((x > component.getX() && x < component.getX()
				+ component.getWidth())
				&& (y > component.getY() && y < component.getY()
						+ component.getHeight())) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.MouseListener#mousePressed(int, int,
	 * int)
	 */
	@Override
	public void mousePressed(int key, int x, int y) {

		rootComponent.fireMouseDown(key, x, y);

		/*
		 * if (mouseFocus != null) { if (mouseFocus.fireMouseDown(key, x, y)) {
		 * return; } }
		 * 
		 * for (int i = components.values().size() - 1; i >= 0; i--) { if
		 * (((Component) components.values().toArray()[i]).fireMouseDown( key,
		 * x, y)) { return; } }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.MouseListener#mouseReleased(int, int,
	 * int)
	 */
	@Override
	public void mouseReleased(int key, int x, int y) {

		rootComponent.fireMouseUp(key, x, y);
		/*
		 * if (mouseFocus != null) { if (mouseFocus.fireMouseUp(key, x, y)) {
		 * return; } }
		 * 
		 * for (int i = components.values().size() - 1; i >= 0; i--) { if
		 * (((Component) components.values().toArray()[i]).fireMouseUp(key, x,
		 * y)) { return; } }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.MouseListener#mouseMoved(int, int)
	 */
	@Override
	public void mouseMoved(int dx, int dy) {

		rootComponent.fireMouseMove(Component.getMouseX(),
				Component.getMouseY(), dx, dy);

		// TODO: Revise the method of getting mouse coordinates
		// TODO: Revise this method, uses a lot of processing.

		/*
		 * if (mouseFocus != null) { if
		 * (mouseFocus.fireMouseMove(Component.getMouseX(),
		 * Component.getMouseY(), dx, dy)) { return; } }
		 * 
		 * for (int i = components.values().size() - 1; i >= 0; i--) { if
		 * (((Component) components.values().toArray()[i]).fireMouseMove(
		 * Component.getMouseX(), Component.getMouseY(), dx, dy)) { return; } }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.KeyListener#keyPressed(int)
	 */
	@Override
	public void keyPressed(int key) {

		rootComponent.fireKeyDown(key);
		/*
		 * if (keyFocus != null) { if (keyFocus.fireKeyDown(key)) { return; } }
		 * 
		 * for (int i = components.values().size() - 1; i >= 0; i--) { if
		 * (((Component) components.values().toArray()[i]).fireKeyDown(key)) {
		 * return; } }
		 */
	}

	/*
	 * Fire key releases to everyone! (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.KeyListener#keyReleased(int)
	 */
	@Override
	public void keyReleased(int key) {
		rootComponent.fireKeyUp(key);
		/*
		 * for (int i = components.values().size() - 1; i >= 0; i--) { if
		 * (((Component) components.values().toArray()[i]).fireKeyUp(key)) {
		 * return; } }
		 */
	}
}
