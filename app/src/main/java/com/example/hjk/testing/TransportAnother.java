package com.example.hjk.testing;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class TransportAnother extends Service implements LocationListener {

    WorkWithDataBase workWithDataBase = new WorkWithDataBase();

    private Location location = null;
    private LocationManager locationManager = null;
    boolean flag = false;
    int id, driver, target, idPing = 0;
    double[] data;
    int countPingSet = 5;
    int distation = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id = intent.getIntExtra("id", -1);
        driver = intent.getIntExtra("driver", 0);
        target = intent.getIntExtra("target", 0);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 5, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, this);
        } catch (Exception e) {
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
        this.location = location;

        if (!flag) {
            data = workWithDataBase.onlineStart(id, driver, target, this.location.getLatitude(), this.location.getLongitude());

            if (data[0] != 0) {
                idPing = (int) data[0];
                distation = Info.gps2m(data[1],data[2],this.location.getLatitude(),this.location.getLongitude());
            } else {
                idPing = 0;
            }

            Intent in = new Intent("Info_start_online")
                    .putExtra("ping", data[0])
                    .putExtra("mycoo1", this.location.getLatitude())
                    .putExtra("mycoo2", this.location.getLongitude())
                    .putExtra("coo1", data[1])
                    .putExtra("coo2", data[2])
                    .putExtra("centr", (int) data[3])
                    .putExtra("auto", (int) data[4])
                    .putExtra("north", (int) data[5])
                    .putExtra("ubil", (int) data[6])
                    .putExtra("bass", (int) data[7]);

            sendBroadcast(in);

            flag = true;
        } else {

            startPing:

            if (idPing != 0) {
                data = workWithDataBase.ping(id, idPing, driver, this.location.getLatitude(), this.location.getLongitude());

                if (distation != 0 && (distation - Info.gps2m(data[0], data[1], this.location.getLatitude()
                        , this.location.getLongitude())) > -50) {

                    data = workWithDataBase.search(id, driver, target, this.location.getLatitude(),
                            this.location.getLongitude(), "" + idPing);

                    if (data[0] != 0) {
                        idPing = (int) data[0];

                        Intent in = new Intent("Info_ping")
                                .putExtra("mycoo1", this.location.getLatitude())
                                .putExtra("mycoo2", this.location.getLongitude())
                                .putExtra("coo1", data[1])
                                .putExtra("coo2", data[2]);
                        sendBroadcast(in);

                        break startPing;
                    }
                }else {
                    idPing=0;
                }
            } else {
                if (countPingSet > 1) {
                    workWithDataBase.pingSet(id, driver, this.location.getLatitude(), this.location.getLongitude());

                    countPingSet--;
                } else {
                    data = workWithDataBase.search(id, driver, target, this.location.getLatitude(), this.location.getLongitude(), "");

                    if (data[0] != 0) {
                        idPing = (int) data[0];
                        distation = Info.gps2m(data[1],data[2],this.location.getLatitude(),this.location.getLongitude());

                        break startPing;
                    }
                }
            }
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
