package be.thomasmore.tm_parkeerwachter.Network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Classes.Parkeerwachter;

public class JsonHelper {

    // Methoden
    public List<Parkeerwachter> getParkeerwachters(String jsonTekst) {
        List<Parkeerwachter> parkeerwachters = new ArrayList<Parkeerwachter>();

        try {
            JSONArray jsonArrayParkeerwachters = new JSONArray(jsonTekst);
            for (int i = 0; i < jsonArrayParkeerwachters.length(); i++) {
                JSONObject jsonObjectParkeerwachter = jsonArrayParkeerwachters.getJSONObject(i);

                Parkeerwachter parkeerwachter = new Parkeerwachter();
                parkeerwachter.setId(jsonObjectParkeerwachter.getString("_id"));
                parkeerwachter.setUsername(jsonObjectParkeerwachter.getString("username"));
                parkeerwachter.setPincode(jsonObjectParkeerwachter.getString("pincode"));
                parkeerwachter.setVoornaam(jsonObjectParkeerwachter.getString("voornaam"));
                parkeerwachter.setNaam(jsonObjectParkeerwachter.getString("naam"));
                parkeerwachters.add(parkeerwachter);
            }
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return parkeerwachters;
    }

}

