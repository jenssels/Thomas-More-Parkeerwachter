package be.thomasmore.tm_parkeerwachter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Classes.GevolgType;
import be.thomasmore.tm_parkeerwachter.Classes.Overtreding;
import be.thomasmore.tm_parkeerwachter.Network.HttpUtils;
import be.thomasmore.tm_parkeerwachter.Network.JsonHelper;
import cz.msebera.android.httpclient.Header;

public class NieuweOvertredingAfhandelingActivity extends AppCompatActivity {

    // Attributen
    private Overtreding overtreding;
    private List<Overtreding> geassocieerdeOvertredingen;
    private List<GevolgType> gevolgTypes;
    private JsonHelper jsonHelper;

    // Methoden

    private void haalOvertredingOp(String url) {
        HttpUtils.get(url, null, new JsonHttpResponseHandler() {
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
        overtreding = jsonHelper.getOvertreding(jsonString);
        haalGeassocieerdeOvertredingenOp();
    }

    private void haalGeassocieerdeOvertredingenOp() {
        HttpUtils.get("overtredingen?where=nummerplaat&whereValue=" + overtreding.getNummerplaat(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    JSONArray serverResp = new JSONArray(response.toString());
                    leesGeassocieerdeOvertredingen(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void leesGeassocieerdeOvertredingen(String jsonString){
        geassocieerdeOvertredingen = new ArrayList<Overtreding>();
        geassocieerdeOvertredingen = jsonHelper.getOvertredingen(jsonString);
        toonGeassocieerdeOvertredingen();
    }

    private void toonGeassocieerdeOvertredingen() {
        if(geassocieerdeOvertredingen.size() <= 1) {
            TextView geenVoorgaandeOvertredingenView = (TextView) findViewById(R.id.geenVoorgaandeOvertredingen);
            geenVoorgaandeOvertredingenView.setVisibility(View.VISIBLE);
        } else {
            ListView geassocieerdeOvertredingenView = (ListView) findViewById(R.id.geassocieerdeOvertredingen);
            ArrayAdapter<Overtreding> overtredingAdapter = new ArrayAdapter<Overtreding>(this, android.R.layout.simple_list_item_1, geassocieerdeOvertredingen);
            geassocieerdeOvertredingenView.setAdapter(overtredingAdapter);
            geassocieerdeOvertredingenView.setVisibility(View.VISIBLE);
        }
    }

    private void haalGevolgTypesOp(String url) {
        HttpUtils.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            try {
                JSONArray serverResp = new JSONArray(response.toString());
                leesGevolgTypes(response.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });
    }

    private void leesGevolgTypes(String jsonString){
        gevolgTypes = new ArrayList<GevolgType>();
        gevolgTypes = jsonHelper.getGevolgTypes(jsonString);
        toonGevolgTypes();
    }

    private void toonGevolgTypes(){
        Spinner gevolgTypesSpinner = findViewById(R.id.gevolgTypes);
        GevolgType nullSelectie = new GevolgType();
        nullSelectie.set_id("0");
        nullSelectie.setNaam("Selecteer een gevolg");
        gevolgTypes.add(0,nullSelectie);
        ArrayAdapter<GevolgType> gevolgTypeAdapter = new ArrayAdapter<GevolgType>(this, android.R.layout.simple_list_item_1, gevolgTypes);
        gevolgTypesSpinner.setAdapter(gevolgTypeAdapter);
    }

    // Overrides

    // Oncreate override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nieuwe_overtreding_afhandeling);
        jsonHelper = new JsonHelper();
        Bundle bundel = getIntent().getExtras();
        if(bundel != null) {
            String overtredingId = bundel.getString("overtredingId");
            haalOvertredingOp("overtredingen/" + overtredingId);
        }
        haalGevolgTypesOp("Gevolgtypes");
    }
}
