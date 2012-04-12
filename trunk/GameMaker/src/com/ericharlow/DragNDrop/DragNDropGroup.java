package com.ericharlow.DragNDrop;

import java.util.LinkedList;

import edu.elon.honors.price.game.Game;

public class DragNDropGroup {

	public LinkedList<DragNDropListView> listViews =
		new LinkedList<DragNDropListView>();

	public void addListView(DragNDropListView view) {
		listViews.add(view);
		view.setGroup(this);
	}

	public boolean canDrop(DragNDropListView view, int x, int y) {
		for (DragNDropListView to : listViews) {
			int nx = x + view.getLeft() - to.getLeft();
			int ny = y + view.getTop() - to.getTop();
			if (to.canDrop(nx, ny)) {
				return true;
			}
		}
		return false;
	}
	
	public void drop(DragNDropListView view, int x, int y, String item) {
		for (DragNDropListView to : listViews) {
			int nx = x + view.getLeft() - to.getLeft();
			int ny = y + view.getTop() - to.getTop();
			if (to.canDrop(nx, ny)) {
				to.drop(nx, ny, item);
				return;
			}
		}
	}
}
