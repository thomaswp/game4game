/*
 * Copyright (C) 2010 Eric Harlow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ericharlow.DragNDrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

public class DragNDropListView extends ListView {

	private static final int MARGIN = 30;
	
	boolean mDragMode;

	int mStartPosition;
	int mDragPointOffsetY, mDragPointOffsetX;	//Used to adjust drag view location
	DragNDropGroup group;

	ImageView mDragView;
	GestureDetector mGestureDetector;
	
	private DragNDropListener dragNDropListener;

	DropListener mDropListener = new DropListener() {
		@Override
		public void onDropTo(int to, String item) {
			if (getAdapter() instanceof DragNDropAdapter) {
				((DragNDropAdapter)getAdapter()).onDropTo(to, item);
				invalidateViews();
				if (dragNDropListener != null) {
					dragNDropListener.onItemDroppedTo(item, to);
				}
			}
		}
		
		@Override
		public String onDropFrom(int from) {
			if (getAdapter() instanceof DragNDropAdapter) {
				invalidateViews();
				String item = ((DragNDropAdapter)getAdapter()).onDropFrom(from); 
				if (dragNDropListener != null) {
					dragNDropListener.onItemDroppedTo(item, from);
				}
				return item;
			}
			return null;
		}
	};
	//	RemoveListener mRemoveListener;
	DragListener mDragListener = new DragListener() {
		
		@Override
		public void onStopDrag(View itemView) {
			itemView.setVisibility(View.VISIBLE);
			
		}
		
		@Override
		public void onStartDrag(View itemView) {
			itemView.setVisibility(View.INVISIBLE);
			
		}
		
		@Override
		public void onDrag(int x, int y, ListView listView) {
		}
	};

	public void setGroup(DragNDropGroup dragNDropGroup) {
		this.group = dragNDropGroup;
	}
	
	public DragNDropListView(Context context) {
		super(context);
		setup();
	}

	public DragNDropListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}
	
	private void setup() {
		this.setPadding(0, 0, 0, MARGIN);
	}

	public void setOnDragNDropListener(DragNDropListener dragAndDropListener) {
		this.dragNDropListener = dragAndDropListener;
	}
	
	public interface DragNDropListener {
		public void onItemDroppedFrom(String item, int from);
		public void onItemDroppedTo(String item, int to);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final int x = (int) ev.getX();
		final int y = (int) ev.getY();	

		if (action == MotionEvent.ACTION_DOWN) {
			mDragMode = true;
		}

		if (!mDragMode) 
			return super.onTouchEvent(ev);
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mStartPosition = pointToPosition(x,y);
			if (mStartPosition != INVALID_POSITION) {
				int mItemPosition = mStartPosition - getFirstVisiblePosition();
				mDragPointOffsetY = y - getChildAt(mItemPosition).getTop();
				mDragPointOffsetY -= (int)ev.getRawY() - y;
				mDragPointOffsetX = x - getChildAt(mItemPosition).getLeft(); 
				mDragPointOffsetX -= (int)ev.getRawX() - x;
				startDrag(mItemPosition, x, y);
				drag(x, y);// replace 0 with x if desired
			}	
			break;
		case MotionEvent.ACTION_MOVE:
			drag(x, y);// replace 0 with x if desired
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		default:
			mDragMode = false;
			stopDrag(mStartPosition - getFirstVisiblePosition());
			if (mStartPosition != INVALID_POSITION) {
				boolean canDrop;
				if (group != null) {
					canDrop = group.canDrop(this, x, y);
				} else {
					canDrop = canDrop(x, y);
				}
				if (canDrop) {
					if (mDropListener != null) {
						String item = mDropListener.onDropFrom(mStartPosition);
						if (group != null) {
							group.drop(this, x, y, item);
						} else {
							drop(x, y, item);
						}
					}
				}
			}
			mStartPosition = -1;
			break;
		}
		return true;
	}	

	public boolean canDrop(int x, int y) {
		return pointToDropPosition(x,y) != INVALID_POSITION;
	}
	
	public void drop(int x, int y, String item) {
		int mEndPosition = pointToDropPosition(x,y);
		if (mDropListener != null && mEndPosition != INVALID_POSITION) {
			if (mEndPosition > getAdapter().getCount()) {
				mEndPosition = getAdapter().getCount();
			}
			mDropListener.onDropTo(mEndPosition, item);
		}
	}
	
	public int pointToDropPosition(int x, int y) {
		int pos = pointToPosition(x, y);
		if (pos == INVALID_POSITION) {
			int bottom;
			if (this.getChildCount() == 0) {
				bottom = 0;
			} else {
				bottom = this.getChildAt(this.getChildCount() - 1)
				.getBottom();
			}
			if (x >= 0 && x < this.getWidth() &&
					y >= bottom && y < this.getHeight()) {
				pos = this.getChildCount() + 1;
			}
		}
		return pos;
	}

	// move the drag view
	private void drag(int x, int y) {
		if (mDragView != null) {
			WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView.getLayoutParams();
			layoutParams.x = x - mDragPointOffsetX;
			layoutParams.y = y - mDragPointOffsetY;
			WindowManager mWindowManager = (WindowManager) getContext()
			.getSystemService(Context.WINDOW_SERVICE);
			mWindowManager.updateViewLayout(mDragView, layoutParams);

			if (mDragListener != null)
				mDragListener.onDrag(x, y, null);// change null to "this" when ready to use
		}
	}

	// enable the drag view for dragging
	private void startDrag(int itemIndex, int x, int y) {
		stopDrag(itemIndex);

		View item = getChildAt(itemIndex);
		if (item == null) return;
		item.setDrawingCacheEnabled(true);
		if (mDragListener != null)
			mDragListener.onStartDrag(item);

		// Create a copy of the drawing cache so that it does not get recycled
		// by the framework when the list tries to clean up memory
		Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());

		WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
		mWindowParams.x = x - mDragPointOffsetX;
		mWindowParams.y = y - mDragPointOffsetY;

		mWindowParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
		| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		Context context = getContext();
		ImageView v = new ImageView(context);
		v.setImageBitmap(bitmap);      

		WindowManager mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(v, mWindowParams);
		mDragView = v;
	}

	// destroy drag view
	private void stopDrag(int itemIndex) {
		if (mDragView != null) {
			if (mDragListener != null)
				mDragListener.onStopDrag(getChildAt(itemIndex));
			mDragView.setVisibility(GONE);
			WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(mDragView);
			mDragView.setImageDrawable(null);
			mDragView = null;
		}
	}

	//	private GestureDetector createFlingDetector() {
	//		return new GestureDetector(getContext(), new SimpleOnGestureListener() {
	//            @Override
	//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	//                    float velocityY) {         	
	//                if (mDragView != null) {              	
	//                	int deltaX = (int)Math.abs(e1.getX()-e2.getX());
	//                	int deltaY = (int)Math.abs(e1.getY() - e2.getY());
	//               
	//                	if (deltaX > mDragView.getWidth()/2 && deltaY < mDragView.getHeight()) {
	//                		mRemoveListener.onRemove(mStartPosition);
	//                	}
	//                	
	//                	stopDrag(mStartPosition - getFirstVisiblePosition());
	//
	//                    return true;
	//                }
	//                return false;
	//            }
	//        });
	//	}
}
