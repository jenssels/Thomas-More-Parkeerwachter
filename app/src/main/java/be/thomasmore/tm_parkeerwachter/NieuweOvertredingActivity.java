package be.thomasmore.tm_parkeerwachter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.thomasmore.tm_parkeerwachter.Classes.Overtreding;
import be.thomasmore.tm_parkeerwachter.Network.DownloadImageTask;
import be.thomasmore.tm_parkeerwachter.Network.HttpUtils;
import be.thomasmore.tm_parkeerwachter.Network.JsonHelper;
import cz.msebera.android.httpclient.Header;

public class NieuweOvertredingActivity extends AppCompatActivity {

    // Attributen
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Overtreding overtreding;
    LocationManager locatieManager;
    private String lokaalNummerplaatPad;
    LocationListener locatieListener = new LocationListener() {
        public void onLocationChanged(Location locatie) {
            overtreding.setLengtegraad(locatie.getLongitude() + "");
            overtreding.setBreedtegraad(locatie.getLatitude() + "");
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nieuwe_overtreding);
        locatieManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        toonStart();
    }

    private void toonStart() {
        final Context that = this;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Neem een foto van de nummerplaat.")
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        nieuweOvertreding();
                    }
                })
                .setNegativeButton("Annuleer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final Intent intent = new Intent(that, MainActivity.class);
                        startActivity(intent);
                    }
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void nieuweOvertreding() {
        overtreding = new Overtreding();
        try {
            locatieManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locatieListener);
        } catch (SecurityException se) {}
        saveOvertreding("overtredingen");
        fotografeerNummerplaat();
    }

    private void saveOvertreding(String url) {
        HttpUtils.post(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Object", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    leesOvertreding(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void leesOvertreding(String jsonString){
        JsonHelper jsonHelper = new JsonHelper();
        overtreding = jsonHelper.getOvertreding(jsonString);
    }

    private void fotografeerNummerplaat() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File nieuweFotoFile = null;
            try {
                nieuweFotoFile = maakFotoFile();
            } catch (IOException ex) { }
            if (nieuweFotoFile != null) {
                Uri fotoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", nieuweFotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File maakFotoFile() throws IOException {
        String imageFileName = overtreding.get_id() + "nummerplaat";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        lokaalNummerplaatPad = image.getAbsolutePath();
        return image;
    }

    private void ontcijferNummerplaat(){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        Bitmap testBitmap = BitmapFactory.decodeFile(lokaalNummerplaatPad);
        Frame frame = new Frame.Builder().setBitmap(testBitmap).build();
        SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
        String nummerplaat = "";
        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
            nummerplaat += textBlock.getValue();
        }
        nummerplaat = formateerNummerplaat(nummerplaat);
        overtreding.setNummerplaat(nummerplaat);
        laatNummerplaatZien();
    }

    private String formateerNummerplaat(String nummerplaat) {
        if(nummerplaat.length() > 10) {
            return nummerplaat.substring(0, 10);
        }
        return nummerplaat;
    }

    private void laatNummerplaatZien() {
        EditText nummerplaatTekst = (EditText) findViewById(R.id.nummerplaat);
        nummerplaatTekst.setText(overtreding.getNummerplaat());
    }

    public void testUpload(View v) {
        Ion.with(this)
                .load("POST", "http://84.196.37.24:8080/uploadImage")
                .setMultipartFile("testImage", new File("/storage/emulated/0/pictures/brazil_bus_wpo.jpg"))
                .asJsonObject();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView nummerplaatView = (ImageView) findViewById(R.id.nummerplaatView);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            nummerplaatView.setImageBitmap(BitmapFactory.decodeFile(lokaalNummerplaatPad));
            ontcijferNummerplaat();
        }
    }
}
