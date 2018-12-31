package be.thomasmore.tm_parkeerwachter.Session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {
    private SharedPreferences prefs;

    public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setParkeerwachterData(String id, String naam) {
        prefs.edit().putString("id", id).apply();
        prefs.edit().putString("naam", naam).apply();
    }

    public void unSetParkeerwachterData(){
        prefs.edit().remove("id").apply();
        prefs.edit().remove("naam").apply();
    }

    public String getNaam() {
        return prefs.getString("naam","");
    }

    public String getId(){
        return prefs.getString("id","");
    }
}
