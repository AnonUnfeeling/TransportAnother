package ua.anon.unfeeling.transportanother;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hjk.transportanother.R;

public class Settings extends Activity implements View.OnClickListener{
    private ImageButton sound;
    private ImageButton vibration;
    private boolean[] isCheck = new boolean[3];
    private final WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    private String[] status;
    private ImageButton back;
    private int showAuthor = 0;
    private ImageView title;
    private final Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            status = workWithDataBase.getStatus(getIntent().getIntExtra("id",-1));
        }
    });

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

        TextView youStatus = (TextView) findViewById(R.id.youStatus);
        TextView statist = (TextView) findViewById(R.id.statist);
        TextView ratingForPasage = (TextView) findViewById(R.id.ratingForPasag);
        TextView ratingForDriver = (TextView) findViewById(R.id.ratingForDriver);
        TextView countPasag = (TextView) findViewById(R.id.coutPasag);
        TextView countDriver = (TextView) findViewById(R.id.countDriver);

        ProgressDialog progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.loadSettings));

        thread.start();

        try {
            thread.join();
            progressDialog.dismiss();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(status[4]!=null) {
            youStatus.append(status[4]);
        }

        if(Integer.parseInt(status[0])!=0) {
            statist.append(String.valueOf(((Integer.parseInt(status[0]) / Integer.parseInt(status[1]))
                    + (Integer.parseInt(status[2]) / Integer.parseInt(status[3]))) / 2));
        }else {
            statist.append("0");
        }

        ratingForPasage.append(status[0]);
        countPasag.append(status[1]);

        ratingForDriver.append(status[2]);
        countDriver.append(status[3]);

        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout titleLayout = (LinearLayout) findViewById(R.id.title_layout);
                titleLayout.setBackgroundColor(Color.parseColor("#52596B"));
                Settings.this.finish();
                startActivity(new Intent(Settings.this, MainActivity.class));
                saveSettings(isCheck);
            }
        });

        title = (ImageView) findViewById(R.id.title);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showAuthor==5){
                    Toast.makeText(getApplicationContext(),"Create by AnonUnfeeling",Toast.LENGTH_LONG).show();
                }else {
                    showAuthor++;
                }
            }
        });

        sound = (ImageButton) findViewById(R.id.sound);
        sound.setOnClickListener(this);

        vibration = (ImageButton) findViewById(R.id.vibration);
        vibration.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {

        if(thread.isAlive()) {
            thread.interrupt();
        }
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        saveSettings(isCheck);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);

        switch (v.getId()) {
            case R.id.sound:
                if (!isCheck[0]) {
                    sound.setBackgroundResource(R.drawable.sound_activ);
                    isCheck[0] = true;
                    if(!mp.isPlaying()){
                        mp.start();
                    }
                } else {
                    sound.setBackgroundResource(R.drawable.sound_passive);
                    isCheck[0] = false;
                }
                break;
            case R.id.vibration:
                if (!isCheck[1]) {
                    vibration.setBackgroundResource(R.drawable.vibr_activ);
                    isCheck[1] = true;
                    vibrator.vibrate(1000);
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
        isCheck[0] = sharedPreferences.getBoolean("sound",true);
        isCheck[1] = sharedPreferences.getBoolean("vibration",true);
        return isCheck;
    }

    private void mastabation(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        if(width >=320&& height >=480) {
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
