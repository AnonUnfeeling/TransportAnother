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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
    private int pingFlag = 0;
    private Location location;
    private int distation = 0, isContactFlag = 2,isCloseContact = 3;
    private boolean isShowStartContact = true, isSos = false;
    private Thread taskThread;
    private boolean flagLocation=true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isSos = intent.getBooleanExtra("isSos",false);

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

        location = null;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

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

        if (flagLocation) {
            flagLocation = false;
            start();
        }
    }

    @Override
    public void onDestroy() {
        if(this.location!=null) {
            this.location = null;
        }
        if(taskThread!=null) {
            taskThread.stop();
        }
        super.onDestroy();
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

    private void start() {
        taskThread = new Thread(new Runnable() {
            @Override
            public void run() {

                do {
                    if (id != 0 || id != -1) {
                        double[] data;

                        if (flag) {
                            data = workWithDataBase.onlineStart(id, driver, defaultTarget, location.getLatitude(), location.getLongitude());

                            if (data[0] != 0) {

                                idPing = (int) data[0];
                                distation = Info.gps2m(data[2], data[3], location.getLatitude(), location.getLongitude());

                                Intent in = new Intent("Info_start_online")
                                        .putExtra("centr", (int) data[4])
                                        .putExtra("auto", (int) data[5])
                                        .putExtra("north", (int) data[6])
                                        .putExtra("ubil", (int) data[7])
                                        .putExtra("bass", (int) data[8])
                                        .putExtra("status", workWithDataBase.contactStatus(idPing));
                                sendBroadcast(in);
                            } else {
                                Intent in = new Intent("Info_start_online")
                                        .putExtra("centr", (int) data[4])
                                        .putExtra("auto", (int) data[5])
                                        .putExtra("north", (int) data[6])
                                        .putExtra("ubil", (int) data[7])
                                        .putExtra("bass", (int) data[8]);
                                sendBroadcast(in);
                            }

                            flag = false;
                        } else {
                            if (idPing != 0) {
                                if (pingFlag == 0) {
                                    data = workWithDataBase.ping(id, idPing, driver, location.getLatitude(), location.getLongitude());
                                    for (int i = 0; i < data.length; i++) {
                                        System.out.println(data[i]);
                                    }
                                    if(data[0]==0.0){
                                        idPing=0;
                                        flagPoint=0;
                                        pingFlag=0;
                                    }else if (data[2] == 0) {
                                        pointDistantion = Info.gps2m(data[0], data[1], location.getLatitude(), location.getLongitude());

                                        System.out.println(distation+ " "+pointDistantion);

                                        if (distation - pointDistantion >= 0) {

                                            if (flagPoint > 2) {
                                                if (pointDistantion < 800) {

                                                    if (isShowStartContact&&driver==0) {
                                                        startActivity(new Intent(TransportAnother.this, StartContact.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                .putExtra("statusContact", workWithDataBase.contactStatus(idPing)).putExtra("target", defaultTarget));

                                                        isShowStartContact = false;
                                                    }else if(isShowStartContact&&driver==1){
                                                        startActivity(new Intent(TransportAnother.this, Sound.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                        isShowStartContact = false;
                                                    }else {
                                                        if (pointDistantion < 50) {
                                                            if (isContactFlag == 0) {
                                                                if (idContact == 0) {
                                                                    idContact = workWithDataBase.contact(id, idPing, location.getLatitude(), location.getLongitude(), driver);

                                                                    Intent inv = new Intent("Contact");
                                                                    inv.putExtra("id", idContact);
                                                                    sendBroadcast(inv);

                                                                } else {
                                                                    data = workWithDataBase.contactSet(id, location.getLatitude(), location.getLongitude());
                                                                    pointDistantion = Info.gps2m(data[0], data[1], location.getLatitude(), location.getLongitude());

                                                                }
                                                            } else {
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

                                            Intent inv = new Intent("Ping_Set");
                                            sendBroadcast(inv);
                                            System.out.println("search");
                                            exception += "," + idPing;
                                            data = workWithDataBase.search(id, driver, defaultTarget, location.getLatitude(), location.getLongitude(), exception);

                                            if (data[0] != 0) {
                                                exception = "0";

                                                idPing = (int) data[0];
                                                distation = Info.gps2m(data[2], data[3], location.getLatitude(), location.getLongitude());

                                                Intent in = new Intent("Search")
                                                        .putExtra("idPing", idPing)
                                                        .putExtra("dist", distation)
                                                        .putExtra("centr", (int) data[4])
                                                        .putExtra("auto", (int) data[5])
                                                        .putExtra("north", (int) data[6])
                                                        .putExtra("ubil", (int) data[7])
                                                        .putExtra("bass", (int) data[8])
                                                        .putExtra("status",workWithDataBase.contactStatus(idPing));
                                                sendBroadcast(in);
                                            } else {
                                                exception = "0";

                                                Intent in = new Intent("Search")
                                                        .putExtra("centr", (int) data[4])
                                                        .putExtra("auto", (int) data[5])
                                                        .putExtra("north", (int) data[6])
                                                        .putExtra("ubil", (int) data[7])
                                                        .putExtra("bass", (int) data[8]);
                                                sendBroadcast(in);
                                            }
                                        }
                                    } else {
                                        pingFlag++;
                                    }
                                } else {
                                    data = workWithDataBase.contactSet(id, location.getLatitude(), location.getLongitude());
                                    pointDistantion = Info.gps2m(data[0], data[1], location.getLatitude(), location.getLongitude());

                                    if(data[0]!=0.0){
                                        Intent in = new Intent("Contact_start")
                                                .putExtra("isContact", id);
                                        sendBroadcast(in);
                                    }else {
                                        idPing=0;
                                        pingFlag=0;
                                    }
                                }
                            } else {
                                if (countPingSet > 1) {
                                    System.out.println("ping set");

                                    Intent in = new Intent("Ping_Set");
                                    sendBroadcast(in);

                                    workWithDataBase.pingSet(id, driver, location.getLatitude(), location.getLongitude());
                                    countPingSet--;
                                } else {
                                    data = workWithDataBase.search(id, driver, defaultTarget, location.getLatitude(), location.getLongitude(), "0");

                                    if (data[0] != 0) {

                                        idPing = (int) data[0];
                                        distation = Info.gps2m(data[2], data[3], location.getLatitude(), location.getLongitude());

                                        Intent in = new Intent("Search")
                                                .putExtra("idPing", idPing)
                                                .putExtra("dist", distation)
                                                .putExtra("centr", (int) data[4])
                                                .putExtra("auto", (int) data[5])
                                                .putExtra("north", (int) data[6])
                                                .putExtra("ubil", (int) data[7])
                                                .putExtra("bass", (int) data[8])
                                                .putExtra("status",workWithDataBase.contactStatus(idPing));

                                        sendBroadcast(in);
                                    } else {
                                        System.out.println("no point search");

                                        exception = "0";

                                        Intent in = new Intent("Search")
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
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while(true);
            }
        });
        taskThread.start();
    }
}
