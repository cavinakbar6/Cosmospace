package com.example.myapplication.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import java.util.Locale;

public class MarsWeatherFragment extends Fragment {

    private TextView textMarsDate, textMarsMaxTemp, textMarsMinTemp, textMarsPressure;
    private EditText inputEarthTemp;
    private TextView textComparison;

    // Data fallback (dummy) berdasarkan data terakhir dari rover
    private final String MARS_SOL_DATE = "Sol 1093";
    private final double MARS_MAX_TEMP_C = -21.0;
    private final double MARS_MIN_TEMP_C = -76.0;
    private final String MARS_PRESSURE = "835 Pa";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mars_weather, container, false);

        textMarsDate = view.findViewById(R.id.text_mars_date);
        textMarsMaxTemp = view.findViewById(R.id.text_mars_max_temp);
        textMarsMinTemp = view.findViewById(R.id.text_mars_min_temp);
        textMarsPressure = view.findViewById(R.id.text_mars_pressure);
        inputEarthTemp = view.findViewById(R.id.input_earth_temp);
        textComparison = view.findViewById(R.id.text_weather_comparison);

        displayMarsWeather();
        setupTemperatureComparison();

        return view;
    }

    private void displayMarsWeather() {
        textMarsDate.setText(MARS_SOL_DATE);
        textMarsMaxTemp.setText(String.format(Locale.getDefault(), "%.1f°C", MARS_MAX_TEMP_C));
        textMarsMinTemp.setText(String.format(Locale.getDefault(), "%.1f°C", MARS_MIN_TEMP_C));
        textMarsPressure.setText(MARS_PRESSURE);
    }

    private void setupTemperatureComparison() {
        inputEarthTemp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty() || s.toString().equals("-")) {
                    textComparison.setVisibility(View.GONE);
                    return;
                }
                try {
                    double earthTemp = Double.parseDouble(s.toString());
                    double difference = earthTemp - MARS_MAX_TEMP_C;
                    String comparisonText = String.format(Locale.getDefault(),
                            "Your location is %.1f°C warmer than Mars's high today.", Math.abs(difference));
                    textComparison.setText(comparisonText);
                    textComparison.setVisibility(View.VISIBLE);
                } catch (NumberFormatException e) {
                    textComparison.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}