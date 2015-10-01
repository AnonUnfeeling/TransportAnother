package com.example.hjk.testing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import java.sql.SQLException;

public class MainActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    ImageButton menu, pedestrian, driver;
    CheckBox auto, north, anniversary, centr, angleBass;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this, TransportAnother.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(checkAccess()) {
                        saveID(workWithDataBase.setNumberPhone(getNumberPhone()));

                        startService(new Intent(MainActivity.this, TransportAnother.class).putExtra("id", loadID()));
                    }else {
                        Looper.prepare();
                        accessError();
                        Looper.loop();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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

    public void saveID(int id){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id",id);
        editor.commit();
    }

    public int loadID(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        return sharedPreferences.getInt("id",-1);
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
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.driver:
                pedestrian.setImageResource(R.drawable.pedestrian_passiv);
                driver.setImageResource(R.drawable.driver_activ);
                menu.setImageResource(R.drawable.menu);
                break;
            case R.id.pedestrian:
                pedestrian.setImageResource(R.drawable.pedestrian_activ);
                driver.setImageResource(R.drawable.driver_passiv);
                menu.setImageResource(R.drawable.menu);
                break;
            case R.id.menu:
                pedestrian.setImageResource(R.drawable.pedestrian_passiv);
                driver.setImageResource(R.drawable.driver_passiv);
                menu.setImageResource(R.drawable.menu_back);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()){
            case R.id.centr:
                if(!isChecked) {
                    centr.setButtonDrawable(R.drawable.centr_activ);
                }else {
                    centr.setButtonDrawable(R.drawable.centr_passiv);
                }

                break;
            case R.id.auto:
                if(!isChecked) {
                    auto.setButtonDrawable(R.drawable.auto_active);
                }else {
                    auto.setButtonDrawable(R.drawable.auto_passiv);
                }

                break;
            case R.id.north:
                if(!isChecked) {
                    north.setButtonDrawable(R.drawable.north_active);
                }else {
                    north.setButtonDrawable(R.drawable.north_passiv);
                }
                break;
            case R.id.anniversary:
                if(!isChecked) {
                    anniversary.setButtonDrawable(R.drawable.anniversary_active);
                }else {
                    anniversary.setButtonDrawable(R.drawable.anniversary_passiv);
                }

                break;
            case R.id.angleBass:
                if(!isChecked) {
                    angleBass.setButtonDrawable(R.drawable.angle_bass_active);
                }else {
                    angleBass.setButtonDrawable(R.drawable.angle_bass_passive);
                }

                break;
        }
    }
}
