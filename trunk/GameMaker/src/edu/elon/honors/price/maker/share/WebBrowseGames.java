package edu.elon.honors.price.maker.share;

import java.util.LinkedList;

import com.eujeux.data.GameInfo;
import com.eujeux.data.GameList;
import com.eujeux.data.WebSettings.SortType;

import edu.elon.honors.price.maker.AutoAssign;
import edu.elon.honors.price.maker.AutoAssignUtils;
import edu.elon.honors.price.maker.IViewContainer;
import edu.elon.honors.price.maker.R;
import edu.elon.honors.price.maker.share.DataUtils.FetchCallback;
import edu.elon.honors.price.maker.share.GameAdapter.OnScrolledToBottomListener;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

@AutoAssign
public class WebBrowseGames extends Activity implements IViewContainer {
	private static final int FETCH_COUNT = 4;
	
	private static final String[] sortStrings = new String[] {
		"Upload Date",
		"Downloads",
		"Rating"
	};
	
	private ListView listViewGames;
	private Button buttonSortUpload, buttonSortDownloads, buttonSortRating;
	private Button[] sortButtons;
	
	
	private LinkedList<GameInfo> games = new LinkedList<GameInfo>();
	private String cursorString;
	private GameAdapter adapter;
	private ProgressBar footerLoadbar;
	
	private SortType sortType;
	private boolean sortDesc;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_browse_games);
		
		AutoAssignUtils.autoAssign(this);
		
		sortButtons = new Button[] {
				buttonSortUpload,
				buttonSortDownloads,
				buttonSortRating
		};
		
		
		footerLoadbar = new ProgressBar(this);
		footerLoadbar.setIndeterminate(true);
		footerLoadbar.setPadding(5, 5, 5, 5);
		listViewGames.addFooterView(footerLoadbar);
		
		adapter = new GameAdapter(this, games);
		listViewGames.setAdapter(adapter);
		
		setSort(SortType.UploadDate, true);
		
		adapter.setOnScrolledToBottomListener(new OnScrolledToBottomListener() {
			@Override
			public void onScrolledToBottom() {
				if (cursorString != null) {
					fetch();
				}
			}
		});
		
		for (int i = 0; i < sortButtons.length; i++) {
			final SortType newType = SortType.values()[i];
			sortButtons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (sortType == newType) {
						setSort(newType, !sortDesc);
					} else {
						setSort(newType, true);
					}
				}
			});
		}

	}
	
	public void setSort(SortType type, boolean desc) {
		if (sortType == type && sortDesc == desc) return;
		
		this.sortType = type;
		this.sortDesc = desc;
		
		games.clear();
		adapter.resetLoadPosition();
		cursorString = null;
		fetch();
		
		
		for (int i = 0; i < sortButtons.length; i++) {
			String text = sortStrings[i];
			if (i == type.ordinal()) text += desc ? " \u25BC" : " \u25B2"; 
			sortButtons[i].setText(text);
		}
	}
	
	
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		return DialogUtils.handleDialog(this, id, args);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		super.onPrepareDialog(id, dialog, args);
		DialogUtils.prepareDialog(id, dialog, args);
	}

	private void fetch() {
		DataUtils.fetchGameList(this, FETCH_COUNT, cursorString,
				sortType, sortDesc, new FetchCallback<GameList>() {
			@Override
			public void fetchFailed(String error) {
				DialogUtils.showErrorDialog(WebBrowseGames.this, 
						"Browse Failed", "Could not browse games. " +
						"Check the connection and try again", error);
			}
			
			@Override
			public void fetchComplete(GameList result) {
				cursorString = result.cursorString;
				if (result.size() < FETCH_COUNT) {
					listViewGames.removeFooterView(footerLoadbar);
					cursorString = null;
				}
				games.addAll(result);
				adapter.notifyDataSetChanged();
			}
		});
	}
}

