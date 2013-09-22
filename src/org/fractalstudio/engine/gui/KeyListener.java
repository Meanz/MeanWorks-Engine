package org.fractalstudio.engine.gui;

public interface KeyListener {

    /**
     * Called whenever a key is pressed
     *
     * @param key The key pressed
     */
    public void keyPressed(int key);

    /**
     * Called whenever a key is released
     *
     * @param key The key released
     */
    public void keyReleased(int key);
}
