package com.example.hjk.testing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private final WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    private ImageButton menu;
    private ImageButton pedestrian;
    private ImageButton driver;
    private CheckBox auto;
    private CheckBox north;
    private CheckBox anniversary;
    private CheckBox centr;
    private CheckBox angleBass;
    private String target="0";
    private int id=-1;
    private int driv;
    private int[] data;
    private boolean flagForLoginProcedure = false;
    private ProgressDialog progressDialog;
    private final Thread thread =  new Thread(new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                if (i == 4) {
                    if(target.toCharArray().length>0) {
                        counting(Integer.parseInt(target));
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    protected void onStart() {
        super.onStart();

        if (checkAccess()) {
            if(!flagForLoginProcedure) {

               Thread startThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        data = workWithDataBase.setNumberPhone(getNumberPhone());
                    }
                });

                startThread.start();

                try {
                    startThread.join() ;
                        id = data[0];
                        driv = data[1];

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                flagForLoginProcedure = true;
            }

            if(driv==0){
                pedestrian.setBackgroundColor(Color.parseColor("#2E313E"));
                pedestrian.setImageResource(R.drawable.pedestrian_activ);
                driver.setImageResource(R.drawable.driver_passiv);
            }else if(driv==1){
                driver.setBackgroundColor(Color.parseColor("#2E313E"));
                driver.setImageResource(R.drawable.driver_activ);
                pedestrian.setImageResource(R.drawable.pedestrian_passiv);
            }


        }else {
            accessError();
        }

        @SuppressWarnings("deprecation") String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(checkAccess()) {
            stopService(new Intent(MainActivity.this, TransportAnother.class));
            if (id != -1) {
                workWithDataBase.onlineEnd(id);
            }

            if(thread.isAlive()){
                thread.interrupt();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auto = (CheckBox) findViewById(R.id.auto);
        auto.setOnTouchListener(this);

        north = (CheckBox) findViewById(R.id.north);
        north.setOnTouchListener(this);

        anniversary = (CheckBox) findViewById(R.id.anniversary);
        anniversary.setOnTouchListener(this);

        centr = (CheckBox) findViewById(R.id.centr);
        centr.setOnTouchListener(this);

        angleBass = (CheckBox) findViewById(R.id.angleBass);
        angleBass.setOnTouchListener(this);

        menu = (ImageButton) findViewById(R.id.menu);
        menu.setOnClickListener(this);

        pedestrian = (ImageButton) findViewById(R.id.pedestrian);
        pedestrian.setOnClickListener(this);

        driver = (ImageButton) findViewById(R.id.driver);
        driver.setOnClickListener(this);
    }

    private Long getNumberPhone(){
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return Long.valueOf(telephonyManager.getDeviceId());
    }

    private void accessError(){
        Toast.makeText(getApplicationContext(),getResources().getString(R.string.accessErorr),Toast.LENGTH_LONG).show();
    }

    private boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            @SuppressWarnings("deprecation") NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }

        return false;
    }

    private boolean checkAccess(){
        @SuppressWarnings("deprecation") String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return isConnectingToInternet() && provider.contains("gps");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.driver:
                driver.setBackgroundColor(Color.parseColor("#2E313E"));
                pedestrian.setBackgroundColor(Color.parseColor("#424756"));
                pedestrian.setImageResource(R.drawable.pedestrian_passiv);
                driver.setImageResource(R.drawable.driver_activ);
                menu.setImageResource(R.drawable.menu);
                driv = 1;

                break;
            case R.id.pedestrian:
                pedestrian.setBackgroundColor(Color.parseColor("#2E313E"));
                pedestrian.setImageResource(R.drawable.pedestrian_activ);
                driver.setImageResource(R.drawable.driver_passiv);
                driver.setBackgroundColor(Color.parseColor("#424756"));
                menu.setImageResource(R.drawable.menu);
                driv = 0;

                break;
            case R.id.menu:
                startActivity(new Intent(this, com.example.hjk.testing.Settings.class).putExtra("id", id));

                break;
        }
    }

    private void startService(final int target){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkAccess()) {
                    startActivity(new Intent(MainActivity.this, Info.class).putExtra("id", id)
                            .putExtra("driver", driv).putExtra("target", target));

                } else {
                    Looper.prepare();
                    accessError();
                    Looper.loop();
                }
            }
        }).start();
    }

    private void counting(final int target) {
        Looper.prepare();
        progressDialog = ProgressDialog.show(this, "", "Завантажуються ваші координати");
        new Thread(new Runnable() {
            @Override
            public void run() {
                startService(target);
            }
        }).start();
        Looper.loop();
    }

    @Override
    protected void onPause() {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.centr:
                        if (!centr.isChecked()) {
                            centr.setButtonDrawable(R.drawable.centr_activ);

                            target += 1;
                            if (target.toCharArray().length > 2) {
                                target = removeTarget(target.toCharArray());
                            }

                            if (!thread.isAlive()) {
                                thread.start();
                            } else {
                                threadPause();
                            }
                        } else {
                            if (target.toCharArray().length > 0) {
                                target = removeTarget(target.toCharArray());
                            }
                            centr.setButtonDrawable(R.drawable.centr_passiv);
                        }

                        break;
                    case R.id.auto:
                        if (!auto.isChecked()) {
                            auto.setButtonDrawable(R.drawable.auto_activ);

                            target += 2;

                            if (target.toCharArray().length > 2) {
                                target = removeTarget(target.toCharArray());
                            }

                            if (!thread.isAlive()) {
                                thread.start();
                            } else {
                                threadPause();
                            }
                        } else {
                            if (target.toCharArray().length > 0) {
                                target = removeTarget(target.toCharArray());
                            }
                            auto.setButtonDrawable(R.drawable.auto_passiv);
                        }

                        break;
                    case R.id.north:
                        if (!north.isChecked()) {
                            north.setButtonDrawable(R.drawable.north_activ);

                            target += 3;

                            if (target.toCharArray().length > 2) {
                                target = removeTarget(target.toCharArray());
                            }

                            if (!thread.isAlive()) {
                                thread.start();
                            } else {
                                threadPause();
                            }
                        } else {
                            if (target.toCharArray().length > 0) {
                                target = removeTarget(target.toCharArray());
                            }
                            north.setButtonDrawable(R.drawable.north_passiv);
                        }
                        break;
                    case R.id.anniversary:
                        if (!anniversary.isChecked()) {
                            anniversary.setButtonDrawable(R.drawable.anniversary_activ);

                            target += 4;

                            if (target.toCharArray().length > 2) {
                                target = removeTarget(target.toCharArray());
                            }

                            if (!thread.isAlive()) {
                                thread.start();
                            } else {
                                threadPause();
                            }
                        } else {
                            if (target.toCharArray().length > 0) {
                                target = removeTarget(target.toCharArray());
                            }
                            anniversary.setButtonDrawable(R.drawable.anniversary_passiv);
                        }

                        break;
                    case R.id.angleBass:
                        if (!angleBass.isChecked()) {
                            angleBass.setButtonDrawable(R.drawable.angle_bass_active);

                            target += 5;

                            if (target.toCharArray().length > 2) {
                                target = removeTarget(target.toCharArray());
                            }

                            if (!thread.isAlive()) {
                                thread.start();
                            } else {
                                threadPause();
                            }
                        } else {
                            if (target.toCharArray().length > 0) {
                                target = removeTarget(target.toCharArray());
                            }
                            angleBass.setButtonDrawable(R.drawable.angle_bass_passive);
                        }

                        break;
                }
                break;
        }
        return false;
    }

    private void threadPause(){
        Thread thread1= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    thread.join(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        if(!thread1.isAlive()){
            thread1.start();
        }else {
            try {
                thread1.join(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String removeTarget(char[] target){
        String str="";

        if(target.length>0) {
            if (target[0] != '1') {
                if (target[0] == '2') {
                    auto.setButtonDrawable(R.drawable.auto_passiv);
                } else if (target[0] == '3') {
                    north.setButtonDrawable(R.drawable.north_passiv);
                } else if (target[0] == '4') {
                    anniversary.setButtonDrawable(R.drawable.anniversary_passiv);
                } else if (target[0] == '5') {
                    angleBass.setButtonDrawable(R.drawable.angle_bass_passive);
                }
            } else {
                if (target[1] == '1') {
                    centr.setButtonDrawable(R.drawable.centr_passiv);
                } else if (target[1] == '2') {
                    auto.setButtonDrawable(R.drawable.auto_passiv);
                } else if (target[1] == '3') {
                    north.setButtonDrawable(R.drawable.north_passiv);
                } else if (target[1] == '4') {
                    anniversary.setButtonDrawable(R.drawable.anniversary_passiv);
                } else if (target[1] == '5') {
                    angleBass.setButtonDrawable(R.drawable.angle_bass_passive);
                }
            }
        }

        if(target.length>0) {
            for (int i = 1; i < target.length; i++) {
                str += target[i];
            }
        }

        return str;
    }


}
