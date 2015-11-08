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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hjk.transportanother.R;

@SuppressWarnings("MismatchedReadAndWriteOfArray")
public class Info extends Activity implements View.OnClickListener {

    private TextView distation, north_cout, centr_cout, auto_count, ubil_cout, bass_count, lengthFromContact;
    private ImageButton sos;
    private TextView statistics;
    private final WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    private int id = 0, driver = 0;
    private int defaultTarget = 0,distantn=-1;
    private BroadcastReceiver service=null;
    private ProgressDialog progressDialog;
    private String statCenrt = "", statAuto="",statNorth="",statUbil="",statBass="";
    private boolean isCheckSos=false;
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final double[] coo = new double[2];
    private final double[] cooSos = new double[2];
    private ImageButton back;
    private Bundle bundle;
    private boolean isSendSos = false;
    private ImageView round,contactStat;
    private LinearLayout backgroungRound, backgroundContact;

    @Override
        protected void onStart() {
        super.onStart();

        mastabation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        System.out.println("save");
        bundle = new Bundle();
        outState.putString("centr", centr_cout.getText().toString());
        outState.putString("auto", auto_count.getText().toString());
        outState.putString("north", north_cout.getText().toString());
        outState.putString("ubil", ubil_cout.getText().toString());
        outState.putString("bass", bass_count.getText().toString());

        outState.putInt("driver", driver);
        outState.putInt("target", defaultTarget);
        outState.putInt("id", id);
        bundle.putAll(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(bundle!=null) {
            centr_cout.setText(bundle.getString("centr"));
            auto_count.setText(bundle.getString("auto"));
            north_cout.setText(bundle.getString("north"));
            ubil_cout.setText(bundle.getString("ubil"));
            bass_count.setText(bundle.getString("bass"));

            driver = bundle.getInt("driver");
            defaultTarget = bundle.getInt("target");
            id = bundle.getInt("id");

            setBackground(String.valueOf(defaultTarget).toCharArray());

            if(driver ==1) {

                statistics.setText(getResources().getString(R.string.count_people));

            }else if(driver ==0) {

                statistics.setText(getResources().getString(R.string.count_drivers));

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);

        id = 0;
        driver = 0;
        defaultTarget = 0;

        id = getIntent().getIntExtra("id", -1);
        driver = getIntent().getIntExtra("driver", -1);
        defaultTarget = getIntent().getIntExtra("target", -1);

        cooSos[0] = 0;
        cooSos[1] = 0;

        System.out.println("info: "+id);

        lengthFromContact = (TextView) findViewById(R.id.lengthFromContact);
        lengthFromContact.setText("");

        backgroundContact = (LinearLayout) findViewById(R.id.backgraundContact);
        backgroungRound = (LinearLayout) findViewById(R.id.backgroundRound);
        contactStat = (ImageView) findViewById(R.id.contactStat);
        contactStat.setBackgroundResource(R.drawable.cross);

        setBackground(String.valueOf(defaultTarget).toCharArray());

        statistics = (TextView) findViewById(R.id.statistics);
        statistics.setText("");

        if(driver ==1) {

            statistics.setText(getResources().getString(R.string.count_people));

        }else if(driver ==0) {

            statistics.setText(getResources().getString(R.string.count_drivers));

        }

        round = (ImageView) findViewById(R.id.round);

        progressDialog = ProgressDialog.show(this, "", "Завантажуються ваші координати... \nЦе може зайняти декілька хвилин");

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

    @Override
    public void onBackPressed() {
        System.out.println("no back");
    }

    private void workWithService() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("Info_start_online");
        filter.addAction("checking the distance");
        filter.addAction("Info_ping");
        filter.addAction("Contact_start");
        filter.addAction("Contact_end");
        filter.addAction("Contact");

        service = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("Info_start_online")) {

                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    contactStat.setBackgroundResource(R.drawable.cross);

                    distation.setText("");

                    if(driver==1) {
                        lengthFromContact.setText("");
                        lengthFromContact.setText(getResources().getString(R.string.retry_search_driver));
                    }else {
                        lengthFromContact.setText("");
                        lengthFromContact.setText(getResources().getString(R.string.retry_search_passage));
                    }

                    statCenrt = (String.valueOf(intent.getIntExtra("centr", 0)));
                    statAuto = (String.valueOf(intent.getIntExtra("auto", 0)));
                    statNorth = (String.valueOf(intent.getIntExtra("north", 0)));
                    statUbil = (String.valueOf(intent.getIntExtra("ubil", 0)));
                    statBass = (String.valueOf(intent.getIntExtra("bass", 0)));

                    viewStat();

                } else if (intent.getAction().equals("Info_ping")) {

                    distantn = intent.getIntExtra("dist", -1);

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

                    viewStat();

                    contactStat.setBackgroundResource(R.drawable.arrow_up);

                    distation.setText(distantn + "м");
                }
                else if (intent.getAction().equals("Contact_start")) {

                    viewStat();

                    contactStat.setBackgroundResource(R.drawable.ok);

                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    lengthFromContact.setText("");
                    lengthFromContact.setText(getResources().getString(R.string.contact));

                }else if(intent.getAction().equals("Contact_end")){
                    close();
                    startActivity(new Intent(Info.this, Rating.class).putExtra("id", intent.getIntExtra("id",-1)));
                }else if(intent.getAction().equals("Contact")){
                    viewStat();

                    startActivity(new Intent(Info.this, StartContact.class).putExtra("isExit", true)
                            .putExtra("id", id).putExtra("defaultTarget", defaultTarget).putExtra("driver", driver));
                }
            }
        };

        registerReceiver(service, filter);
        startService(new Intent(this, TransportAnother.class)
                .putExtra("id", id)
                .putExtra("driver", driver)
                .putExtra("defaultTarget", defaultTarget));
    }

    public void viewStat(){
        centr_cout.setText(statCenrt);
        auto_count.setText(statAuto);
        north_cout.setText(statNorth);
        ubil_cout.setText(statUbil);
        bass_count.setText(statBass);
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

    private void close() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        try {
            unregisterReceiver(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            stopService(new Intent(this, TransportAnother.class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (id != -1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    workWithDataBase.onlineEnd(id);
                }
            }).start();

        }

        startActivity(new Intent(this, MainActivity.class));

        System.gc();
        System.exit(0);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                LinearLayout headLayout = (LinearLayout) findViewById(R.id.headLayout);
                headLayout.setBackgroundColor(Color.parseColor("#2E313E"));

                close();

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

                                    startService(new Intent(Info.this, TransportAnother.class)
                                            .putExtra("isSos", true)
                                            .putExtra("id", id)
                                            .putExtra("driver", driver)
                                            .putExtra("defaultTarget", defaultTarget));
                                }
                            }else {
                                startService(new Intent(Info.this, TransportAnother.class)
                                        .putExtra("isSos",true)
                                        .putExtra("id", id)
                                        .putExtra("driver", driver)
                                        .putExtra("defaultTarget", defaultTarget));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,1,Menu.NONE,getResources().getString(R.string.map)).setIcon(R.drawable.map);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case 1:
                startActivity(new Intent(this,Map.class));
                break;
        }

        return true;
    }

    private void mastabation(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        if(width >=320&& height >=480) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.3f + 0.5f));
            sos.setLayoutParams(params);

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) (height * 0.3f + 0.5f));
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
