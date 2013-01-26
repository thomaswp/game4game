package edu.elon.honors.price.maker.share;

import com.eujeux.data.GameInfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import edu.elon.honors.price.maker.AutoAssign;
import edu.elon.honors.price.maker.AutoAssignUtils;
import edu.elon.honors.price.maker.IViewContainer;
import edu.elon.honors.price.maker.R;

@AutoAssign
public class ShowGameActivity extends Activity implements IViewContainer {

	private GameInfo gameInfo;
	
	private EditText editTextName, editTextDescription;
	private TextView textViewVersion, textViewDateCreated, textViewCreator, textViewDownloads; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.show_game);
		
		gameInfo = (GameInfo)getIntent().getSerializableExtra("gameInfo");
		
		AutoAssignUtils.autoAssign(this);
		
		editTextName.setText(gameInfo.name);
		textViewVersion.setText(gameInfo.getVersionString());
		textViewDateCreated.setText(gameInfo.uploadDate.toString());
		textViewCreator.setText(gameInfo.creator.userName);
		textViewDownloads.setText("" + gameInfo.downloads);
	}
	
	
}
