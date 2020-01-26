package com.iglyphic.tictactoe;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;

public class PlayActivity extends Activity{
	
	Button btnAndroid,btnBluetooth;
	ImageButton imgBtnScore, imgBtnSound, imgBtnFacebook, imgBtnRating;
	AdView mAdView;
	InterstitialAd interstitial;
	AdRequest adRequest;
	boolean isActivityDestroyed;
//	int owner=1,decision=1;
//	boolean stopStatus;
	
	public final int REQUEST_ENABLE_BT=1;
	boolean requested, soundStatus;
	SharedPrefUtil sharedPrefUtil;

	private SoundPool spool;
	private int soundID;
	float volume;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_layout);
		btnAndroid=(Button)findViewById(R.id.btnPlayAndroid);
		btnBluetooth=(Button)findViewById(R.id.btnBluetoothBattle);
		imgBtnScore=(ImageButton)findViewById(R.id.imgBtnScore);
		imgBtnSound = (ImageButton)findViewById(R.id.imgBtnSound);
		imgBtnFacebook = (ImageButton)findViewById(R.id.imgBtnFacebook);
		imgBtnRating = (ImageButton)findViewById(R.id.imgBtnRating);
		
	 //   stopStatus=false;
        sharedPrefUtil = new SharedPrefUtil(this);
		soundStatus = sharedPrefUtil.getSoundStatus();
        requested=false;

		isActivityDestroyed = false;
		adRequest = new AdRequest.Builder()
				.build();

		mAdView = (AdView) findViewById(R.id.adView);
		mAdView.loadAd(adRequest);

		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(getResources().getString(R.string.tic_tac_toe_interstitial));
		interstitial.loadAd(adRequest);

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundID = spool.load(this, R.raw.move, 1);

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);


		if(soundStatus) {
			imgBtnSound.setBackgroundResource(R.drawable.sound_on);
		} else {
			imgBtnSound.setBackgroundResource(R.drawable.sound_off);
		}


		interstitial.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				super.onAdClosed();
			}

			@Override
			public void onAdLoaded() {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (!isActivityDestroyed) {
							displayInterstitial();
						}
					}
				}, 1000);
			}

			@Override
			public void onAdOpened() {
				super.onAdOpened();
			}
		});


		btnAndroid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(soundStatus) {
				//	player = MediaPlayer.create(PlayActivity.this, R.raw.move);
				//	player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				Intent in = new Intent(PlayActivity.this, TossActivity.class);
				//	stopStatus=true;
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
				finish();
			}
		});
		
		
		
		btnBluetooth.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(soundStatus) {
				//	player = MediaPlayer.create(PlayActivity.this, R.raw.move);
				//	player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if(mBluetoothAdapter==null) {
					showBluetoothDialog();
				}
				else
				{
					boolean enabled=false;
					try
					{
						if (!mBluetoothAdapter.isEnabled()) 
						{
							requested=true;
						    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
						}
						else
						{
							Intent in = new Intent(PlayActivity.this,BluetoothConnectActivity.class);
							startActivity(in);
							overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
							finish();
						}
					}
					catch(Exception e)
					{
						Toast.makeText(getApplicationContext(),"Bluetooth not supported",Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		
		
		
		
		
		imgBtnScore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(soundStatus) {
				//	player = MediaPlayer.create(PlayActivity.this, R.raw.move);
				//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}

				Intent intent = new Intent(PlayActivity.this, ScoreActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
				finish();
				
			/*	LayoutInflater li = LayoutInflater.from(PlayActivity.this);
				View promptsView = li.inflate(R.layout.game_type_dialog, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
				builder.setView(promptsView);
				builder.setCancelable(true);
				final AlertDialog alertDialog = builder.create();
				alertDialog.show();
				
				LinearLayout linearAndroidBattle=(LinearLayout)promptsView.findViewById(R.id.linearAndroidBattle);
				LinearLayout linearBluetoothBattle=(LinearLayout)promptsView.findViewById(R.id.linearBluetoothBattle);
				
				
				linearAndroidBattle.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub	
						player=MediaPlayer.create(PlayActivity.this, R.raw.move);
						player.start();
						alertDialog.cancel();
						showScore("android");
					}
				});	
				
				linearBluetoothBattle.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub	
						player=MediaPlayer.create(PlayActivity.this, R.raw.move);
						player.start();
						alertDialog.cancel();
						showScore("bluetooth");
					}
				});   */
				
			}
		});
		
		
		imgBtnRating.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(soundStatus) {
					//player = MediaPlayer.create(PlayActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				Uri uri = Uri.parse("market://details?id=" + getPackageName());
				Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					startActivity(myAppLinkToMarket);
				} catch (ActivityNotFoundException e) {
					//Toast.makeText(getApplicationContext(), "Sorry. Rating of this application is not possible", Toast.LENGTH_LONG).show();
				}
			}
		});


		imgBtnFacebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(soundStatus) {
					//player = MediaPlayer.create(PlayActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/appslab.mobile/?ref=hl")));
			}
		});

		imgBtnSound.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(soundStatus) {
					soundStatus = false;
					sharedPrefUtil.setSoundStatus(soundStatus);
					imgBtnSound.setBackgroundResource(R.drawable.sound_off);
				}
				else {
					//player=MediaPlayer.create(PlayActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
					soundStatus = true;
					sharedPrefUtil.setSoundStatus(soundStatus);
					imgBtnSound.setBackgroundResource(R.drawable.sound_on);
				}
			}
		});
		
		
	}


	public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_ENABLE_BT&&resultCode==RESULT_OK)
		{
			if(requested)
			{
				Intent in = new Intent(PlayActivity.this,BluetoothConnectActivity.class);
			//	stopStatus=true;
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
				finish();
			}
		}
	}
	
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	/*	if(stopStatus==false)
		{
			stopStatus=true;
			Intent service=new Intent(PlayActivity.this,BackGroundMusic.class);
			stopService(service);
		}  */
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	/*	if(stopStatus)
		{
			stopStatus=false;
			Intent service=new Intent(PlayActivity.this,BackGroundMusic.class);
			startService(service);
		}   */
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isActivityDestroyed = true;
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		LayoutInflater li = LayoutInflater.from(PlayActivity.this);
		View promptsView = li.inflate(R.layout.exit, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
		builder.setView(promptsView);
		builder.setCancelable(true);
		final AlertDialog alertDialog = builder.create();
		alertDialog.show();
		
		LinearLayout linearDialogYes=(LinearLayout)promptsView.findViewById(R.id.linearDialogYes);
		LinearLayout linearDialogNo=(LinearLayout)promptsView.findViewById(R.id.linearDialogNo);
		
		linearDialogNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	  
				if(soundStatus) {
					//player = MediaPlayer.create(PlayActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				alertDialog.cancel();
			}
		});	
		
		linearDialogYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				if (soundStatus) {
					//player = MediaPlayer.create(PlayActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				alertDialog.cancel();
				System.exit(1);
			}
		});
	}




	private void showBluetoothDialog() {
		LayoutInflater li = LayoutInflater.from(PlayActivity.this);
		View promptsView = li.inflate(R.layout.toss_dialog1, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
		builder.setView(promptsView);
		builder.setCancelable(false);
		final AlertDialog alertDialog = builder.create();
		alertDialog.show();
		TextView txtAlert=(TextView)promptsView.findViewById(R.id.txtAlert);
		TextView txtDialogOk=(TextView)promptsView.findViewById(R.id.txtPlay);
		TextView txtDialogBody=(TextView)promptsView.findViewById(R.id.txtDialogBody);
		//Button btnOk=(Button)promptsView.findViewById(R.id.btnOk);
		LinearLayout linearDialogPlay=(LinearLayout)promptsView.findViewById(R.id.linearDialogPlay);
		txtAlert.setText("Alert!");
		txtDialogOk.setText("Ok");
		String text="Sorry! Bluetooth is not supported in your device.";
		txtDialogBody.setText(text);

		linearDialogPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(soundStatus) {
					//player=MediaPlayer.create(PlayActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				alertDialog.cancel();

			}
		});
	}
	
	

}
