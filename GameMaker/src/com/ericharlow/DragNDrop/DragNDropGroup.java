package com.ericharlow.DragNDrop;

import java.util.LinkedList;

public class DragNDropGroup {

	int[] loc = new int[2];
	
	public LinkedList<DragNDropListView> listViews =
		new LinkedList<DragNDropListView>();

	public void addListView(DragNDropListView view) {
		listViews.add(view);
		view.setGroup(this);
	}

	public boolean canDrop(DragNDropListView view, int x, int y) {
		view.getLocationOnScreen(loc);
		int vx = loc[0], vy = loc[1];
		for (DragNDropListView to : listViews) {
			to.getLocationOnScreen(loc);
			int nx = x + vx - loc[0];
			int ny = y + vy - loc[1];
			if (to.canDrop(nx, ny)) {
				return true;
			}
		}
		return false;
	}
	
	public void drop(DragNDropListView view, int x, int y, String item) {
		view.getLocationOnScreen(loc);
		int vx = loc[0], vy = loc[1];
		for (DragNDropListView to : listViews) {
			to.getLocationOnScreen(loc);
			int nx = x + vx - loc[0];
			int ny = y + vy - loc[1];
			if (to.canDrop(nx, ny)) {
				to.drop(nx, ny, item);
				return;
			}
		}
	}
}
