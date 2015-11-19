package ua.anon.unfeeling.transportanother;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;

import com.example.hjk.transportanother.R;

public class Sound extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);

        for (int i = 0; i < 2; ) {
            if (!mp.isPlaying()) {
                vibrator.vibrate(1000);
                mp.start();
                i++;
            }
        }

        finish();
    }
}
