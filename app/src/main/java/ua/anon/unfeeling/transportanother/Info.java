package ua.anon.unfeeling.transportanother;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hjk.transportanother.R;

@SuppressWarnings("MismatchedReadAndWriteOfArray")
public class Info extends Activity implements View.OnClickListener{

    private TextView distation, north_cout, centr_cout, auto_count, ubil_cout, bass_count, lengthFromContact;
    private ImageButton sos;
    private final WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    private int id,driver,target;
    private BroadcastReceiver service;
    private ProgressDialog progressDialog;
    private boolean isCheckSos=false;
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final double[] coo = new double[2];
    private final double[] cooSos = new double[2];
    private ImageButton back;
    private boolean isSendSos = true;
    private ImageView round,contactStat;
    private LinearLayout backgroungRound, backgroundContact;

    @Override
    protected void onStart() {
        super.onStart();
        mastabation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);

        cooSos[0] = 0;
        cooSos[1] = 0;

        lengthFromContact = (TextView) findViewById(R.id.lengthFromContact);

        backgroundContact = (LinearLayout) findViewById(R.id.backgraundContact);
        backgroungRound = (LinearLayout) findViewById(R.id.backgroundRound);
        contactStat = (ImageView) findViewById(R.id.contactStat);

        id = getIntent().getIntExtra("id", -1);
        driver = getIntent().getIntExtra("driver", -1);
        target = getIntent().getIntExtra("target", -1);

        setBackground(String.valueOf(target).toCharArray());

        TextView statistics = (TextView) findViewById(R.id.statistics);
        round = (ImageView) findViewById(R.id.round);

        if(driver ==1) {

            statistics.setText(getResources().getString(R.string.count_people));
            progressDialog = ProgressDialog.show(this, "", "Пошук попутника");

        }else if(driver ==0) {

            statistics.setText(getResources().getString(R.string.count_drivers));
            progressDialog = ProgressDialog.show(this, "", "Пошук водія");

        }

        distation = (TextView) findViewById(R.id.testDis);

        centr_cout = (TextView) findViewById(R.id.centr_count);

        auto_count = (TextView) findViewById(R.id.auto_count);

        ubil_cout = (TextView) findViewById(R.id.ubil_count);

        north_cout = (TextView) findViewById(R.id.north_count);

        bass_count = (TextView) findViewById(R.id.bass_cout);

        workWithService();

        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(this);

