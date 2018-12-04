package be.thomasmore.tm_parkeerwachter.Network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Classes.Overtreding;
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

    public List<Overtreding> getOvertredingen(String jsonTekst){
        List<Overtreding> overtredingen = new ArrayList<Overtreding>();

        try {
            JSONArray jsonArrayOvertredingen = new JSONArray(jsonTekst);
            for (int i = 0; i < jsonArrayOvertredingen.length(); i++) {
                JSONObject jsonObjectOvertreding = jsonArrayOvertredingen.getJSONObject(i);

                Overtreding overtreding = new Overtreding();
                overtreding.set_id(jsonObjectOvertreding.getString("_id"));
                overtreding.setBreedtegraad(jsonObjectOvertreding.getString("breedtegraad"));
                overtreding.setLengtegraad(jsonObjectOvertreding.getString("lengtegraad"));

                String datum = jsonObjectOvertreding.getString("datum");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = new Date();

                try {
                    date = format.parse(datum);
                    System.out.println(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                overtreding.setDatum(date);

                overtreding.setNummerplaat(jsonObjectOvertreding.getString("nummerplaat"));
                overtreding.setNummerplaatUrl(jsonObjectOvertreding.getString("nummerplaatUrl"));
                overtreding.setOpmerking(jsonObjectOvertreding.getString("opmerking"));
                overtreding.setParkeerwachterId(jsonObjectOvertreding.getString("parkeerwachterId"));
                overtreding.setGevolgTypeId(jsonObjectOvertreding.getString("gevolgTypeId"));

                overtredingen.add(overtreding);
            }
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return overtredingen;
    }

}
