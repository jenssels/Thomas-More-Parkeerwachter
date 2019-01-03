package be.thomasmore.tm_parkeerwachter.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Classes.GevolgType;
import be.thomasmore.tm_parkeerwachter.Classes.Overtreding;
import be.thomasmore.tm_parkeerwachter.Network.DownloadImageTask;
import be.thomasmore.tm_parkeerwachter.Network.HttpUtils;
import be.thomasmore.tm_parkeerwachter.Network.JsonHelper;
import be.thomasmore.tm_parkeerwachter.NieuweOvertredingActivity;
import be.thomasmore.tm_parkeerwachter.R;
import cz.msebera.android.httpclient.Header;

public class BevestigingOvertredingAdapter extends ArrayAdapter<Overtreding> {

    private final Context context;
    private final List<Overtreding> values;
    private List<GevolgType> gevolgTypes;

    public BevestigingOvertredingAdapter(Context context, List<Overtreding> values, List<GevolgType> gevolgTypes) {
        super(context, R.layout.overtredinglijstviewitem, values);
        this.context = context;
        this.values = values;
        this.gevolgTypes = gevolgTypes;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.geassocieerdeovertredinglijstviewitem, parent, false);

        final TextView textViewDatum = (TextView) rowView.findViewById(R.id.datum);
        final TextView textViewNummerplaat = (TextView) rowView.findViewById(R.id.nummerplaat);
        final TextView textViewOpmerking = (TextView) rowView.findViewById(R.id.opmerking);
        final TextView textViewGevolg = (TextView) rowView.findViewById(R.id.gevolg);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy - HH:mm");
        String datumString = "";
        Date date = values.get(position).getDatum();
        datumString = dateFormat.format(date);

        GevolgType gevolgType = new GevolgType();
        for(int i = 0; i < gevolgTypes.size(); i++) {
            if(gevolgTypes.get(i).get_id().equals(values.get(position).getGevolgTypeId())) {
                gevolgType = gevolgTypes.get(i);
            }
        }

        textViewDatum.setText(datumString);
        textViewNummerplaat.setText(values.get(position).getNummerplaat().toUpperCase());
        textViewOpmerking.setText(values.get(position).getOpmerking());
        textViewGevolg.setText(gevolgType.getNaam());

        return rowView;
    }

}


