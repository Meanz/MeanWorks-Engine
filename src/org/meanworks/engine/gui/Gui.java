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
import org.meanworks.engine.render.opengl.GLImmediate;
import org.meanworks.engine.render.opengl.Screen;
import org.meanworks.engine.scene.Scene;
import org.meanworks.engine.util.NumberFormatter;

/**
 * Copyright (C) 2014 Steffen Evensen
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
 * 
 *         Handles Gui related events
 * 
 */
public class Gui implements KeyListener, MouseListener {

	/**
	 * 
	 * @author Meanz
	 * 
	 */
	private static class GuiString {

		public String text;
		public int x;
		public int y;

		public GuiString(String text, int x, int y) {
			this.text = text;
			this.x = x;
			this.y = y;
		}

	}

	/**
	 * The gui singleton
	 */
	private static Gui singleton;

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

	/**
	 * Wherher the key input was consumed or not
	 */
	private boolean consumedKeyInput = false;

	/**
	 * Whether or not key input is locked
	 */
	private boolean keyInputLock = false;

	/**
	 * The component that is locking the key input
	 */
	private Component keyInputLockComponent;

	/*
	 * The root component
	 */
	private Component rootComponent;

	/**
	 * The GuiStrings
	 */
	private LinkedList<GuiString> guiStrings = new LinkedList<GuiString>();

	/**
	 * Constructor
	 * 
	 * @param application
	 */
	public Gui(Application application) {
		if (singleton != null) {
			throw new RuntimeException(
					"Only one instance of the gui might be created at a given time.");
		}
		singleton = this;
		this.application = application;
		fontRenderer = new FontRenderer("./data/fonts/arial.ttf", 14);

		/*
		 * We add components to this root component
		 */
		rootComponent = new Component("RootComponent", 0, 0, Screen.getWidth(),
				Screen.getHeight()) {

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
		singleton.deleteQueue.add(componentId);
	}

	/**
	 * Lock key input
	 * 
	 * @param c
	 * @return
	 */
	public static boolean lockKeyInput(Component c) {
		if (isKeyInputLocked() && getKeyInputLockComponent() != c) {
			return false;
		}
		singleton.keyInputLock = true;
		singleton.keyInputLockComponent = c;
		return true;
	}

	/**
	 * Open the key input
	 */
	public static void openKeyInput() {
		singleton.keyInputLock = false;
		singleton.keyInputLockComponent = null;
	}

	/**
	 * Get the key input lock component
	 * 
	 * @return
	 */
	public static Component getKeyInputLockComponent() {
		return singleton.keyInputLockComponent;
	}

	/**
	 * Check whether key input is blocked or not
	 * 
	 * @return
	 */
	public static boolean isKeyInputLocked() {
		return singleton.keyInputLock;
	}

	/**
	 * Get whether the input was consumed or not
	 * 
	 * @return
	 */
	public static boolean didConsumeInput() {
		return singleton.consumedInput;
	}

	/**
	 * Get whether the input was consumed or not
	 * 
	 * @return
	 */
	public static boolean didConsumeKeyInput() {
		return singleton.consumedKeyInput;
	}

	/**
	 * Update the components 60times/second
	 */
	public void update() {
		rootComponent.fireUpdate();
	}

	/**
	 * Flag clearing
	 */
	public void postUpdate() {
		consumedInput = false;
		consumedKeyInput = false;
	}

	/**
	 * Add a component to the gui
	 * 
	 * @param component
	 * @return
	 */
	public static boolean addComponent(Component component) {
		if (component == null) {
			EngineLogger.warning("Tried to add null component.");
			return false;
		}
		if (singleton.components.containsKey(component.getName())) {
			EngineLogger
					.warning("Tried adding duplicate component ( or at least duplicate name ) : "
							+ component.getName());
			return false;
		}
		singleton.components.put(component.getName(), component);
		singleton.rootComponent.add(component);
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
	 * 
	 * @param s
	 * @param x
	 * @param y
	 */
	public static void drawString(String s, int x, int y) {
		Application.getApplication().getGui().guiStrings.add(new GuiString(s,
				x, y));
	}

	/**
	 * The render function for the gui handler
	 */
	public void render() {

		GLImmediate.setupOrtho(0, Screen.getWidth(), 0, Screen.getHeight());

		glLoadIdentity();
		glDisable(GL_DEPTH_TEST);
		{

			glEnable(GL_BLEND);
			{
				glEnable(GL_TEXTURE_2D);
				{
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
					{

						for (GuiString string : guiStrings) {
							fontRenderer.drawString(string.text, string.x,
									string.y);
						}
						guiStrings.clear();

						if (EngineConfig.drawGuiDebug) {
							fontRenderer.drawString("FractalEngine v "
									+ EngineConfig.MW_VERSION
									+ " Alpha [DEBUG]", 10, 10);
							fontRenderer.drawString(
									"FPS: " + application.getFps(), 10, 25);
							fontRenderer.drawString("Cam("
									+ Scene.getCamera().getPosition().x + ", "
									+ Scene.getCamera().getPosition().y + ", "
									+ Scene.getCamera().getPosition().z + ")",
									10, 40);

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

						}

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
		if (rootComponent.fireMouseDown(key, x, y)) {
			consumedInput = true; // If the gui consumed the input
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.MouseListener#mouseReleased(int, int,
	 * int)
	 */
	@Override
	public void mouseReleased(int key, int x, int y) {
		if (rootComponent.fireMouseUp(key, x, y)) {
			consumedInput = true;
		}
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.KeyListener#keyPressed(int)
	 */
	@Override
	public void keyPressed(int key) {
		System.err.println("KeyPressed");
		if (rootComponent.fireKeyDown(key)) {
			consumedKeyInput = true;
		}
	}

	/*
	 * Fire key releases to everyone! (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.KeyListener#keyReleased(int)
	 */
	@Override
	public void keyReleased(int key) {
		if (rootComponent.fireKeyUp(key)) {
			consumedKeyInput = true;
		}
	}
}
