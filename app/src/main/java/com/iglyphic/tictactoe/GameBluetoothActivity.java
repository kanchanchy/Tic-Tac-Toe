package com.iglyphic.tictactoe;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class GameBluetoothActivity extends Activity{
	
	LinearLayout linearGameStarted,linearRoot1,linearRoot2,linear0,linear1,linear2,linear3,linear4,linear5,linear6,linear7,linear8;
	TextView txtGameStarted,txt0,txt1,txt2,txt3,txt4,txt5,txt6,txt7,txt8;
	ImageView imgMeSymbol,imgOppSymbol;
	LinearLayout[] linearFinished=new LinearLayout[5];
	
	boolean[] selectedStatus=new boolean[10];
	boolean oppTurn=false,meAvailable=false,oppAvailable=false,firstTime=false,clickReady=false,normalExit, soundStatus, gameOver;
	String[] state=new String[10];
	String stateMe,stateOpp;
	int mePlayer=0,oppPlayer=0;
	int selectedPos;
	int[] finishedPos=new int[5];
	ArrayList<Integer> availablePos=new ArrayList<Integer>();
	ArrayList<Integer> allPos=new ArrayList<Integer>();
	
	int decision,symbol;
	Animation startAnim,scaleAnim1,scaleAnim2,transAnimUp,transAnimIn;

	private SoundPool spool;
	private int soundMoveID, soundWinningId, soundFailureId, soundTiedId;
	float volume;
	
	MyBluetoothService myService;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_bluetooth);
		
		linearGameStarted=(LinearLayout)findViewById(R.id.linearGameStarted);
		linearRoot1=(LinearLayout)findViewById(R.id.linearRoot1);
		linearRoot2=(LinearLayout)findViewById(R.id.linearRoot2);
		linear0=(LinearLayout)findViewById(R.id.linear0);
		linear1=(LinearLayout)findViewById(R.id.linear1);
		linear2=(LinearLayout)findViewById(R.id.linear2);
		linear3=(LinearLayout)findViewById(R.id.linear3);
		linear4=(LinearLayout)findViewById(R.id.linear4);
		linear5=(LinearLayout)findViewById(R.id.linear5);
		linear6=(LinearLayout)findViewById(R.id.linear6);
		linear7=(LinearLayout)findViewById(R.id.linear7);
		linear8=(LinearLayout)findViewById(R.id.linear8);
		txtGameStarted=(TextView)findViewById(R.id.txtGameStarted);
		txt0=(TextView)findViewById(R.id.txt0);
		txt1=(TextView)findViewById(R.id.txt1);
		txt2=(TextView)findViewById(R.id.txt2);
		txt3=(TextView)findViewById(R.id.txt3);
		txt4=(TextView)findViewById(R.id.txt4);
		txt5=(TextView)findViewById(R.id.txt5);
		txt6=(TextView)findViewById(R.id.txt6);
		txt7=(TextView)findViewById(R.id.txt7);
		txt8=(TextView)findViewById(R.id.txt8);
		imgMeSymbol=(ImageView)findViewById(R.id.imgMeSymbol);
		imgOppSymbol=(ImageView)findViewById(R.id.imgOppSymbol);
		
		
		//winning=MediaPlayer.create(this, R.raw.winning);
		//failure=MediaPlayer.create(this, R.raw.fail);
		//tie=MediaPlayer.create(this, R.raw.draw);
		
		firstTime=true;
		clickReady=false;
		normalExit=false;
		gameOver = false;

		SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(this);
		soundStatus = sharedPrefUtil.getSoundStatus();
		
		myService=new MyBluetoothService();
		myService.setFHandler(mHandler);
		
		for(int i=0;i<9;i++)
		{
			selectedStatus[i]=false;
			state[i]="";
		}
		
		symbol=getIntent().getExtras().getInt("symbol");
		decision=getIntent().getExtras().getInt("decision");
	
		startAnim = AnimationUtils.loadAnimation(this, R.anim.scale_animation02);
		scaleAnim1 = AnimationUtils.loadAnimation(this, R.anim.scale_animation01);
		scaleAnim2 = AnimationUtils.loadAnimation(this, R.anim.scale_animation03);
		transAnimUp = AnimationUtils.loadAnimation(this, R.anim.slide_out_up);
		transAnimIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
		
		if(symbol==1)
		{
			mePlayer=R.drawable.cross;
			oppPlayer=R.drawable.circle;
			stateMe="X";
			stateOpp="Y";
		}
		else
		{
			mePlayer=R.drawable.circle;
			oppPlayer=R.drawable.cross;
			stateMe="Y";
			stateOpp="X";
		}
		
		imgMeSymbol.setImageResource(mePlayer);
		imgOppSymbol.setImageResource(oppPlayer);


		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundMoveID = spool.load(this, R.raw.move, 1);
		soundWinningId = spool.load(this, R.raw.winning, 1);
		soundFailureId = spool.load(this, R.raw.fail, 1);
		soundTiedId = spool.load(this, R.raw.draw, 1);

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		AdRequest adRequest = new AdRequest.Builder()
				.build();

		AdView mAdView = (AdView) findViewById(R.id.adView);
		mAdView.loadAd(adRequest);
		
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(firstTime)
		{
			firstTime=false;
			
			try
			{
				Thread.sleep(500);
			}
			catch(Exception e){}
			selectedPos=-1;
			linearGameStarted.startAnimation(startAnim);
			GameStartTask task=new GameStartTask();
			task.execute();
		}
		
	}
	
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		LayoutInflater li = LayoutInflater.from(GameBluetoothActivity.this);
		View promptsView = li.inflate(R.layout.exit, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(GameBluetoothActivity.this);
		builder.setView(promptsView);
		builder.setCancelable(true);
		final AlertDialog alertDialog = builder.create();
		alertDialog.show();

		TextView txtDialogBody = (TextView)promptsView.findViewById(R.id.txtDialogBody);
		LinearLayout linearDialogYes=(LinearLayout)promptsView.findViewById(R.id.linearDialogYes);
		LinearLayout linearDialogNo=(LinearLayout)promptsView.findViewById(R.id.linearDialogNo);

		txtDialogBody.setText("Are you sure you want to quit the game?");
		
		linearDialogNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(soundStatus) {
					//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
					//move.start();
					spool.play(soundMoveID, volume, volume, 1, 0, 1f);
				}
				alertDialog.cancel();
			}
		});	
		
		linearDialogYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(soundStatus) {
					//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
					//move.start();
					spool.play(soundMoveID, volume, volume, 1, 0, 1f);
				}
				alertDialog.cancel();
				
				if(MyBluetoothService.connectedStatus) myService.stopConnectedThread();
				if(MyBluetoothService.connectStatus) myService.stopConnectThread();
				if(MyBluetoothService.acceptStatus) myService.stopAcceptThread();
				
				Intent in=new Intent(GameBluetoothActivity.this,BluetoothConnectActivity.class);
				normalExit=true;
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
				finish();
			}
		});
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
                int oppPos=Integer.valueOf(readMessage);
                setTextPosition(oppPos);
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
				clickReady = false;
				if(!gameOver) {
					showDisconnectDialog();
				} else {
					BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					if(!mBluetoothAdapter.isEnabled()) {
						showDisconnectDialog();
					}
				}
				break;
            }
        }
    };
	


	private void showDisconnectDialog() {
		LayoutInflater li = LayoutInflater.from(GameBluetoothActivity.this);
		View promptsView = li.inflate(R.layout.toss_dialog1, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(GameBluetoothActivity.this);
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
					//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
					//move.start();
					spool.play(soundMoveID, volume, volume, 1, 0, 1f);
				}

				if(MyBluetoothService.connectedStatus) myService.stopConnectedThread();
				if(MyBluetoothService.connectStatus) myService.stopConnectThread();
				if(MyBluetoothService.acceptStatus) myService.stopAcceptThread();

				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

				alertDialog.cancel();

				if(mBluetoothAdapter.isEnabled()) {
					Intent in=new Intent(GameBluetoothActivity.this,BluetoothConnectActivity.class);
					normalExit=true;
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
					finish();
				} else {
					Intent in=new Intent(GameBluetoothActivity.this,PlayActivity.class);
					normalExit=true;
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
					finish();
				}
			}
		});
	}
	
	
	
	public void showOutput(String winner)
	{
		if(winner.equals(""))
		{
			if(oppTurn)
			{
				oppTurn=false;
			}
			else
			{
				oppTurn=true;
			}
			setTurn();
		}
		else if(winner.equals("drawn"))
		{
			try{
				Thread.sleep(300);
			}
			catch(Exception e){}
			txtGameStarted.setText("Game Over!!!");
			linearGameStarted.startAnimation(scaleAnim1);
			GameFinishedTask task=new GameFinishedTask();
			task.execute(winner);
		}
		else
		{
			try{
				Thread.sleep(300);
			}
			catch(Exception e){}
			
			for(int i=0;i<3;i++)
			{
				if(finishedPos[i]==0) linearFinished[i]=(LinearLayout)findViewById(R.id.linear0r);
				else if(finishedPos[i]==1) linearFinished[i]=(LinearLayout)findViewById(R.id.linear1r);
				else if(finishedPos[i]==2) linearFinished[i]=(LinearLayout)findViewById(R.id.linear2r);
				else if(finishedPos[i]==3) linearFinished[i]=(LinearLayout)findViewById(R.id.linear3r);
				else if(finishedPos[i]==4) linearFinished[i]=(LinearLayout)findViewById(R.id.linear4r);
				else if(finishedPos[i]==5) linearFinished[i]=(LinearLayout)findViewById(R.id.linear5r);
				else if(finishedPos[i]==6) linearFinished[i]=(LinearLayout)findViewById(R.id.linear6r);
				else if(finishedPos[i]==7) linearFinished[i]=(LinearLayout)findViewById(R.id.linear7r);
				else linearFinished[i]=(LinearLayout)findViewById(R.id.linear8r);
			}
			txtGameStarted.setText("Game Over!!!");
			linearGameStarted.startAnimation(scaleAnim1);
			if(winner.equals(stateMe))
			{
				for(int i=0;i<3;i++)
				{
					linearFinished[i].setBackgroundColor(Color.parseColor("#008000"));
					linearFinished[i].startAnimation(scaleAnim2);
				}
			}
			else
			{
				for(int i=0;i<3;i++)
				{
					linearFinished[i].setBackgroundColor(Color.parseColor("#FF4500"));
					linearFinished[i].startAnimation(scaleAnim2);
				}
				
			}
			GameFinishedTask task=new GameFinishedTask();
			task.execute(winner);
		}
	}
	
	
	
	public void setTextPosition(int setPos)
	{
		if(soundStatus) {
			//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
			//move.start();
			spool.play(soundMoveID, volume, volume, 1, 0, 1f);
		}
		selectedStatus[setPos]=true;
		state[setPos]=stateOpp;
		if(setPos==0)
		{
			//linear0.setBackgroundColor(Color.parseColor("#B59445"));
			linear0.setBackgroundResource(oppPlayer);
			//txt0.setText(stateMe);
			linear0.startAnimation(scaleAnim2);
		}
		else if(setPos==1)
		{
			//linear1.setBackgroundColor(Color.parseColor("#B59445"));
			linear1.setBackgroundResource(oppPlayer);
			//txt1.setText(stateMe);
			linear1.startAnimation(scaleAnim2);
		}
		else if(setPos==2)
		{
			//linear2.setBackgroundColor(Color.parseColor("#B59445"));
			linear2.setBackgroundResource(oppPlayer);
			//txt2.setText(stateMe);
			linear2.startAnimation(scaleAnim2);
		}
		else if(setPos==3)
		{
		//	linear3.setBackgroundColor(Color.parseColor("#B59445"));
			linear3.setBackgroundResource(oppPlayer);
			//txt3.setText(stateMe);
			linear3.startAnimation(scaleAnim2);
		}
		else if(setPos==4)
		{
			//linear4.setBackgroundColor(Color.parseColor("#B59445"));
			linear4.setBackgroundResource(oppPlayer);
			//txt4.setText(stateMe);
			linear4.startAnimation(scaleAnim2);
		}
		else if(setPos==5)
		{
			//linear5.setBackgroundColor(Color.parseColor("#B59445"));
			linear5.setBackgroundResource(oppPlayer);
			//txt5.setText(stateMe);
			linear5.startAnimation(scaleAnim2);
		}
		else if(setPos==6)
		{
			//linear6.setBackgroundColor(Color.parseColor("#B59445"));
			linear6.setBackgroundResource(oppPlayer);
			//txt6.setText(stateMe);
			linear6.startAnimation(scaleAnim2);
		}
		else if(setPos==7)
		{
			//linear7.setBackgroundColor(Color.parseColor("#B59445"));
			linear7.setBackgroundResource(oppPlayer);
			//txt7.setText(stateMe);
			linear7.startAnimation(scaleAnim2);
		}
		else
		{
			//linear8.setBackgroundColor(Color.parseColor("#B59445"));
			linear8.setBackgroundResource(oppPlayer);
			//txt8.setText(stateMe);
			linear8.startAnimation(scaleAnim2);
		}
		//hfhhh
		//checkResult();
		AfterEachPosTask task=new AfterEachPosTask();
		task.execute();
	}	
	
	
	
	
	public void checkResult()
	{
		boolean finished=false;
		for(int i=0;i<3;i++)
		{
			if(state[i*3].equals("X")&&state[i*3+1].equals("X")&&state[i*3+2].equals("X"))
			{
				finishedPos[0]=i*3;
				finishedPos[1]=i*3+1;
				finishedPos[2]=i*3+2;
				finished=true;
				showOutput("X");
				break;
			}
			if(state[i*3].equals("Y")&&state[i*3+1].equals("Y")&&state[i*3+2].equals("Y"))
			{
				finishedPos[0]=i*3;
				finishedPos[1]=i*3+1;
				finishedPos[2]=i*3+2;
				finished=true;
				showOutput("Y");
				break;
			}
			if(state[i].equals("X")&&state[i+3].equals("X")&&state[i+6].equals("X"))
			{
				finishedPos[0]=i;
				finishedPos[1]=i+3;
				finishedPos[2]=i+6;
				finished=true;
				showOutput("X");
				break;
			}
			if(state[i].equals("Y")&&state[i+3].equals("Y")&&state[i+6].equals("Y"))
			{
				finishedPos[0]=i;
				finishedPos[1]=i+3;
				finishedPos[2]=i+6;
				finished=true;
				showOutput("Y");
				break;
			}
		}
		if(finished==false)
		{
			if(state[0].equals("X")&&state[4].equals("X")&&state[8].equals("X"))
			{
				finishedPos[0]=0;
				finishedPos[1]=4;
				finishedPos[2]=8;
				finished=true;
				showOutput("X");
			}
			if(state[0].equals("Y")&&state[4].equals("Y")&&state[8].equals("Y"))
			{
				finishedPos[0]=0;
				finishedPos[1]=4;
				finishedPos[2]=8;
				finished=true;
				showOutput("Y");
			}
			if(state[2].equals("X")&&state[4].equals("X")&&state[6].equals("X"))
			{
				finishedPos[0]=2;
				finishedPos[1]=4;
				finishedPos[2]=6;
				finished=true;
				showOutput("X");
			}
			if(state[2].equals("Y")&&state[4].equals("Y")&&state[6].equals("Y"))
			{
				finishedPos[0]=2;
				finishedPos[1]=4;
				finishedPos[2]=6;
				finished=true;
				showOutput("Y");
			}
		}
		if(finished==false)
		{
			boolean drawn=true;
			for(int i=0;i<9;i++)
			{
				if(state[i].equals(""))
				{
					drawn=false;
					break;
				}
			}
			if(drawn) showOutput("drawn");
			else showOutput("");
		}
	}
	
	public void layoutClicked(View view)
	{
		if(clickReady)
		{
			clickReady=false;
			//LinearLayout linearClicked=(LinearLayout)view;
			if(oppTurn==false)
			{
				if(view.getId()==R.id.linear0)
				{
					if(selectedStatus[0]==false)
					{
						if(soundStatus) {
							//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[0]=true;
						//linear0.setBackgroundColor(Color.parseColor("#B59445"));
						linear0.setBackgroundResource(mePlayer);
						//txt0.setText(stateOpp);
						state[0]=stateMe;
						selectedPos=0;
						String text=String.valueOf(selectedPos);
						myService.sendMessage(text);
						view.startAnimation(scaleAnim2);
						AfterEachPosTask task=new AfterEachPosTask();
						task.execute();
					}
				}
				if(view.getId()==R.id.linear1)
				{
					if(selectedStatus[1]==false)
					{
						if(soundStatus) {
							//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[1]=true;
						//linear1.setBackgroundColor(Color.parseColor("#B59445"));
						linear1.setBackgroundResource(mePlayer);
						//txt1.setText(stateOpp);
						state[1]=stateMe;
						//checkResult();	
						selectedPos=1;
						String text=String.valueOf(selectedPos);
						myService.sendMessage(text);
						view.startAnimation(scaleAnim2);
						AfterEachPosTask task=new AfterEachPosTask();
						task.execute();			
					}
				}
				if(view.getId()==R.id.linear2)
				{
					if(selectedStatus[2]==false)
					{
						if(soundStatus) {
							//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[2]=true;
						//linear2.setBackgroundColor(Color.parseColor("#B59445"));
						linear2.setBackgroundResource(mePlayer);
						//txt2.setText(stateOpp);
						state[2]=stateMe;
						//checkResult();
						selectedPos=2;
						String text=String.valueOf(selectedPos);
						myService.sendMessage(text);
						view.startAnimation(scaleAnim2);
						AfterEachPosTask task=new AfterEachPosTask();
						task.execute();				
					}
				}
				if(view.getId()==R.id.linear3)
				{
					if(selectedStatus[3]==false)
					{
						if(soundStatus) {
							//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[3]=true;
						//linear3.setBackgroundColor(Color.parseColor("#B59445"));
						linear3.setBackgroundResource(mePlayer);
						//txt3.setText(stateOpp);
						state[3]=stateMe;
						//checkResult();	
						selectedPos=3;
						String text=String.valueOf(selectedPos);
						myService.sendMessage(text);
						view.startAnimation(scaleAnim2);
						AfterEachPosTask task=new AfterEachPosTask();
						task.execute();	
						
					}
				}
				if(view.getId()==R.id.linear4)
				{
					if(selectedStatus[4]==false)
					{
						if(soundStatus) {
							//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[4]=true;
						//linear4.setBackgroundColor(Color.parseColor("#B59445"));
						linear4.setBackgroundResource(mePlayer);
						//txt4.setText(stateOpp);
						state[4]=stateMe;
						//checkResult();
						selectedPos=4;
						String text=String.valueOf(selectedPos);
						myService.sendMessage(text);
						view.startAnimation(scaleAnim2);
						AfterEachPosTask task=new AfterEachPosTask();
						task.execute();					
					}
				}
				if(view.getId()==R.id.linear5)
				{
					if(selectedStatus[5]==false)
					{
						if(soundStatus) {
							//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[5]=true;
						//linear5.setBackgroundColor(Color.parseColor("#B59445"));
						linear5.setBackgroundResource(mePlayer);
						//txt5.setText(stateOpp);
						state[5]=stateMe;
						//checkResult();	
						selectedPos=5;
						String text=String.valueOf(selectedPos);
						myService.sendMessage(text);
						view.startAnimation(scaleAnim2);
						AfterEachPosTask task=new AfterEachPosTask();
						task.execute();					
					}
				}
				if(view.getId()==R.id.linear6)
				{
					if(selectedStatus[6]==false)
					{
						if(soundStatus) {
							//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[6]=true;
						//linear6.setBackgroundColor(Color.parseColor("#B59445"));
						linear6.setBackgroundResource(mePlayer);
						//txt6.setText(stateOpp);
						state[6]=stateMe;
						//checkResult();
						selectedPos=6;
						String text=String.valueOf(selectedPos);
						myService.sendMessage(text);
						view.startAnimation(scaleAnim2);
						AfterEachPosTask task=new AfterEachPosTask();
						task.execute();					
					}
				}
				if(view.getId()==R.id.linear7)
				{
					if(selectedStatus[7]==false)
					{
						if(soundStatus) {
							//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[7]=true;
						//linear7.setBackgroundColor(Color.parseColor("#B59445"));
						linear7.setBackgroundResource(mePlayer);
						//txt7.setText(stateOpp);
						state[7]=stateMe;
						//checkResult();
						selectedPos=7;
						String text=String.valueOf(selectedPos);
						myService.sendMessage(text);
						view.startAnimation(scaleAnim2);
						AfterEachPosTask task=new AfterEachPosTask();
						task.execute();					
					}
				}
				if(view.getId()==R.id.linear8)
				{
					if(selectedStatus[8]==false)
					{
						if(soundStatus) {
							//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[8]=true;
						//linear8.setBackgroundColor(Color.parseColor("#B59445"));
						linear8.setBackgroundResource(mePlayer);
						//txt8.setText(stateOpp);
						state[8]=stateMe;
						//checkResult();
						selectedPos=8;
						String text=String.valueOf(selectedPos);
						myService.sendMessage(text);
						view.startAnimation(scaleAnim2);
						AfterEachPosTask task=new AfterEachPosTask();
						task.execute();				
					}
				}
			}
		}
	}
	
	
	
	public void setTurn()
	{
		try{
			Thread.sleep(200);
		}
		catch(Exception e){}
		if(oppTurn) txtGameStarted.setText("Turn of Opponent");
		else txtGameStarted.setText("Turn of you");
		linearGameStarted.startAnimation(scaleAnim1);
		
		TurnTask task=new TurnTask();
		task.execute();
	}
	
	public void afterTurn()
	{
	
		if(!oppTurn) clickReady=true;
	}
	
	
	
	class GameStartTask extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(1000);
			}
			catch(Exception e){}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if(decision==1)
			{
				oppTurn=false;
			}
			else
			{
				oppTurn=true;
			} 
			setTurn();
		}
		
	}
	
	
	
	class TurnTask extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(350);
			}
			catch(Exception e){}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			afterTurn();
		}
		
	}
	
	
	
	class AfterEachPosTask extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(500);
			}
			catch(Exception e){}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			checkResult();
		}
		
	}  
	
	
	class GameFinishedTask extends AsyncTask<String, String, String>
	{

		String winner="";
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			winner=params[0];
			if(winner.equals("drawn")) {
				if(soundStatus) {
					//tie.start();
					spool.play(soundTiedId, volume, volume, 1, 0, 1f);
				}
			}
			else if(winner.equals(stateMe)) {
				if(soundStatus) {
					//winning.start();
					spool.play(soundWinningId, volume, volume, 1, 0, 1f);
				}
			}
			else {
				if(soundStatus) {
					//failure.start();
					spool.play(soundFailureId, volume, volume, 1, 0, 1f);
				}
			}
			try{
				Thread.sleep(500);
			}
			catch(Exception e){}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(800);
			}
			catch(Exception e){}
			
			
			SharedPreferences pref=getSharedPreferences("tic_tac_toe_result",0);
			int battles=pref.getInt("battles_bluetooth", 0);
			int tied=pref.getInt("tied_bluetooth", 0);
			int wins=pref.getInt("wins_bluetooth", 0);
			
			battles=battles+1;
			if(winner.equals("drawn")) tied=tied+1;
			if(winner.equals(stateMe)) wins=wins+1;
			
			SharedPreferences.Editor editor=pref.edit();
			editor.putInt("battles_bluetooth", battles);
			editor.putInt("tied_bluetooth", tied);
			editor.putInt("wins_bluetooth", wins);
			editor.commit();
			
			LayoutInflater li = LayoutInflater.from(GameBluetoothActivity.this);
			View promptsView = li.inflate(R.layout.exit, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(GameBluetoothActivity.this);
			builder.setView(promptsView);
			builder.setCancelable(false);
			final AlertDialog alertDialog = builder.create();
			WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
			lp.dimAmount = 1.4f;
			alertDialog.show();
			gameOver = true;
			
			LinearLayout linearDialogYes=(LinearLayout)promptsView.findViewById(R.id.linearDialogYes);
			LinearLayout linearDialogNo=(LinearLayout)promptsView.findViewById(R.id.linearDialogNo);
			TextView txtAlert=(TextView)promptsView.findViewById(R.id.txtAlert);
			TextView txtBody=(TextView)promptsView.findViewById(R.id.txtDialogBody);
			TextView txtYes=(TextView)promptsView.findViewById(R.id.txtYes);
			TextView txtNo=(TextView)promptsView.findViewById(R.id.txtNo);
			
			String text;
			if(winner.equals("drawn")) text="Match Drawn!!!";
			else if(winner.equals(stateMe)) text="Congrates!!!\nYou won the match";
			else text="You Lose!!";
			txtAlert.setText("Match Result");
			txtBody.setText(text);
			txtYes.setText("Play Again");
			txtNo.setText("Exit");
			
			linearDialogNo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub	  
					if(soundStatus) {
						//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
						//move.start();
						spool.play(soundMoveID, volume, volume, 1, 0, 1f);
					}
					alertDialog.cancel();
					if(MyBluetoothService.connectedStatus) myService.stopConnectedThread();
					if(MyBluetoothService.connectStatus) myService.stopConnectThread();
					if(MyBluetoothService.acceptStatus) myService.stopAcceptThread();
					
				/*	Intent service=new Intent(GameBluetoothActivity.this,BackGroundMusic.class);
					startService(service);   */
					Intent in=new Intent(GameBluetoothActivity.this,PlayActivity.class);
					normalExit=true;
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
					finish();
				}
			});	
			
			linearDialogYes.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub	
					if(soundStatus) {
						//move=MediaPlayer.create(GameBluetoothActivity.this, R.raw.move);
						//move.start();
						spool.play(soundMoveID, volume, volume, 1, 0, 1f);
					}
					alertDialog.cancel();
					if(MyBluetoothService.connectedStatus) myService.stopConnectedThread();
					if(MyBluetoothService.connectStatus) myService.stopConnectThread();
					if(MyBluetoothService.acceptStatus) myService.stopAcceptThread();

				/*	Intent service=new Intent(GameBluetoothActivity.this,BackGroundMusic.class);
					startService(service);   */
					Intent in=new Intent(GameBluetoothActivity.this,BluetoothConnectActivity.class);
					normalExit=true;
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
					finish();
				}
			});
			
			
		}
		
	}
	
	

}
