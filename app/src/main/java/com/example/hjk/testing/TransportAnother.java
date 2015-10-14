package com.example.hjk.testing;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class TransportAnother extends Service implements LocationListener {

    private final WorkWithDataBase workWithDataBase = new WorkWithDataBase();

    private boolean flag = false;
    private int id;
    private int driver;
    private int target;
    private int idPing = 0;
    private int countDistantion=0;
    private int countPingSet = 5;
    private int distation = 0;
    private int idContact = 0;
    private Location location = null;
    private boolean isShowStartContact = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id = intent.getIntExtra("id", -1);
        driver = intent.getIntExtra("driver", 0);
        target = intent.getIntExtra("target", 0);

        location = null;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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
        double[] data;
        if (!flag) {
            data = workWithDataBase.onlineStart(id, driver, target, this.location.getLatitude(), this.location.getLongitude());

            if (data[0] != 0) {
                idPing = (int) data[0];
                distation = Info.gps2m(data[1], data[2], this.location.getLatitude(), this.location.getLongitude());
            } else {
                idPing = 0;
            }

            Intent in = new Intent("Info_start_online")
                    .putExtra("dist",distation)
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
                if (countDistantion < 1) {
                    data = workWithDataBase.ping(id, idPing, driver, this.location.getLatitude(), this.location.getLongitude());

                    if (distation != 0 && (distation - Info.gps2m(data[0], data[1], this.location.getLatitude()
                            , this.location.getLongitude())) > -50) {

                        distation = Info.gps2m(data[0], data[1], this.location.getLatitude()
                                , this.location.getLongitude());

                        if (distation < 1000) {
                            countDistantion++;
                        }

                        Intent in = new Intent("Info_ping")
                                .putExtra("dist", distation);
                        sendBroadcast(in);

                        break startPing;
                    }else {
                        distation = 0;
                        idPing = 0;
                    }
                } else if (countDistantion >= 1) {
                    if (countDistantion < 2) {

                        data = workWithDataBase.ping(id, idPing, driver, this.location.getLatitude(), this.location.getLongitude());

                        distation = Info.gps2m(this.location.getLatitude(), this.location.getLongitude(), data[0], data[1]);

                        if(distation<500) {
                            idContact = workWithDataBase.contact(id, idPing, this.location.getLatitude(), this.location.getLongitude(), driver);
                            countDistantion++;
                            startActivity(new Intent(this,StartContact.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("isExit",true));
                        }else {
                            if (!isShowStartContact) {
                                startActivity(new Intent(this, StartContact.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("target", target));
                                isShowStartContact = true;
                            }
                        }

                        Intent in = new Intent("Contact_start")
                                .putExtra("isContact",true)
                                .putExtra("dist", distation)
                                .putExtra("contact", true);
                        sendBroadcast(in);

                        break startPing;
                    } else {
                        countDistantion++;

                        System.out.println("yes contact_set");
                        data = workWithDataBase.contactSet(idContact, this.location.getLatitude(), this.location.getLongitude());

                        System.out.println(data[0] + " " +data[1]);
                        distation = Info.gps2m(data[0], data[1], this.location.getLatitude()
                                , this.location.getLongitude());

                        if(distation>100){
                            startActivity(new Intent(this,Rating.class).putExtra("id",id));
                        }else {
                            Intent in = new Intent("Contact_start")
                                    .putExtra("isContact", true)
                                    .putExtra("dist", 0)
                                    .putExtra("contact", true);
                            sendBroadcast(in);
                        }

                        break startPing;
                    }
                }

            } else {
                if (countPingSet > 1) {
                    workWithDataBase.pingSet(id, driver, this.location.getLatitude(), this.location.getLongitude());

                    countPingSet--;
                } else {
                    data = workWithDataBase.search(id, driver, target, this.location.getLatitude(), this.location.getLongitude());

                    countPingSet = 5;

                    if (data[0] != 0) {
                        idPing = (int) data[0];
                        distation = Info.gps2m(data[1], data[2], this.location.getLatitude(), this.location.getLongitude());

                        Intent in = new Intent("Info_start_online")
                                .putExtra("dist", distation)
                                .putExtra("centr", (int) data[3])
                                .putExtra("auto", (int) data[4])
                                .putExtra("north", (int) data[5])
                                .putExtra("ubil", (int) data[6])
                                .putExtra("bass", (int) data[7]);

                        sendBroadcast(in);
                    }

                    break startPing;
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
