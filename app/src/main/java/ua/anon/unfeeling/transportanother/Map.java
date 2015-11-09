package ua.anon.unfeeling.transportanother;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.example.hjk.transportanother.R;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Map extends Activity{

    private WebView mapWebView;
    private ImageButton closeContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        mapWebView = (WebView) findViewById(R.id.mapWebView);
        mapWebView.getSettings().setJavaScriptEnabled(true);
        mapWebView.loadUrl("http://carstop.in.ua/app/map.html");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        mapWebView.clearCache(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        closeContact = (ImageButton) findViewById(R.id.closeContact);
        closeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Map.this,MainActivity.class));
            }
        });
    }
}
