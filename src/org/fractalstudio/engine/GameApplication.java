package org.fractalstudio.engine;

import org.fractalstudio.render.opengl.Window;

public abstract class GameApplication extends Application {
	
	public void setup() {
		setWindow(Window.createWindow(800, 640));
	}
	
}
