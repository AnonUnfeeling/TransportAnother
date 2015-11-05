package ua.anon.unfeeling.transportanother;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.hjk.transportanother.R;

@SuppressWarnings("UnnecessaryBreak")
public class TransportAnother extends Service implements LocationListener {

    private WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    private boolean flag = true;
    private int id,pointDistantion=0;
    private int driver,flagPoint=0;
    private boolean isShowContactEnd = true;
    private int defaultTarget;
    private int idPing = 0, idContact = 0;
    private int countPingSet = 5;
    private String exception = "0";
    private Location location;
    private int distation = 0, isContactFlag = 2,isCloseContact = 3;
    private boolean isShowStartContact = true, isSos = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("start service");

        isSos = intent.getBooleanExtra("isSos",false);

        System.out.println(isSos);

        flag = true;
        id = 0;
        pointDistantion = 0;
        driver = 0;
        flagPoint = 0;
        isShowContactEnd = true;
        defaultTarget = 0;
        idPing = 0;
        idContact = 0;
        countPingSet = 5;
        exception = "0";
        distation = 0;
        isContactFlag = 2;
        isCloseContact = 3;
        isShowStartContact = true;

        id = intent.getIntExtra("id", -1);
        driver = intent.getIntExtra("driver", 0);
        defaultTarget = intent.getIntExtra("defaultTarget", 0);

        System.out.println("serv: "+id);

        location = null;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("create");

        Intent notificationIntent = new Intent(this, Info.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
                notificationIntent,0 );

