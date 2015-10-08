package com.example.hjk.testing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class Info extends Activity implements View.OnClickListener{

    TextView distation,centr_cout,auto_count,ubil_cout,bass_count,north_cout;
    ImageButton back,sos;
    WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    int id,driver;
    BroadcastReceiver service;
    ProgressDialog progressDialog;
    boolean isCheckSos=false;
    boolean isContact=false;
    final double[] coo = new double[2];
    final double[] coordinate = new double[2];

    double[] cooSos = new double[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);

        cooSos[0] = 0;
        cooSos[1] = 0;

        id = getIntent().getIntExtra("id", -1);
        driver = getIntent().getIntExtra("driver", -1);

        if(driver==1) {
            progressDialog = ProgressDialog.show(this, "", "Пошук попутника");
        }else if(driver==0){
            progressDialog = ProgressDialog.show(this, "", "Пошук водія");
        }

        distation = (TextView) findViewById(R.id.testDis);

        centr_cout = (TextView) findViewById(R.id.centr_count);

        auto_count = (TextView) findViewById(R.id.auto_count);

        ubil_cout = (TextView) findViewById(R.id.ubil_count);

        north_cout = (TextView) findViewById(R.id.north_count);

        bass_count = (TextView) findViewById(R.id.bass_cout);

        workWithService();

        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(this);

        sos = (ImageButton) findViewById(R.id.sos);
        sos.setOnClickListener(this);
    }

    public double[] workWithService() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("Info");

        service = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("Info")) {

                    final int ping = (int) intent.getDoubleExtra("ping", 0);
                    System.out.println(ping);

                    coo[0] = intent.getDoubleExtra("mycoo1", 0.0);
                    coo[1] = intent.getDoubleExtra("mycoo2", 0.0);

                    coordinate[0] = intent.getDoubleExtra("coo1", 0.0);
                    coordinate[1] = intent.getDoubleExtra("coo2", 0.0);

                    centr_cout.setText(String.valueOf(intent.getDoubleExtra("centr", 0)));
                    ubil_cout.setText(String.valueOf(intent.getDoubleExtra("auto", 0)));
                    auto_count.setText(String.valueOf(intent.getDoubleExtra("north", 0)));
                    north_cout.setText(String.valueOf(intent.getDoubleExtra("ubil", 0)));
                    bass_count.setText(String.valueOf(intent.getDoubleExtra("bass", 0)));

                    distation.setText("");

                    progressDialog.dismiss();

                    final int distantn = gps2m(coordinate[0], coordinate[1], coo[0], coo[1]);

                    distation.append(distantn + "м");

                    if (distantn <= 50) {
                        System.out.println("contact is good");
                        if (!isContact)
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    int dist = 0;
                                    for (int i = 0; i < 1; i++) {
                                        double[] pingXY = workWithDataBase.getCoordinate(id, ping, driver, coo[0], coo[1]);
                                        dist = gps2m(pingXY[0], pingXY[1], coo[0], coo[1]);
                                        try {
                                            TimeUnit.MINUTES.sleep(1);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (distantn- dist>=50||distantn-dist<=50) {
                                        workWithDataBase.contact(id, ping, coo[0], coo[1], driver);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                              double[] xy =  workWithDataBase.contactSet(id,coo[0],coo[1]);

                                                if(gps2m(xy[0],xy[1],coo[0],coo[1])>50){
                                                    //contactEnd pyki
                                                }
                                            }
                                        }).start();
                                    }
                                }
                            }).start();
                        isContact = true;
                    }
                }
            }
        };

        registerReceiver(service, filter);
        startService(new Intent(this, TransportAnother.class)
                .putExtra("id", getIntent().getIntExtra("id", -1))
                .putExtra("driver", getIntent().getIntExtra("driver", -1))
                .putExtra("target", getIntent().getIntExtra("target", -1)));

        return coo;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(service!= null){unregisterReceiver(service);}
        stopService(new Intent(this, TransportAnother.class));
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

        return (int) (6366000 * tt);
    }

    public void close(){
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", false);
        sendBroadcast(intent);

        stopService(new Intent(this, TransportAnother.class));

        if (id != -1) {
            workWithDataBase.onlineEnd(id);
        }
        finish();

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        close();

        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", false);
                sendBroadcast(intent);

                stopService(new Intent(this, TransportAnother.class));

                if (id != -1) {
                    workWithDataBase.onlineEnd(id);
                }
                finish();

                startActivity(new Intent(this, MainActivity.class));

                break;
            case R.id.sos:
                if(!isCheckSos) {
                    isCheckSos=true;
                    sos.setImageResource(R.drawable.sos_active);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 100; i++) {
                                if(gps2m(coo[0],coo[1],cooSos[0],cooSos[1])==200) {

                                    workWithDataBase.sos(id, 0, coo[0], coo[1]);

                                    cooSos[0] = coo[0];
                                    cooSos[1] = coo[1];

                                    try {
                                        TimeUnit.MINUTES.sleep(21);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                    }).start();
                }else {
                    isCheckSos=false;
                    sos.setImageResource(R.drawable.sos_passiv);
                }
                break;
        }
    }
}
