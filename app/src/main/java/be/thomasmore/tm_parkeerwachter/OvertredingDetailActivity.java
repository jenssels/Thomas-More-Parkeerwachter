package be.thomasmore.tm_parkeerwachter;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Adapters.OvertredingAdapter;
import be.thomasmore.tm_parkeerwachter.Classes.GevolgType;
import be.thomasmore.tm_parkeerwachter.Classes.Overtreding;
import be.thomasmore.tm_parkeerwachter.Classes.Parkeerwachter;
import be.thomasmore.tm_parkeerwachter.Network.DownloadImageTask;
import be.thomasmore.tm_parkeerwachter.Network.HttpUtils;
import be.thomasmore.tm_parkeerwachter.Network.JsonHelper;
import cz.msebera.android.httpclient.Header;

public class OvertredingDetailActivity extends AppCompatActivity {
    JsonHelper jsonHelper = new JsonHelper();
    Overtreding overtreding;
    List<Overtreding> overtredingen;
    GevolgType gevolgType;
    Parkeerwachter parkeerwachter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overtreding_detail);
        Bundle bundel = getIntent().getExtras();
        if(bundel != null) {
            getOvertreding("overtredingen/" + bundel.getString("id"));
        }
    }

    private void getOvertreding(String url) {
        HttpUtils.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("sels", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    leesOverteding(response.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void getOvertredingen(String url) {
        HttpUtils.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("sels", "---------------- this is response : " + response);
                try {
                    JSONArray serverResp = new JSONArray(response.toString());
                    leesOvertredingenAndFilter(response.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void getGevolgType(String url) {
        HttpUtils.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("sels", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    leesGevolgType(response.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void getParkeerwachter(String url) {
        HttpUtils.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("sels", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    leesParkeerwachter(response.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void leesOverteding(String jsonString){
        this.overtreding = jsonHelper.getOvertreding(jsonString);
        getGevolgType("gevolgTypes/" + this.overtreding.getGevolgTypeId());
        getParkeerwachter("parkeerwachters/" + this.overtreding.getParkeerwachterId());
        getOvertredingen("overtredingen/?sort=datum&sortRichting=DESC&where=nummerplaat&whereValue=" + this.overtreding.getNummerplaat());

        toonOvertreding();
    }

    private void leesGevolgType(String jsonString){
        this.gevolgType = jsonHelper.getGevolgType(jsonString);

        toonGevolgType();
    }

    private void leesParkeerwachter(String jsonString){
        this.parkeerwachter = jsonHelper.getParkeerwachter(jsonString);

        toonParkeerwachter();
    }

    private void leesOvertredingenAndFilter(String jsonString){
        this.overtredingen = jsonHelper.getOvertredingen(jsonString);

        for (int i = 0; i < this.overtredingen.size(); i++) {
            if (this.overtredingen.get(i).get_id().equals(this.overtreding.get_id())){
                this.overtredingen.remove(i);
                break;
            }
        }
        Log.d("sels", this.overtredingen.toString());
        toonOvertredingen();
    }

    private void toonOvertreding(){
        // Ophalen views
        TextView detailNummerplaat = (TextView) findViewById(R.id.detail_nummerplaat);
        TextView detailDatum = (TextView) findViewById(R.id.detail_datum);
        TextView detailBreedtegraad = (TextView) findViewById(R.id.detail_breedtegraad);
        TextView detailLengtegraad = (TextView) findViewById(R.id.detail_lengtegraad);
        TextView detailOpmerking = (TextView) findViewById(R.id.detail_opmerking);
        ImageView detailNummerplaatImage = (ImageView) findViewById(R.id.detail_nummerplaatImage);

        // Views opvullen met data
        detailNummerplaat.setText(this.overtreding.getNummerplaat());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy - HH:mm");
        String datumString = "";
        Date date = this.overtreding.getDatum();
        datumString = dateFormat.format(date);

        detailDatum.setText(datumString);

        detailBreedtegraad.setText(this.overtreding.getBreedtegraad());
        detailLengtegraad.setText(this.overtreding.getLengtegraad());
        Picasso.get().load(this.overtreding.getNummerplaatUrl()).resize(300, 300)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).into(detailNummerplaatImage);
        detailOpmerking.setText(this.overtreding.getOpmerking());

    }

    private void toonGevolgType(){
        // Views ophalen
        TextView detailGevolg = findViewById(R.id.detail_gevolg);
        // Views opvullen met data
        detailGevolg.setText(this.gevolgType.getNaam());
    }

    private void toonParkeerwachter(){
        // Views ophalen
        TextView detailParkeerwachter = (TextView) findViewById(R.id.detail_parkeerwachter);
        // Views opvullen met data
        String parkeerwachterString = this.parkeerwachter.getUsername() + " - " + this.parkeerwachter.getNaam() + " " + this.parkeerwachter.getVoornaam();
        detailParkeerwachter.setText(parkeerwachterString);
    }

    private void toonOvertredingen(){
        OvertredingAdapter overtredingAdapter = new OvertredingAdapter(getApplicationContext(), this.overtredingen);

        ListView listView = (ListView) findViewById(R.id.andereOvertredingenLijst);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openDetailPagina(i);
            }
        });

        listView.setAdapter(overtredingAdapter);
    }

    private void openDetailPagina(int index) {
        Bundle bundle = new Bundle();
        bundle.putString("id", this.overtredingen.get(index).get_id());
        Intent intent = new Intent(this, OvertredingDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
