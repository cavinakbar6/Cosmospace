package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.myapplication.R;
import com.example.myapplication.data.Planet;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PlanetDetailSheet extends BottomSheetDialogFragment {

    private static final String ARG_PLANET = "arg_planet";
    private Planet planet;


    public static PlanetDetailSheet newInstance(Planet planet) {
        PlanetDetailSheet fragment = new PlanetDetailSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLANET, planet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            planet = (Planet) getArguments().getSerializable(ARG_PLANET);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sheet_planet_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (planet == null) return;


        TextView textPlanetName = view.findViewById(R.id.text_planet_name);
        TextView textPlanetSubtitle = view.findViewById(R.id.text_planet_subtitle);
        TextView textDistance = view.findViewById(R.id.text_data_distance);
        TextView textOrbit = view.findViewById(R.id.text_data_orbit);
        TextView textDiameter = view.findViewById(R.id.text_data_diameter);
        TextView textTemp = view.findViewById(R.id.text_data_temp);
        TextView textMoons = view.findViewById(R.id.text_data_moons);
        textPlanetName.setTextColor(planet.getColor());
        textPlanetName.setText(planet.getName());
        textPlanetSubtitle.setText(planet.getSubtitle());
        textDistance.setText(planet.getDistanceAU());
        textOrbit.setText(planet.getOrbitPeriod());
        textDiameter.setText(planet.getDiameter());
        textTemp.setText(planet.getTemperature());
        textMoons.setText(planet.getMoons());
    }
}