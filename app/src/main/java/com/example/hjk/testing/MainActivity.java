package com.example.hjk.testing;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainActivity extends Activity implements LocationListener{

    TextView textView;
    private static final int NETWORK_KEY=0;
    private static final int GPS_KEY=1;
    private Location location=null;
    private LocationManager locationManager=null;
    WorkWithDataBase workWithDataBase = new WorkWithDataBase();

    @Override
    protected void onResume() {
        super.onResume();
        workWithDataBase.setNumberPhone(getNumberPhone());
        locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        int check = checkAccess();
        if(check==NETWORK_KEY){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
        }else if(check==GPS_KEY){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }else {
            accessError();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTabs();
    }

    public void initTabs(){
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Вкладка 1");
        tabSpec.setContent(R.id.tvTab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Вкладка 2");
        tabSpec.setContent(R.id.tvTab2);
        tabHost.addTab(tabSpec);
    }

    public void accessError(){
        Toast.makeText(this,getResources().getString(R.string.accessErorr),Toast.LENGTH_LONG).show();
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

    public int checkAccess(){

        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(isConnectingToInternet()){
            return NETWORK_KEY;
        }
        else if(provider.contains("gps")== true){
            return GPS_KEY;
        }
        else {
            return 2;
        }
    }

    public Long getNumberPhone(){
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return Long.valueOf(telephonyManager.getLine1Number().replace("+",""));
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location=location;
        workWithDataBase.setCoordinates(this.location.getLatitude(),this.location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
