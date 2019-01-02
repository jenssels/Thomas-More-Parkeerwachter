package be.thomasmore.tm_parkeerwachter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Classes.Foto;
import be.thomasmore.tm_parkeerwachter.Classes.Overtreding;
import be.thomasmore.tm_parkeerwachter.Network.HttpUtils;
import be.thomasmore.tm_parkeerwachter.Network.JsonHelper;
import cz.msebera.android.httpclient.Header;

public class NieuweOvertredingActivity extends AppCompatActivity {

    // Attributen
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Overtreding overtreding;
    LocationManager locatieManager;
    private boolean isNummerplaat;
    private String geopendeFoto;
    private ImageView imageViewVanGeopendeFoto;
    private String lokaalNummerplaatPad;
    private List<String> lokaleFotoPaden;
    private Foto foto;
    SharedPreferences lokaleFotos;
    SharedPreferences.Editor lokaleFotosEditor;
//    LocationListener locatieListener = new LocationListener() {
//        public void onLocationChanged(Location locatie) {
//            overtreding.setLengtegraad(locatie.getLongitude() + "");
//            overtreding.setBreedtegraad(locatie.getLatitude() + "");
//        }
//        public void onStatusChanged(String provider, int status, Bundle extras) {}
//        public void onProviderEnabled(String provider) {}
//        public void onProviderDisabled(String provider) {}
//    };

