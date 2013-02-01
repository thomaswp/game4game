package edu.elon.honors.price.maker.share;

import java.util.List;

import com.eujeux.data.GameInfo;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

public class GameAdapter extends ArrayAdapter<GameInfo>{

	private OnScrolledToBottomListener onScrolledToBottomListener;
	private int lastLoadPosition = 0;

	public void setOnScrolledToBottomListener(
			OnScrolledToBottomListener onScrolledToBottomListener) {
		this.onScrolledToBottomListener = onScrolledToBottomListener;
	}

	public GameAdapter(Activity context, List<GameInfo> objects) {
		super(context, 0, objects);
	}
	
	public void resetLoadPosition() {
		lastLoadPosition = 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (onScrolledToBottomListener != null) {
			if (position == getCount() - 1 && lastLoadPosition < position) {
				lastLoadPosition = position;
				onScrolledToBottomListener.onScrolledToBottom();
			}
		}
		
		GameInfo info = getItem(position);
		if (convertView == null) {
			convertView = new WebGameView((Activity)getContext(), info);
		} else {
			((WebGameView)convertView).setGame(info);
		}
		return convertView;
	}

	public interface OnScrolledToBottomListener {
		public void onScrolledToBottom();
	}
}
