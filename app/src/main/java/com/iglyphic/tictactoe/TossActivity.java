package com.iglyphic.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class TossActivity extends Activity{
	
	Button btnToss;
	ImageView imgToss;
	//AnimationDrawable animDrawable;
	
	boolean headStatus=false, soundStatus;
	//boolean stopStatus;
	//int head=0,tail=0,coin;
	int owner=-1;
	int decision=-1;
	int symbol=-1;
	//MediaPlayer player;
	private SoundPool spool;
	private int soundID;
	float volume;

	Animation rotateAnim;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toss_layout);
		btnToss=(Button)findViewById(R.id.btnToss);
		imgToss=(ImageView)findViewById(R.id.imgToss);
		//imgToss.setImageResource(R.drawable.frame);
		
		//head=R.drawable.coin_4;
		//tail=R.drawable.coin_8;

		SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(this);
		soundStatus = sharedPrefUtil.getSoundStatus();
		
	//	stopStatus=false;
		
	/*	animDrawable=(AnimationDrawable)imgToss.getDrawable();
		animDrawable.stop();  */
		//animDrawable.start();

		//rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);

	/*	TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String uid = tManager.getDeviceId();    */

		try {
			AdRequest adRequest = new AdRequest.Builder()
					.build();

			AdView mAdView = (AdView) findViewById(R.id.adView);
			mAdView.loadAd(adRequest);

		} catch (Exception e) {

		}

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundID = spool.load(this, R.raw.move, 1);

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		
		btnToss.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(soundStatus) {
					//player = MediaPlayer.create(TossActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				caller();
				
			}
		});
		
		
	}
	
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	/*	if(stopStatus==false)
		{
			stopStatus=true;
			Intent service=new Intent(TossActivity.this,BackGroundMusic.class);
			stopService(service);
		}   */
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	/*	if(stopStatus)
		{
			stopStatus=false;
			Intent service=new Intent(TossActivity.this,BackGroundMusic.class);
			startService(service);
		}  */
	}

	

	public void caller()
	{
		try
		{
			Thread.sleep(200);
		}
		catch(Exception e)
		{
		}
		rotateAnim = AnimationUtils.loadAnimation(TossActivity.this, R.anim.rotate);
		imgToss.startAnimation(rotateAnim);
		TossTask1 task1=new TossTask1();
		task1.execute();
	}
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent in=new Intent(TossActivity.this,PlayActivity.class);
		//stopStatus=true;
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
		finish();
	}
	
	
	
	class TossTask1 extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try
			{
				Thread.sleep(2000);
			}
			catch(Exception e)
			{
			}
			while(true)
			{
				double a=Math.random();
				a=a*1000;
				int b=(int)a;
				if(b==1||b==2)
				{
					owner=b;
					break;
				}
			}
			
			if(owner==1)
			{
			//	coin=tail;
				
				while(true)
				{
					double a=Math.random();
					a=a*1000;
					int b=(int)a;
					if(b==1||b==2)
					{
						decision=b;
						break;
					}
				}
				
				while(true)
				{
					double a=Math.random();
					a=a*1000;
					int b=(int)a;
					if(b==1||b==2)
					{
						symbol=b;
						break;
					}
				}
			}
		/*	else
			{
				coin=head;
			}  */
			
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			
			//animDrawable.stop();
			//imgToss.setImageResource(coin);
			//imgToss.setImageResource(R.drawable.coin_4);
			try{
				Thread.sleep(500);
			}
			catch(Exception e) {}
			
			if(owner==1)
			{
				LayoutInflater li = LayoutInflater.from(TossActivity.this);
				View promptsView = li.inflate(R.layout.toss_dialog1, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(TossActivity.this);
				builder.setView(promptsView);
				builder.setCancelable(true);
				final AlertDialog alertDialog = builder.create();
				alertDialog.show();
				TextView txtDialogBody=(TextView)promptsView.findViewById(R.id.txtDialogBody);
				//Button btnOk=(Button)promptsView.findViewById(R.id.btnOk);
				LinearLayout linearDialogPlay=(LinearLayout)promptsView.findViewById(R.id.linearDialogPlay);
				String text="Android has won the toss";	
				if(decision==1) text+=" and elected to play first";
				else text+=" and elected to play last";
				if(symbol==1) text+=" with the symbol Cross.";
				else text+=" with the symbol Circle";
				txtDialogBody.setText(text);
				
				linearDialogPlay.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						alertDialog.cancel();
						if(soundStatus) {
							//player = MediaPlayer.create(TossActivity.this, R.raw.move);
							//player.start();
							spool.play(soundID, volume, volume, 1, 0, 1f);
						}
						Intent in=new Intent(TossActivity.this,GameActivity.class);
						in.putExtra("symbol",((symbol*2)%3));
						in.putExtra("decision",((decision*2)%3));
						startActivity(in);
						overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
						finish();
					}
				});
				
			}
			else
			{
				
				LayoutInflater li = LayoutInflater.from(TossActivity.this);
				View promptsView = li.inflate(R.layout.exit, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(TossActivity.this);
				builder.setView(promptsView);
				builder.setCancelable(true);
				final AlertDialog alertDialog = builder.create();
				alertDialog.show();
				LinearLayout linearYes=(LinearLayout)promptsView.findViewById(R.id.linearDialogYes);
				LinearLayout linearNo=(LinearLayout)promptsView.findViewById(R.id.linearDialogNo);
				TextView txtAlert=(TextView)promptsView.findViewById(R.id.txtAlert);
				TextView txtBody=(TextView)promptsView.findViewById(R.id.txtDialogBody);
				TextView txtYes=(TextView)promptsView.findViewById(R.id.txtYes);
				TextView txtNo=(TextView)promptsView.findViewById(R.id.txtNo);

				txtAlert.setText("TOSS");
				txtBody.setText("You have won the toss. Choose whether you will play first or play last");
				txtYes.setText("Play First");
				txtNo.setText("Play Last");
				
				linearYes.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						decision=1;
						if(soundStatus) {
							//player = MediaPlayer.create(TossActivity.this, R.raw.move);
							//player.start();
							spool.play(soundID, volume, volume, 1, 0, 1f);
						}
						alertDialog.cancel();
						try
						{
							Thread.sleep(100);
						}
						catch(Exception e) {}
						showSymbolDialog();
					}
				});
				
				linearNo.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						decision=2;
						if(soundStatus) {
							//player = MediaPlayer.create(TossActivity.this, R.raw.move);
							//player.start();
							spool.play(soundID, volume, volume, 1, 0, 1f);
						}
						alertDialog.cancel();
						try
						{
							Thread.sleep(100);
						}
						catch(Exception e) {}
						showSymbolDialog();
					}
				});
				
			}
			
		}
		
	}
	
	
	
	public void showSymbolDialog()
	{
		
		LayoutInflater li = LayoutInflater.from(TossActivity.this);
		View promptsView = li.inflate(R.layout.toss_dialog2, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(TossActivity.this);
		builder.setView(promptsView);
		builder.setCancelable(true);
		final AlertDialog alertDialog = builder.create();
		alertDialog.show();
		LinearLayout linearDialogCross=(LinearLayout)promptsView.findViewById(R.id.linearDialogCross);
		LinearLayout linearDialogCircle=(LinearLayout)promptsView.findViewById(R.id.linearDialogCircle);
		linearDialogCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				symbol=1;
				if(soundStatus) {
					//player = MediaPlayer.create(TossActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				alertDialog.cancel();
				Intent in=new Intent(TossActivity.this,GameActivity.class);
				in.putExtra("symbol",symbol);
				in.putExtra("decision",decision);
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
				finish();
			}
		});
		
		linearDialogCircle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				symbol=2;
				if(soundStatus) {
					//player = MediaPlayer.create(TossActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				alertDialog.cancel();
				Intent in=new Intent(TossActivity.this,GameActivity.class);
				in.putExtra("symbol",symbol);
				in.putExtra("decision",decision);
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
				finish();
			}
		});
		
	}
	
	
	

}
