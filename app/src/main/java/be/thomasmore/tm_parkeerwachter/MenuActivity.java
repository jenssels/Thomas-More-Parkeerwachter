package be.thomasmore.tm_parkeerwachter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Classes.Foto;
import be.thomasmore.tm_parkeerwachter.Session.Session;

public class MenuActivity extends AppCompatActivity {

    int uploadIndex;
    List<Foto> teUploadenFotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        uploadIndex = 0;
        teUploadenFotos = new ArrayList<Foto>();
        toonParkeerwachter();
        toonUploadKnop();
    }

    public void openNieuweOvertredingActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), NieuweOvertredingActivity.class);
        startActivity(intent);
    }

    public void openOvertredingLijst(View view) {
        Intent intent = new Intent(getApplicationContext(), OvertredingActivity.class);
        startActivity(intent);
    }

    public void logOut(View view){
        Session session = new Session(getApplicationContext());
        session.unSetParkeerwachterData();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void toonParkeerwachter(){
        Session session = new Session(getApplicationContext());
        String parkeerwachter = getString(R.string.menu_welkom_label) + " " + session.getNaam();
        TextView textView = (TextView) findViewById(R.id.menu_parkeerwachter);
        textView.setText(parkeerwachter);
    }

    private void toonUploadKnop() {
        SharedPreferences prefs = getSharedPreferences("teUploadenFotos", MODE_PRIVATE);
        String fotosString = prefs.getString("JSONFotos", "No fotos defined");
        if(!fotosString.equals("No fotos defined")) {
            Button uploadButton = (Button) findViewById(R.id.uploadFotos);
            uploadButton.setVisibility(View.VISIBLE);
        }
    }

    public void uploadFotos(View v) {
        SharedPreferences prefs = getSharedPreferences("teUploadenFotos", MODE_PRIVATE);
        String fotosString = prefs.getString("JSONFotos", "No fotos defined");
        if(!fotosString.equals("No fotos defined")) {
            Gson gson = new Gson();
            teUploadenFotos = gson.fromJson(fotosString, new TypeToken<List<Foto>>(){}.getType());
            LinearLayout progressLayout = findViewById(R.id.progressLayout);
            progressLayout.setVisibility(View.VISIBLE);
            uploadFotos();
            v.setVisibility(View.INVISIBLE);
        }
    }

    private void uploadFotos() {
        if(uploadIndex != teUploadenFotos.size()) {
            File file = new File(teUploadenFotos.get(uploadIndex).getLokaleUrl());
            ProgressBar uploadProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            final TextView fotoCount = (TextView) findViewById(R.id.fotoCount);
            final TextView progressCount = (TextView) findViewById(R.id.progressCount);
            if(file.exists()) {
                Ion.with(getApplicationContext())
                    .load("POST", "http://jenssels.ddns.net:8080/fotos/uploaden")
                    .uploadProgressBar(uploadProgressBar)
                    .uploadProgressHandler(new ProgressCallback() {
                        @Override
                        public void onProgress(long uploaded, long total) {
                            fotoCount.setText("Foto: " + (uploadIndex + 1) + " / " + teUploadenFotos.size());
                            progressCount.setText("Vooruitgang: " + uploaded + " / " + total);
                        }
                    })
                    .setMultipartFile("image", file)
                    .setMultipartParameter("imageName", teUploadenFotos.get(uploadIndex).getNaam())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            uploadIndex ++;
                            uploadFotos();
                        }
                    });
            }
        } else {
            Toast.makeText(this, "Alle foto's werden geüpload", Toast.LENGTH_LONG).show();
            resetLokaleFotos();
            LinearLayout progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
            progressLayout.setVisibility(View.INVISIBLE);
        }

    }

    private void resetLokaleFotos() {
        SharedPreferences prefs = getSharedPreferences("teUploadenFotos", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.remove("JSONFotos");
        prefsEditor.apply();
    }
}