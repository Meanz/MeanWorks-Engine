package org.fractalstudio.engine.gui;

import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

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
	private int saveX, saveY;
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
		// System.err.println("[InputManager]");
		/**
		 * Create Mouse
		 */
		if (!Mouse.isCreated()) {
			try {
				Mouse.create();
				System.err.println("    - Created Mouse");
			} catch (LWJGLException lwjgle) {
				System.err
						.println("    - Unable to create mouse, error occured:"
								+ lwjgle.getMessage());
			}
		} else {
			// System.err.println("    - Unable to create mouse, already created");
		}
		/**
		 * Create Keyboard
		 */
		if (!Keyboard.isCreated()) {
			try {
				Keyboard.create();
				System.err.println("    - Created Keyboard");
			} catch (LWJGLException lwjgle) {
				System.err
						.println("    - Unable to create keyboard, error occured:"
								+ lwjgle.getMessage());
			}
		} else {
			// System.err.println("    - Unable to create keyboard, already created");
		}
		// System.err.println("    - Active -");
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
		mouseY = Mouse.getY();
		// deltaX = mouseX - saveX;
		// deltaY = saveY - mouseY;
		deltaX = Mouse.getDX();
		deltaY = -Mouse.getDY();
		saveX = mouseX;
		saveY = mouseY;
		mouseY = Display.getHeight() - Mouse.getY();
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
			if (!itc.containsKey(Keyboard.getEventKey())) {
				itc.put(Keyboard.getEventKey(), Keyboard.getEventCharacter());
			}
			if (Keyboard.isKeyDown(Keyboard.getEventKey())) {
				/**
				 * Fire a keyboard pressed event and continue
				 */
				for (KeyListener keyListener : keyListeners) {
					keyListener.keyPressed(key);
				}
			} else {
				/**
				 * Fire a keyboard released event and continue
				 */
				for (KeyListener keyListener : keyListeners) {
					keyListener.keyReleased(key);
				}
			}
		}
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
