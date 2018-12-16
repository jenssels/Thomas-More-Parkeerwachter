package be.thomasmore.tm_parkeerwachter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Adapters.OvertredingAdapter;
import be.thomasmore.tm_parkeerwachter.Classes.GevolgType;
import be.thomasmore.tm_parkeerwachter.Classes.Overtreding;
import be.thomasmore.tm_parkeerwachter.Classes.Parkeerwachter;
import be.thomasmore.tm_parkeerwachter.Network.HttpUtils;
import be.thomasmore.tm_parkeerwachter.Network.JsonHelper;
import cz.msebera.android.httpclient.Header;

public class OvertredingActivity extends AppCompatActivity {
    List<Overtreding> overtredingen = new ArrayList<Overtreding>();
    List<Parkeerwachter> parkeerwachters = new ArrayList<Parkeerwachter>();
    List<GevolgType> gevolgTypes = new ArrayList<GevolgType>();
    List<String> zoekDatums = new ArrayList<String>();
    JsonHelper jsonHelper = new JsonHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overtreding);
        getOvertredingen("overtredingen?sort=datum&sortRichting=DESC");
        getParkeerwachters("Parkeerwachters");
        getGevolgTypes("GevolgTypes");
        maakZoekDatumArray();
    }

    private void getOvertredingen(String url) {
        HttpUtils.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
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

    private void getParkeerwachters(String url){
        HttpUtils.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("sels", "---------------- this is response : " + response);
                try {
                    JSONArray serverResp = new JSONArray(response.toString());
                    leesParkeerwachters(response.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void getGevolgTypes(String url){
        HttpUtils.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("sels", "---------------- this is response : " + response);
                try {
                    JSONArray serverResp = new JSONArray(response.toString());
                    leesGevolgTypes(response.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void maakZoekDatumArray(){
        zoekDatums = new ArrayList<String>();
        zoekDatums.add("Alles");
        zoekDatums.add("Laatste week");
        zoekDatums.add("Laatste maand");
        zoekDatums.add("Laatste 3 maanden");
        zoekDatums.add("Laatste 6 maanden");
        zoekDatums.add("Laatste jaar");

        toonZoekDatums();
    }

    private String getEpochFromString(String input){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        switch (input){
            case "Laatste week":
                cal.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case "Laatste maand":
                cal.add(Calendar.MONTH, -1);
                break;
            case "Laatste 3 maanden":
                cal.add(Calendar.MONTH, -3);
                break;
            case "Laatste 6 maanden":
                cal.add(Calendar.MONTH, -6);
                break;
            case "Laatste jaar":
                cal.add(Calendar.YEAR, -1);
                break;
        }

        return cal.getTime().toString();
    }

    private void toonZoekDatums(){
        Spinner spinner = findViewById(R.id.zoekDatum);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, zoekDatums);

        spinner.setAdapter(adapter);
    }

    private void leesGevolgTypes(String jsonString){
        this.gevolgTypes = jsonHelper.getGevolgTypes(jsonString);

        toonGevolgTypes();
    }

    private void leesOvertredingen(String jsonString){
        this.overtredingen = jsonHelper.getOvertredingen(jsonString);

        toonOvertredingen();
    }

    private void leesParkeerwachters(String jsonString){
        this.parkeerwachters = jsonHelper.getParkeerwachters(jsonString);

        toonParkeerwachters();
    }

    private void toonParkeerwachters(){
        Spinner spinner = findViewById(R.id.zoekParkeerwachter);
        Parkeerwachter nullSelectie = new Parkeerwachter();
        nullSelectie.setId("0");
        nullSelectie.setUsername("Alle parkeerwachters");
        parkeerwachters.add(0,nullSelectie);

        ArrayAdapter<Parkeerwachter> adapter = new ArrayAdapter<Parkeerwachter>(this, android.R.layout.simple_list_item_1, parkeerwachters);

        spinner.setAdapter(adapter);
    }

    private void toonOvertredingen(){
        OvertredingAdapter overtredingAdapter = new OvertredingAdapter(getApplicationContext(), this.overtredingen);

        ListView listView = (ListView) findViewById(R.id.overtredingenLijst);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openDetailPagina(i);
            }
        });

        listView.setAdapter(overtredingAdapter);
    }

    private void toonGevolgTypes(){
        Spinner spinner = findViewById(R.id.zoekGevolgType);
        GevolgType nullSelectie = new GevolgType();
        nullSelectie.set_id("0");
        nullSelectie.setNaam("Alle gevolgen");
        gevolgTypes.add(0,nullSelectie);

        ArrayAdapter<GevolgType> adapter = new ArrayAdapter<GevolgType>(this, android.R.layout.simple_list_item_1, gevolgTypes);

        spinner.setAdapter(adapter);
    }

    public void zoeken(View view){
        // Ophalen parameters
        EditText zoekNummerplaat = (EditText) findViewById(R.id.zoekNummerplaat);
        String nummerplaat = zoekNummerplaat.getText().toString().toLowerCase();

        Spinner zoekParkeerwachter = (Spinner) findViewById(R.id.zoekParkeerwachter);
        String parkeerwachterId = parkeerwachters.get((int)zoekParkeerwachter.getSelectedItemId()).getId();

        Spinner zoekGevolgType = (Spinner) findViewById(R.id.zoekGevolgType);
        String gevolgtypeId = gevolgTypes.get((int)zoekGevolgType.getSelectedItemId()).get_id();

        Spinner zoekDatum = (Spinner) findViewById(R.id.zoekDatum);
        String datumString = zoekDatums.get((int)zoekDatum.getSelectedItemId());

        // Aanmaken parameter string
        List<String> parameterList = new ArrayList<String>();
        if (!nummerplaat.equals("")){
            parameterList.add("nummerplaat=" + nummerplaat);
        }
        if (!parkeerwachterId.equals("0")){
            parameterList.add("parkeerwachterId=" + parkeerwachterId);
        }
        if (!gevolgtypeId.equals("0")){
            parameterList.add("gevolgTypeId=" + gevolgtypeId);
        }
        if (!datumString.equals("Alles")){
            parameterList.add("datum=" + getEpochFromString(datumString));
        }

        String parameters = "?";
        parameterList.add("sort=datum");
        parameterList.add("sortRichting=DESC");


        // String builden
        parameters += TextUtils.join("&", parameterList);

        getOvertredingen("overtredingen" + parameters);
    }

    private int getPosition(String parkeerwachterUID)
    {
        int found = -1;
        for (int i = 0; i < parkeerwachters.size(); i++){
            if (parkeerwachters.get(i).getId().equals(parkeerwachterUID)) {
                found = i;
            }
        }
        return found;
    }

    private void openDetailPagina(int index) {
        Bundle bundle = new Bundle();
        bundle.putString("id", this.overtredingen.get(index).get_id());
        Intent intent = new Intent(this, OvertredingDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


}
