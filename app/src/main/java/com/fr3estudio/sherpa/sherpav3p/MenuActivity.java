package com.fr3estudio.sherpa.sherpav3p;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fr3estudio.sherpa.sherpav3p.utils.CONSTANTS;
import com.fr3estudio.sherpa.sherpav3p.utils.OverlayActivity;
import com.fr3estudio.sherpa.sherpav3p.utils.SherpaDialog;


public class MenuActivity extends AppCompatActivity {

	private Intent overlayIntent;
	private int is_overlay_shown = 0;
	private int id_cliente;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		ActionBar mActionBar = getSupportActionBar();
		//mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayHomeAsUpEnabled(true);

		//System.out.println(" al menos lo crea ...");
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustumView = mInflater.inflate(R.layout.simple_action_bar, null);
		mActionBar.setCustomView(mCustumView);
		mActionBar.setDisplayShowCustomEnabled(true);

		EditText passwd = (EditText) findViewById(R.id.info_passwd);
		passwd.setTypeface(Typeface.DEFAULT);
		EditText passwd_r = (EditText) findViewById(R.id.info_passwd_rep);
		passwd_r.setTypeface(Typeface.DEFAULT);

		Intent intent = this.getIntent();
		String type_info = intent.getExtras().getString("code");
		
		if (type_info.equals("contact")) {
			contact(null);
		} else if (type_info.equals("about")) {
			((View) findViewById(R.id.about_opt)).setVisibility(View.VISIBLE);
		} else if (type_info.equals("terms")) {
			((View) findViewById(R.id.terms_opt)).setVisibility(View.VISIBLE);
		} else if (type_info.equals("profile")) {
			((View) findViewById(R.id.info_opt)).setVisibility(View.VISIBLE);
			getInfo();
		}
	}

	public void getInfo() {

		
		System.out.println("y llega aqui");
		SharedPreferences settings = this.getSharedPreferences(
				"com.fr3estudio.sherpaV3P.UsersData", Context.MODE_PRIVATE);

		
		id_cliente = settings.getInt("id_cliente", 0);
		String nombre = settings.getString("nombre", "");
		String apellido = settings.getString("apellido", "");
		String email = settings.getString("email", "");
		String telefono = settings.getString("telefono", "");
		// try {
		// //id_sherpa = (int) ObjectSerializer.deserialize(data);
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// } catch (NullPointerException e) {
		// id_sherpa = 0;
		// }.

		((EditText) findViewById(R.id.info_name)).setText(nombre);
		((EditText) findViewById(R.id.info_last)).setText(apellido);
		((EditText) findViewById(R.id.info_mail)).setText(email);
		((EditText) findViewById(R.id.info_phone)).setText(telefono);

	}

	public void showDialog(String title, String msg, boolean cancel,
			boolean listen) {
		if (!listen) {
			// SherpaDialog.showDialog(title, msg, cancel, this);
		} else {
			/* SherpaDialog.showDialogRes(title, msg, cancel, this, this); */
		}
	}

	public void updateDataFields(View v) {
		System.out.println("update Click");
		String nombre = ((EditText) findViewById(R.id.info_name)).getText()
				.toString();
		String apellido = ((EditText) findViewById(R.id.info_last)).getText()
				.toString();
		String email = ((EditText) findViewById(R.id.info_mail)).getText()
				.toString();
		String telefono = ((EditText) findViewById(R.id.info_phone)).getText()
				.toString();

		String passwd = ((EditText) findViewById(R.id.info_passwd)).getText()
				.toString();
		String confirm = ((EditText) findViewById(R.id.info_passwd_rep))
				.getText().toString();
		String messageError = "";
		if (nombre.isEmpty()) {
			messageError += getResources().getString(R.string.fst_name) + ", ";
		}
		if (apellido.isEmpty()) {
			messageError += getResources().getString(R.string.lst_name) + ",";
		}
		if (email.isEmpty()) {
			messageError += getResources().getString(R.string.email) + ",";
		}

		if (telefono.isEmpty()) {
			messageError += getResources().getString(R.string.cellphone) + ",";
		}

		if (!messageError.isEmpty()) {
			messageError = getResources().getString(
					R.string.st_missingFileds_part1)
					+ " "
					+ messageError
					+ " "
					+ getResources().getString(R.string.st_missingFileds_part2);
			SherpaDialog.showDialog(
					getResources().getString(R.string.msg_title_register),
					messageError, false, this, getResources().getString(R.string.st_accept));
			return;
		}
		if (email.indexOf("@") == -1) {
			SherpaDialog.showDialog(
					getResources().getString(R.string.msg_title_register),
					getResources().getString(R.string.msg_detail_invalidEmail),
					false, this, getResources().getString(R.string.st_accept));
			((EditText) findViewById(R.id.info_mail)).requestFocus();
			return;
		}
		String updatePasswd = "";
		if (!passwd.isEmpty() && !passwd.equals(confirm)) {
			SherpaDialog
					.showDialog(
							getResources().getString(
									R.string.msg_title_register),
							getResources().getString(
									R.string.msg_detail_pswdMissmatch), false,
							this, getResources().getString(R.string.st_accept));
			((EditText) findViewById(R.id.info_passwd)).requestFocus();
			return;
		} else if (!passwd.isEmpty() && passwd.equals(confirm)) {
			updatePasswd = ", password_sherpa='" + passwd + "' ";
		}


		SharedPreferences settings = this
				.getSharedPreferences(
						"com.fr3estudio.sherpaV3P.UsersData",
						Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putInt("id_cliente", id_cliente);
		editor.putString("nombre", nombre);
		editor.putString("apellido", apellido);
		editor.putString("email", email);
		editor.putString("telefono", telefono);
		if (!passwd.isEmpty()){
			editor.putString("passwd", passwd);
		}
		System.out.println("sending update Data "+nombre);
		editor.commit();
		Intent resultIntent = new Intent();
		resultIntent.putExtra("update", true);
		setResult(CONSTANTS.MENU_OVARLAY, resultIntent);
		MenuActivity.this.finish();
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void closeSession(View v) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("close_session", true);
		setResult(CONSTANTS.CLOSE_SESSION, resultIntent);
		MenuActivity.this.finish();

	}

	public void contact(View v) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { CONSTANTS.contact_mail });
		i.putExtra(Intent.EXTRA_SUBJECT, "Contacto Sherpa");
		i.putExtra(Intent.EXTRA_TEXT, "");
		try {
			startActivity(Intent.createChooser(i,
					getResources().getString(R.string.st_title_conctact)));

		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(MenuActivity.this,
					getResources().getString(R.string.st_error_noMsgSvc),
					Toast.LENGTH_SHORT).show();
		}
		MenuActivity.this.finish();
	}

	public void showLoadingOverlay(String text) {
		is_overlay_shown++;
		overlayIntent = new Intent(this, OverlayActivity.class);
		overlayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		overlayIntent.putExtra("code", OverlayActivity.LOADING);
		overlayIntent.putExtra("text2show", text);
		startActivity(overlayIntent);
	}

	public void hideOverlay() {

		overlayIntent = new Intent(this, OverlayActivity.class);
		overlayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		overlayIntent.putExtra("code", OverlayActivity.EXIT);
		startActivity(overlayIntent);
		is_overlay_shown--;
	}

	
	@Override
	public void onBackPressed() {
		Intent resultIntent = new Intent();
		setResult(CONSTANTS.MENU_OVARLAY, resultIntent);
		MenuActivity.this.finish();
	}
}
