package ua.anon.unfeeling.transportanother;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hjk.transportanother.R;

public class StartContact extends Activity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_contact);

        TextView statusDriver = (TextView) findViewById(R.id.driverStatus);
        statusDriver.setText(getIntent().getStringExtra("statusContact"));

        ImageButton closeContact = (ImageButton) findViewById(R.id.closeContact);
        closeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new WorkWithDataBase().contactEnd(getIntent().getIntExtra("id",-1),-1);
                    }
                }).start();
                finish();
                startActivity(new Intent(StartContact.this,Info.class));
            }
        });

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);

        ImageView signal = (ImageView) findViewById(R.id.signal);

        boolean isExit = getIntent().getBooleanExtra("isExit", false);
        if(isExit){
            finish();
        }

        int target = getIntent().getIntExtra("target", -1);

        if(getSound()&&getVibr()){
            for (int i = 0; i < 5; ) {
                if(!mp.isPlaying()){
                    vibrator.vibrate(1000);
                    mp.start();
                    i++;
                }
            }
        }else if(getSound()){
            for (int i = 0; i < 5; i++) {
                if(!mp.isPlaying()){
                    mp.start();
                }
            }
        }else if(getVibr()){
            for (int i = 0; i < 5; i++) {
                vibrator.vibrate(1000);
            }
        }

        if (target == 1) {
            signal.setBackgroundColor(Color.parseColor("#fffdfc"));
        }else if(target ==2){
            signal.setBackgroundColor(Color.parseColor("#4b95ff"));
        }else if(target ==3){
            signal.setBackgroundColor(Color.parseColor("#fde06d"));
        }else if(target ==4){
            signal.setBackgroundColor(Color.parseColor("#73e97e "));
        }else if(target ==5){
            signal.setBackgroundColor(Color.parseColor("#ff5564"));
        }
    }

    private boolean getVibr(){
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        return sharedPreferences.getBoolean("vibration",false);
    }

    private boolean getSound(){
        SharedPreferences sharedPreferences = getSharedPreferences("Settings",MODE_PRIVATE);
        return sharedPreferences.getBoolean("sound",false);
    }
}
