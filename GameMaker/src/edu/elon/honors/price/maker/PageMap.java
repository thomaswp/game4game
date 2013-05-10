package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PageMap extends PageList<Map> {

	private Button buttonSelectMap;
	
	public PageMap(Database parent) {
		super(parent);
	}
	
	@Override
	public void onCreate(ViewGroup parentView) {
		super.onCreate(parentView);
		
		buttonSelectMap = new Button(parent);
		buttonSelectMap.setText("Select Map");
		buttonSelectMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int selected = getSelectedIndex();
				if (getGame().selectedMapId != selected) {
					getGame().selectedMapId = selected;
					listView.invalidateViews();
					TutorialUtils.fireCondition(
							EditorButton.DatabaseMapsSelectMap, parent);
				}
			}
		});
		addPanelView(buttonSelectMap);
		
		Button buttonUI = new Button(parent);
		buttonUI.setText("Edit UI");
		buttonUI.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DatabaseEditUI.startForResult(parent, 
						DatabaseActivity.REQUEST_RETURN_GAME);
			}
		});
		addPanelView(buttonUI);
	}
	

	@Override
	protected void editItem(int index) {
		TutorialUtils.queueButton(EditorButton.DatabaseMapsEdit);
		DatabaseEditMap.startForResult(parent, index, REQUEST_EDIT_ITEM);
	}

	@Override
	protected void resetItem(int index) {
		getGame().maps.set(index, new Map(getGame()));
	}

	@Override
	protected void addItem() {
		getGame().maps.add(new Map(getGame()));
	}

	@Override
	protected CheckableArrayAdapter<Map> getAdapter() {
		return new CheckableArrayAdapter<Map>(parent, 
				R.layout.array_adapter_row_string, getGame().maps) {

			@Override
			protected void setRow(int position, Map item, View view) {
				TextView tv = (TextView)view.findViewById(R.id.textViewTitle);
				String name = item.name;
				if (getGame().selectedMapId == position) {
					name = TextUtils.getColoredText(name, TextUtils.COLOR_VALUE);
					name = String.format("<b>%s</b>", name);
					tv.setText(Html.fromHtml(name));
				} else {
					tv.setText(name);
				}
			}
		};
	}

	@Override
	protected Map getItem(int index) {
		return getGame().maps.get(index);
	}

	@Override
	protected String getItemCategory() {
		return "Map";
	}

	@Override
	public String getName() {
		return "Maps";
	}

	@Override
	public void addEditorButtons() {
		TutorialUtils.addHighlightable(buttonEdit, 
				EditorButton.DatabaseMapsEdit, parent);
		TutorialUtils.addHighlightable(buttonSelectMap, 
				EditorButton.DatabaseMapsSelectMap, parent);
	}
}
