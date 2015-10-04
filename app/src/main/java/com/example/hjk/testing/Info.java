package com.example.hjk.testing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Info extends Activity implements View.OnClickListener{

    TextView distation;
    ImageButton back;
    WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);

        distation = (TextView) findViewById(R.id.testDis);

        id = getIntent().getIntExtra("id", 0);

        Double[] coo = workWithDataBase.getCoordinate(id, 0);

        Double[] coordinate = workWithDataBase.search(id,coo[0],coo[1],0,25,"0");

        distation.append(gps2m(coo[0], coo[1], coordinate[0], coordinate[1])+"м");

        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    private int gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (180/3.14169);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
        double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
        double t3 = Math.sin(a1)* Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return (int) (6366000*tt);
    }

    @Override
    public void onClick(View v) {
        finish();
        new Thread(new Runnable() {
            @Override
            public void run() {
                workWithDataBase.onlineEnd(id);
            }
        }).start();
        startActivity(new Intent(this, MainActivity.class));
    }
}
