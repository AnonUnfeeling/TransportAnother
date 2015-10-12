package com.example.hjk.testing;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Settings extends Activity implements View.OnClickListener{
    ImageButton back,sound,vibration;
    boolean[] isCheck = new boolean[3];
    WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    String[] status;
    TextView youStatus, ratingForPasage,ratingForDriver,statist;

    @Override
    protected void onResume() {
        super.onResume();

        isCheck = loadSettings();
        if(isCheck[0]){
            sound.setImageResource(R.drawable.sound_activ);
        }
        if(isCheck[1]){
            vibration.setImageResource(R.drawable.vibr_active);
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

        youStatus = (TextView) findViewById(R.id.youStatus);
        statist = (TextView) findViewById(R.id.statist);
        ratingForPasage = (TextView) findViewById(R.id.ratingForPasag);
        ratingForDriver = (TextView) findViewById(R.id.ratingForDriver);

        youStatus.append("\t" + status[4]);

        statist.append(String.valueOf(Integer.parseInt(status[0] + status[1] + status[2] + status[3]) / 4));

        ratingForPasage.append("\t" + status[0] + "\n" + status[1]);

        ratingForDriver.append("\t" + status[2] + "\n" + status[3]);


        back = (ImageButton) findViewById(R.id.close);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.this.finish();
                startActivity(new Intent(Settings.this, MainActivity.class));
                saveSettings(isCheck);
            }
        });

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
                if (isCheck[0] == false) {
                    sound.setImageResource(R.drawable.sound_activ);
                    isCheck[0] = true;
                } else {
                    sound.setImageResource(R.drawable.sound_passive);
                    isCheck[0] = false;
                }
                break;
            case R.id.vibration:
                if (isCheck[1] == false) {
                    vibration.setImageResource(R.drawable.vibr_active);
                    isCheck[1] = true;
                } else {
                    vibration.setImageResource(R.drawable.vibr_passive);
                    isCheck[1] = false;
                }
                break;
        }
    }

    public void saveSettings(boolean[] isCheck){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("sound", isCheck[0]);
        editor.putBoolean("vibration", isCheck[1]);
        editor.commit();
    }

    public boolean[] loadSettings(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean[] isCheck = new boolean[30];
        isCheck[0] = sharedPreferences.getBoolean("sound",false);
        isCheck[1] = sharedPreferences.getBoolean("vibration",false);
        return isCheck;
    }
}
