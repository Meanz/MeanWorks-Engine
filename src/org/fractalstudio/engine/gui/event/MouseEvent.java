package org.fractalstudio.engine.gui.event;

public class MouseEvent extends ActionEvent {

	public static int LMB = 0;
	public static int RMB = 1;
	public static int MMB = 2;
	
	private int key;
	private int x;
	private int y;
	private int dx;
	private int dy;

	public MouseEvent(EventType eventType, int key, int x, int y, int dx, int dy) {
		super(eventType);
		this.key = key;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
	public int getKey() {
		return key;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getDX() {
		return dx;
	}

	public int getDY() {
		return dy;
	}

}
