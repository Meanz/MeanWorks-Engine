package org.fractalstudio.engine.gui.event;

public abstract class ActionEvent {

	private EventType eventType;

	public ActionEvent(EventType eventType) {
		this.eventType = eventType;
	}

	public EventType getEventType() {
		return eventType;
	}
	
	public MouseEvent asMouseEvent() {
		return (MouseEvent)this;
	}
	
	public KeyEvent asKeyEvent() {
		return (KeyEvent)this;
	}

}
