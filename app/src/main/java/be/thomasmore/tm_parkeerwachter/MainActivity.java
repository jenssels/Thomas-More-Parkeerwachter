package be.thomasmore.tm_parkeerwachter;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Classes.Parkeerwachter;
import be.thomasmore.tm_parkeerwachter.Network.HttpReader;
import be.thomasmore.tm_parkeerwachter.Network.HttpUtils;
import be.thomasmore.tm_parkeerwachter.Network.JsonHelper;
import be.thomasmore.tm_parkeerwachter.Session.Session;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    // Attributen
    private List<Parkeerwachter> parkeerwachters;
    private JsonHelper jsonHelper = new JsonHelper();

    // Overrides
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        getParkeerwachters("Parkeerwachters");
    }

    // Methoden

    private void getParkeerwachters(String url){
        HttpUtils.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("sels", "---------------- this is response : " + response);
                try {
                    JSONArray serverResp = new JSONArray(response.toString());
                    leesParkeerwachters(response.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void leesParkeerwachters(String jsonString){
        this.parkeerwachters = jsonHelper.getParkeerwachters(jsonString);

        toonParkeerwachters();
    }

    private void toonParkeerwachters(){
        Spinner spinner = findViewById(R.id.parkeerwachterSpinner);


        ArrayAdapter<Parkeerwachter> adapter = new ArrayAdapter<Parkeerwachter>(this, android.R.layout.simple_list_item_1, parkeerwachters);

        spinner.setAdapter(adapter);
    }

    public void login(View view){
        Spinner spinner = (Spinner) findViewById(R.id.parkeerwachterSpinner);
        Parkeerwachter parkeerwachter = parkeerwachters.get((int)spinner.getSelectedItemId());
        String pindcode = parkeerwachter.getPincode();

        EditText editText = (EditText) findViewById(R.id.parkeerwachterPinCode);
        String inputPincode = editText.getText().toString();

        if (inputPincode.equals(pindcode)){
            Session session = new Session(getApplicationContext());
            session.setParkeerwachterData(parkeerwachter.getId(), parkeerwachter.getUsername());
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.main_login_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void openActivity(View view) {
        Activity activity = new Activity();
        switch (view.getTag().toString()){
            case "nieuweOvertreding":
                activity = new NieuweOvertredingActivity();
                break;
            case "overtreding":
                activity = new OvertredingActivity();
                break;
            case "menu":
                activity = new MenuActivity();
                break;
        }
        final Intent intent = new Intent(this, activity.getClass());
        startActivity(intent);
        finish();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
}
