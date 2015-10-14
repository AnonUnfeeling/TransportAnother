package com.example.hjk.testing;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class Rating extends Activity{
    private int id=0,width = 0 ,height=0;
    private ImageButton back,sos,positiv,negativ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_layout);

        id = getIntent().getIntExtra("id",-1);

        back = (ImageButton) findViewById(R.id.back);

        sos = (ImageButton) findViewById(R.id.sos);
        negativ = (ImageButton) findViewById(R.id.negativ);
        positiv = (ImageButton) findViewById(R.id.positiv);

        positiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRating(id,1);
                negativ.setClickable(false);
            }
        });


        negativ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRating(id,-1);
                positiv.setClickable(false);
            }
        });

        mastabation();
    }

    public void setRating (final int id, final int ratig){
        new Thread(new Runnable() {
            @Override
            public void run() {
                new WorkWithDataBase().contactEnd(id,ratig);
            }
        }).start();
    }

    public void mastabation(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        if(width>=320&&height>=480) {
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.3f + 0.5f));
            sos.setLayoutParams(params1);
            back.setLayoutParams(params1);

            LinearLayout info = (LinearLayout) findViewById(R.id.info);

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.13f + 0.5f));
            info.setLayoutParams(params2);

            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.26f + 0.5f));
            negativ.setLayoutParams(params3);
            positiv.setLayoutParams(params3);

            LinearLayout warrning = (LinearLayout) findViewById(R.id.warrning);

            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.31 + 0.5f));
            warrning.setLayoutParams(params4);
        }
    }
}
