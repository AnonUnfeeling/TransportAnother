package ua.anon.unfeeling.transportanother;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.hjk.transportanother.R;

import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private final WorkWithDataBase workWithDataBase = new WorkWithDataBase();
    private ImageButton menu;
    private ImageButton pedestrian;
    private ImageButton driver;
    private CheckBox auto;
    private CheckBox north;
    private CheckBox anniversary;
    private CheckBox centr;
    private CheckBox angleBass;
    private String target="";
    private int id=-1;
    private int driv;
    private boolean isOnline = false;
    private LinearLayout layoutForPedestrian,layoutForDriver;
    private int[] data;
    private boolean flagForLoginProcedure = false;
    private ProgressDialog progressDialog;
    private boolean isShowInfo = false;
    private SelectedTarget selectedTarget = new SelectedTarget();

    class Test extends AsyncTask<Long, Void, int[]> {

        @Override
        protected int[] doInBackground(Long... params) {
            return  workWithDataBase.setNumberPhone(params[0]);
        }

        @Override
        protected void onPostExecute(int[] ints) {
            if(progressDialog!=null) {
                progressDialog.dismiss();
            }
            super.onPostExecute(ints);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startApplication();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(selectedTarget!=null){
            selectedTarget.cancel(true);
        }
    }

    public void startApplication(){

        mastabation();

        @SuppressWarnings("deprecation") String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }

        if (isConnectingToInternet()) {
            if(!flagForLoginProcedure) {

                Test test = new Test();
                test.execute(getNumberPhone());

                progressDialog = ProgressDialog.show(this,"","Завантаження...");

                try {
                    data = test.get();
                    id = data[0];

                    driv = data[1];
                    isOnline = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                flagForLoginProcedure = true;
            }

            if(loadIsDriver()==0){
                layoutForPedestrian = (LinearLayout) findViewById(R.id.layoutForPedestrian);
                layoutForPedestrian.setBackgroundColor(Color.parseColor("#2E313E"));

                pedestrian.setBackgroundResource(R.drawable.pedestrian_activ);

                driver.setBackgroundResource(R.drawable.driver_passiv);

                driv = 0;
            }else if(loadIsDriver()==1){
                layoutForDriver = (LinearLayout) findViewById(R.id.layoutForDriver);
                layoutForDriver.setBackgroundColor(Color.parseColor("#2E313E"));

                driver.setBackgroundResource(R.drawable.driver_activ);

                pedestrian.setBackgroundResource(R.drawable.pedestrian_passiv);

                driv = 1;
            }

        }else {
            accessError();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        auto = (CheckBox) findViewById(R.id.auto);
        auto.setOnTouchListener(this);

        north = (CheckBox) findViewById(R.id.north);
        north.setOnTouchListener(this);

        anniversary = (CheckBox) findViewById(R.id.anniversary);
        anniversary.setOnTouchListener(this);

        centr = (CheckBox) findViewById(R.id.centr);
        centr.setOnTouchListener(this);

        angleBass = (CheckBox) findViewById(R.id.angleBass);
        angleBass.setOnTouchListener(this);

        menu = (ImageButton) findViewById(R.id.menu);
        menu.setOnClickListener(this);

        pedestrian = (ImageButton) findViewById(R.id.pedestrian);
        pedestrian.setOnClickListener(this);

        driver = (ImageButton) findViewById(R.id.driver);
        driver.setOnClickListener(this);
    }

    private Long getNumberPhone(){
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return Long.valueOf(telephonyManager.getDeviceId());
    }

    private void accessError(){
        Toast.makeText(getApplicationContext(),getResources().getString(R.string.accessErorr),Toast.LENGTH_LONG).show();
    }

    private boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            @SuppressWarnings("deprecation") NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }

        return false;
    }

    private boolean checkAccess(){
        @SuppressWarnings("deprecation") String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return isConnectingToInternet() && provider.contains("gps");
    }

    @Override
    public void onBackPressed() {
        finish();
       // System.exit(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.driver:
                layoutForPedestrian = (LinearLayout) findViewById(R.id.layoutForPedestrian);
                layoutForPedestrian.setBackgroundColor(Color.parseColor("#424756"));

                pedestrian.setBackgroundResource(R.drawable.pedestrian_passiv);

                layoutForDriver = (LinearLayout) findViewById(R.id.layoutForDriver);
                layoutForDriver.setBackgroundColor(Color.parseColor("#2E313E"));

                driver.setBackgroundResource(R.drawable.driver_activ);

                saveIsDriver(1);

                driv = 1;

                break;
            case R.id.pedestrian:
                layoutForDriver = (LinearLayout) findViewById(R.id.layoutForDriver);
                layoutForDriver.setBackgroundColor(Color.parseColor("#424756"));

                layoutForPedestrian = (LinearLayout) findViewById(R.id.layoutForPedestrian);
                layoutForPedestrian.setBackgroundColor(Color.parseColor("#2E313E"));

                pedestrian.setBackgroundResource(R.drawable.pedestrian_activ);

                driver.setBackgroundResource(0);
                driver.setBackgroundResource(R.drawable.driver_passiv);

                saveIsDriver(0);

                driv = 0;

                break;
            case R.id.menu:
                LinearLayout menuLayout = (LinearLayout) findViewById(R.id.menuLayout);

                menuLayout.setBackgroundColor(Color.parseColor("#2E313E"));

                if(selectedTarget!=null){
                    selectedTarget.cancel(true);
                }

                startActivity(new Intent(this, ua.anon.unfeeling.transportanother.Settings.class).putExtra("id", id));

                break;
        }
    }

    private void counting(final int target) {
        Looper.prepare();
        if (checkAccess()) {
            selectedTarget.cancel(true);
            selectedTarget.cancel(true);

            if(isOnline) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        isShowInfo=true;

                        startActivity(new Intent(MainActivity.this, Info.class).putExtra("id", id)
                                .putExtra("driver", driv).putExtra("target", target));

                    }
                }).start();
            }else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Test test = new Test();
                        test.execute(getNumberPhone());
                        try {
                            data = test.get();
                            isShowInfo=true;

                            startActivity(new Intent(MainActivity.this, Info.class).putExtra("id", data[0])
                                    .putExtra("driver", driv).putExtra("target", target));

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } else {
            accessError();
        }
        Looper.loop();
    }

    private void saveIsDriver(int isDriver){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("isDriver",isDriver);
        editor.apply();
    }

    private int loadIsDriver (){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getInt("isDriver", 1);
    }

    @Override
    protected void onPause() {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.centr:
                        if (!centr.isChecked()) {
                            centr.setBackgroundResource(R.drawable.centr_activ);

                            target += "1";

                            if (target.toCharArray().length > 2) {
                                target = removeTarget(target.toCharArray());
                            }

                            startSearch();
                        } else {
                            target = deleteTarget('1',target.toCharArray());
                            centr.setBackgroundResource(R.drawable.centr_passiv);
                        }

                        break;
                    case R.id.auto:
                        if (!auto.isChecked()) {
                            auto.setBackgroundResource(R.drawable.auto_activ);
                            target += "2";

                            if (target.toCharArray().length > 2) {
                                target = removeTarget(target.toCharArray());
                            }

                            startSearch();
                        }
                         if(auto.isChecked()){
                            target = deleteTarget('2',target.toCharArray());
                            auto.setBackgroundResource(R.drawable.auto_passiv);
                        }

                        break;
                    case R.id.north:
                        if (!north.isChecked()) {
                            north.setBackgroundResource(R.drawable.north_activ);

                            target += 3;

                            if (target.toCharArray().length > 2) {
                                target = removeTarget(target.toCharArray());
                            }

                            startSearch();
                        } else {
                            target = deleteTarget('3',target.toCharArray());
                            north.setBackgroundResource(R.drawable.north_passiv);
                        }
                        break;
                    case R.id.anniversary:
                        if (!anniversary.isChecked()) {
                            anniversary.setBackgroundResource(R.drawable.anniversary_activ);

                            target += 4;

                            if (target.toCharArray().length > 2) {
                                target = removeTarget(target.toCharArray());
                            }

                            startSearch();
                        } else {
                            target = deleteTarget('4',target.toCharArray());
                            anniversary.setBackgroundResource(R.drawable.anniversary_passiv);
                        }

                        break;
                    case R.id.angleBass:
                        if (!angleBass.isChecked()) {
                            angleBass.setBackgroundResource(R.drawable.angle_bass_active);

                            target += 5;

                            if (target.toCharArray().length > 2) {
                                target = removeTarget(target.toCharArray());
                            }

                            startSearch();
                        } else {
                            target = deleteTarget('5',target.toCharArray());
                            angleBass.setBackgroundResource(R.drawable.angle_bass_passive);
                        }

                        break;
                }
                break;
        }
        return false;
    }

    private String deleteTarget(char index, char[] target){
        String str="";

        for (int i = 0; i < target.length; i++) {
            if(target[i]!=index){
                str+=target[i];
            }
        }

        return str;
    }

    private String removeTarget(char[] target){
        String str="";

        switch (target[0]){
            case '1':
                centr.setBackgroundResource(R.drawable.centr_passiv);
                centr.setChecked(false);
                break;
            case '2':
                auto.setBackgroundResource(R.drawable.auto_passiv);
                auto.setChecked(false);
                break;
            case '3':
                north.setBackgroundResource(R.drawable.north_passiv);
                north.setChecked(false);
                break;
            case '4':
                anniversary.setBackgroundResource(R.drawable.anniversary_passiv);
                anniversary.setChecked(false);
                break;
            case '5':
                angleBass.setBackgroundResource(R.drawable.angle_bass_passive);
                angleBass.setChecked(false);
                break;
        }

        if(target.length>0) {
            for (int i = 1; i < target.length; i++) {
                str += target[i];
            }
        }

        return str;
    }

    private void mastabation(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        if(width >=320&& height >=480) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) (height * 0.3f + 0.5f));
            menu.setLayoutParams(params);

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.3f + 0.5f));
            pedestrian.setLayoutParams(params1);
            driver.setLayoutParams(params1);

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (height * 0.13f + 0.5f));
            centr.setLayoutParams(params2);
            auto.setLayoutParams(params2);
            north.setLayoutParams(params2);
            anniversary.setLayoutParams(params2);
            angleBass.setLayoutParams(params2);
        }
    }

    private void startSearch(){
        if(selectedTarget.getStatus()== AsyncTask.Status.RUNNING||selectedTarget.getStatus()== AsyncTask.Status.PENDING) {
            selectedTarget.cancel(true);
            selectedTarget = new SelectedTarget();
            selectedTarget.execute();
        }else {
            selectedTarget.execute();
        }
    }

    class SelectedTarget extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(progressDialog!=null) {
                progressDialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < 5; i++) {
                if (isCancelled()) {
                    break;
                }

                if (i == 4) {
                    if (target.toCharArray().length > 0) {
                        counting(Integer.parseInt(target));
                    }
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}
