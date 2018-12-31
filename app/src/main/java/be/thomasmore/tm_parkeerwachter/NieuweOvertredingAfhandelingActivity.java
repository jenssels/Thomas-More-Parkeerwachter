package be.thomasmore.tm_parkeerwachter;

import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
        TextView nummerplaatView = (TextView) findViewById(R.id.nummerplaat);
        nummerplaatView.setText(overtreding.getNummerplaat());
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
        geassocieerdeOvertredingen.remove(geassocieerdeOvertredingen.size() - 1);
        toonGeassocieerdeOvertredingen();
    }

    private void toonGeassocieerdeOvertredingen() {
        TextView voorgaandeOvertredingenView = (TextView) findViewById(R.id.voorgaandeOvertredingen);
        if(geassocieerdeOvertredingen.size() <= 0) {
            voorgaandeOvertredingenView.setText(R.string.nieuweOvertredingAfhandeling_geenGeassocieerdeOvertredingen);
        } else {
            voorgaandeOvertredingenView.setText(R.string.nieuweOvertredingAfhandeling_geassocieerdeOvertredingen);
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
        Spinner gevolgTypesSpinner = (Spinner) findViewById(R.id.gevolgTypes);
        GevolgType nullSelectie = new GevolgType("0", "Selecteer een gevolg");
        gevolgTypes.add(0,nullSelectie);
        ArrayAdapter<GevolgType> gevolgTypeAdapter = new ArrayAdapter<GevolgType>(this, android.R.layout.simple_list_item_1, gevolgTypes);
        gevolgTypesSpinner.setAdapter(gevolgTypeAdapter);
    }

    public void slaOvertredingOp(View v) {
        Spinner gevolgTypesSpinner = (Spinner) findViewById(R.id.gevolgTypes);
        EditText opmerkingView = (EditText) findViewById(R.id.opmerking);
        overtreding.setGevolgTypeId(gevolgTypes.get((int)gevolgTypesSpinner.getSelectedItemId()).get_id());
        overtreding.setOpmerking(opmerkingView.getText().toString());
        RequestParams requestParams = new RequestParams();
        requestParams.put("id", overtreding.get_id());
        requestParams.put("gevolgTypeId", overtreding.getGevolgTypeId());
        requestParams.put("opmerking", overtreding.getOpmerking());
        HttpUtils.put("overtredingen/" + overtreding.get_id(), requestParams, new JsonHttpResponseHandler());
        final Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void annuleerOvertreding(View v) {
        verwijderOvertreding();
        final Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

//    private void verwijderFoto(String pad) {
//        File teVerwijderen = new File(pad);
//        if(teVerwijderen.exists()) {
//            teVerwijderen.delete();
//        }
//        lokaleFotosEditor.remove(overtreding.get_id() + "_nummerplaat");
//        lokaleFotosEditor.apply();
//    }

    private void verwijderOvertreding() {
        HttpUtils.delete("overtredingen/" + overtreding.get_id(), null, new JsonHttpResponseHandler());
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
