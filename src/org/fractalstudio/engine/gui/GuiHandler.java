package org.fractalstudio.engine.gui;

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

import org.fractalstudio.engine.Application;
import org.fractalstudio.engine.EngineLogger;
import org.fractalstudio.render.opengl.ImmediateRenderer;
import org.lwjgl.input.Mouse;

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
	 * 
	 */
	private boolean consumedInput = false;

	/*
	 * Whether or there is a input lock active.
	 */
	private boolean inputLock = false;

	/*
	 * The component that has input focus
	 */
	private Component mouseFocus;

	/*
	 * The component that has key focus
	 */
	private Component keyFocus;

	/**
	 * Constructor
	 * 
	 * @param application
	 */
	public GuiHandler(Application application) {
		this.application = application;
		fontRenderer = new FontRenderer("./data/fonts/arial.ttf", 14);
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
	 * 
	 * @param component
	 * @return
	 */
	public boolean mouseLock(Component component) {
		if (mouseFocus != null) {
			return false;
		}
		mouseFocus = component;
		return true;
	}

	/**
	 * 
	 * @param component
	 * @return
	 */
	public boolean mouseRelease(Component component) {
		if (component == mouseFocus) {
			mouseFocus = null;
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public void update() {
		consumedInput = false;
		if (mouseFocus != null || keyFocus != null) {
			consumedInput = true;
		}
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
		return true;
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
						for (Component component : components.values()) {
							component.fireRender();
						}
						fontRenderer.drawString("Tech Demo v0.2 Alpha [DEBUG]",
								10, 10);
						fontRenderer.drawString("FPS: " + application.getFps(),
								10, 25);
						fontRenderer
								.drawString(
										"Cam("
												+ application.getCamera()
														.getPosition().x
												+ ", "
												+ application.getCamera()
														.getPosition().y
												+ ", "
												+ application.getCamera()
														.getPosition().z + ")",
										10, 40);

						fontRenderer
								.drawString(
										"Memory usage: "
												+ ((Runtime.getRuntime()
														.totalMemory() - Runtime
														.getRuntime()
														.freeMemory()) / 1000 / 1000)
												+ "mb", 10, 55);

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

		if (mouseFocus != null) {
			if (mouseFocus.fireMouseDown(key, x, y)) {
				return;
			}
		}

		for (int i = components.values().size() - 1; i >= 0; i--) {
			if (((Component) components.values().toArray()[i]).fireMouseDown(
					key, x, y)) {
				return;
			}
		}

		/*
		 * if (mouseFocus != null) { mouseFocus.actionPerformed(this, new
		 * MouseEvent( EventType.MOUSE_PRESSED, key, x, y, application
		 * .getInputHandler().getDX(), application .getInputHandler().getDY()));
		 * return; } for (Component component : components.values()) { if
		 * (isInsideComponent(component, x, y)) {
		 * component.actionPerformed(this, new MouseEvent(
		 * EventType.MOUSE_PRESSED, key, x, y, application
		 * .getInputHandler().getDX(), application .getInputHandler().getDY()));
		 * consumedInput = true; break; } }
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
		if (mouseFocus != null) {
			if (mouseFocus.fireMouseUp(key, x, y)) {
				return;
			}
		}

		for (int i = components.values().size() - 1; i >= 0; i--) {
			if (((Component) components.values().toArray()[i]).fireMouseUp(key,
					x, y)) {
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.MouseListener#mouseMoved(int, int)
	 */
	@Override
	public void mouseMoved(int dx, int dy) {

		// TODO: Revise the method of getting mouse coordinates
		// TODO: Revise this method, uses a lot of processing.

		if (mouseFocus != null) {
			if (mouseFocus.fireMouseMove(Component.getMouseX(), Component.getMouseY(), dx, dy)) {
				return;
			}
		}

		for (int i = components.values().size() - 1; i >= 0; i--) {
			if (((Component) components.values().toArray()[i]).fireMouseMove(
					Mouse.getX(), Mouse.getY(), dx, dy)) {
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.KeyListener#keyPressed(int)
	 */
	@Override
	public void keyPressed(int key) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.KeyListener#keyReleased(int)
	 */
	@Override
	public void keyReleased(int key) {
		// TODO Auto-generated method stub

	}
}