        Notification notification=new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.big11)
                .setContentText(getString(R.string.app_name))
                .setContentIntent(pendingIntent).build();

        startForeground(101, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        start(this.location);
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

    private void start(final Location location){

        System.out.println("start");
          new Thread(new Runnable() {
            @Override
            public void run() {
                double[] data;

                if(isSos){
                    workWithDataBase.sos(id,idContact,location.getLatitude(), location.getLongitude());
                    isSos=false;
                }

                if (flag) {
                    System.out.println("online start");

                    data = workWithDataBase.onlineStart(id, driver, defaultTarget, location.getLatitude(), location.getLongitude());

                    if (data[0] != 0) {
                        System.out.println("yes point os");

                        idPing = (int) data[0];
                        distation = Info.gps2m(data[2], data[3], location.getLatitude(), location.getLongitude());

                    } else {
                        System.out.println("no point os");
                    }

                    Intent in = new Intent("Info_start_online")
                            .putExtra("centr", (int) data[4])
                            .putExtra("auto", (int) data[5])
                            .putExtra("north", (int) data[6])
                            .putExtra("ubil", (int) data[7])
                            .putExtra("bass", (int) data[8]);
                    sendBroadcast(in);

                    flag = false;
                } else {
                    if (idPing != 0) {
                        System.out.println(id+" "+ idPing+" "+ driver+" "+ location.getLatitude()+" "+ location.getLongitude());

                        data = workWithDataBase.ping(id, idPing, driver, location.getLatitude(), location.getLongitude());
                        System.out.println("ping data[2] " + data[2]);

                        if (data[2] == 0) {
                            pointDistantion = Info.gps2m(data[0], data[1], location.getLatitude(), location.getLongitude());

                            System.out.println("ping");

                            System.out.println(distation + " "+pointDistantion);

                            if (distation - pointDistantion >= 0) {
                                System.out.println("no >");

                                if (flagPoint > 2) {

                                    System.out.println(pointDistantion);
                                    if (pointDistantion < 800) {
                                        System.out.println("<800");

                                        if (isShowStartContact) {

                                            System.out.println("show contast");
                                            startActivity(new Intent(TransportAnother.this, StartContact.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    .putExtra("statusContact", workWithDataBase.contactStatus(idPing))
                                                    .putExtra("id", id).putExtra("target", defaultTarget).putExtra("driver", driver));

                                            isShowStartContact = false;
                                        } else {
                                            if (pointDistantion < 50) {
                                                if (isContactFlag == 0) {
                                                    System.out.println("contact");

                                                    if (idContact == 0) {
                                                        System.out.println("create contact");
                                                        idContact = workWithDataBase.contact(id, idPing, location.getLatitude(), location.getLongitude(), driver);

                                                        Intent inv = new Intent("Contact");
                                                        inv.putExtra("id", idContact);
                                                        sendBroadcast(inv);

                                                    } else {
                                                        System.out.println("contact set");

                                                        data = workWithDataBase.contactSet(id, location.getLatitude(), location.getLongitude());
                                                        System.out.println("proc contact set");
                                                        pointDistantion = Info.gps2m(data[0], data[1], location.getLatitude(), location.getLongitude());

                                                        System.out.println(pointDistantion);

                                                    }
                                                    Intent in = new Intent("Contact_start")
                                                            .putExtra("isContact", true)
                                                            .putExtra("dist", pointDistantion)
                                                            .putExtra("contact", true);
                                                    sendBroadcast(in);
                                                } else {
                                                    System.out.println("yes contact flag");
                                                    isContactFlag--;
                                                }
                                            }
                                        }
                                    }
                                    Intent in = new Intent("Info_ping")
                                            .putExtra("dist", pointDistantion);
                                    sendBroadcast(in);
                                } else {
                                    flagPoint++;
                                }
                            } else {
                                System.out.println("yes >");
                                exception += idPing;
                                System.out.println("search");
                                System.out.println(id+" "+ driver+" "+defaultTarget+" "+ location.getLatitude()+" "+ location.getLongitude()+" "+exception);
                                data = workWithDataBase.search(id, driver, defaultTarget, location.getLatitude(), location.getLongitude(), exception);

                                if (data[0] != 0) {
                                    System.out.println("yes point search");

                                    exception = "0";

                                    idPing = (int) data[0];
                                    distation = Info.gps2m(data[2], data[3], location.getLatitude(), location.getLongitude());
                                    Intent in = new Intent("Info_start_online")
                                            .putExtra("idPing", idPing)
                                            .putExtra("dist", distation)
                                            .putExtra("centr", (int) data[4])
                                            .putExtra("auto", (int) data[5])
                                            .putExtra("north", (int) data[6])
                                            .putExtra("ubil", (int) data[7])
                                            .putExtra("bass", (int) data[8]);

                                    sendBroadcast(in);
                                } else {
                                    System.out.println("no point search");

                                    exception = "0";

                                    Intent in = new Intent("Info_start_online")
                                            .putExtra("centr", (int) data[4])
                                            .putExtra("auto", (int) data[5])
                                            .putExtra("north", (int) data[6])
                                            .putExtra("ubil", (int) data[7])
                                            .putExtra("bass", (int) data[8]);
                                    sendBroadcast(in);

                                }
                            }
                        }else {
                                System.out.println("contact set");

                                data = workWithDataBase.contactSet(id, location.getLatitude(), location.getLongitude());
                                pointDistantion = Info.gps2m(data[0], data[1], location.getLatitude(), location.getLongitude());

                                if(pointDistantion>50){
                                    if (isCloseContact == 0&&isShowContactEnd) {
                                        isShowContactEnd = false;
                                        System.out.println("contact end");
                                        Intent in = new Intent("Contact_end");
                                        in.putExtra("id", idContact);
                                        sendBroadcast(in);
                                    } else {
                                        isCloseContact--;
                                    }
                            }
                            Intent in = new Intent("Contact_start")
                                    .putExtra("isContact", true)
                                    .putExtra("dist", pointDistantion)
                                    .putExtra("contact", true);
                            sendBroadcast(in);
                        }
                    } else {
                        if (countPingSet > 1) {
                            System.out.println("ping set");

                            workWithDataBase.pingSet(id, driver, location.getLatitude(), location.getLongitude());
                            countPingSet--;
                        } else {
                            System.out.println("search");
                            System.out.println(id+" "+ driver+" "+defaultTarget+" "+ location.getLatitude()+" "+ location.getLongitude()+" "+exception);
                            data = workWithDataBase.search(id, driver, defaultTarget, location.getLatitude(), location.getLongitude(), "0");
                            if (data[0] != 0) {
                                System.out.println("yes point search");

                                idPing = (int) data[0];
                                distation = Info.gps2m(data[2], data[3], location.getLatitude(), location.getLongitude());
                                Intent in = new Intent("Info_start_online")
                                        .putExtra("idPing", idPing)
                                        .putExtra("dist", distation)
                                        .putExtra("centr", (int) data[4])
                                        .putExtra("auto", (int) data[5])
                                        .putExtra("north", (int) data[6])
                                        .putExtra("ubil", (int) data[7])
                                        .putExtra("bass", (int) data[8]);

                                sendBroadcast(in);
                            } else {
                                System.out.println("no point search");

                                exception = "0";

                                Intent in = new Intent("Info_start_online")
                                        .putExtra("centr", (int) data[4])
                                        .putExtra("auto", (int) data[5])
                                        .putExtra("north", (int) data[6])
                                        .putExtra("ubil", (int) data[7])
                                        .putExtra("bass", (int) data[8]);
                                sendBroadcast(in);
                            }
                            countPingSet = 5;
                        }
                    }
                }
            }
        }).start();
    }
}
