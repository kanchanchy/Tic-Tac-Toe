package com.iglyphic.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by user on 3/10/2016.
 */
public class ScoreActivity extends Activity {

    TextView txtTotalAndroid, txtWinAndroid, txtLoseAndroid, txtTieAndroid, txtTotalBluetooth, txtWinBluetooth, txtLoseBluetooth, txtTieBluetooth;

    int battleAndroid, winAndroid, loseAndroid, tieAndroid, battleBluetooth, winBluetooth, loseBluetooth, tieBluetooth;

    InterstitialAd interstitial;
    AdRequest adRequest;
    boolean isActivityDestroyed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_layout);

        txtTotalAndroid = (TextView) findViewById(R.id.txtTotalAndroid);
        txtWinAndroid = (TextView) findViewById(R.id.txtWinAndroid);
        txtLoseAndroid = (TextView) findViewById(R.id.txtLoseAndroid);
        txtTieAndroid = (TextView) findViewById(R.id.txtTieAndroid);
        txtTotalBluetooth = (TextView) findViewById(R.id.txtTotalBluetooth);
        txtWinBluetooth = (TextView) findViewById(R.id.txtWinBluetooth);
        txtLoseBluetooth = (TextView) findViewById(R.id.txtLoseBluetooth);
        txtTieBluetooth = (TextView) findViewById(R.id.txtTieBluetooth);

        isActivityDestroyed = false;
        adRequest = new AdRequest.Builder()
                .build();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(adRequest);

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(R.string.tic_tac_toe_interstitial));
        interstitial.loadAd(adRequest);

        SharedPreferences pref=getSharedPreferences("tic_tac_toe_result",0);

        battleAndroid=pref.getInt("battles_android", 0);
        tieAndroid=pref.getInt("tied_android", 0);
        winAndroid=pref.getInt("wins_android", 0);
        loseAndroid=battleAndroid-(tieAndroid+winAndroid);

        battleBluetooth=pref.getInt("battles_bluetooth", 0);
        tieBluetooth=pref.getInt("tied_bluetooth", 0);
        winBluetooth=pref.getInt("wins_bluetooth", 0);
        loseBluetooth=battleBluetooth-(tieBluetooth+winBluetooth);

        txtTotalAndroid.setText("Total Played: "+battleAndroid+" times");
        txtWinAndroid.setText("Won: "+winAndroid+" times");
        txtLoseAndroid.setText("Lose: "+loseAndroid+" times");
        txtTieAndroid.setText("Tied: "+tieAndroid+" times");

        txtTotalBluetooth.setText("Total Played: "+battleBluetooth+" times");
        txtWinBluetooth.setText("Won: "+winBluetooth+" times");
        txtLoseBluetooth.setText("Lose: "+loseBluetooth+" times");
        txtTieBluetooth.setText("Tied: " + tieBluetooth + " times");

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

    }


    public void displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityDestroyed = true;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent in=new Intent(ScoreActivity.this,PlayActivity.class);
        startActivity(in);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
        finish();
    }


}
