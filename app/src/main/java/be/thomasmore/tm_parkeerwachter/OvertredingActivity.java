package be.thomasmore.tm_parkeerwachter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Adapters.OvertredingAdapter;
import be.thomasmore.tm_parkeerwachter.Classes.Overtreding;
import be.thomasmore.tm_parkeerwachter.Network.HttpUtils;
import be.thomasmore.tm_parkeerwachter.Network.JsonHelper;
import cz.msebera.android.httpclient.Header;

public class OvertredingActivity extends AppCompatActivity {
    List<Overtreding> overtredingen = new ArrayList<Overtreding>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overtreding);
        getOvertredingen("overtredingen?sort=datum&sortRichting=DESC");
    }

    private void getOvertredingen(String url) {
        HttpUtils.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("sels", "---------------- this is response : " + response);
                try {
                    JSONArray serverResp = new JSONArray(response.toString());
                    leesOvertredingen(response.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void leesOvertredingen(String jsonString){
        JsonHelper jsonHelper = new JsonHelper();
        this.overtredingen = jsonHelper.getOvertredingen(jsonString);

        toonOvertredingen();
    }

    private void toonOvertredingen(){
        OvertredingAdapter overtredingAdapter = new OvertredingAdapter(getApplicationContext(), this.overtredingen);

        ListView listView = (ListView) findViewById(R.id.overtredingenLijst);

        listView.setAdapter(overtredingAdapter);
    }

    public void zoeken(View view){
        // Ophalen parameters
        EditText zoekNummerplaat = (EditText) findViewById(R.id.zoekNummerplaat);
        String nummerplaat = zoekNummerplaat.getText().toString();
        Log.d("sels", "Nummerplaat : " + nummerplaat);
        // Aanmaken parameter string
        List<String> parameterList = new ArrayList<String>();
        if (!nummerplaat.equals("")){
            parameterList.add("nummerplaat=" + nummerplaat);
        }
        String parameters = "?";
        parameterList.add("sort=datum");
        parameterList.add("sortRichting=DESC");



        parameters += TextUtils.join("&", parameterList);

        getOvertredingen("overtredingen" + parameters);
    }
}
