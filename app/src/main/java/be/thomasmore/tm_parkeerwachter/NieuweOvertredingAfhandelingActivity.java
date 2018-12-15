package be.thomasmore.tm_parkeerwachter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Classes.Overtreding;
import be.thomasmore.tm_parkeerwachter.Network.HttpUtils;
import be.thomasmore.tm_parkeerwachter.Network.JsonHelper;
import cz.msebera.android.httpclient.Header;

public class NieuweOvertredingAfhandelingActivity extends AppCompatActivity {

    // Attributen
    Overtreding overtreding;
    private List<Overtreding> geassocieerdeOvertredingen;

    // Methoden

    private void haalOvertredingOp(String overtredingId) {
        HttpUtils.get("overtredingen/" + overtredingId, null, new JsonHttpResponseHandler() {
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
        JsonHelper jsonHelper = new JsonHelper();
        this.geassocieerdeOvertredingen = jsonHelper.getOvertredingen(jsonString);
    }

    // Overrides

    // Oncreate override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nieuwe_overtreding_afhandeling);
        Bundle bundel = getIntent().getExtras();
        if(bundel != null) {
            haalOvertredingOp(bundel.getString("overtredingId"));
        }
        haalGeassocieerdeOvertredingenOp();
    }
}
