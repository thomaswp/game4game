package edu.elon.honors.price.maker.share;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

public class DialogUtils {

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
	
}
