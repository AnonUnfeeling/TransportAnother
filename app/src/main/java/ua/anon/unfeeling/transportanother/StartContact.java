package ua.anon.unfeeling.transportanother;

import android.app.Activity;
import android.content.Context;
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

    private int target;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_contact);

        target = getIntent().getIntExtra("target",-1);

        TextView statusDriver = (TextView) findViewById(R.id.driverStatus);
        statusDriver.setText(getIntent().getStringExtra("statusContact"));

        ImageView signal = (ImageView) findViewById(R.id.signal);

        if (target == 1) {
            signal.setBackgroundColor(Color.parseColor("#fffdfc"));
        }else if(target ==2){
            signal.setBackgroundColor(Color.parseColor("#4b95ff"));
        }else if(target ==3){
            signal.setBackgroundColor(Color.parseColor("#fde06d"));
        }else if(target ==4){
            signal.setBackgroundColor(Color.parseColor("#73e97e"));
        }else if(target ==5){
            signal.setBackgroundColor(Color.parseColor("#ff5564"));
        }

        ImageButton closeContact = (ImageButton) findViewById(R.id.closeContact);
        closeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);

        boolean isExit = getIntent().getBooleanExtra("isExit", false);
        if(isExit){
            finish();
        }else {
            if (getSound() && getVibr()) {
                for (int i = 0; i < 2; ) {
                    if (!mp.isPlaying()) {
                        vibrator.vibrate(1000);
                        mp.start();
                        i++;
                    }
                }
            } else if (getSound()) {
                for (int i = 0; i < 2; i++) {
                    if (!mp.isPlaying()) {
                        mp.start();
                    }
                }
            } else if (getVibr()) {
                for (int i = 0; i < 2; i++) {
                    vibrator.vibrate(1000);
                }
            }
        }
    }

    private boolean getVibr(){
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        return sharedPreferences.getBoolean("vibration",true);
    }

    private boolean getSound(){
        SharedPreferences sharedPreferences = getSharedPreferences("Settings",MODE_PRIVATE);
        return sharedPreferences.getBoolean("sound",true);
    }
}
