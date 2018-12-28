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

import be.thomasmore.tm_parkeerwachter.Classes.GevolgType;
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
                parkeerwachter.setAdmin(jsonObjectParkeerwachter.getBoolean("isAdmin"));
                parkeerwachters.add(parkeerwachter);
            }
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return parkeerwachters;
    }

    public Parkeerwachter getParkeerwachter(String jsonTekst) {
        Parkeerwachter parkeerwachter = new Parkeerwachter();

        try {
            JSONObject jsonObjectParkeerwachter = new JSONObject(jsonTekst);

            parkeerwachter.setId(jsonObjectParkeerwachter.getString("_id"));
            parkeerwachter.setUsername(jsonObjectParkeerwachter.getString("username"));
            parkeerwachter.setPincode(jsonObjectParkeerwachter.getString("pincode"));
            parkeerwachter.setVoornaam(jsonObjectParkeerwachter.getString("voornaam"));
            parkeerwachter.setNaam(jsonObjectParkeerwachter.getString("naam"));
            parkeerwachter.setAdmin(jsonObjectParkeerwachter.getBoolean("isAdmin"));


        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return parkeerwachter;
    }

    public List<Overtreding> getOvertredingen(String jsonTekst){
        List<Overtreding> overtredingen = new ArrayList<Overtreding>();

        try {
            JSONArray jsonArrayOvertredingen = new JSONArray(jsonTekst);
            for (int i = 0; i < jsonArrayOvertredingen.length(); i++) {
                JSONObject jsonObjectOvertreding = jsonArrayOvertredingen.getJSONObject(i);

                if (!jsonObjectOvertreding.getString("nummerplaat").equals("null") && !jsonObjectOvertreding.getString("datum").equals("null")){
                    Overtreding overtreding = new Overtreding();
                    overtreding.set_id(jsonObjectOvertreding.getString("_id"));
                    overtreding.setBreedtegraad(jsonObjectOvertreding.getString("breedtegraad"));
                    overtreding.setLengtegraad(jsonObjectOvertreding.getString("lengtegraad"));

                    String datum = jsonObjectOvertreding.getString("datum");
                    Date date = new Date();

                    try{
                        long datumLong = Long.parseLong(datum);
                        date = new Date(datumLong);
                    }
                    catch (NumberFormatException e){
                        e.printStackTrace();
                    }

                    overtreding.setDatum(date);

                    overtreding.setNummerplaat(jsonObjectOvertreding.getString("nummerplaat"));
                    overtreding.setNummerplaatUrl(jsonObjectOvertreding.getString("nummerplaatUrl"));
                    overtreding.setOpmerking(jsonObjectOvertreding.getString("opmerking"));
                    overtreding.setParkeerwachterId(jsonObjectOvertreding.getString("parkeerwachterId"));
                    overtreding.setGevolgTypeId(jsonObjectOvertreding.getString("gevolgTypeId"));

                    Log.d("sels", "Epoch van " + overtreding.getNummerplaat() + ": " + overtreding.getDatum().getTime());

                    overtredingen.add(overtreding);
                }
            }
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return overtredingen;
    }

    public List<GevolgType> getGevolgTypes(String jsonTekst) {
        List<GevolgType> gevolgTypes = new ArrayList<GevolgType>();

        try {
            JSONArray jsonArraygevolgType = new JSONArray(jsonTekst);
            for (int i = 0; i < jsonArraygevolgType.length(); i++) {
                JSONObject jsonObjectgevolgType = jsonArraygevolgType.getJSONObject(i);

                GevolgType gevolgType = new GevolgType();
                gevolgType.set_id(jsonObjectgevolgType.getString("_id"));
                gevolgType.setNaam(jsonObjectgevolgType.getString("naam"));

                gevolgTypes.add(gevolgType);
            }
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return gevolgTypes;
    }

    public GevolgType getGevolgType(String jsonTekst) {
        GevolgType gevolgType = new GevolgType();

        try {
            JSONObject jsonObjectgevolgType = new JSONObject(jsonTekst);

                gevolgType.set_id(jsonObjectgevolgType.getString("_id"));
                gevolgType.setNaam(jsonObjectgevolgType.getString("naam"));

        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return gevolgType;
    }

    public Overtreding getOvertreding(String jsonTekst){
        Overtreding overtreding = new Overtreding();
        try {
            JSONObject overtredingJSON = new JSONObject(jsonTekst);
            overtreding.set_id(overtredingJSON.getString("_id"));
            overtreding.setBreedtegraad(overtredingJSON.getString("breedtegraad"));
            overtreding.setLengtegraad(overtredingJSON.getString("lengtegraad"));

            String datum = overtredingJSON.getString("datum");
            Date date = new Date();

            try{
                long datumLong = Long.parseLong(datum);
                Log.d("sels",datumLong + "");
                date = new Date(datumLong);
            }
            catch (NumberFormatException e){
                e.printStackTrace();
            }

            overtreding.setDatum(date);
            overtreding.setNummerplaat(overtredingJSON.getString("nummerplaat"));
            overtreding.setNummerplaatUrl(overtredingJSON.getString("nummerplaatUrl"));
            overtreding.setOpmerking(overtredingJSON.getString("opmerking"));
            overtreding.setParkeerwachterId(overtredingJSON.getString("parkeerwachterId"));
            overtreding.setGevolgTypeId(overtredingJSON.getString("gevolgTypeId"));

        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return overtreding;
    }

}

