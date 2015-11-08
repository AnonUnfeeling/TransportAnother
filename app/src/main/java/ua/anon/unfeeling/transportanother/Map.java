package ua.anon.unfeeling.transportanother;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.example.hjk.transportanother.R;

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

        closeContact = (ImageButton) findViewById(R.id.closeContact);
        closeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
