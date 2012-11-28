package edu.elon.honors.price.input;

public abstract class UIControl {
	protected boolean isActive = true;
	
	public abstract boolean isVisible();
	public abstract void setVisible(boolean visible);
	
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}
}
