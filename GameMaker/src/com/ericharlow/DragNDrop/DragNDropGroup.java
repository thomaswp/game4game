package com.ericharlow.DragNDrop;

import java.util.LinkedList;

public class DragNDropGroup<T> {

	int[] loc = new int[2];
	
	public LinkedList<DragNDropListView<T>> listViews =
		new LinkedList<DragNDropListView<T>>();

	public void addListView(DragNDropListView<T> view) {
		listViews.add(view);
		view.setGroup(this);
	}

	public boolean canDrop(DragNDropListView<T> view, int x, int y) {
		view.getLocationOnScreen(loc);
		int vx = loc[0], vy = loc[1];
		for (DragNDropListView<T> to : listViews) {
			to.getLocationOnScreen(loc);
			int nx = x + vx - loc[0];
			int ny = y + vy - loc[1];
			if (to.canDrop(nx, ny)) {
				return true;
			}
		}
		return false;
	}
	
	public void drop(DragNDropListView<T> view, int x, int y, T item) {
		view.getLocationOnScreen(loc);
		int vx = loc[0], vy = loc[1];
		for (DragNDropListView<T> to : listViews) {
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
