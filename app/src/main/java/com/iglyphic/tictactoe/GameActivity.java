package com.iglyphic.tictactoe;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class GameActivity extends Activity{
	
	LinearLayout linearGameStarted,linearRoot1,linearRoot2,linear0,linear1,linear2,linear3,linear4,linear5,linear6,linear7,linear8;
	TextView txtGameStarted,txt0,txt1,txt2,txt3,txt4,txt5,txt6,txt7,txt8;
	ImageView imgAndroidSymbol,imgUserSymbol;
	LinearLayout[] linearFinished=new LinearLayout[5];
	
	boolean[] selectedStatus=new boolean[10];
	boolean xTurn=false,meAvailable=false,oppAvailable=false,firstTime=false,clickReady=false, soundStatus;
	String[] state=new String[10];
	String stateMe="X",stateOpp="Y";
	int mePlayer=0,oppPlayer=0;
	int sidePos1,sidePos2,sidePos3;
	int[] finishedPos=new int[5];
	ArrayList<Integer> availablePos=new ArrayList<Integer>();
	ArrayList<Integer> allPos=new ArrayList<Integer>();
	
	int symbol,decision;
	Animation startAnim,scaleAnim1,scaleAnim2,transAnimUp,transAnimIn;

	private SoundPool spool;
	private int soundMoveID, soundWinningId, soundFailureId, soundTiedId;
	float volume;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_layout);
		
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
		imgAndroidSymbol=(ImageView)findViewById(R.id.imgAndroidSymbol);
		imgUserSymbol=(ImageView)findViewById(R.id.imgUserSymbol);
		
		//winning=MediaPlayer.create(this, R.raw.winning);
		//failure=MediaPlayer.create(this, R.raw.fail);
		//tie=MediaPlayer.create(this, R.raw.draw);
		
		firstTime=true;
		clickReady=false;

		SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(this);
		soundStatus = sharedPrefUtil.getSoundStatus();
		
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
		
		// here opponent is user and me is android and received symbol and decision is of user
		if(symbol==1)
		{
			mePlayer=R.drawable.circle;
			oppPlayer=R.drawable.cross;
		}
		else
		{
			mePlayer=R.drawable.cross;
			oppPlayer=R.drawable.circle;
		}
		
		imgAndroidSymbol.setImageResource(mePlayer);
		imgUserSymbol.setImageResource(oppPlayer);

	/*	TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String uid = tManager.getDeviceId();  */

		AdRequest adRequest = new AdRequest.Builder()
				.build();

		AdView mAdView = (AdView) findViewById(R.id.adView);
		mAdView.loadAd(adRequest);

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundMoveID = spool.load(this, R.raw.move, 1);
		soundWinningId = spool.load(this, R.raw.winning, 1);
		soundFailureId = spool.load(this, R.raw.fail, 1);
		soundTiedId = spool.load(this, R.raw.draw, 1);

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
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
			linearGameStarted.startAnimation(startAnim);
			GameStartTask task=new GameStartTask();
			task.execute();
		}
		//else setTurn();
		
	}
	
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		LayoutInflater li = LayoutInflater.from(GameActivity.this);
		View promptsView = li.inflate(R.layout.exit, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
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
					//move=MediaPlayer.create(GameActivity.this, R.raw.move);
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
					//move=MediaPlayer.create(GameActivity.this, R.raw.move);
					//move.start();
					spool.play(soundMoveID, volume, volume, 1, 0, 1f);
				}
				alertDialog.cancel();
				Intent in=new Intent(GameActivity.this,PlayActivity.class);
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
				finish();
			}
		});
	}
	
	
	
	
	public void showOutput(String winner)
	{
		if(winner.equals(""))
		{
			if(xTurn)
			{
				xTurn=false;
			}
			else
			{
				xTurn=true;
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
			if(winner.equals("Y"))
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
	
	
	public void setAvailablePos()
	{
		if(availablePos.size()>0) availablePos.clear();
		for(int i=0;i<allPos.size();i++)
		{
			int j=allPos.get(i);
			if(state[j].equals("")) availablePos.add(j);
		}
	}
	
	public void setRandomPos()
	{
		int setPos=-1;
		boolean[] eligibleStatus=new boolean[10];
		for(int i=0;i<9;i++)
			eligibleStatus[i]=false;
		int count=0;
		boolean success=false;
		while(true)
		{
			double a=Math.random();
			a=a*1000;
			int b=(int)a;
			for(int i=0;i<availablePos.size();i++)
			{
				if(availablePos.get(i)==b)
				{
					if(eligibleStatus[b]==false)
					{
						if(oppYesMeNoPosition(b))
						{
							eligibleStatus[b]=true;
							count++;
							setPos=b;
							break;
						}
						else
						{
							count++;
							eligibleStatus[b]=true;
						}
					}
					
				}
			}
			if(setPos!=-1) break;
			if(count>=availablePos.size()) break;
		}
		
		if(setPos==-1)
		{
			for(int i=0;i<9;i++)
				eligibleStatus[i]=false;
			count=0;
			success=false;
			while(true)
			{
				double a=Math.random();
				a=a*1000;
				int b=(int)a;
				for(int i=0;i<availablePos.size();i++)
				{
					if(availablePos.get(i)==b)
					{
						if(eligibleStatus[b]==false)
						{
							if(MeYesOppNoPosition(b))
							{
								eligibleStatus[b]=true;
								count++;
								setPos=b;
								break;
							}
							else
							{
								count++;
								eligibleStatus[b]=true;
							}
						}
						
					}
				}
				if(setPos!=-1) break;
				if(count>=availablePos.size()) break;
			}
			
			if(setPos==-1)
			{
				while(true)
				{
					double a=Math.random();
					a=a*1000;
					int b=(int)a;
					for(int i=0;i<availablePos.size();i++)
					{
						if(availablePos.get(i)==b)
						{
							setPos=b;
							break;
						}
					}
					if(setPos!=-1) break;
				}
			}
		}
		
		
		setTextPosition(setPos);
	}
	
	public void setTextPosition(int setPos)
	{
		if(soundStatus) {
			//move=MediaPlayer.create(GameActivity.this, R.raw.move);
			//move.start();
			spool.play(soundMoveID, volume, volume, 1, 0, 1f);
		}
		selectedStatus[setPos]=true;
		state[setPos]=stateMe;
		if(setPos==0)
		{
			//linear0.setBackgroundColor(Color.parseColor("#B59445"));
			linear0.setBackgroundResource(mePlayer);
			//txt0.setText(stateMe);
			linear0.startAnimation(scaleAnim2);
		}
		else if(setPos==1)
		{
			//linear1.setBackgroundColor(Color.parseColor("#B59445"));
			linear1.setBackgroundResource(mePlayer);
			//txt1.setText(stateMe);
			linear1.startAnimation(scaleAnim2);
		}
		else if(setPos==2)
		{
			//linear2.setBackgroundColor(Color.parseColor("#B59445"));
			linear2.setBackgroundResource(mePlayer);
			//txt2.setText(stateMe);
			linear2.startAnimation(scaleAnim2);
		}
		else if(setPos==3)
		{
		//	linear3.setBackgroundColor(Color.parseColor("#B59445"));
			linear3.setBackgroundResource(mePlayer);
			//txt3.setText(stateMe);
			linear3.startAnimation(scaleAnim2);
		}
		else if(setPos==4)
		{
			//linear4.setBackgroundColor(Color.parseColor("#B59445"));
			linear4.setBackgroundResource(mePlayer);
			//txt4.setText(stateMe);
			linear4.startAnimation(scaleAnim2);
		}
		else if(setPos==5)
		{
			//linear5.setBackgroundColor(Color.parseColor("#B59445"));
			linear5.setBackgroundResource(mePlayer);
			//txt5.setText(stateMe);
			linear5.startAnimation(scaleAnim2);
		}
		else if(setPos==6)
		{
			//linear6.setBackgroundColor(Color.parseColor("#B59445"));
			linear6.setBackgroundResource(mePlayer);
			//txt6.setText(stateMe);
			linear6.startAnimation(scaleAnim2);
		}
		else if(setPos==7)
		{
			//linear7.setBackgroundColor(Color.parseColor("#B59445"));
			linear7.setBackgroundResource(mePlayer);
			//txt7.setText(stateMe);
			linear7.startAnimation(scaleAnim2);
		}
		else
		{
			//linear8.setBackgroundColor(Color.parseColor("#B59445"));
			linear8.setBackgroundResource(mePlayer);
			//txt8.setText(stateMe);
			linear8.startAnimation(scaleAnim2);
		}
		//hfhhh
		//checkResult();
		AfterEachPosTask task=new AfterEachPosTask();
		task.execute();
	}
	
	public void setAndroid()
	{
		if(allPos.size()>0) allPos.clear();
		int selectedPos=MeTwoOppNoSide();
		
		if(selectedPos!=-1)
		{
			setTextPosition(selectedPos);
		}
		
		else
		{
			selectedPos=oppTwoMeNoSide();
			if(selectedPos!=-1)
			{
				setTextPosition(selectedPos);
			}
			else
			{
				boolean flag=OppYesMeNoSide();
				if(flag==false)
				{
					flag=MeYesOppNoSide();
					if(flag==false)
					{
						for(int i=0;i<9;i++)
						{
							allPos.add(i);
						}
						setAvailablePos();
						setRandomPos();
					}
					else
					{
						allPos.add(sidePos1);
						allPos.add(sidePos2);
						allPos.add(sidePos3);
						setAvailablePos();
						setRandomPos();
					}
				}
				else
				{
					allPos.add(sidePos1);
					allPos.add(sidePos2);
					allPos.add(sidePos3);
					setAvailablePos();
					setRandomPos();
				}
			}
		}
	}
	
	
	public void checkAvailability(int pos1,int pos2,int pos3)
	{
		if(state[pos1].equals(stateMe)||state[pos2].equals(stateMe)||state[pos3].equals(stateMe)) meAvailable=true;
		else meAvailable=false;
		if(state[pos1].equals(stateOpp)||state[pos2].equals(stateOpp)||state[pos3].equals(stateOpp)) oppAvailable=true;
		else oppAvailable=false;		
	}
	
	
	public int positionMeUnavailable(int pos1,int pos2,int pos3)
	{
		if(!state[pos1].equals(stateMe)) return pos1;
		else if(!state[pos2].equals(stateMe))return pos2;
		else return pos3;
	}
	
	public int positionOppUnavailable(int pos1,int pos2,int pos3)
	{
		if(!state[pos1].equals(stateOpp)) return pos1;
		else if(!state[pos2].equals(stateOpp))return pos2;
		else return pos3;
	}
	
	public int oppTwoMeNoCheck(int pos1,int pos2,int pos3)
	{
		if(state[pos1].equals(stateOpp)&&state[pos2].equals(stateOpp)&&state[pos3].equals("")) return pos3;
		if(state[pos1].equals(stateOpp)&&state[pos3].equals(stateOpp)&&state[pos2].equals("")) return pos2;
		if(state[pos2].equals(stateOpp)&&state[pos3].equals(stateOpp)&&state[pos1].equals("")) return pos1;
		
		return -1;
	}
	
	public int MeTwoOppNoCheck(int pos1,int pos2,int pos3)
	{
		if(state[pos1].equals(stateMe)&&state[pos2].equals(stateMe)&&state[pos3].equals("")) return pos3;
		if(state[pos1].equals(stateMe)&&state[pos3].equals(stateMe)&&state[pos2].equals("")) return pos2;
		if(state[pos2].equals(stateMe)&&state[pos3].equals(stateMe)&&state[pos1].equals("")) return pos1;
		
		return -1;
	}
	
	public boolean oppYesMeNoPosition(int scanPos)
	{
		if(scanPos==2||scanPos==4||scanPos==6)
		{
			checkAvailability(2,4,6);
			if(meAvailable==false&&oppAvailable==true) return true;
		}
		if(scanPos==0||scanPos==4||scanPos==8)
		{
			checkAvailability(0,4,8);
			if(meAvailable==false&&oppAvailable==true) return true;
		}
		int pos1,pos2;
		if(scanPos-3>=0)
		{
			pos1=scanPos-3;
			if(pos1-3>=0)
			{
				pos2=pos1-3;
			}
			else pos2=scanPos+3;
		}
		else
		{
			pos1=scanPos+3;
			pos2=pos1+3;
		}
		checkAvailability(scanPos,pos1,pos2);
		if(meAvailable==false&&oppAvailable==true) return true;
		if(scanPos-1==-1||scanPos-1==2||scanPos-1==5)
		{
			pos1=scanPos+1;
			pos2=pos1+1;
		}
		else if(scanPos-1==0||scanPos-1==3||scanPos-1==6)
		{
			pos1=scanPos-1;
			pos2=scanPos+1;
		}
		else
		{
			pos1=scanPos-1;
			pos2=pos1-1;
		}
		checkAvailability(scanPos,pos1,pos2);
		if(meAvailable==false&&oppAvailable==true) return true;
		
		return false;
	}
	
	
	public boolean MeYesOppNoPosition(int scanPos)
	{
		if(scanPos==2||scanPos==4||scanPos==6)
		{
			checkAvailability(2,4,6);
			if(meAvailable==true&&oppAvailable==false) return true;
		}
		
		if(scanPos==0||scanPos==4||scanPos==8)
		{
			checkAvailability(0,4,8);
			if(meAvailable==true&&oppAvailable==false) return true;
		}
		
		int pos1,pos2;
		if(scanPos-3>=0)
		{
			pos1=scanPos-3;
			if(pos1-3>=0)
			{
				pos2=pos1-3;
			}
			else pos2=scanPos+3;
		}
		else
		{
			pos1=scanPos+3;
			pos2=pos1+3;
		}
		checkAvailability(scanPos,pos1,pos2);
		if(meAvailable==true&&oppAvailable==false) return true;
		
		if(scanPos-1==-1||scanPos-1==2||scanPos-1==5)
		{
			pos1=scanPos+1;
			pos2=pos1+1;
		}
		else if(scanPos-1==0||scanPos-1==3||scanPos-1==6)
		{
			pos1=scanPos-1;
			pos2=scanPos+1;
		}
		else
		{
			pos1=scanPos-1;
			pos2=pos1-1;
		}
		checkAvailability(scanPos,pos1,pos2);
		if(meAvailable==true&&oppAvailable==false) return true;
		
		return false;
	}
	
	
	public int oppTwoMeNoSide()
	{
		int selectedPos=oppTwoMeNoCheck(0, 4, 8);
		if(selectedPos!=-1) return selectedPos;
		
		selectedPos=oppTwoMeNoCheck(2, 4, 6);
		if(selectedPos!=-1) return selectedPos;
		
		for(int i=0;i<3;i++)
		{
			selectedPos=oppTwoMeNoCheck(i*3, i*3+1, i*3+2);
			if(selectedPos!=-1) return selectedPos;

			selectedPos=oppTwoMeNoCheck(i, i+3, i+6);
			if(selectedPos!=-1) return selectedPos;
		}
		
		return -1;
	}
	
	public int MeTwoOppNoSide()
	{
		int selectedPos=MeTwoOppNoCheck(0, 4, 8);
		if(selectedPos!=-1) return selectedPos;
		
		selectedPos=MeTwoOppNoCheck(2, 4, 6);
		if(selectedPos!=-1) return selectedPos;
		
		for(int i=0;i<3;i++)
		{
			selectedPos=MeTwoOppNoCheck(i*3, i*3+1, i*3+2);
			if(selectedPos!=-1) return selectedPos;

			selectedPos=MeTwoOppNoCheck(i, i+3, i+6);
			if(selectedPos!=-1) return selectedPos;
		}
		
		return -1;
	}
	
	
	public boolean OppYesMeNoSide()
	{
		checkAvailability(0, 4, 8);
		if(meAvailable==false&&oppAvailable==true)
		{
			sidePos1=0;
			sidePos2=4;
			sidePos3=8;
			return true;
		}
		
		checkAvailability(2, 4, 6);
		if(meAvailable==false&&oppAvailable==true)
		{
			sidePos1=2;
			sidePos2=4;
			sidePos3=6;
			return true;
		}
		
		for(int i=0;i<3;i++)
		{
			checkAvailability(i*3, i*3+1, i*3+2);
			if(meAvailable==false&&oppAvailable==true)
			{
				sidePos1=i*3;
				sidePos2=i*3+1;
				sidePos3=i*3+2;
				return true;
			}
			
			checkAvailability(i, i+3, i+6);
			if(meAvailable==false&&oppAvailable==true)
			{
				sidePos1=i;
				sidePos2=i+3;
				sidePos3=i+6;
				return true;
			}
		}
		
		return false;
	}
	
	public boolean MeYesOppNoSide()
	{
		checkAvailability(0, 4, 8);
		if(meAvailable==true&&oppAvailable==false)
		{
			sidePos1=0;
			sidePos2=4;
			sidePos3=8;
			return true;
		}
		
		checkAvailability(2, 4, 6);
		if(meAvailable==true&&oppAvailable==false)
		{
			sidePos1=2;
			sidePos2=4;
			sidePos3=6;
			return true;
		}
		
		for(int i=0;i<3;i++)
		{
			checkAvailability(i*3, i*3+1, i*3+2);
			if(meAvailable==true&&oppAvailable==false)
			{
				sidePos1=i*3;
				sidePos2=i*3+1;
				sidePos3=i*3+2;
				return true;
			}
			
			checkAvailability(i, i+3, i+6);
			if(meAvailable==true&&oppAvailable==false)
			{
				sidePos1=i;
				sidePos2=i+3;
				sidePos3=i+6;
				return true;
			}
		}
		
		return false;
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

			if(xTurn==false)
			{
				if(view.getId()==R.id.linear0)
				{
					if(selectedStatus[0]==false)
					{
						if(soundStatus) {
							//move=MediaPlayer.create(GameActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[0]=true;
						//linear0.setBackgroundColor(Color.parseColor("#B59445"));
						linear0.setBackgroundResource(oppPlayer);
						//txt0.setText(stateOpp);
						state[0]=stateOpp;
					//	checkResult();	
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
							//move=MediaPlayer.create(GameActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[1]=true;
						//linear1.setBackgroundColor(Color.parseColor("#B59445"));
						linear1.setBackgroundResource(oppPlayer);
						//txt1.setText(stateOpp);
						state[1]=stateOpp;
						//checkResult();	
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
							//move=MediaPlayer.create(GameActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[2]=true;
						//linear2.setBackgroundColor(Color.parseColor("#B59445"));
						linear2.setBackgroundResource(oppPlayer);
						//txt2.setText(stateOpp);
						state[2]=stateOpp;
						//checkResult();	
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
							//move=MediaPlayer.create(GameActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[3]=true;
						//linear3.setBackgroundColor(Color.parseColor("#B59445"));
						linear3.setBackgroundResource(oppPlayer);
						//txt3.setText(stateOpp);
						state[3]=stateOpp;
						//checkResult();	
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
							//move=MediaPlayer.create(GameActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[4]=true;
						//linear4.setBackgroundColor(Color.parseColor("#B59445"));
						linear4.setBackgroundResource(oppPlayer);
						//txt4.setText(stateOpp);
						state[4]=stateOpp;
						//checkResult();	
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
							//move=MediaPlayer.create(GameActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[5]=true;
						//linear5.setBackgroundColor(Color.parseColor("#B59445"));
						linear5.setBackgroundResource(oppPlayer);
						//txt5.setText(stateOpp);
						state[5]=stateOpp;
						//checkResult();	
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
							//move=MediaPlayer.create(GameActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[6]=true;
						//linear6.setBackgroundColor(Color.parseColor("#B59445"));
						linear6.setBackgroundResource(oppPlayer);
						//txt6.setText(stateOpp);
						state[6]=stateOpp;
						//checkResult();	
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
							//move=MediaPlayer.create(GameActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[7]=true;
						//linear7.setBackgroundColor(Color.parseColor("#B59445"));
						linear7.setBackgroundResource(oppPlayer);
						//txt7.setText(stateOpp);
						state[7]=stateOpp;
						//checkResult();	
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
							//move=MediaPlayer.create(GameActivity.this, R.raw.move);
							//move.start();
							spool.play(soundMoveID, volume, volume, 1, 0, 1f);
						}
						selectedStatus[8]=true;
						//linear8.setBackgroundColor(Color.parseColor("#B59445"));
						linear8.setBackgroundResource(oppPlayer);
						//txt8.setText(stateOpp);
						state[8]=stateOpp;
						//checkResult();	
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
		if(xTurn) txtGameStarted.setText("Turn of Android");
		else txtGameStarted.setText("Turn of you");
		linearGameStarted.startAnimation(scaleAnim1);
		
		TurnTask task=new TurnTask();
		task.execute();
	}
	
	public void afterTurn()
	{
	
		if(xTurn)
		{
			try
			{
				Thread.sleep(500);
			}
			catch(Exception e){}
			setAndroid();
		}
		else clickReady=true;
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
			if(decision==2)
			{
				xTurn=true;
			}
			else
			{
				xTurn=false;
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
			else if(winner.equals("Y")) {
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
			int battles=pref.getInt("battles_android", 0);
			int tied=pref.getInt("tied_android", 0);
			int wins=pref.getInt("wins_android", 0);
			
			battles=battles+1;
			if(winner.equals("drawn")) tied=tied+1;
			if(winner.equals("Y")) wins=wins+1;
			
			SharedPreferences.Editor editor=pref.edit();
			editor.putInt("battles_android", battles);
			editor.putInt("tied_android", tied);
			editor.putInt("wins_android", wins);
			editor.commit();
			
			LayoutInflater li = LayoutInflater.from(GameActivity.this);
			View promptsView = li.inflate(R.layout.exit, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
			builder.setView(promptsView);
			builder.setCancelable(false);

			final AlertDialog alertDialog = builder.create();
			WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
			lp.dimAmount = 1.4f;
			alertDialog.show();
			
			LinearLayout linearDialogYes=(LinearLayout)promptsView.findViewById(R.id.linearDialogYes);
			LinearLayout linearDialogNo=(LinearLayout)promptsView.findViewById(R.id.linearDialogNo);
			TextView txtAlert=(TextView)promptsView.findViewById(R.id.txtAlert);
			TextView txtBody=(TextView)promptsView.findViewById(R.id.txtDialogBody);
			TextView txtYes=(TextView)promptsView.findViewById(R.id.txtYes);
			TextView txtNo=(TextView)promptsView.findViewById(R.id.txtNo);
			
			String text;
			if(winner.equals("drawn")) text="Match Drawn!!!";
			else if(winner.equals("Y")) text="Congrates!!!\nYou won the match";
			else text="You Lose!!";
			txtAlert.setText("Match Result");
			txtBody.setText(text);
			txtYes.setText("Play Again");
			txtNo.setText("Exit");
			
			linearDialogNo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (soundStatus) {
						//move = MediaPlayer.create(GameActivity.this, R.raw.move);
						//move.start();
						spool.play(soundMoveID, volume, volume, 1, 0, 1f);
					}
					alertDialog.cancel();
				/*	Intent service=new Intent(GameActivity.this,BackGroundMusic.class);
					startService(service); */
					Intent in = new Intent(GameActivity.this, PlayActivity.class);
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
					finish();
				}
			});	
			
			linearDialogYes.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub	   
					if (soundStatus) {
						//move = MediaPlayer.create(GameActivity.this, R.raw.move);
						//move.start();
						spool.play(soundMoveID, volume, volume, 1, 0, 1f);
					}
					alertDialog.cancel();
				/*	Intent service=new Intent(GameActivity.this,BackGroundMusic.class);
					startService(service);  */
					Intent in=new Intent(GameActivity.this,TossActivity.class);
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
					finish();
				}
			});
			
		}
		
	}
	
	

}
