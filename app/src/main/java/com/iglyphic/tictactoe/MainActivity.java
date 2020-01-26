package com.iglyphic.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity {
	
	boolean stopStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	/*	stopStatus=false;
		
		Intent service=new Intent(MainActivity.this,BackGroundMusic.class);
		startService(service);  */
		
		SplashTask task=new SplashTask();
		task.execute();
		
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	/*	if(stopStatus==false)
		{
			stopStatus=true;
			Intent service=new Intent(MainActivity.this,BackGroundMusic.class);
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
			Intent service=new Intent(MainActivity.this,BackGroundMusic.class);
			startService(service);
		}   */
	}  
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
	}
	
	
	class SplashTask extends AsyncTask<String, String, String>
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
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Intent in=new Intent(MainActivity.this,PlayActivity.class);
		//	stopStatus=true;
			startActivity(in);
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
			finish();
		}
		
	}
	

}
