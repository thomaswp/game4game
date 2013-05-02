package edu.elon.honors.price.maker;

import java.util.HashMap;
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
import android.graphics.NinePatch;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import edu.elon.honors.price.data.tutorial.Tutorial;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorAction;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButtonAction;
import edu.elon.honors.price.data.tutorial.Tutorial.TutorialAction;
import edu.elon.honors.price.game.Debug;

public class TutorialUtils {
	private static Tutorial tutorial;
	private static LinkedList<EditorButton> highlighted =
			new LinkedList<Tutorial.EditorButton>();
	private static Pattern highlightPattern = Pattern.compile("<h>([^<]*)</h>");
	public final static int HIGHLIGHT_COLOR_1 = Color.parseColor("#ffaa00");
	public final static int HIGHLIGHT_COLOR_G1 = Color.parseColor("#dd8800");
	public final static int HIGHLIGHT_COLOR_2 = Color.parseColor("#ff0000");
	public final static int HIGHLIGHT_COLOR_G2 = Color.parseColor("#dd0000");
	public final static int HIGHLIGHT_CYCLE = 1500;	
	
	private static boolean dialogShowing;
	private static Runnable onHighlightChangedListener;

	private static HashMap<EditorButton, View> highlightableButtons = 
			new HashMap<EditorButton, View>();
	private static HashMap<View, Background> highlightedButtons = new HashMap<View, Background>();
	
	public static void setOnHighlightChangedListener(Runnable onHighlightChangedListener) {
		TutorialUtils.onHighlightChangedListener = onHighlightChangedListener;
	}
	
	public static void setTutorial(Tutorial tutorial, Context context) {
		TutorialUtils.tutorial = tutorial;
		highlighted.clear();
		highlightableButtons.clear();
		highlightedButtons.clear();
		
		onHighlightChangedListener = null;
		if (tutorial == null) return;
		fireCondition(context);
	}
	
	private static void onHighlightChanged(Context context) {
		for (View view : highlightedButtons.keySet()) {
			highlightedButtons.get(view).set(view);
		}
		highlightedButtons.clear();
		for (EditorButton button : highlightableButtons.keySet()) {
			if (highlighted.contains(button)) {
				View view = highlightableButtons.get(button);
				highlightView(view, context);
			}
		}
	}

	private static void highlightView(View view, Context context) {
		Background bg = new Background(view);
		highlightedButtons.put(view, bg);
		
		TransitionDrawable drawable;
		if (view instanceof Button) {
			int padding = Screen.dipToPx(2, context);
			
			GradientDrawable d1 = new GradientDrawable(Orientation.TOP_BOTTOM, 
					new int[] { HIGHLIGHT_COLOR_1, HIGHLIGHT_COLOR_G1 });
			d1.setCornerRadius(3f);

		    GradientDrawable d2 = new GradientDrawable(Orientation.TOP_BOTTOM, 
					new int[] { HIGHLIGHT_COLOR_2, HIGHLIGHT_COLOR_G2 });
			d2.setCornerRadius(3f);
			
			
			drawable = new TransitionDrawable(new Drawable[] { 
					new InsetDrawable(d1, padding),
					//d1,
					d2
			});
			view.setBackgroundDrawable(drawable);
			
		} else {
			Drawable d1 = new ColorDrawable(HIGHLIGHT_COLOR_1);
			Drawable d2 = new ColorDrawable(HIGHLIGHT_COLOR_2);
			drawable = new TransitionDrawable(new Drawable[] { d1, d2 });
			view.setBackgroundDrawable(drawable);
		}

		drawable.setCrossFadeEnabled(true);
		
		bg.pad(view);
		bg.setMinimumSize(view);
		animateBackground(drawable, true, view);
		
	}
	
	private static void animateBackground(final TransitionDrawable drawable,
			final boolean forward, final View view) {
		if (view.getBackground() != drawable) return;
		if (!highlightableButtons.containsValue(view)) return;
		Debug.write("%s: %s", drawable.toString(), "" + forward);
		if (forward) {
			drawable.startTransition(HIGHLIGHT_CYCLE / 2);
		} else {
			drawable.reverseTransition(HIGHLIGHT_CYCLE / 2);
		}
		view.postDelayed(new Runnable() {
			@Override
			public void run() {
				animateBackground(drawable, !forward, view);
			}
		}, HIGHLIGHT_CYCLE / 2);
		//view.refreshDrawableState();
	}
	
	public static void addHighlightable(View view, EditorButton button, Context context) {
		if (view == null) return;
		highlightableButtons.remove(button);		
		highlightableButtons.put(button, view);
		if (highlighted.contains(button)) {
			highlightView(view, context);
		}
	}
	
	private static class Background {
		public Drawable drawable;
		public int[] padding = new int[4];
		
		public Background(View view) {
			this.drawable = view.getBackground();
			padding[0] = view.getPaddingLeft();
			padding[1] = view.getPaddingTop();
			padding[2] = view.getPaddingRight();
			padding[3] = view.getPaddingBottom();
		}
		
		public void set(View view) {
			view.setBackgroundDrawable(drawable);
			pad(view);
		}
		
		public void pad(View view) {
			view.setPadding(padding[0], padding[1], padding[2], padding[3]);
		}
		
		public void setMinimumSize(View view) {
			if (drawable != null) {
				view.setMinimumWidth(drawable.getMinimumWidth());
				view.setMinimumHeight(drawable.getMinimumHeight());
			}
		}
	}

	public static void clearHighlightables() {
		for (View view : highlightedButtons.keySet()) {
			highlightedButtons.get(view).set(view);
		}
		highlightedButtons.clear();
		highlightableButtons.clear();
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
		Handler handler = new Handler(context.getMainLooper());
		if (onHighlightChangedListener != null) {
			handler.post(onHighlightChangedListener);
		}
		handler.post(new Runnable() {
			@Override
			public void run() {
				onHighlightChanged(context);
			}
		});
		
		if (action.hasDialog()) {
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
					
					LinearLayout view = null;
					if (action.dialogImageId > 0) {
						view = new LinearLayout(context);
						view.setGravity(Gravity.CENTER);
						
						ImageView imageView = new ImageView(context);
						Drawable drawable = context.getResources()
								.getDrawable(action.dialogImageId);
						imageView.setImageDrawable(drawable);
						imageView.setBackgroundResource(R.drawable.border_tutorial_image);
						imageView.setAdjustViewBounds(true);
						imageView.setMaxHeight(Screen.dipToPx(75, context));
						
						LinearLayout.LayoutParams lps = new LayoutParams(
								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						view.addView(imageView, lps);
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
	
	private static EditorButton queuedButton;
	
	public synchronized static void queueButton(View okButton) {
		if (highlightableButtons.values().contains(okButton)) {
			for (EditorButton button : highlightableButtons.keySet()) {
				if (highlightableButtons.get(button) == okButton) {
					queuedButton = button; 
					return;
				}
			}
		}
	}
	
	public synchronized static void fireActivityResult(Context context) {
		if (queuedButton != null) {
			fireCondition(queuedButton, context);
			queuedButton = null;
		}
	}
}
