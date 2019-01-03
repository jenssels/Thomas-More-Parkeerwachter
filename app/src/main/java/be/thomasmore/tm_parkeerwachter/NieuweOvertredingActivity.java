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
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
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
import be.thomasmore.tm_parkeerwachter.Session.Session;
import cz.msebera.android.httpclient.Header;

public class NieuweOvertredingActivity extends AppCompatActivity {

    // Attributen
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int aantalExtraFotos;
    private Overtreding overtreding;
    LocationManager locatieManager;
    private boolean isNummerplaat;
    private List<Foto> lokaleFotos;
    private String geopendeFoto;
    private ImageView imageViewVanGeopendeFoto;
    LocationListener locatieListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location locatie) {
            overtreding.setLengtegraad(locatie.getLongitude() + "");
            overtreding.setBreedtegraad(locatie.getLatitude() + "");
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

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
                final Intent intent = new Intent(that, MenuActivity.class);
                startActivity(intent);
                }
            });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void nieuweOvertreding() {
        overtreding = new Overtreding();
        try {
            locatieManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60, 0, locatieListener);
        } catch (SecurityException se) {}
        saveOvertreding("overtredingen");
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
        isNummerplaat = true;
        neemFoto();
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
        Foto foto = new Foto();
        if(isNummerplaat) {
            foto.setNaam(overtreding.get_id() + "_nummerplaat");
        } else {
            aantalExtraFotos ++;
            foto.setNaam(overtreding.get_id() + "_foto_" + aantalExtraFotos);
        }
        File image = File.createTempFile(foto.getNaam(), ".jpg", storageDir);
        String lokaalFotoPad = image.getAbsolutePath();
        foto.setLokaleUrl(lokaalFotoPad);
        foto.setOvertredingId(overtreding.get_id());
        foto.setUrl("http://jenssels.ddns.net:8080/pwPics/" + foto.getNaam() + ".jpg");
        lokaleFotos.add(foto);
        return image;
    }

    private void ontcijferNummerplaat(){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        Bitmap testBitmap = BitmapFactory.decodeFile(lokaleFotos.get(0).getLokaleUrl());
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
        verwijderLokaleFotos();
        verwijderOvertreding();
        final Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void verwijderLokaleFotos() {
        for(int i = 0; i < lokaleFotos.size(); i++) {
            File teVerwijderen = new File(lokaleFotos.get(i).getLokaleUrl());
            if(teVerwijderen.exists()) {
                teVerwijderen.delete();
            }
        }
    }

    private void verwijderOvertreding() {
        HttpUtils.delete("overtredingen/" + overtreding.get_id(), null, new JsonHttpResponseHandler());
    }

    public void bevestigNummerplaat(View v) {
        Session session = new Session(getApplicationContext());
        EditText nummerplaatView = (EditText) findViewById(R.id.nummerplaat);
        overtreding.setNummerplaat(nummerplaatView.getText().toString());
        overtreding.setNummerplaatUrl(lokaleFotos.get(0).getUrl());
        overtreding.setDatum(new Date());
        overtreding.setParkeerwachterId(session.getId());
        uploadLokaleFotos();
        slaLokaleFotosOp();
        stopLocatieUpdates();
    }

    public void extraFoto(View v) {
        isNummerplaat = false;
        neemFoto();
    }

    public void stopLocatieUpdates() {
        if(overtreding.getLengtegraad() == null || overtreding.getBreedtegraad() == null) {
            RelativeLayout volledigeLayout = findViewById(R.id.volledigeLayout);
            LinearLayout evenGeduldLayout = findViewById(R.id.evenGeduld);
            volledigeLayout.setVisibility(View.INVISIBLE);
            evenGeduldLayout.setVisibility(View.VISIBLE);
            new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        NieuweOvertredingActivity.this.stopLocatieUpdates();
                    }
                },1000);
        } else {
            locatieManager.removeUpdates(locatieListener);
            locatieManager = null;
            uploadOvertreding();
            naarNieuweOvertredingAfhandeling();
        }
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
        File file = new File(geopendeFoto);
        if(file.exists()) {
            file.delete();
        }
    }

    private void uploadOvertreding() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("id", overtreding.get_id());
        requestParams.put("nummerplaat", overtreding.getNummerplaat());
        requestParams.put("nummerplaatUrl", overtreding.getNummerplaatUrl());
        requestParams.put("datum", overtreding.getDatum().getTime());
        requestParams.put("parkeerwachterId", overtreding.getParkeerwachterId());
        HttpUtils.put("overtredingen/" + overtreding.get_id(), requestParams, new JsonHttpResponseHandler());
    }

    private void uploadLokaleFotos() {
        for (int i = 0; i < lokaleFotos.size(); i++) {
            String naam = lokaleFotos.get(i).getNaam();
            if(!naam.substring(naam.length() - 11, naam.length()).equals("nummerplaat")) {
                RequestParams requestParams = new RequestParams();
                requestParams.put("url", lokaleFotos.get(i).getUrl());
                requestParams.put("overtredingId", lokaleFotos.get(i).getOvertredingId());
                HttpUtils.post("fotos/", requestParams, new JsonHttpResponseHandler());
            }
        }
    }

    private void slaLokaleFotosOp() {
        SharedPreferences prefs = getSharedPreferences("teUploadenFotos", MODE_PRIVATE);
        String fotosString = prefs.getString("JSONFotos", "No fotos defined");
        Gson gson = new Gson();
        if(!fotosString.equals("No fotos defined")) {
            List<Foto> fotos = new ArrayList<Foto>();
            fotos = gson.fromJson(fotosString, new TypeToken<List<Foto>>(){}.getType());
            for (int i = 0; i < fotos.size(); i++) {
                lokaleFotos.add(fotos.get(i));
            }
        }
        String opTeSlagenFotosString = gson.toJson(lokaleFotos);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("JSONFotos", opTeSlagenFotosString);
        prefsEditor.apply();
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
        aantalExtraFotos = 0;
        lokaleFotos = new ArrayList<Foto>();
        locatieManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
                nummerplaatView.setImageBitmap(BitmapFactory.decodeFile(lokaleFotos.get(0).getLokaleUrl()));
                ontcijferNummerplaat();
            } else {
                final String fotopad = lokaleFotos.get(lokaleFotos.size() - 1).getLokaleUrl();
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
