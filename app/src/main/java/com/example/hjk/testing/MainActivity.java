package com.example.hjk.testing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    ImageButton menu, pedestrian, driver;
    CheckBox auto, north, anniversary, centr, angleBass;
    String target="0";
    int id,driv;
    int[] data;
    boolean flagForLoginProcedure = false;
    ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();

        if (checkAccess()) {
            if(!flagForLoginProcedure) {

                        try {
                            data = workWithDataBase.setNumberPhone(getNumberPhone());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        id = data[0];
                        driv = data[1];

                flagForLoginProcedure = true;
            }

            if(driv==0){
                pedestrian.setImageResource(R.drawable.pedestrian_activ);
                driver.setImageResource(R.drawable.driver_passiv);
            }else if(driv==1){
                driver.setImageResource(R.drawable.driver_activ);
                pedestrian.setImageResource(R.drawable.pedestrian_passiv);
            }


        }else {
            accessError();
        }

        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

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
        stopService(new Intent(MainActivity.this, TransportAnother.class));
        if(id != -1) {
            workWithDataBase.onlineEnd(id);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auto = (CheckBox) findViewById(R.id.auto);
        auto.setOnCheckedChangeListener(this);

        north = (CheckBox) findViewById(R.id.north);
        north.setOnCheckedChangeListener(this);

        anniversary = (CheckBox) findViewById(R.id.anniversary);
        anniversary.setOnCheckedChangeListener(this);

        centr = (CheckBox) findViewById(R.id.centr);
        centr.setOnCheckedChangeListener(this);

        angleBass = (CheckBox) findViewById(R.id.angleBass);
        angleBass.setOnCheckedChangeListener(this);

        menu = (ImageButton) findViewById(R.id.menu);
        menu.setOnClickListener(this);

        pedestrian = (ImageButton) findViewById(R.id.pedestrian);
        pedestrian.setOnClickListener(this);

        driver = (ImageButton) findViewById(R.id.driver);
        driver.setOnClickListener(this);
    }

    public Long getNumberPhone(){
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return Long.valueOf(telephonyManager.getDeviceId());
    }

    public void accessError(){
        Toast.makeText(getApplicationContext(),getResources().getString(R.string.accessErorr),Toast.LENGTH_LONG).show();
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }

        return false;
    }

    public boolean checkAccess(){
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(isConnectingToInternet()&&provider.contains("gps")== true){
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        MainActivity.this.finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.driver:
                pedestrian.setImageResource(R.drawable.pedestrian_passiv);
                driver.setImageResource(R.drawable.driver_activ);
                menu.setImageResource(R.drawable.menu);
                driv=1;
                break;
            case R.id.pedestrian:
                pedestrian.setImageResource(R.drawable.pedestrian_activ);
                driver.setImageResource(R.drawable.driver_passiv);
                menu.setImageResource(R.drawable.menu);
                driv=0;
                break;
            case R.id.menu:
                pedestrian.setImageResource(R.drawable.pedestrian_passiv);
               // startActivity(new Intent(this,com.example.hjk.testing.Settings.class).putExtra("id",id));
                break;
        }
    }

    public void startService(final int target){
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

    public void counting(final int target) {
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        buttonView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isConnectingToInternet()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < 5; i++) {
                                        if (i == 4) {
                                            counting(Integer.parseInt(target));
                                        }
                                        try {
                                            TimeUnit.SECONDS.sleep(1);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                        } else {
                            accessError();
                        }
                        break;
                }
                return false;
            }
        });

        switch (buttonView.getId()) {
            case R.id.centr:
                if (!isChecked) {
                    centr.setButtonDrawable(R.drawable.centr_activ);
                    target += 1;
                } else {
                    centr.setButtonDrawable(R.drawable.centr_passiv);
                }

                break;
            case R.id.auto:
                if (!isChecked) {
                    auto.setButtonDrawable(R.drawable.auto_activ);
                    target += 2;
                } else {
                    auto.setButtonDrawable(R.drawable.auto_passiv);
                }

                break;
            case R.id.north:
                if (!isChecked) {
                    north.setButtonDrawable(R.drawable.north_activ);
                    target += 3;
                } else {
                    north.setButtonDrawable(R.drawable.north_passiv);
                }
                break;
            case R.id.anniversary:
                if (!isChecked) {
                    anniversary.setButtonDrawable(R.drawable.anniversary_activ);
                    target += 4;
                } else {
                    anniversary.setButtonDrawable(R.drawable.anniversary_passiv);
                }

                break;
            case R.id.angleBass:
                if (!isChecked) {
                    angleBass.setButtonDrawable(R.drawable.angle_bass_active);
                    target += 5;
                } else {
                    angleBass.setButtonDrawable(R.drawable.angle_bass_passive);
                }

                break;
        }
    }
}
