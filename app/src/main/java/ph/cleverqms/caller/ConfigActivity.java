package ph.cleverqms.caller;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.net.HttpURLConnection;
import java.net.URL;

public class ConfigActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Button configSaveBtn;
    private TextInputLayout urlEditText, uiEditText, versionEditText, keyEditText;
    private String apiUrl, apiKey, apiVer, ui;
    private EditText _urlEditText;
    private EditText _keyEditText;
    private EditText _uiEditText;
    private EditText _verEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        sharedPreferences = getBaseContext().getSharedPreferences("com.example.kiosk.PREFS", MODE_PRIVATE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        setUpListeners();
    }

    private void setUpListeners() {
        versionEditText = findViewById(R.id.versionInputLayout);
        urlEditText = findViewById(R.id.urlInputLayout);
        keyEditText = findViewById(R.id.keyInputLayout);
        uiEditText = findViewById(R.id.uiInputLayout);

        _verEditText = versionEditText.getEditText();
        _urlEditText = urlEditText.getEditText();
        _keyEditText = keyEditText.getEditText();
        _uiEditText = uiEditText.getEditText();

        configSaveBtn = findViewById(R.id.config_save_btn);

        configSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String _apiUrl = _urlEditText.getText().toString();
                    String _apiKey = _keyEditText.getText().toString();
                    String _apiVer = _verEditText.getText().toString();
                    String _ui = _uiEditText.getText().toString();

                    if (_apiUrl.length() < 1 || _apiUrl.equals("")) {
                        urlEditText.setError("Invalid Url");
                    } else {
                        urlEditText.setError(null);
                        apiUrl = _apiUrl;
                    }

                    if (_apiKey.length() < 1 || _apiKey.equals("")) {
                        keyEditText.setError("Invalid Key");
                    } else {
                        keyEditText.setError(null);
                        apiKey = _apiKey;
                    }

                    if (_apiVer.length() < 1 || _apiVer.equals("")) {
                        versionEditText.setError("Invalid Version");
                    } else {
                        versionEditText.setError(null);
                        apiVer = _apiVer;
                    }

                    if (_ui.length() < 1 || _ui.equals("")) {
                        uiEditText.setError("Invalid UI");
                    } else {
                        uiEditText.setError(null);
                        ui = _ui;
                    }

                    if (apiUrl.isEmpty() && apiKey.isEmpty() && apiVer.isEmpty() && ui.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Invalid config", Toast.LENGTH_LONG).show();
                    } else {
                        configSaveBtn.setEnabled(false);

                        CheckUrl task = new CheckUrl();
                        task.execute(_apiUrl + "/engine/api.php");
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveConfig() {
        Log.e("SAVE", sharedPreferences.getAll().toString());
        SharedPreferences.Editor editor = getBaseContext().getSharedPreferences("com.example.kiosk.PREFS", MODE_PRIVATE).edit();

        editor.putString("apiUrl", apiUrl);
        editor.putString("apiKey", apiKey);
        editor.putString("apiVer", apiVer);
        editor.putString("ui", ui);
        editor.putBoolean("isFirstStart", false);

        editor.apply();
        this.finish();
    }


    private class CheckUrl extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con = (HttpURLConnection) new URL(params[0]).openConnection();
                con.setRequestMethod("HEAD");
                System.out.println(con.getResponseCode());

                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean bResponse = result;
            Log.e("CHECKURL", result.toString());

            if (bResponse) {
                saveConfig();
            } else {
                configSaveBtn.setEnabled(true);
                urlEditText.setError("Could not connect to API");
            }
        }
    }
}
