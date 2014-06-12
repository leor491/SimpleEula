package com.simpledynamics.eulasample;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
//import android.util.Log;
import android.webkit.WebView;

public class SimpleEula {
	public static final String MIME_HTML = "text/html";
	public static final String MIME_PLAIN = "text/plain";
	private String mimeType;
	private String EULA_PREFIX = "eula_";
	private Activity context;
	private int appName;
	private int eula;
	private int accept;
	private int reject;
	private int icon;

	public SimpleEula(Activity context, int icon, int appName, int eula,
			int accept, int reject, String mimeType) {
		this.context = context;
		this.icon = icon;
		this.appName = appName;
		this.eula = eula;
		this.accept = accept;
		this.reject = reject;
		this.mimeType = mimeType;
	}

	private PackageInfo getPackageInfo() {
		PackageInfo pi = null;
		try {
			pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi;
	}

	public void show() {

		PackageInfo versionInfo = getPackageInfo();
		/*
		 * The eulaKey changes every time you increment the version number in
		 * the AndroidManifest.xml
		 */
		final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean hasBeenShown = prefs.getBoolean(eulaKey, false);

		if (hasBeenShown == false) {
			String title = context.getString(appName) + " v"
					+ versionInfo.versionName;
			WebView wv = new WebView(context);
			wv.loadData(getFileText().toString(), mimeType, null);
			wv.setScrollbarFadingEnabled(false);

			AlertDialog.Builder builder = new AlertDialog.Builder(context)
					.setTitle(title)
					.setView(wv)
					.setCancelable(false)
					.setIcon(icon)
					.setPositiveButton(context.getString(accept),
							new Dialog.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									SharedPreferences.Editor editor = prefs
											.edit();
									editor.putBoolean(eulaKey, true);
									editor.commit();
									dialogInterface.dismiss();
								}
							})
					.setNegativeButton(context.getString(reject),
							new Dialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									context.finish();
								}

							});
			Dialog dialog = builder.create();
			dialog.show();
		}
	}

	private StringBuilder getFileText() {
		StringBuilder sb = new StringBuilder();
		String line;
		InputStream in = context.getResources().openRawResource(eula);
		BufferedReader bf = null;

		try {
			bf = new BufferedReader(new InputStreamReader(in));
			while ((line = bf.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			close(bf);
			close(in);
/*			Log.wtf(SimpleEula.class.getCanonicalName(),
					"Please specify a EULA file in your resources.");*/
			context.finish();
		}

		return sb;
	}

	private void close(Closeable item) {
		if (item != null) {
			try {
				item.close();
			} catch (IOException e) {

			}
		}

	}

}
