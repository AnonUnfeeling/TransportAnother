package com.example.hjk.testing;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

public class StartContact extends Activity {
    private int target = 0;
    private ImageView signal;
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_contact);

        signal = (ImageView) findViewById(R.id.signal);

        isExit = getIntent().getBooleanExtra("isExit",false);
        if(isExit){
            finish();
        }

        target = getIntent().getIntExtra("target",-1);

        if (target == 1) {
            signal.setBackgroundColor(Color.parseColor("#fffdfc"));
        }else if(target==2){
            signal.setBackgroundColor(Color.parseColor("#4b95ff"));
        }else if(target==3){
            signal.setBackgroundColor(Color.parseColor("#fde06d"));
        }else if(target==4){
            signal.setBackgroundColor(Color.parseColor("#73e97e "));
        }else if(target==5){
            signal.setBackgroundColor(Color.parseColor("#ff5564"));
        }
    }
}
