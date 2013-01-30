package edu.elon.honors.price.maker.share;

import java.util.LinkedList;

import com.eujeux.data.GameInfo;
import com.eujeux.data.GameList;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.AutoAssign;
import edu.elon.honors.price.maker.AutoAssignUtils;
import edu.elon.honors.price.maker.IViewContainer;
import edu.elon.honors.price.maker.R;
import edu.elon.honors.price.maker.share.DataUtils.FetchCallback;
import edu.elon.honors.price.maker.share.GameAdapter.OnScrolledToBottomListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

@AutoAssign
public class WebBrowseGames extends Activity implements IViewContainer {
	private static int FETCH_COUNT = 2;
	
	private ListView listViewGames;
	private LinkedList<GameInfo> games = new LinkedList<GameInfo>();
	private String cursorString;
	private GameAdapter adapter;
	private ProgressBar footerLoadbar;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_browse_games);
		
		AutoAssignUtils.autoAssign(this);
		
		
		footerLoadbar = new ProgressBar(this);
		footerLoadbar.setIndeterminate(true);
		footerLoadbar.setPadding(5, 5, 5, 5);
		listViewGames.addFooterView(footerLoadbar);
		
		adapter = new GameAdapter(this, games);
		listViewGames.setAdapter(adapter);
		
		adapter.setOnScrolledToBottomListener(new OnScrolledToBottomListener() {
			@Override
			public void onScrolledToBottom() {
				if (cursorString != null) {
					fetch();
				}
			}
		});
		
		fetch();
	}
	
	private void fetch() {
		DataUtils.fetchGameList(this, FETCH_COUNT, cursorString, new FetchCallback<GameList>() {
			@Override
			public void fetchFailed() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void fetchComplete(GameList result) {
				cursorString = result.cursorString;
				if (result.size() < FETCH_COUNT) {
					listViewGames.removeFooterView(footerLoadbar);
					//footerLoadbar.setVisibility(View.GONE);
					cursorString = null;
				}
				games.addAll(result);
				adapter.notifyDataSetChanged();
			}
		});
	}
}

