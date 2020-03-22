package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    public EarthquakeAdapter(@NonNull Context context, @NonNull List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) convertView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.item, parent, false);

        Earthquake earthquake = getItem(position);

        if (earthquake != null) {
            final TextView magTV = convertView.findViewById(R.id.mag_tv);
            final TextView ofTV = convertView.findViewById(R.id.of_tv);
            final TextView placeTV = convertView.findViewById(R.id.place_tv);
            final TextView dateTV = convertView.findViewById(R.id.date_tv);

            magTV.setText(String.format("%.1f", earthquake.getMag()));
            ofTV.setText(earthquake.getOf());
            placeTV.setText(earthquake.getPlace());
            dateTV.setText(earthquake.getDate());

            // Set the proper background color on the magnitude circle.
            // Get the appropriate background color based on the current earthquake magnitude
            int magnitudeColor = getMagnitudeColor(earthquake.getMag() % 6.0 * 10.0); //TODO remove extra operations

            // Fetch the background from the TextView, which is a GradientDrawable.
            GradientDrawable magnitudeCircle = (GradientDrawable) magTV.getBackground();

            // Set the color on the magnitude circle
            magnitudeCircle.setColor(magnitudeColor);

            convertView.setOnClickListener(event -> getContext().startActivity(
                    new Intent(Intent.ACTION_VIEW).setData(Uri.parse(earthquake.getUrl()))));
        }

        return convertView;
    }

    private int getMagnitudeColor(double magnitude) {
        int mag = (int) Math.floor(magnitude);

        return ContextCompat.getColor(getContext(),
                mag == 0 || mag == 1 ? R.color.magnitude1
                        : mag == 2 ? R.color.magnitude2
                        : mag == 3 ? R.color.magnitude3
                        : mag == 4 ? R.color.magnitude4
                        : mag == 5 ? R.color.magnitude5
                        : mag == 6 ? R.color.magnitude6
                        : mag == 7 ? R.color.magnitude7
                        : mag == 8 ? R.color.magnitude8
                        : mag == 9 ? R.color.magnitude9
                        : R.color.magnitude10plus
        );
    }
}