package ua.anon.unfeeling.transportanother;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.hjk.transportanother.R;

@SuppressLint("Registered")
public class Rating extends Activity implements View.OnClickListener {
    private int id=0;
    private ImageButton back,sos,positiv,negativ;
    private boolean isCheckSos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_layout);

        id = getIntent().getIntExtra("id",-1);

        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(this);
        sos = (ImageButton) findViewById(R.id.sos);
        sos.setOnClickListener(this);
        negativ = (ImageButton) findViewById(R.id.negativ);
        negativ.setOnClickListener(this);
        positiv = (ImageButton) findViewById(R.id.positiv);
        positiv.setOnClickListener(this);

        mastabation();
    }

    private void setRating(final int id, final int ratig){
        new Thread(new Runnable() {
            @Override
            public void run() {
                new WorkWithDataBase().contactEnd(id,ratig);
            }
        }).start();
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
            sos.setLayoutParams(params1);
            back.setLayoutParams(params1);

            LinearLayout info = (LinearLayout) findViewById(R.id.info);

            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.13f + 0.5f));
            params2.addRule(RelativeLayout.BELOW, R.id.linearLayout2);
            info.setLayoutParams(params2);

            LinearLayout golos = (LinearLayout) findViewById(R.id.golos);

            RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.26f + 0.5f));
            params5.addRule(RelativeLayout.BELOW,R.id.info);
            golos.setLayoutParams(params5);

            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.26f + 0.5f));
            negativ.setLayoutParams(params3);
            positiv.setLayoutParams(params3);

            LinearLayout warrning = (LinearLayout) findViewById(R.id.warrning);

            RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.31 + 0.5f));
            params4.addRule(RelativeLayout.BELOW,R.id.golos);
            warrning.setLayoutParams(params4);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:

                finish();

                startActivity(new Intent(this, MainActivity.class));

                break;
            case R.id.sos:
                if(!isCheckSos) {
                    isCheckSos=true;
                    sos.setBackgroundResource(R.drawable.sos_active);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                                new WorkWithDataBase().sos(id, 0, 0, 0);
                        }
                    }).start();
                }else {
                    isCheckSos=false;
                    sos.setBackgroundResource(R.drawable.sos_passiv);
                }
                break;
            case R.id.positiv:
                LinearLayout pos_layout = (LinearLayout) findViewById(R.id.positiv_layout);
                pos_layout.setBackgroundColor(Color.parseColor("#2E313E"));
                setRating(id, 1);
                positiv.setClickable(false);
                negativ.setClickable(false);

                break;
            case R.id.negativ:
                LinearLayout neg_layout = (LinearLayout) findViewById(R.id.negativ_layout);
                neg_layout.setBackgroundColor(Color.parseColor("#2E313E"));
                setRating(id, -1);
                positiv.setClickable(false);
                negativ.setClickable(false);

                break;
        }
    }
}
