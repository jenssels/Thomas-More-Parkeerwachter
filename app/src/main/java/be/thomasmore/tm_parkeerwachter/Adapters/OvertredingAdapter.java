package be.thomasmore.tm_parkeerwachter.Adapters;

import android.content.Context;
import android.nfc.FormatException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import be.thomasmore.tm_parkeerwachter.Classes.Overtreding;
import be.thomasmore.tm_parkeerwachter.Network.DownloadImageTask;
import be.thomasmore.tm_parkeerwachter.R;

public class OvertredingAdapter extends ArrayAdapter<Overtreding> {

    private final Context context;
    private final List<Overtreding> values;

    public OvertredingAdapter(Context context, List<Overtreding> values) {
        super(context, R.layout.overtredinglijstviewitem, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.overtredinglijstviewitem, parent, false);

        final ImageView imageViewNummerplaat = (ImageView) rowView.findViewById(R.id.nummerplaatUrl);
        final TextView textViewNummerplaat = (TextView) rowView.findViewById(R.id.nummerplaat);
        final TextView textViewDatum = (TextView) rowView.findViewById(R.id.datum);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy - HH:mm");
        String datumString = "";
        Date date = values.get(position).getDatum();
        datumString = dateFormat.format(date);

        new DownloadImageTask(imageViewNummerplaat)
                .execute(values.get(position).getNummerplaatUrl());
        textViewNummerplaat.setText(values.get(position).getNummerplaat().toUpperCase());
        textViewDatum.setText(datumString);


        return rowView;
    }

}


