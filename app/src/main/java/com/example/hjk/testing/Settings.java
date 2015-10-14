package com.example.hjk.testing;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Settings extends Activity implements View.OnClickListener{
    private ImageButton sound;
    private ImageButton vibration;
    private boolean[] isCheck = new boolean[3];
    private final WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    private String[] status;
    private int width=0,height=0;
    private ImageButton back;
    private ImageView title;

    @Override
    protected void onResume() {
        super.onResume();

        mastabation();

        isCheck = loadSettings();
        if(isCheck[0]){
            sound.setBackgroundResource(R.drawable.sound_activ);
        }
        if(isCheck[1]){
            vibration.setBackgroundResource(R.drawable.vibr_activ);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                status = workWithDataBase.getStatus(getIntent().getIntExtra("id",-1));
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TextView youStatus = (TextView) findViewById(R.id.youStatus);
        TextView statist = (TextView) findViewById(R.id.statist);
        TextView ratingForPasage = (TextView) findViewById(R.id.ratingForPasag);
        TextView ratingForDriver = (TextView) findViewById(R.id.ratingForDriver);
        TextView countPasag = (TextView) findViewById(R.id.coutPasag);
        TextView countDriver = (TextView) findViewById(R.id.countDriver);

        youStatus.append(status[4]);

        statist.append(String.valueOf(Integer.parseInt(status[0] + status[1] + status[2] + status[3]) / 4));

        ratingForPasage.append(status[0]);
        countPasag.append(status[1]);

        ratingForDriver.append(status[2]);
        countDriver.append(status[3]);

        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.this.finish();
                startActivity(new Intent(Settings.this, MainActivity.class));
                saveSettings(isCheck);
            }
        });

        title = (ImageView) findViewById(R.id.title);

        sound = (ImageButton) findViewById(R.id.sound);
        sound.setOnClickListener(this);

        vibration = (ImageButton) findViewById(R.id.vibration);
        vibration.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveSettings(isCheck);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sound:
                if (!isCheck[0]) {
                    sound.setBackgroundResource(R.drawable.sound_activ);
                    isCheck[0] = true;
                } else {
                    sound.setBackgroundResource(R.drawable.sound_passive);
                    isCheck[0] = false;
                }
                break;
            case R.id.vibration:
                if (!isCheck[1]) {
                    vibration.setBackgroundResource(R.drawable.vibr_activ);
                    isCheck[1] = true;
                } else {
                    vibration.setBackgroundResource(R.drawable.vibr_passive);
                    isCheck[1] = false;
                }
                break;
        }
    }

    private void saveSettings(boolean[] isCheck){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("sound", isCheck[0]);
        editor.putBoolean("vibration", isCheck[1]);
        editor.apply();
    }

    private boolean[] loadSettings(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean[] isCheck = new boolean[30];
        isCheck[0] = sharedPreferences.getBoolean("sound",false);
        isCheck[1] = sharedPreferences.getBoolean("vibration",false);
        return isCheck;
    }

    public void mastabation(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        if(width>=320&&height>=480) {
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) (height * 0.3f + 0.5f));
            back.setLayoutParams(params1);
            title.setLayoutParams(params1);

            RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.status);
            RelativeLayout pasageLayout = (RelativeLayout) findViewById(R.id.passage);
            RelativeLayout driverLayout = (RelativeLayout) findViewById(R.id.driver);

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.13f + 0.5f));
            statusLayout.setLayoutParams(params2);
            pasageLayout.setLayoutParams(params2);
            driverLayout.setLayoutParams(params2);

            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.13f + 0.5f));
            sound.setLayoutParams(params3);
            vibration.setLayoutParams(params3);
        }
    }
}
