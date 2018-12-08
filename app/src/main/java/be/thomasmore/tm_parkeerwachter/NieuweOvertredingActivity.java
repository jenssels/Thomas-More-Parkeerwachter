package be.thomasmore.tm_parkeerwachter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.koushikdutta.ion.Ion;

import java.io.File;

public class NieuweOvertredingActivity extends AppCompatActivity {

    // Attributen
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nieuwe_overtreding);
        toonStart();
    }

    private void toonStart() {
        final Context that = this;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Neem een foto van de nummerplaat.")
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        testDecoder();
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

    public void testUpload(View v) {
        Ion.with(this)
                .load("POST", "http://84.196.37.24:8080/uploadImage")
                .setMultipartFile("testImage", new File("/storage/emulated/0/pictures/brazil_bus_wpo.jpg"))
                .asJsonObject();
    }

    private void genereerNummerplaat(){

    }

    private void fotografeerNummerplaat() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void testDecoder(){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        Bitmap testBitmap = BitmapFactory.decodeFile("/storage/emulated/0/pictures/nummerplaat.png");
        Frame frame = new Frame.Builder().setBitmap(testBitmap).build();
        SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
        String nummerplaat = "";

        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));

            Log.i("whut", textBlock.getValue());
            nummerplaat += textBlock.getValue();
            Toast.makeText(getBaseContext(),nummerplaat,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView nummerplaatView = (ImageView) findViewById(R.id.nummerplaatView);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            nummerplaatView.setImageBitmap(imageBitmap);
        }
    }
}
