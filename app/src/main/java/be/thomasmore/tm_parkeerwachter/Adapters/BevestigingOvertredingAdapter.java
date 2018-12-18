package be.thomasmore.tm_parkeerwachter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Classes.Overtreding;
import be.thomasmore.tm_parkeerwachter.Network.DownloadImageTask;
import be.thomasmore.tm_parkeerwachter.R;

public class BevestigingOvertredingAdapter extends ArrayAdapter<Overtreding> {

    private final Context context;
    private final List<Overtreding> values;

    public BevestigingOvertredingAdapter(Context context, List<Overtreding> values) {
        super(context, R.layout.overtredinglijstviewitem, values);
        this.context = context;
        this.values = values;
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
        Date date = values.get(position).getDatum();
        String datumString = dateFormat.format(date);

        textViewDatum.setText(datumString);
        textViewNummerplaat.setText(values.get(position).getNummerplaat().toUpperCase());
        textViewOpmerking.setText(values.get(position).getOpmerking());
        textViewGevolg.setText(values.get(position).getGevolgTypeId());

        return rowView;
    }

}


