package com.iglyphic.tictactoe;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class TossBluetoothActivity extends Activity{
	
	ImageView imgToss;
	//AnimationDrawable animDrawable;
	Animation rotateAnim;
	
	boolean headStatus=false,normalExit, soundStatus;
	//boolean stopStatus;
	//int head=0,tail=0,coin;
	//MediaPlayer player;
	
	int owner,decision,symbol;
	
	MyBluetoothService myService;
	
	AlertDialog waitingDialog;

	private SoundPool spool;
	private int soundID;
	float volume;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toss_bluetooth);
		imgToss=(ImageView)findViewById(R.id.imgToss);
		//imgToss.setImageResource(R.drawable.frame);
		
		//head=R.drawable.coin_4;
		//tail=R.drawable.coin_8;
		
		//stopStatus=false;
		normalExit=false;
		
		owner=-1;
		decision=-1;

		SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(this);
		soundStatus = sharedPrefUtil.getSoundStatus();


	/*	TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String uid = tManager.getDeviceId();   */

		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.build();
		mAdView.loadAd(adRequest);


		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundID = spool.load(this, R.raw.move, 1);

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		
	//	animDrawable=(AnimationDrawable)imgToss.getDrawable();
		rotateAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_bluetooth);
		
		myService=new MyBluetoothService();
		myService.setFHandler(mHandler);
		
		//animDrawable.start();
		
		caller(); 
		
		
	}
	
	
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	/*	if(stopStatus==false)
		{
			stopStatus=true;
			Intent service=new Intent(TossBluetoothActivity.this,BackGroundMusic.class);
			stopService(service);
		} */
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	/*	if(stopStatus)
		{
			stopStatus=false;
			Intent service=new Intent(TossBluetoothActivity.this,BackGroundMusic.class);
			startService(service);
		}  */
	}
	
	
	
	
	public void caller()
	{
		//animDrawable.start();
		imgToss.startAnimation(rotateAnim);
		TossTask1 task1=new TossTask1();
		task1.execute();
	}
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(MyBluetoothService.connectedStatus) myService.stopConnectedThread();
		if(MyBluetoothService.connectStatus) myService.stopConnectThread();
		if(MyBluetoothService.acceptStatus) myService.stopAcceptThread();
		
		Intent in=new Intent(TossBluetoothActivity.this,BluetoothConnectActivity.class);
		//stopStatus=true;
		normalExit=true;
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
		finish();
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(!normalExit)
		{
			if(MyBluetoothService.connectedStatus) myService.stopConnectedThread();
			if(MyBluetoothService.connectStatus) myService.stopConnectThread();
			if(MyBluetoothService.acceptStatus) myService.stopAcceptThread();
		}
	}
	
	
	
	// The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MyBluetoothService.MESSAGE_STATE_CHANGE:
                break;
            case MyBluetoothService.MESSAGE_WRITE:
              //  byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
              //  String writeMessage = new String(writeBuf);
              //  mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MyBluetoothService.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
             //   construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                splitMessage(readMessage);
                break;
            case MyBluetoothService.MESSAGE_DEVICE_NAME:
                break;
            case MyBluetoothService.MESSAGE_TOAST:
               // Toast.makeText(getApplicationContext(), msg.getData().getString(MyBluetoothService.TOAST),Toast.LENGTH_SHORT).show();
                break;
            case MyBluetoothService.MESSAGE_CONNECTED:
            	//Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                break;
            case MyBluetoothService.MESSAGE_CONNECTION_FAILED:
            	//Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_LONG).show();
                break;
			case MyBluetoothService.MESSAGE_DISCONNECTED:
				showDisconnectDialog();
				break;

            }
        }
    };
	
    
    
    public void splitMessage(String msg)
    {
    	if(msg.startsWith("toss:"))
    	{
    		int num=0,state=0;
    		for(int i=0;i<msg.length();i++)
    		{
    			if(msg.charAt(i)==':') state++;
    			else
    			{
    				if(state==1) num=num*10+(msg.charAt(i)-48);
    			}
    		}
    		if(num==1) owner=2;
    		else owner=1;
    	   // owner=num;
    	    
    	/*    if(owner==2) coin=head;
			else coin=tail;  */
			//animDrawable.stop();
			//imgToss.setImageResource(coin);
			
    	    if(owner==2) showWinDialog();
			else showWaitingDialog("Your opponent has won the toss.\nWaiting for the decision...");
    	}
    	if(msg.startsWith("decision:"))
    	{
    		int num1=0,num2=0,state=0;
    		for(int i=0;i<msg.length();i++)
    		{
    			if(msg.charAt(i)==':') state++;
    			else
    			{
    				if(state==1) num1=num1*10+(msg.charAt(i)-48);
    				if(state==2) num2=num2*10+(msg.charAt(i)-48);
    			}
    		}
    		if(num1==1) decision=2;
    		else decision=1;
    		if(num2==1) symbol=2;
    		else symbol=1;
    		waitingDialog.cancel();
    		showReadyDialog();
    	}
    	if(msg.equals("ready"))
    	{
    		waitingDialog.cancel();
			Intent in=new Intent(TossBluetoothActivity.this,GameBluetoothActivity.class);
			in.putExtra("symbol",symbol);
			in.putExtra("decision",decision);
			normalExit=true;
			startActivity(in);
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
			finish();
    	}
    }



	private void showDisconnectDialog() {
		LayoutInflater li = LayoutInflater.from(TossBluetoothActivity.this);
		View promptsView = li.inflate(R.layout.toss_dialog1, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(TossBluetoothActivity.this);
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
		txtDialogOk.setText("Quit Now");
		String text="Your opponent device is disconnected now. Please quit the game and reconnect the device.";
		txtDialogBody.setText(text);

		linearDialogPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(soundStatus) {
					//player=MediaPlayer.create(TossBluetoothActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}

				if(MyBluetoothService.connectedStatus) myService.stopConnectedThread();
				if(MyBluetoothService.connectStatus) myService.stopConnectThread();
				if(MyBluetoothService.acceptStatus) myService.stopAcceptThread();

				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

				alertDialog.cancel();

				if(mBluetoothAdapter.isEnabled()) {
					Intent in=new Intent(TossBluetoothActivity.this,BluetoothConnectActivity.class);
					normalExit=true;
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
					finish();
				} else {
					Intent in=new Intent(TossBluetoothActivity.this,PlayActivity.class);
					normalExit=true;
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
					finish();
				}
			}
		});
	}

	
	
	class TossTask1 extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try
			{
				Thread.sleep(2700);
			}
			catch(Exception e)
			{
			}
			if(MyBluetoothService.server)
			{
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
			}
			
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			
			if(MyBluetoothService.server)
			{
				/*if(owner==2) coin=head;
				else coin=tail;  */
				//animDrawable.stop();
				//imgToss.setImageResource(coin);
				
				myService.sendMessage("toss:"+owner);
				
				try{
					Thread.sleep(500);
				}
				catch(Exception e) {}
				
				
				if(owner==2) showWinDialog();
				else showWaitingDialog("Your opponent has won the toss.\nWaiting for opponent's decision...");			
				
			}
			
			
		}
		
	}
	
	
	
	
	
	public void showWinDialog()
	{
		
		LayoutInflater li = LayoutInflater.from(TossBluetoothActivity.this);
		View promptsView = li.inflate(R.layout.exit, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(TossBluetoothActivity.this);
		builder.setView(promptsView);
		builder.setCancelable(false);
		final AlertDialog alertDialog = builder.create();
		alertDialog.show();
	//	ImageView imgDialog1=(ImageView)promptsView.findViewById(R.id.imgDialog1);
	//	ImageView imgDialog2=(ImageView)promptsView.findViewById(R.id.imgDialog2);
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
				decision = 1;
				if(soundStatus) {
					//player = MediaPlayer.create(TossBluetoothActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				alertDialog.cancel();
				showSymbolDialog();
			}
		});
		
		linearNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				decision=2;
				if(soundStatus) {
					//player = MediaPlayer.create(TossBluetoothActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				alertDialog.cancel();
				showSymbolDialog();
			}
		});
		
	}
	
	
	
	public void showReadyDialog()
	{
		
		LayoutInflater li = LayoutInflater.from(TossBluetoothActivity.this);
		View promptsView = li.inflate(R.layout.toss_dialog1, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(TossBluetoothActivity.this);
		builder.setView(promptsView);
		builder.setCancelable(false);
		final AlertDialog alertDialog = builder.create();
		alertDialog.show();
		TextView txtDialogBody=(TextView)promptsView.findViewById(R.id.txtDialogBody);
		//Button btnOk=(Button)promptsView.findViewById(R.id.btnOk);
		LinearLayout linearDialogPlay=(LinearLayout)promptsView.findViewById(R.id.linearDialogPlay);
		String text="";
		if(decision==1) text+="Your opponent has decided to play last";
		else text+="Your opponent has decided to play first";
		if(symbol==1) text+=" with the symbol Circle";
		else text+=" with the symbol Cross";
		txtDialogBody.setText(text);
		
		linearDialogPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(soundStatus) {
					//player = MediaPlayer.create(TossBluetoothActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				myService.sendMessage("ready");
				alertDialog.cancel();
				Intent in=new Intent(TossBluetoothActivity.this,GameBluetoothActivity.class);
				in.putExtra("symbol",symbol);
				in.putExtra("decision",decision);
				normalExit = true;
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
				finish();
			}
		});
		
	}
	
	
	
	public void showSymbolDialog()
	{
		
		LayoutInflater li = LayoutInflater.from(TossBluetoothActivity.this);
		View promptsView = li.inflate(R.layout.toss_dialog2, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(TossBluetoothActivity.this);
		builder.setView(promptsView);
		builder.setCancelable(false);
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
					//player = MediaPlayer.create(TossBluetoothActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				myService.sendMessage("decision:"+decision+":"+symbol);
				alertDialog.cancel();
				showWaitingDialog("Waiting for the opponent to be ready...");
			}
		});
		
		linearDialogCircle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				symbol=2;
				if(soundStatus) {
					//player = MediaPlayer.create(TossBluetoothActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				myService.sendMessage("decision:"+decision+":"+symbol);
				alertDialog.cancel();
				showWaitingDialog("Waiting for the opponent to be ready...");
			}
		});
		
	}
	
	
	
	public void showWaitingDialog(String text)
	{
		
		LayoutInflater li = LayoutInflater.from(TossBluetoothActivity.this);
		View promptsView = li.inflate(R.layout.progress_dialog, null);
		TextView txtWait=(TextView)promptsView.findViewById(R.id.txtDialogBody);
		txtWait.setText(text);
		AlertDialog.Builder builder = new AlertDialog.Builder(TossBluetoothActivity.this);
		builder.setView(promptsView);
		builder.setCancelable(false);
	    waitingDialog = builder.create();
		waitingDialog.show();
		
	}
	
	

}
