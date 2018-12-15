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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Classes.Parkeerwachter;
import be.thomasmore.tm_parkeerwachter.Network.HttpReader;
import be.thomasmore.tm_parkeerwachter.Network.JsonHelper;

public class MainActivity extends AppCompatActivity {
    // Attributen
    private List<Parkeerwachter> parkeerwachters;

    // Overrides
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        // genereerParkeerwachters();
        // leesParkeerwachters();
        // toonParkeerwachters();
    }

    // Methoden
//    private void toonParkeerwachters() {
//        ListView parkeerwachterLijst = (ListView) findViewById(R.id.parkeerwachterLijst);
//
//        ArrayAdapter<Parkeerwachter> parkeerwachterAdapter = new ArrayAdapter<Parkeerwachter>(this, android.R.layout.simple_list_item_1, parkeerwachters);
//        parkeerwachterLijst.setAdapter(parkeerwachterAdapter);
//        parkeerwachterLijst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                toonDialoog();
//            }
//        });
//
//    }

    private void toonDialoog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Boodschap")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getBaseContext(),"Ok",Toast.LENGTH_SHORT).show();
                    }
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void leesParkeerwachters() {
        this.parkeerwachters = new ArrayList<Parkeerwachter>();
        HttpReader httpReader = new HttpReader();
        httpReader.setOnResultReadyListener(new HttpReader.OnResultReadyListener() {
            @Override
            public void resultReady(String result) {
                JsonHelper jsonHelper = new JsonHelper();
                parkeerwachters = jsonHelper.getParkeerwachters(result);
            }
        });
        httpReader.execute("http://jenssels.ddns.net:8080/parkeerwachters");
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
