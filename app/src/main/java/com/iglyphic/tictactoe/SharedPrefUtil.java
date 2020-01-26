package com.iglyphic.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telecom.Call;

public class SharedPrefUtil {

	Context mContext;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor spEditor;

    private final String SOUND_STATUS = "sound_status";


	public SharedPrefUtil(Context mContext)
	{
		super();
		this.mContext = mContext;
		sharedPreferences = this.mContext.getSharedPreferences("tic_tac_toe_sound",0);
	}


    public void setSoundStatus (boolean soundStatus) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(SOUND_STATUS, soundStatus);
        spEditor.commit();

    }

    public boolean getSoundStatus () {
        return sharedPreferences.getBoolean(SOUND_STATUS, true);

    }


}