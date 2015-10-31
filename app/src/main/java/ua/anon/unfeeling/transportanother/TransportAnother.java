package ua.anon.unfeeling.transportanother;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

@SuppressWarnings("UnnecessaryBreak")
public class TransportAnother extends Service implements LocationListener {

    private WorkWithDataBase workWithDataBase = new WorkWithDataBase();

    private boolean flag = false;
    private int id,pointDistantion=0;
    private int driver,flagPoint=0;
    private int target,defaultTarget;
    private int idPing = 0, idContact = 0;
    private int countDistantion = 0;
    private int countPingSet = 5;
    private String exception = "0";
    private int distation = 0, isContactFlag = 1,isCloseContact = 3;
    private Location location = null;
    private boolean isShowStartContact = true, isShowContactEnd = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id = intent.getIntExtra("id", -1);
        driver = intent.getIntExtra("driver", 0);
        defaultTarget = intent.getIntExtra("target", 0);

        location = null;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);

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

    double[] data;

    @Override
    public void onLocationChanged(final Location location) {
        this.location = location;

        if (!flag) {

           Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    data = workWithDataBase.onlineStart(id, driver, defaultTarget, location.getLatitude(), location.getLongitude());
                }
            });

            thread.start();

            try {
                thread.join();
                if (data[0] != 0) {
                    idPing = (int) data[0];
                    target = (int) data[8];
                    distation = Info.gps2m(data[1], data[2], location.getLatitude(), location.getLongitude());
                } else {
                    idPing = 0;
                }

                Intent in = new Intent("Info_start_online")
                        .putExtra("dist", distation)
                        .putExtra("centr", (int) data[3])
                        .putExtra("auto", (int) data[4])
                        .putExtra("north", (int) data[5])
                        .putExtra("ubil", (int) data[6])
                        .putExtra("bass", (int) data[7]);

                sendBroadcast(in);

                flag = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            startPing:

            if (idPing != 0) {
                if (countDistantion < 1) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            data = workWithDataBase.ping(id, idPing, driver, location.getLatitude(), location.getLongitude());
                        }
                    });

                    thread.start();

                    try {
                        thread.join();
                        if (data[2] == 0) {
                            pointDistantion = Info.gps2m(data[0], data[1], location.getLatitude()
                                    , location.getLongitude());
                            System.out.println("dist " + distation);
                            System.out.println("point dist "+pointDistantion);
                            if (distation != 0 && (distation - pointDistantion) >= -50) {

                                if (flagPoint < 3) {
                                    System.out.println("flag ++");
                                    flagPoint++;
                                } else if(flagPoint>2){

                                    System.out.println("id point " + idPing);

                                    if (distation < 1000) {
                                        flagPoint=0;
                                        countDistantion++;
                                    }

                                    Intent in = new Intent("Info_ping")
                                            .putExtra("dist", pointDistantion);
                                    sendBroadcast(in);

                                }

                                break startPing;

                            } else {
                                Thread thread1 = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("serch ex target "+defaultTarget);

                                        exception+=","+idPing;
                                        System.out.println(exception);
                                        data =workWithDataBase.search(id, driver, defaultTarget, location.getLatitude(), location.getLongitude(),exception);
                                    }
                                });

                                thread1.start();

                                try {
                                    thread1.join();

                                    if (data[0] != 0) {
                                        for (int i = 0; i < data.length; i++) {
                                            System.out.println(data[i]);
                                        }

                                        idPing = (int) data[0];
                                        if ((int) data[8] != -1) {
                                            target = (int) data[8];
                                        }
                                        distation = Info.gps2m(data[1], data[2], location.getLatitude(), location.getLongitude());

                                        Intent in = new Intent("Info_start_online")
                                                .putExtra("dist", distation)
                                                .putExtra("centr", (int) data[3])
                                                .putExtra("auto", (int) data[4])
                                                .putExtra("north", (int) data[5])
                                                .putExtra("ubil", (int) data[6])
                                                .putExtra("bass", (int) data[7]);

                                        sendBroadcast(in);
                                    }else {
                                        exception="0";
                                        idPing=0;
                                    }

                                    break startPing;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            countDistantion += 10;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (countDistantion >= 1) {
                    if (countDistantion < 2) {

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                data = workWithDataBase.ping(id, idPing, driver, location.getLatitude(), location.getLongitude());
                            }
                        });

                        thread.start();

                        try {
                            thread.join();
                            pointDistantion = Info.gps2m(location.getLatitude(), location.getLongitude(), data[0], data[1]);

                            if (pointDistantion < 50) {
                                if (isContactFlag == 0) {

                                    Thread thread1 = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            idContact = workWithDataBase.contact(id, idPing, location.getLatitude(), location.getLongitude(), driver);
                                        }
                                    });

                                    thread1.start();

                                    try {
                                        thread1.join();

                                        countDistantion += 10;

                                        if (!isShowStartContact) {
                                            Intent in = new Intent("Contact")
                                                    .putExtra("isContact", true);
                                            sendBroadcast(in);
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    isContactFlag--;
                                    break startPing;
                                }
                            } else {
                                if (isShowStartContact) {
                                    startActivity(new Intent(TransportAnother.this, StartContact.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("target", target)
                                            .putExtra("statusContact", workWithDataBase.contactStatus(idPing))
                                            .putExtra("id", id).putExtra("target", target).putExtra("driver", driver));

                                    isShowStartContact = false;
                                }
                            }

                            Intent in = new Intent("Contact_start")
                                    .putExtra("isContact", true)
                                    .putExtra("dist", pointDistantion)
                                    .putExtra("contact", true);
                            sendBroadcast(in);

                            break startPing;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        countDistantion++;

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                data = workWithDataBase.contactSet(id, location.getLatitude(), location.getLongitude());
                            }
                        });

                        thread.start();

                        try {
                            thread.join();

                            distation = Info.gps2m(data[0], data[1], location.getLatitude()
                                    , location.getLongitude());

                            if (distation > 200) {
                                if(isShowContactEnd) {
                                    if (isCloseContact == 0) {
                                        Intent in = new Intent("Contact_end");
                                        in.putExtra("id", idContact);
                                        sendBroadcast(in);

                                        isShowContactEnd=false;
                                    } else {
                                        isCloseContact--;
                                    }
                                }
                            } else {
                                Intent in = new Intent("Contact_start")
                                        .putExtra("isContact", true)
                                        .putExtra("dist", 0)
                                        .putExtra("contact", true);
                                sendBroadcast(in);
                            }

                            break startPing;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else {
                if (countPingSet > 1) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            workWithDataBase.pingSet(id, driver, location.getLatitude(), location.getLongitude());
                        }
                    });

                    thread.start();

                    try {
                        thread.join();

                        countPingSet--;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            data = workWithDataBase.search(id, driver, defaultTarget, location.getLatitude(), location.getLongitude());
                        }
                    });

                    thread.start();

                    try {
                        thread.join();

                        if (data[0] != 0) {
                            idPing = (int) data[0];
                            if ((int) data[8] != -1) {
                                target = (int) data[8];
                            }
                            pointDistantion = Info.gps2m(data[1], data[2], location.getLatitude(), location.getLongitude());

                            Intent in = new Intent("Info_start_online")
                                    .putExtra("dist", pointDistantion)
                                    .putExtra("centr", (int) data[3])
                                    .putExtra("auto", (int) data[4])
                                    .putExtra("north", (int) data[5])
                                    .putExtra("ubil", (int) data[6])
                                    .putExtra("bass", (int) data[7]);

                            sendBroadcast(in);
                        } else {
                            countPingSet = 5;
                        }

                        break startPing;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
