package com.fr3estudio.sherpa.sherpav3p.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class SherpaDialog {

	public static void showDialog(String title, String message, boolean isCancelable, Context context, String accept) {
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);

		dlgAlert.setMessage(message);
		dlgAlert.setTitle(title);
		dlgAlert.setPositiveButton(accept,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
		dlgAlert.setCancelable(isCancelable);
		dlgAlert.create().show();
	}

	
	public static void showDialogRes(String title, String message, boolean isCancelable, Context context, OnClickListener listener, String accept) {
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);

		dlgAlert.setMessage(message);
		dlgAlert.setTitle(title);
		dlgAlert.setPositiveButton(accept, listener);
		dlgAlert.setCancelable(isCancelable);
		dlgAlert.create().show();
	}
}