    // Methoden

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
//        try {
//            locatieManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locatieListener);
//        } catch (SecurityException se) {}
        saveOvertreding("overtredingen");
        isNummerplaat = true;
        neemFoto();
    }

    private void saveOvertreding(String url) {
        HttpUtils.post(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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

    private void neemFoto() {
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
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String fotoFileNaam = "";
        if(isNummerplaat) {
            fotoFileNaam = overtreding.get_id() + "_nummerplaat";
        } else {
            fotoFileNaam = overtreding.get_id() + "_foto_" + lokaleFotos.getAll().size();
            foto = new Foto();
            foto.setOvertredingId(overtreding.get_id());
            foto.setUrl("http://jenssels.ddns.net:8080/pwPics/" + fotoFileNaam + ".jpg");
        }
        File image = File.createTempFile(fotoFileNaam, ".jpg", storageDir);
        String lokaalFotoPad = image.getAbsolutePath();
        if(isNummerplaat) {
            lokaalNummerplaatPad = lokaalFotoPad;
        } else {
            lokaleFotoPaden.add(lokaalFotoPad);
        }
        lokaleFotosEditor.putString(fotoFileNaam, lokaalFotoPad);
        lokaleFotosEditor.apply();
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
            return "1-" + nummerplaat.substring(0, 10);
        }
        return "1-" + nummerplaat;
    }

    private void laatNummerplaatZien() {
        EditText nummerplaatTekst = (EditText) findViewById(R.id.nummerplaat);
        nummerplaatTekst.setText(overtreding.getNummerplaat());
    }

    public void annuleerOvertreding(View v) {
        verwijderFoto(lokaalNummerplaatPad, false);
        verwijderOvertreding();
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void verwijderFoto(String pad, boolean isExtraFoto) {
        File teVerwijderen = new File(pad);
        if(teVerwijderen.exists()) {
            teVerwijderen.delete();
        }
        if(isExtraFoto) {
            lokaleFotosEditor.remove(overtreding.get_id() + "_foto_" + lokaleFotoPaden.indexOf(pad));
        } else {
            lokaleFotosEditor.remove(overtreding.get_id() + "_nummerplaat");
        }
        lokaleFotosEditor.apply();
    }

    private void verwijderOvertreding() {
        HttpUtils.delete("overtredingen/" + overtreding.get_id(), null, new JsonHttpResponseHandler());
    }

    public void bevestigNummerplaat(View v) {
        EditText nummerplaatView = (EditText) findViewById(R.id.nummerplaat);
        overtreding.setNummerplaat(nummerplaatView.getText().toString());
        overtreding.setNummerplaatUrl("http://jenssels.ddns.net:8080/pwPics/" + overtreding.get_id() + "_nummerplaat.jpg");
        overtreding.setDatum(new Date());
        overtreding.setParkeerwachterId("5bfd3ff86fe8c14bb87872cc");
        // TODO: gpscoordinaten toevoegen
        uploadOvertreding();
        naarNieuweOvertredingAfhandeling();
    }

    public void extraFoto(View v) {
        isNummerplaat = false;
        neemFoto();
    }

    public void sluitFoto(View v) {
        RelativeLayout fullscreenLayout = (RelativeLayout) findViewById(R.id.fullscreen);
        RelativeLayout volledigeLayout = (RelativeLayout) findViewById(R.id.volledigeLayout);
        fullscreenLayout.setVisibility(View.INVISIBLE);
        volledigeLayout.setVisibility(View.VISIBLE);
    }

    public void verwijderFoto(View v) {
        sluitFoto(v);
        ((ViewGroup) imageViewVanGeopendeFoto.getParent()).removeView(imageViewVanGeopendeFoto);
        verwijderFoto(geopendeFoto, true);
    }

    private void uploadOvertreding() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("id", overtreding.get_id());
        requestParams.put("nummerplaat", overtreding.getNummerplaat());
        requestParams.put("nummerplaatUrl", overtreding.getNummerplaatUrl());
        requestParams.put("datum", overtreding.getDatum().getTime());
        requestParams.put("parkeerwachterId", overtreding.getParkeerwachterId());
        HttpUtils.put("overtredingen/" + overtreding.get_id(), requestParams, new JsonHttpResponseHandler());
        uploadNummerplaat(overtreding.get_id() + "_nummerplaat");
    }

    public void uploadNummerplaat(String naam) {
        Ion.with(getApplicationContext())
                .load("POST", "http://jenssels.ddns.net:8080/fotos/uploaden")
                .setMultipartFile("image", new File(lokaalNummerplaatPad))
                .setMultipartParameter("imageName", naam)
                .asJsonObject();
    }

    private void naarNieuweOvertredingAfhandeling() {
        Intent intent = new Intent(this, NieuweOvertredingAfhandelingActivity.class);
        Bundle bundel = new Bundle();
        bundel.putString("overtredingId", overtreding.get_id());
        intent.putExtras(bundel);
        startActivity(intent);
        finish();
    }

    // Overrides

    // Oncreate override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nieuwe_overtreding);
        lokaleFotoPaden = new ArrayList<String>();
        lokaleFotos = this.getSharedPreferences("teUploadenFotos", Context.MODE_PRIVATE);
        lokaleFotosEditor = lokaleFotos.edit();
//        locatieManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        toonStart();
    }

    // Override die respons van de camera app afhandelt
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final RelativeLayout volledigeLayout = (RelativeLayout) findViewById(R.id.volledigeLayout);
        ImageView nummerplaatView = (ImageView) findViewById(R.id.nummerplaatView);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            volledigeLayout.setVisibility(View.VISIBLE);
            if(isNummerplaat) {
                nummerplaatView.setImageBitmap(BitmapFactory.decodeFile(lokaalNummerplaatPad));
                ontcijferNummerplaat();
            } else {
                final String fotopad = lokaleFotoPaden.get(lokaleFotoPaden.size() - 1);
                final Bitmap foto = BitmapFactory.decodeFile(fotopad);
                LinearLayout extraFotosLayout = (LinearLayout) findViewById(R.id.extraFotos);
                final ImageView fotoView = new ImageView(NieuweOvertredingActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
                layoutParams.setMargins(10,10,10,10);
                fotoView.setLayoutParams(layoutParams);
                fotoView.setImageBitmap(foto);
                fotoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        geopendeFoto = fotopad;
                        imageViewVanGeopendeFoto = fotoView;
                        volledigeLayout.setVisibility(View.INVISIBLE);
                        RelativeLayout fullscreenLayout = (RelativeLayout) findViewById(R.id.fullscreen);
                        ImageView fullscreenFoto = (ImageView) findViewById(R.id.fullscreenFotoView);
                        fullscreenFoto.setImageBitmap(foto);
                        fullscreenFoto.setRotation(90);
                        fullscreenLayout.setVisibility(View.VISIBLE);
                    }
                });
                extraFotosLayout.addView(fotoView);
            }
        }
    }
}
