package com.example.hjk.testing;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class TransportAnother extends Service implements LocationListener{

    WorkWithDataBase workWithDataBase=new WorkWithDataBase();

    private Location location = null;
    private LocationManager locationManager = null;
    boolean flag =false;
    int id,driver,target;
    double[] data;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id = intent.getIntExtra("id",-1);
        driver = intent.getIntExtra("driver",0);
        target = intent.getIntExtra("target",0);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 5, 0, this);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, this);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, this);
        }catch (Exception e) {
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

            if (!flag) {
                data = workWithDataBase.onlineStart(id, driver, target, this.location.getLatitude(), this.location.getLongitude());

                Intent in = new Intent("Info")
                        .putExtra("id", id)
                        .putExtra("ping",data[0])
                        .putExtra("mycoo1", this.location.getLatitude())
                        .putExtra("mycoo2", this.location.getLongitude())
                        .putExtra("coo1", data[1])
                        .putExtra("coo2", data[2])
                        .putExtra("centr", data[3])
                        .putExtra("auto", data[4])
                        .putExtra("north", data[5])
                        .putExtra("ubil", data[6])
                        .putExtra("bass", data[7]);

                sendBroadcast(in);

                flag = true;
            } else {
                double[] coo = workWithDataBase.search(id, this.location.getLatitude(), this.location.getLongitude(), driver, target, "");

                Intent in = new Intent("Info")
                                .putExtra("id", id)
                                .putExtra("mycoo1", this.location.getLatitude())
                                .putExtra("mycoo2", this.location.getLongitude())
                                .putExtra("coo1", coo[0])
                                .putExtra("coo2", coo[1]);
                sendBroadcast(in);
            }
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
