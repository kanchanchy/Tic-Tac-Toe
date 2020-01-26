package com.iglyphic.tictactoe;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class BluetoothConnectActivity extends Activity{
	
	Button btnConnect;
	TextView dialogTitle;
	ProgressBar progressBarScan;
	AlertDialog connectionDialog;
	//ProgressDialog dialog;
	
	BluetoothAdapter mBluetoothAdapter=null;
	Set<BluetoothDevice> pairedDevices;
	BluetoothDevice device=null;
	
	ArrayList<BluetoothDevice>allDevices;
	
	ArrayList<String> deviceNames;
	ArrayList<String> deviceAddresses;
	
	String deviceName="";
	String deviceAddress="";
	
	boolean regStatus=false,endStatus=false,discoveryStarted=false,normalExit, soundStatus;
	//boolean stopStatus;
	
	MyBluetoothService myService;
	BroadcastReceiver mReceiver,endReceiver;
	
	//MediaPlayer player;
	private SoundPool spool;
	private int soundID;
	float volume;

	AdRequest adRequest;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_connect);
		btnConnect=(Button)findViewById(R.id.btnConnect);
		//dialog=new ProgressDialog(BluetoothConnectActivity.this);
		setUpConnectionDialog();
		
		normalExit=false;
		//stopStatus=false;

		SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(this);
		soundStatus = sharedPrefUtil.getSoundStatus();
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		myService=new MyBluetoothService();
		myService.setFHandler(mHandler);
		
		myService.startAcceptThread();
		
		deviceNames=new ArrayList<String>();
		deviceAddresses=new ArrayList<String>();
		allDevices=new ArrayList<BluetoothDevice>();


	/*	TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String uid = tManager.getDeviceId();  */

		adRequest = new AdRequest.Builder()
				.build();

		AdView mAdView = (AdView) findViewById(R.id.adView);
		mAdView.loadAd(adRequest);

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundID = spool.load(this, R.raw.move, 1);

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		
		btnConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(soundStatus) {
					//player = MediaPlayer.create(BluetoothConnectActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				
				deviceNames.clear();
				deviceAddresses.clear();
				
				MyAdapter adapter;
				pairedDevices = mBluetoothAdapter.getBondedDevices();
				if(pairedDevices.size()>0)
				{
					 for (BluetoothDevice bdevice : pairedDevices) {
					        // Add the name and address to an array adapter to show in a ListView
					     allDevices.add(bdevice);   
						 deviceNames.add(bdevice.getName());
					        deviceAddresses.add(bdevice.getAddress());
					        }
					 
					    LayoutInflater li = LayoutInflater.from(BluetoothConnectActivity.this);
						View promptsView = li.inflate(R.layout.scan_layout, null);
						AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothConnectActivity.this);
						builder.setView(promptsView);
						builder.setCancelable(true);
						final AlertDialog alertDialog = builder.create();
						alertDialog.show();
						dialogTitle=(TextView)promptsView.findViewById(R.id.txtDialogTitle);
						progressBarScan=(ProgressBar)promptsView.findViewById(R.id.progressBarScan);
						final ListView listDialog=(ListView)promptsView.findViewById(R.id.listDialog);

						Button btnScan=(Button)promptsView.findViewById(R.id.btnScan);
						Button btnCancel=(Button)promptsView.findViewById(R.id.btnCancel);
						
						progressBarScan.setVisibility(View.INVISIBLE);
						
						adapter=new MyAdapter(BluetoothConnectActivity.this, deviceNames,deviceAddresses);
						listDialog.setAdapter(adapter);
						
						mReceiver = new BroadcastReceiver() {
						    public void onReceive(Context context, Intent intent) {
						        String action = intent.getAction();
						        // When discovery finds a device
						        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
						            // Get the BluetoothDevice object from the Intent
						            BluetoothDevice ndevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						    
						            // Add the name and address to an array adapter to show in a ListView
						          //  mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
						          String nAddress=ndevice.getAddress();
						          boolean flag=false;
						          for(int i=0;i<deviceAddresses.size();i++)
						          {
						        	  if(deviceAddresses.get(i).equals(nAddress))
						        	  {
						        		  flag=true;
						        		  break;						        		  
						        	  }
						          }
						          if(flag==false)
						          {
						        	  //pairedDevices.add(ndevice);
						        	  allDevices.add(ndevice);
						        	  deviceNames.add(ndevice.getName());
						        	  deviceAddresses.add(ndevice.getAddress());
						        	  MyAdapter adapter2=new MyAdapter(BluetoothConnectActivity.this, deviceNames,deviceAddresses);
							          listDialog.setAdapter(adapter2);
							        //  Toast.makeText(getApplicationContext(),dName, Toast.LENGTH_SHORT).show();						       							            
						          }   
						        }
						    }
						};
						
						
						
						endReceiver=new BroadcastReceiver()
						{

							@Override
							public void onReceive(Context context, Intent intent) {
								// TODO Auto-generated method stub
								String action = intent.getAction();
								if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
								{
									discoveryStarted=false;
									progressBarScan.setVisibility(View.INVISIBLE);
									dialogTitle.setText("Found devices");
									if(endStatus)
									{
										unregisterReceiver(endReceiver);
										endStatus=false;
									}
								}
							}
							
						};
						
						
						
						listDialog.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int position, long arg3) {
								// TODO Auto-generated method stub
								if(soundStatus) {
								//	player = MediaPlayer.create(BluetoothConnectActivity.this, R.raw.move);
								//	player.start();
									spool.play(soundID, volume, volume, 1, 0, 1f);
								}
								for(int i=0;i<allDevices.size();i++)
								{
									BluetoothDevice ndevice=allDevices.get(i);
									if(ndevice.getAddress().equals(deviceAddresses.get(position)))
							        {
							        	device=ndevice;
							        	break;
							        }
								}
								deviceName=device.getName();
								deviceAddress=device.getAddress();
								if(discoveryStarted)
								{
									mBluetoothAdapter.cancelDiscovery();
									if(endStatus)
									{
										unregisterReceiver(endReceiver);
										endStatus=false;
									}
								}
								if(regStatus)
								{
									regStatus=false;
									unregisterReceiver(mReceiver);
								}
								alertDialog.cancel();  
								
							//	Toast.makeText(getApplicationContext(),"Connecting with "+deviceName, Toast.LENGTH_LONG).show();
								myService.stopAcceptThread();
								myService.startConnectThread(device);
								
								//dialog.setMessage("Connecting....");
								//dialog.show();
								connectionDialog.show();
							}
						});
						
						
						btnScan.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(soundStatus) {
									//player = MediaPlayer.create(BluetoothConnectActivity.this, R.raw.move);
									//player.start();
									spool.play(soundID, volume, volume, 1, 0, 1f);
								}
								if(regStatus==false)
								{
									IntentFilter disFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
									registerReceiver(mReceiver,disFilter);
									regStatus=true;
								}
								boolean discoveryStarted=mBluetoothAdapter.startDiscovery();
								if(discoveryStarted)
								{
									progressBarScan.setVisibility(View.VISIBLE);
									IntentFilter endFilter=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
									registerReceiver(endReceiver,endFilter);
									endStatus=true;
								}
								else
								{
									unregisterReceiver(mReceiver);
									regStatus=false;
								}
							}
						});
						
						
						btnCancel.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(soundStatus) {
									//player = MediaPlayer.create(BluetoothConnectActivity.this, R.raw.move);
									//player.start();
									spool.play(soundID, volume, volume, 1, 0, 1f);
								}
								if(discoveryStarted)
								{
									 mBluetoothAdapter.cancelDiscovery();
									 if(endStatus)
										{
											unregisterReceiver(endReceiver);
											endStatus=false;
										}
								}
								if(regStatus)
								{
									unregisterReceiver(mReceiver);
									regStatus=false;
								}
								alertDialog.cancel();
							}
						});
						
						
				}
				else {
					LayoutInflater li = LayoutInflater.from(BluetoothConnectActivity.this);
					View promptsView = li.inflate(R.layout.toss_dialog1, null);
					AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothConnectActivity.this);
					builder.setView(promptsView);
					builder.setCancelable(true);
					final AlertDialog alertDialog = builder.create();
					alertDialog.show();
					TextView txtAlert=(TextView)promptsView.findViewById(R.id.txtAlert);
					TextView txtDialogOk=(TextView)promptsView.findViewById(R.id.txtPlay);
					TextView txtDialogBody=(TextView)promptsView.findViewById(R.id.txtDialogBody);
					//Button btnOk=(Button)promptsView.findViewById(R.id.btnOk);
					LinearLayout linearDialogPlay=(LinearLayout)promptsView.findViewById(R.id.linearDialogPlay);
					txtAlert.setText("Alert!");
					txtDialogOk.setText("Ok");
					String text="No device found. Please check your bluetooth connectivity..";
					txtDialogBody.setText(text);

					linearDialogPlay.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if(soundStatus) {
								//player=MediaPlayer.create(BluetoothConnectActivity.this, R.raw.move);
								//player.start();
								spool.play(soundID, volume, volume, 1, 0, 1f);
							}
							alertDialog.cancel();
						}
					});
				}
				
				
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
			Intent service=new Intent(BluetoothConnectActivity.this,BackGroundMusic.class);
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
			Intent service=new Intent(BluetoothConnectActivity.this,BackGroundMusic.class);
			startService(service);
		}  */
	}
	
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(MyBluetoothService.connectedStatus) myService.stopConnectedThread();
		if(MyBluetoothService.connectStatus) myService.stopConnectThread();
		if(MyBluetoothService.acceptStatus) myService.stopAcceptThread();
		
		Intent in=new Intent(BluetoothConnectActivity.this,PlayActivity.class);
	//	stopStatus=true;
		normalExit=true;
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
		finish();
	}
	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(discoveryStarted)
		{
			 mBluetoothAdapter.cancelDiscovery();
			 if(endStatus)
				{
					unregisterReceiver(endReceiver);
					endStatus=false;
				}
		}
		if(regStatus)
		{
			unregisterReceiver(mReceiver);
			regStatus=false;
		}
		if(!normalExit)
		{
			if(MyBluetoothService.connectedStatus) myService.stopConnectedThread();
			if(MyBluetoothService.connectStatus) myService.stopConnectThread();
			if(MyBluetoothService.acceptStatus) myService.stopAcceptThread();
		}
	}
	
	
	private void setUpConnectionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.progress_dialog, null);
		TextView txtDialogBody = (TextView)view.findViewById(R.id.txtDialogBody);
		txtDialogBody.setText("Connecting...");
		builder.setView(view);
		connectionDialog = builder.create();
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
              //  Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_LONG).show();
              //  mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                break;
            case MyBluetoothService.MESSAGE_DEVICE_NAME:
                break;
            case MyBluetoothService.MESSAGE_TOAST:
               // Toast.makeText(getApplicationContext(), msg.getData().getString(MyBluetoothService.TOAST),Toast.LENGTH_SHORT).show();
                break;
            case MyBluetoothService.MESSAGE_CONNECTED:
            	//dialog.cancel();
				connectionDialog.dismiss();
            	Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                Intent in=new Intent(BluetoothConnectActivity.this,TossBluetoothActivity.class);
                normalExit=true;
            //    stopStatus=true;
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
				finish();
            	break;
            case MyBluetoothService.MESSAGE_CONNECTION_FAILED:
            	//dialog.cancel();
				connectionDialog.dismiss();
            	//Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_LONG).show();
				showFailureDialog();
                break;
            }
        }
    };




	private void showFailureDialog() {
		LayoutInflater li = LayoutInflater.from(BluetoothConnectActivity.this);
		View promptsView = li.inflate(R.layout.toss_dialog1, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothConnectActivity.this);
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
		String text="Sorry! Connecting " + deviceName + " was not possible.";
		txtDialogBody.setText(text);

		linearDialogPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(soundStatus) {
					//player=MediaPlayer.create(BluetoothConnectActivity.this, R.raw.move);
					//player.start();
					spool.play(soundID, volume, volume, 1, 0, 1f);
				}
				alertDialog.cancel();
			}
		});
	}




}
