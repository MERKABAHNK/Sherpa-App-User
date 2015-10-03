package com.fr3estudio.sherpa.sherpav3p.utils;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fr3estudio.sherpa.sherpav3p.R;


public class OverlayActivity extends Activity implements OnClickListener {

	public static int EXIT = 0;
	public static int LOADING = 1;
	public static int MAKER_MOVE = 2;
	public static int CONFIRMATION = 3;
	public static int CANCEL_CONFIRMATION = 35;
	public static int SHOW_MENU = 4;
	public static int SHOW_MAIN_MENU = 5;

	int type_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overlay);
		

		
		
		Intent intent = this.getIntent();
		if (intent.getExtras() == null){
			return;
		}
		type_info = intent.getExtras().getInt("code");

		System.out.println("Creando intent " + type_info);
		if (type_info == LOADING) {
			RotateAnimation ra = new RotateAnimation(0.0f, 360.0f,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);

			ra.setInterpolator(new LinearInterpolator());
			ra.setRepeatCount(Animation.INFINITE);
			ra.setDuration(3000l);
			((ImageView) findViewById(R.id.as_loading_ring)).setAnimation(ra);
			((ImageView) findViewById(R.id.as_loading_ring)).startAnimation(ra);
			((ImageView) findViewById(R.id.as_loading_ring))
					.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.lb_searching))
					.setVisibility(View.VISIBLE);
			if (intent.getExtras().getString("text2show") != null){
				((TextView) findViewById(R.id.lb_searching)).setText(intent.getExtras().getString("text2show"));
			}
		}
		else if (type_info == MAKER_MOVE) {
			//System.out.println("marker move");
			((View) findViewById(R.id.an_move_mark_1))
					.setVisibility(View.VISIBLE);
			Handler h = new Handler();
			h.postDelayed(new Runnable() {

				@Override
				public void run() {
					paso1();
				}
			}, 1400);
		}
		
		else if (type_info == CONFIRMATION || type_info == CANCEL_CONFIRMATION) {
			((View) findViewById(R.id.confirm_dialog)).setVisibility(View.VISIBLE);
			Intent intent1 = this.getIntent();
			if (intent1.getExtras().containsKey("title")){
				((TextView) findViewById(R.id.title_confirmation)).setText(intent1.getExtras().getString("title"));
				((TextView) findViewById(R.id.subtitle_confirmation)).setText(intent1.getExtras().getString("subtitle"));
			}
		}else if (type_info == SHOW_MENU){
			((View) findViewById(R.id.menu_sherpa)).setVisibility(View.VISIBLE);
			((Button) findViewById(R.id.close_menu)).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					OverlayActivity.this.finish();
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);				}
			});
		}else if (type_info == SHOW_MAIN_MENU){
			((View) findViewById(R.id.menu_sherpa)).setVisibility(View.VISIBLE);
			((View) findViewById(R.id.profile_opt)).setEnabled(false);
			((View) findViewById(R.id.profile_opt)).setAlpha(0.3f);
			((View) findViewById(R.id.profile_opt)).setClickable(false);
			((Button) findViewById(R.id.close_menu)).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					OverlayActivity.this.finish();
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);				}
			});
		}
		else if (type_info == EXIT) {
			OverlayActivity.this.finish();
		}

	}

	public void showProfile(View v){
		Intent resultIntent = new Intent();
		resultIntent.putExtra("menu_option", "profile");
		setResult(CONSTANTS.MENU_OPTION, resultIntent);
		OverlayActivity.this.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
	}
	public void sendContactMail(View v){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{CONSTANTS.contact_mail});
		i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.st_contact_subj));
		i.putExtra(Intent.EXTRA_TEXT   , "");
		try {
		    startActivity(Intent.createChooser(i, getResources().getString(R.string.st_title_conctact)));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(OverlayActivity.this, getResources().getString(R.string.st_error_noMsgSvc), Toast.LENGTH_SHORT).show();
		}
		
		OverlayActivity.this.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
	}
	
	public void showAbout(View v){
		Intent resultIntent = new Intent();
		resultIntent.putExtra("menu_option", "about");
		setResult(CONSTANTS.MENU_OPTION, resultIntent);
		OverlayActivity.this.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	public void showTerms(View v){
		Intent resultIntent = new Intent();
		resultIntent.putExtra("menu_option", "terms");
		setResult(CONSTANTS.MENU_OPTION, resultIntent);
		OverlayActivity.this.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}

	
	public void paso1() {
		System.out.println("marker move paso1");
		((View) findViewById(R.id.an_move_mark_2)).setVisibility(View.VISIBLE);
		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				paso2();
			}
		}, 1000);
	}

	public void paso2() {
		System.out.println("marker move paso2");
		((View) findViewById(R.id.an_move_mark_3)).setVisibility(View.VISIBLE);
		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				paso3();
			}
		}, 1000);
	}

	public void paso3() {
		System.out.println("marker move paso3");
		((View) findViewById(R.id.an_move_mark_1)).setVisibility(View.GONE);
		((View) findViewById(R.id.an_move_mark_2)).setVisibility(View.GONE);
		((View) findViewById(R.id.an_move_mark_3)).setVisibility(View.GONE);
		((View) findViewById(R.id.an_move_mark_4)).setVisibility(View.VISIBLE);
		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				paso4();
			}
		}, 1000);

	}

	public void paso4() {
		System.out.println("marker move paso4");
		// AnimationSet as = new AnimationSet(false);
		TranslateAnimation ta = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f);

		ta.setInterpolator(new LinearInterpolator());
		ta.setRepeatCount(0);
		ta.setDuration(3000l);
		// TranslateAnimation ta1 = new TranslateAnimation(
		// Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
		// -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
		// Animation.RELATIVE_TO_SELF, 1.0f);

		// ta1.setInterpolator(new LinearInterpolator());
		// ta1.setRepeatCount(1);
		// ta1.setDuration(3000l);
		// ta1.setStartOffset(2800l);
		// as.addAnimation(ta);
		// as.addAnimation(ta1);

		((View) findViewById(R.id.an_move_mark_4)).setAnimation(ta);
		((View) findViewById(R.id.an_move_mark_4)).startAnimation(ta);
		ta.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				paso5();
			}
		});
	}

	public void paso5() {
		OverlayActivity.this.finish();
	}
	
	public void onAcceptButtonClicked(View v){
		Intent resultIntent = new Intent();
		resultIntent.putExtra("confirm", true);
		setResult(CONSTANTS.CONFIRMATION_OVARLAY, resultIntent);
		OverlayActivity.this.finish();
	}
	
	public void onCancelButtonClicked(View v){
		Intent resultIntent = new Intent();
		resultIntent.putExtra("confirm", false);
		setResult(CONSTANTS.CONFIRMATION_OVARLAY, resultIntent);
		OverlayActivity.this.finish();
	}
	
	
	//to override de back button 
	@Override
	public void onBackPressed(){
		if (type_info == CONFIRMATION) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra("confirm", false);
			setResult(CONSTANTS.CONFIRMATION_OVARLAY, resultIntent);
			OverlayActivity.this.finish();
		}else if (type_info == SHOW_MENU){
			OverlayActivity.this.finish();
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
		} else{
			//System.out.println("Back Pressed ... finishing ....");
			OverlayActivity.this.finish();
		}
	}
	

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		type_info = intent.getExtras().getInt("code");
		//System.out.println("Re-Creando intent " + type_info);
		if (type_info == EXIT) {
			OverlayActivity.this.finish();
		}
	}

	@Override
	public void onClick(View v) {
		
		System.out.println("clicked el layout arriba");
	}

}
