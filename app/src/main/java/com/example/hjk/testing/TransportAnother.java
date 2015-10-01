package com.example.hjk.testing;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class TransportAnother extends Service implements LocationListener{

    WorkWithDataBase workWithDataBase;
    private Location location = null;
    private LocationManager locationManager = null;
    int id;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        id = intent.getIntExtra("id",-1);
        try{
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, this);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
        }catch (Exception e){
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location=location;

        workWithDataBase = new WorkWithDataBase();

        workWithDataBase.setCoordinates(id,this.location.getLatitude(), this.location.getLongitude());

        Toast.makeText(getApplicationContext(),this.location.getLatitude() + " " + this.location.getLongitude(),Toast.LENGTH_SHORT).show();
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
