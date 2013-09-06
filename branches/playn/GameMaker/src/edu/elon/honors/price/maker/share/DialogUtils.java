package edu.elon.honors.price.maker.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public class DialogUtils {

	private static final int DIALOG_ERROR = 0xFF;

	public static ProgressDialog createLoadingDialog(Context context) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Connecting");
		progressDialog.setMessage("Connecting to website... Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		return progressDialog;
	}
	
	public static AlertDialog createAlertDialog(Context context, 
			String title, String message) { 
		return new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(message)
		.setNeutralButton("Ok", null)
		.create();
	}
	
	public static Bundle createErrorDialogBundle(String title, String message) {
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("message", message);
		return bundle;
	}
	
	public static void showErrorDialog(Activity activity, Bundle bundle, String error) {
		bundle.putString("error", error);
		activity.showDialog(DIALOG_ERROR, bundle);
	}
	
	public static  void showErrorDialog(Activity activity, Bundle bundle) {
		showErrorDialog(activity, bundle, null);
	}
	
	public static  void showErrorDialog(Activity activity, String title, String message) {
		showErrorDialog(activity, title, message, null);
	}
	
	public static  void showErrorDialog(Activity activity, String title, String message, String error) {
		showErrorDialog(activity, createErrorDialogBundle(title, message), error);
	}

	public static Dialog handleDialog(Context context, int id, Bundle args) {
		if (id == DIALOG_ERROR) {
			return createAlertDialog(context, "", "");
		}
		return null;
	}

	public static void prepareDialog(int id, Dialog dialog, Bundle args) {
		if (id == DIALOG_ERROR) {
			String title = args.getString("title");
			String message = args.getString("message");
			String error = args.getString("error");

			AlertDialog errorDialog = (AlertDialog)dialog;
			errorDialog.setTitle(title);
			if (error != null) {
				errorDialog.setMessage(String.format("%s\n\n" +
						"Website Error: %s", message, error));
			} else {
				errorDialog.setMessage(message);
			}
		}
	}
	
}
