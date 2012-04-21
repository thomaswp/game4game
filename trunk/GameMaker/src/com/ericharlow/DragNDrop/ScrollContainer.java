package com.ericharlow.DragNDrop;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class ScrollContainer extends LinearLayout {

	private DragNDropListView listView;
	private RelativeLayout block;
	private OScrollView scrollView;

	public DragNDropListView getListView() {
		return listView;
	}

	public ScrollContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public ScrollContainer(Context context) {
		super(context);
		setup();
	}

	private void setup() {
		setOrientation(HORIZONTAL);

		LayoutParams lps = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		lps.weight = 1;
		RelativeLayout layout = new RelativeLayout(getContext());
		addView(layout, lps);

		RelativeLayout background = new RelativeLayout(getContext());
		RelativeLayout.LayoutParams rlps = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		rlps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rlps.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		rlps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rlps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layout.addView(background, rlps);
		
		listView = new DragNDropListView(getContext());
		rlps = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		rlps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rlps.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layout.addView(listView, rlps);
		listView.setDropListener(new DropListener() {
			@Override
			public void onDropTo(int to, String item) {
				if (listView.getAdapter() instanceof DragNDropAdapter) {
					((DragNDropAdapter)listView.getAdapter())
					.onDropTo(to, item);
					adjustSize();
					listView.invalidateViews();
					invalidate();
				}
			}

			@Override
			public String onDropFrom(int from) {
				if (listView.getAdapter() instanceof DragNDropAdapter) {
					String s = ((DragNDropAdapter)listView.getAdapter())
					.onDropFrom(from);
					adjustSize();
					listView.invalidateViews();
					invalidate();
					return s;
				}
				return null;
			}
		});


		RelativeLayout div = new RelativeLayout(getContext());
		div.setBackgroundResource(R.drawable.border_white_no_pad);
		lps = new LayoutParams(2, LayoutParams.MATCH_PARENT);
		addView(div, lps);

		scrollView = new OScrollView(getContext());
		lps = new LayoutParams(60, LayoutParams.MATCH_PARENT);
		addView(scrollView, lps);

		scrollView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(int scrollX, int scrollY) {
				adjustScroll();
			}
		});

		block = new RelativeLayout(getContext());
		lps = new LayoutParams(LayoutParams.MATCH_PARENT, 100);
		scrollView.addView(block, lps);
		
		this.post(new Runnable() {
			@Override
			public void run() {
				adjustSize();
			}
		});

		setBackgroundResource(R.drawable.border_white);
		//listView.setBackgroundResource(R.drawable.border_green);
	}
	
	private void adjustSize() {
		int height = 0;
		Adapter adapter = listView.getAdapter();
		int count = adapter.getCount();
		if (listView.getChildCount() != 0 && count != 0) {
			height = count * listView.getChildAt(0).getHeight();
		}
		block.getLayoutParams().height = height;
		block.requestLayout();
		listView.post(new Runnable() {
			@Override
			public void run() {
				adjustScroll();
			}
		});
		
//		int scroll = height - (scrollView.getHeight() + scrollView.getScrollY());
//		Game.debug(scroll);
//		if (scroll < 0) {
//			scrollView.scrollBy(0, scroll);	
//		}
	}
	
	private void adjustScroll() {
		((RelativeLayout.LayoutParams)listView.getLayoutParams())
		.topMargin = -scrollView.getScrollY();
		listView.invalidateViews();
		listView.invalidate();
	}

	private static class OScrollView extends ScrollView {
		private OnScrollListener onScrollListener;

		public void setOnScrollListener(OnScrollListener onScrollListener) {
			this.onScrollListener = onScrollListener;
		}

		public OScrollView(Context context) {
			super(context);
		}

		@Override
		protected void onScrollChanged(int l, int t, int oldl, int oldt) {
			super.onScrollChanged(l, t, oldl, oldt);
			if (onScrollListener != null) {
				onScrollListener.onScroll(getScrollX(), getScrollY());
			}
		}
	}

	private interface OnScrollListener {
		public void onScroll(int scrollX, int scrollY);
	}

}
