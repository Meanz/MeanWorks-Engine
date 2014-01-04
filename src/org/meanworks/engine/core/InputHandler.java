package org.meanworks.engine.core;

import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.meanworks.engine.EngineLogger;

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
public class InputHandler {
	/**
	 * The mouse and key listeners
	 */
	private LinkedList<KeyListener> keyListeners;
	private LinkedList<MouseListener> mouseListeners;
	/**
	 * Mouse values
	 */
	private int mouseX;
	private int mouseY;
	private int deltaX;
	private int deltaY;
	private int deltaWheel;

	/**
	 * The pressed values
	 */
	private LinkedList<Integer> mousePressedButtons;
	private LinkedList<Integer> mouseReleasedButtons;
	private LinkedList<Integer> keyboardPressedKeys;
	private LinkedList<Integer> keyboardReleasedKeys;

	/**
	 * Some cool stuff
	 */
	private String allowedKeys = "abcdefghijklmnopqrstuvwxyzæøåABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ1234567890§\"![]{}$#%&/()=?\\-+,.-_:;<>*^ ";
	private HashMap<Integer, Character> itc = new HashMap<Integer, Character>();
	private LinkedList<Integer> keysDown = new LinkedList<Integer>();
	private LinkedList<Integer> keysPressed = new LinkedList<Integer>();

	/**
	 * Initializes a new input manager
	 */
	public InputHandler() {
		keyListeners = new LinkedList<KeyListener>();
		mouseListeners = new LinkedList<MouseListener>();
		mousePressedButtons = new LinkedList<Integer>();
		mouseReleasedButtons = new LinkedList<Integer>();
		keyboardPressedKeys = new LinkedList<Integer>();
		keyboardReleasedKeys = new LinkedList<Integer>();

		/**
		 * Initialize
		 */
		/**
		 * Create Mouse
		 */
		if (!Mouse.isCreated()) {
			try {
				Mouse.create();
			} catch (LWJGLException lwjgle) {
				EngineLogger
						.error("    - Unable to create mouse, error occured:"
								+ lwjgle.getMessage());
			}
		} else {
			EngineLogger.error("    - Unable to create mouse, already created");
		}
		/**
		 * Create Keyboard
		 */
		if (!Keyboard.isCreated()) {
			try {
				Keyboard.create();
			} catch (LWJGLException lwjgle) {
				EngineLogger
						.error("    - Unable to create keyboard, error occured:"
								+ lwjgle.getMessage());
			}
		} else {
			EngineLogger
					.error("    - Unable to create keyboard, already created");
		}
	}

	public static boolean isMouseGrabbed() {
		return Mouse.isGrabbed();
	}

	public static void grabMouse() {
		Mouse.setGrabbed(true);
	}

	public static void ungrabMouse() {
		Mouse.setGrabbed(false);
	}

	/**
	 * Add a mouse listener to this input manager
	 * 
	 * @param mouseListener
	 */
	public void addMouseListener(MouseListener mouseListener) {
		mouseListeners.add(mouseListener);
	}

	/**
	 * Add a key listener to this input manager
	 * 
	 * @param keyListener
	 */
	public void addKeyListener(KeyListener keyListener) {
		keyListeners.add(keyListener);
	}

	/**
	 * Key to char
	 * 
	 * @param key
	 * @return
	 */
	public char keyToChar(int key) {
		if (itc.containsKey(key)) {
			return itc.get(key);
		} else {
			return '0';
		}
	}

	/**
	 * The main update loop
	 */
	public void update() {
		/**
		 * Stored mouse delta values
		 */
		mouseX = Mouse.getX();
		mouseY = Display.getHeight() - Mouse.getY();
		// deltaX = mouseX - saveX;
		// deltaY = saveY - mouseY;
		deltaX = Mouse.getDX();
		deltaY = -Mouse.getDY();
		deltaWheel = Mouse.getDWheel();

		/**
		 * Check if the mouse was moved or not
		 */
		if (deltaX != 0 || deltaY != 0) {
			for (MouseListener mouseListener : mouseListeners) {
				mouseListener.mouseMoved(deltaX, deltaY);
			}
		}

		/**
		 * Check if any mouse button was pressed
		 */
		while (Mouse.next()) {
			int button = Mouse.getEventButton();
			if (Mouse.getButtonName(button) == null) {
				continue;
			}
			// System.err.println("Button: " + Mouse.getButtonName(button) +
			// " was " + (Mouse.getEventButtonState() ? "pressed" :
			// "released"));
			/**
			 * Check if mouse was newly pressed
			 */
			if (Mouse.isButtonDown(button)) {

				/**
				 * Fire a mouse pressed event and continue
				 */
				for (MouseListener mouseListener : mouseListeners) {
					mouseListener.mousePressed(button, mouseX, mouseY);
				}
			} else {
				/**
				 * Fire a mouse released event and continue
				 */
				for (MouseListener mouseListener : mouseListeners) {
					mouseListener.mouseReleased(button, mouseX, mouseY);
				}
			}
		}

		/**
		 * Check for keyboard presses
		 */
		while (Keyboard.next()) {
			int key = Keyboard.getEventKey();
			boolean press = Keyboard.getEventKeyState();
			if (!itc.containsKey(Keyboard.getEventKey())) {
				itc.put(Keyboard.getEventKey(), Keyboard.getEventCharacter());
			}
			if (press) {
				/**
				 * Save the state of the key
				 */
				if (keysPressed.contains(key)) {
					keysPressed.remove(key); // Cast to object to make sure we
												// don't use remove(index)
				} else {
					keysPressed.add(key);
				}
				if (!keysDown.contains(key)) {
					keysDown.add(key);
				}
				/**
				 * Fire a keyboard pressed event and continue
				 */
				for (KeyListener keyListener : keyListeners) {
					keyListener.keyPressed(key);
				}
			} else {
				/**
				 * Remove the state of the key
				 */
				if (keysPressed.contains(key)) {
					keysPressed.remove((Object) key); // Cast to object to make
														// sure we don't use
														// remove(index)
				}
				if (keysDown.contains(key)) {
					keysDown.remove((Object) key); // Cast to object to make
													// sure we don't use
													// remove(index)
				}
				/**
				 * Fire a keyboard released event and continue
				 */
				for (KeyListener keyListener : keyListeners) {
					keyListener.keyReleased(key);
				}
			}
		}
	}

	public boolean isValidKey(int key) {
		if (allowedKeys.contains("" + keyToChar(key))) {
			return true;
		} else {
			return false;
		}
	}

	public int getSize() {
		return keysPressed.size() + keysDown.size();
	}

	public boolean isKeyPressed(int key) {
		return keysPressed.contains(key);
	}

	public boolean isKeyDown(int key) {
		return keysDown.contains(key);
	}

	public int getDeltaWheel() {
		return deltaWheel;
	}

	public int getDX() {
		return deltaX;
	}

	public int getDY() {
		return deltaY;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}
}
