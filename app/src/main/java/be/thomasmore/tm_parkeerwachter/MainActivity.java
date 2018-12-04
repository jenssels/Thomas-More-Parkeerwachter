package be.thomasmore.tm_parkeerwachter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
        genereerParkeerwachters();
        // leesParkeerwachters();
        toonParkeerwachters();
    }

    // Methoden
    private void genereerParkeerwachters() {
        this.parkeerwachters = new ArrayList<Parkeerwachter>();
        this.parkeerwachters.add(new Parkeerwachter("0", "Wachter1", "1111", "Eerste", "Wachter1"));
        this.parkeerwachters.add(new Parkeerwachter("1", "Wachter2", "2222", "Tweede", "Wachter2"));
        this.parkeerwachters.add(new Parkeerwachter("2", "Wachter3", "3333", "Derde", "Wachter3"));
        this.parkeerwachters.add(new Parkeerwachter("3", "Wachter4", "4444", "Vierde", "Wachter4"));
    }

    private void toonParkeerwachters() {
        ListView parkeerwachterLijst = (ListView) findViewById(R.id.parkeerwachterLijst);

        ArrayAdapter<Parkeerwachter> parkeerwachterAdapter = new ArrayAdapter<Parkeerwachter>(this, android.R.layout.simple_list_item_1, parkeerwachters);
        parkeerwachterLijst.setAdapter(parkeerwachterAdapter);
        parkeerwachterLijst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toonDialoog();
            }
        });

    }

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

    private void leesParkeerwachters()
    {
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

    public void openOvertreding(View view) {

        final Intent intent = new Intent(this, OvertredingActivity.class);
        startActivity(intent);
    }

}
