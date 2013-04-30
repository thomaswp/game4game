package edu.elon.honors.price.maker;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;

import edu.elon.honors.price.data.tutorial.Tutorial;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorAction;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButtonAction;
import edu.elon.honors.price.data.tutorial.Tutorial.TutorialAction;

public class TutorialUtils {
	private static Tutorial tutorial;
	private static LinkedList<EditorButton> highlighted =
			new LinkedList<Tutorial.EditorButton>();
	private static Pattern highlightPattern = Pattern.compile("<h>([^<]*)</h>");
	public final static int HIGHLIGHT_COLOR_1 = Color.parseColor("#ffaa00");
	public final static int HIGHLIGHT_COLOR_2 = Color.parseColor("#ff0000");
	public final static int HIGHLIGHT_CYCLE = 1500;	
	
	private static boolean dialogShowing;
	private static Runnable onHighlightChangedListener;
	
	public static void setOnHighlightChangedListener(Runnable onHighlightChangedListener) {
		TutorialUtils.onHighlightChangedListener = onHighlightChangedListener;
	}
	
	public static void setTutorial(Tutorial tutorial, Context context) {
		TutorialUtils.tutorial = tutorial;
		highlighted.clear();
		onHighlightChangedListener = null;
		if (tutorial == null) return;
		fireCondition(context);
	}
	
	public static void backOneMessage(Context context) {
		if (!hasPreviousMessage()) return;
		do {
			tutorial.previous();
		} while (!tutorial.peek().hasDialog() && tutorial.hasPrevious());
		doAction(context);
	}
	
	public static void backTwoMessages(Context context) {
		for (int i = 0; i < 2; i++) {
			if (!hasPreviousMessage()) return;
			do {
				tutorial.previous();
			} while (!tutorial.peek().hasDialog() && tutorial.hasPrevious());
		}
		
		doAction(context);
	}
	
	public static void skipOneMessage(Context context) {
		while (tutorial.hasNext() && !tutorial.peek().hasDialog()) {
			tutorial.next();
		}
		doAction(context);
	}
	
	public static boolean hasPreviousMessage() {
		return tutorial != null && tutorial.hasPrevious();
	}
	
	public static boolean isHighlighted(EditorButton button) {
		return highlighted.contains(button);
	}
	
	public static int getHightlightColor() {
		double perc = (double)(System.currentTimeMillis() % HIGHLIGHT_CYCLE) / 
				HIGHLIGHT_CYCLE;
		//perc = Math.sin(perc * Math.PI);
		perc = Math.abs(perc - 0.5);
		int c1 = HIGHLIGHT_COLOR_1, c2 = HIGHLIGHT_COLOR_2;
		return Color.argb(255, 
				splice(Color.red(c1), Color.red(c2), perc),
				splice(Color.green(c1), Color.green(c2), perc),
				splice(Color.blue(c1), Color.blue(c2), perc));
	}
	
	private static int splice(int c1, int c2, double perc) {
		return (int)(c2 * perc + c1 * (1 - perc));
	}
	
	public synchronized static void fireCondition(EditorButton button, final Context context) {
		fireCondition(button, null, context);
	}
	
	private static void doAction(final Context context) {
		final TutorialAction action = tutorial.next();
		
		highlighted.clear();
		highlighted.addAll(action.highlights);
		if (onHighlightChangedListener != null) {
			Handler handler = new Handler(context.getMainLooper());
			handler.post(onHighlightChangedListener);
		}
		
		if (action.hasDialog()) {
			Handler handler = new Handler(context.getMainLooper());
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					String message = action.dialogMessage;
					Matcher m = highlightPattern.matcher(message);
					StringBuffer sb = new StringBuffer();
					while (m.find()) {
						String replace = m.group(1);
						replace = TextUtils.getColoredText(replace, TextUtils.COLOR_VALUE);
						replace = "<b>" + replace + "</b>";
						m.appendReplacement(sb, replace);
					}
					m.appendTail(sb);
					message = sb.toString();
					
					ImageView view = null;
					if (action.dialogImageId > 0) {
						view = new ImageView(context);
						Drawable drawable = context.getResources()
								.getDrawable(action.dialogImageId);
						view.setImageDrawable(drawable);
					}
					
					AlertDialog dialog = new AlertDialog.Builder(context)
					.setTitle(action.dialogTitle)
					.setMessage(Html.fromHtml(message))
					.setNeutralButton("Ok", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialogShowing = false;
							fireCondition(context);
						}
					})
					.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							dialogShowing = false;
							fireCondition(context);
						}
					})
					.setNegativeButton(">", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							skipOneMessage(context);
						}
					})
					.setPositiveButton("<", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							tutorial.previous();
							backOneMessage(context);
						}
					})
					.setView(view)
					.create();
					
					dialog.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							dialogShowing = false;
						}
					});
					dialogShowing = true;
					dialog.show();
					
					Button backButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
					backButton.setEnabled(tutorial.getActionIndex() > 1);
					backButton.setWidth(Screen.dipToPx(50, context));
					
					Button nextButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
					nextButton.setEnabled(tutorial.hasNext());
					backButton.setWidth(Screen.dipToPx(50, context));
				}
			}, action.dialogDelay);
		}
	}
	
	private static boolean checkCondition(Context context) {
		if (dialogShowing) return false;
		if (tutorial != null && tutorial.hasNext()) {
			TutorialAction action = tutorial.peek();
			if (action.condition == null) {
				doAction(context);
			} else {
				return true;
			}
		} else {
			highlighted.clear();
		}
		return false;
	}
	
	private static void fireCondition(Context context) {
		checkCondition(context);
	}
	
	public synchronized static void fireCondition(EditorButton button, EditorButtonAction editorAction, 
			final Context context) {
		if (checkCondition(context)) {
			if (tutorial.peek().condition.isTriggered(button, editorAction)) {
				doAction(context);
			}
		}
		
	}

	public synchronized static void fireCondition(EditorAction action,
			Context context) {
		if (checkCondition(context)) {
			if (tutorial.peek().condition.isTriggered(action)) {
				doAction(context);
			}
		}
	}
}