        sos = (ImageButton) findViewById(R.id.sos);
        sos.setOnClickListener(this);
    }

    private void setBackground(char[] target){
        for (char aTarget : target) {
            if (aTarget == '1') {
                backgroungRound.setBackgroundColor(Color.parseColor("#fffdfc"));
                backgroundContact.setBackgroundColor(Color.parseColor("#fffdfc"));
            } else if (aTarget == '2') {
                backgroundContact.setBackgroundColor(Color.parseColor("#4b95ff"));
                backgroungRound.setBackgroundColor(Color.parseColor("#4b95ff"));
            } else if (aTarget == '3') {
                backgroungRound.setBackgroundColor(Color.parseColor("#fde06d"));
                backgroundContact.setBackgroundColor(Color.parseColor("#fde06d"));
            } else if (aTarget == '4') {
                backgroungRound.setBackgroundColor(Color.parseColor("#73e97e"));
                backgroundContact.setBackgroundColor(Color.parseColor("#73e97e"));
            } else if (aTarget == '5') {
                backgroungRound.setBackgroundColor(Color.parseColor("#ff5564"));
                backgroundContact.setBackgroundColor(Color.parseColor("#ff5564"));
            }
        }
    }

    private void workWithService() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("Info_start_online");
        filter.addAction("Info_ping");
        filter.addAction("Contact_start");

        service = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("Info_start_online")) {

                    int ping = (int) intent.getDoubleExtra("ping", 0);

                    if (ping != 0) {

                        if(progressDialog!=null) {
                            progressDialog.dismiss();
                        }

                        contactStat.setBackgroundResource(R.drawable.arrow_up);

                        final int distantn = intent.getIntExtra("dist", 0);

                        if(driver==1) {
                            lengthFromContact.setText("");
                            lengthFromContact.setText(getResources().getString(R.string.distationForPedestrian));
                        }else {
                            lengthFromContact.setText("");
                            lengthFromContact.setText(getResources().getString(R.string.distationForDriver));
                        }

                        distation.setText(distantn + "м");

                    } else {
                        if(progressDialog!=null) {
                            progressDialog.dismiss();
                        }

                        contactStat.setBackgroundResource(R.drawable.cross);

                        lengthFromContact.setText("");
                        lengthFromContact.setText(getResources().getString(R.string.retry_search));
                    }

                    if (target == 1) {
                        centr_cout.setText(String.valueOf(intent.getIntExtra("centr", 0)+1));
                        auto_count.setText(String.valueOf(intent.getIntExtra("auto", 0)));
                        north_cout.setText(String.valueOf(intent.getIntExtra("north", 0)));
                        ubil_cout.setText(String.valueOf(intent.getIntExtra("ubil", 0)));
                        bass_count.setText(String.valueOf(intent.getIntExtra("bass", 0)));
                    }else if(target==2){
                        centr_cout.setText(String.valueOf(intent.getIntExtra("centr", 0)));
                        auto_count.setText(String.valueOf(intent.getIntExtra("auto", 0)+1));
                        north_cout.setText(String.valueOf(intent.getIntExtra("north", 0)));
                        ubil_cout.setText(String.valueOf(intent.getIntExtra("ubil", 0)));
                        bass_count.setText(String.valueOf(intent.getIntExtra("bass", 0)));
                    }else if(target==3){
                        centr_cout.setText(String.valueOf(intent.getIntExtra("centr", 0)));
                        auto_count.setText(String.valueOf(intent.getIntExtra("auto", 0)));
                        north_cout.setText(String.valueOf(intent.getIntExtra("north", 0)+1));
                        ubil_cout.setText(String.valueOf(intent.getIntExtra("ubil", 0)));
                        bass_count.setText(String.valueOf(intent.getIntExtra("bass", 0)));
                    }else if(target==4){
                        centr_cout.setText(String.valueOf(intent.getIntExtra("centr", 0)));
                        auto_count.setText(String.valueOf(intent.getIntExtra("auto", 0)));
                        north_cout.setText(String.valueOf(intent.getIntExtra("north", 0)));
                        ubil_cout.setText(String.valueOf(intent.getIntExtra("ubil", 0)+1));
                        bass_count.setText(String.valueOf(intent.getIntExtra("bass", 0)));
                    }else if(target==5){
                        centr_cout.setText(String.valueOf(intent.getIntExtra("centr", 0)));
                        auto_count.setText(String.valueOf(intent.getIntExtra("auto", 0)));
                        north_cout.setText(String.valueOf(intent.getIntExtra("north", 0)));
                        ubil_cout.setText(String.valueOf(intent.getIntExtra("ubil", 0)));
                        bass_count.setText(String.valueOf(intent.getIntExtra("bass", 0)+1));
                    }

                } else if (intent.getAction().equals("Info_ping")) {

                    final int distantn = intent.getIntExtra("dist", -1);

                    if(progressDialog!=null) {
                        progressDialog.dismiss();
                    }

                    if(driver==1) {
                        lengthFromContact.setText("");
                        lengthFromContact.setText(getResources().getString(R.string.distationForPedestrian));
                    }else {
                        lengthFromContact.setText("");
                        lengthFromContact.setText(getResources().getString(R.string.distationForDriver));
                    }

                    contactStat.setBackgroundResource(R.drawable.arrow_up);

                    distation.setText(distantn + "м");

                } else if (intent.getAction().equals("Contact_start")) {

                    final int distantn = intent.getIntExtra("dist", -1);
                    if(intent.getBooleanExtra("isContact",false)) {
                        contactStat.setBackgroundResource(R.drawable.ok);

                        if(progressDialog!=null) {
                            progressDialog.dismiss();
                        }

                        if(driver==1) {
                            lengthFromContact.setText("");
                            lengthFromContact.setText(getResources().getString(R.string.distationForPedestrian));
                        }else {
                            lengthFromContact.setText("");
                            lengthFromContact.setText(getResources().getString(R.string.distationForDriver));
                        }

                        distation.setText(distantn + "м");
                    }else {
                        distation.setText(getResources().getString(R.string.contact));
                    }
                }
            }
        };

        registerReceiver(service, filter);
        startService(new Intent(this, TransportAnother.class)
                .putExtra("id", id)
                .putExtra("driver", driver)
                .putExtra("target", target));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(service!= null){
            try {
                unregisterReceiver(service);
            }catch (IllegalArgumentException ex){
                ex.printStackTrace();
            }

            try{
                stopService(new Intent(this,TransportAnother.class));
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public static int gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (180/3.14169);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
        double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
        double t3 = Math.sin(a1)* Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return (int) (6366000 * tt);
    }

    private void close(){
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", false);
        sendBroadcast(intent);
        if(service!= null){
            unregisterReceiver(service);
        }

        if (id != -1) {
            workWithDataBase.onlineEnd(id);
        }
        finish();

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        close();
        finish();
    }

    @Override
    protected void onStop() {
        close();
        finish();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                LinearLayout headLayout = (LinearLayout) findViewById(R.id.headLayout);
                headLayout.setBackgroundColor(Color.parseColor("#52596B"));

                Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", false);
                sendBroadcast(intent);

                stopService(new Intent(this, TransportAnother.class));

                if (id != -1) {
                    workWithDataBase.onlineEnd(id);
                }
                finish();

                startActivity(new Intent(this, MainActivity.class));

                break;
            case R.id.sos:
                if(!isCheckSos) {
                    isCheckSos=true;
                    sos.setBackgroundResource(R.drawable.sos_active);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(isSendSos) {
                                if (gps2m(coo[0], coo[1], cooSos[0], cooSos[1]) >= 200) {

                                    workWithDataBase.sos(id, 0, coo[0], coo[1]);

                                    cooSos[0] = coo[0];
                                    cooSos[1] = coo[1];
                                }
                            }else {
                                workWithDataBase.sos(id, 0, coo[0], coo[1]);
                                isSendSos = true;

                                cooSos[0] = coo[0];
                                cooSos[1] = coo[1];
                            }
                        }
                    }).start();
                }else {
                    isCheckSos=false;
                    sos.setBackgroundResource(R.drawable.sos_passiv);
                }
                break;
        }
    }

    private void mastabation(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        if(width >=320&& height >=480) {
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) (height * 0.3f + 0.5f));
            sos.setLayoutParams(params1);
            back.setLayoutParams(params1);

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.3f + 0.5f));
            round.setLayoutParams(params2);

            RelativeLayout stat = (RelativeLayout) findViewById(R.id.stat);

            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.4f + 0.5f));
            params3.addRule(RelativeLayout.BELOW, R.id.mainInfo);
            stat.setLayoutParams(params3);

        }
    }
}
