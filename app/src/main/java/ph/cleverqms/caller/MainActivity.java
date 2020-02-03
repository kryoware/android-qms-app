package ph.cleverqms.caller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private String URL;
    private SharedPreferences sharedPreferences;
    private Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUtils = new Utils(this);
        mUtils.initialize();

        sharedPreferences = getBaseContext().getSharedPreferences("ph.cleverqms.caller.PREFS", MODE_PRIVATE);

        String apiUrl = sharedPreferences.getString("apiUrl", getResources().getString(R.string.url_default));
        String ui = sharedPreferences.getString("ui", getResources().getString(R.string.ui_default));
        String apiVer = sharedPreferences.getString("apiVer", "");
        String apiKey = sharedPreferences.getString("apiKey", "");

        URL = apiUrl + "/modules/" + ui + ".php?v=" + apiVer + "&ak=" + apiKey;

        if (getIsFirstStart()) {
            startActivity(new Intent(MainActivity.this, ConfigActivity.class));
        } else {
            setUpWebView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUtils.initialize();
    }

    @Override
    public void onBackPressed() {
    }

    private boolean getIsFirstStart() {
        return sharedPreferences.getBoolean("isFirstStart", true);
    }

    private void setUpWebView() {
        WebView mWebView = findViewById(R.id.activity_main_webview);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());

        WebSettings mWebSettings = mWebView.getSettings();

        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDisplayZoomControls(false);
        mWebSettings.setBuiltInZoomControls(false);

        mWebView.loadUrl(URL);
    }
}
