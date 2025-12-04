package com.example.myapplication.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.data.Planet;
import com.example.myapplication.views.SolarSystemView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SolarSystemFragment extends Fragment implements SolarSystemView.OnPlanetClickListener {

    private SolarSystemView solarSystemView;
    private List<Planet> planets = new ArrayList<>();

    private Button btnStartGame;
    private LinearLayout layoutGameUi;
    private TextView textQuestion, textScore;

    private boolean isGameActive = false;
    private Planet targetPlanet;
    private int currentScore = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solar_system, container, false);

        solarSystemView = view.findViewById(R.id.solar_system_view);
        btnStartGame = view.findViewById(R.id.btn_start_game);
        layoutGameUi = view.findViewById(R.id.layout_game_ui);
        textQuestion = view.findViewById(R.id.text_game_question);
        textScore = view.findViewById(R.id.text_game_score);

        createPlanets();

        solarSystemView.setPlanetList(planets);
        solarSystemView.setOnPlanetClickListener(this);

        btnStartGame.setOnClickListener(v -> {
            if (!isGameActive) {
                startGame();
            } else {
                stopGame();
            }
        });

        return view;
    }

    private void startGame() {
        isGameActive = true;
        currentScore = 0;

        btnStartGame.setText("❌ Berhenti Main");
        btnStartGame.setBackgroundColor(Color.RED);
        layoutGameUi.setVisibility(View.VISIBLE);
        textScore.setText("Skor: 0");

        solarSystemView.setShowNames(false);

        generateNewQuestion();
    }

    private void stopGame() {
        isGameActive = false;

        btnStartGame.setText("🎮 Mulai Tebak Planet");
        btnStartGame.setBackgroundColor(getResources().getColor(R.color.neon_blue, null));
        layoutGameUi.setVisibility(View.GONE);

        solarSystemView.setShowNames(true);

        Toast.makeText(getContext(), "Permainan Selesai! Skor Akhir: " + currentScore, Toast.LENGTH_LONG).show();
    }

    private void generateNewQuestion() {
        Random random = new Random();
        int index = random.nextInt(planets.size());
        targetPlanet = planets.get(index);

        textQuestion.setText("Cari Planet: " + targetPlanet.getName().toUpperCase());
    }

    @Override
    public void onPlanetClick(Planet planet) {
        if (isGameActive) {
            if (planet.getName().equals(targetPlanet.getName())) {
                currentScore += 10;
                textScore.setText("Skor: " + currentScore);
                Toast.makeText(getContext(), "BENAR! 🎉 (+10 Poin)", Toast.LENGTH_SHORT).show();
                generateNewQuestion();
            } else {
                Toast.makeText(getContext(), "Salah! Itu adalah " + planet.getName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            PlanetDetailSheet bottomSheet = PlanetDetailSheet.newInstance(planet);
            bottomSheet.show(getParentFragmentManager(), "PlanetDetail");
        }
    }

    private void createPlanets() {
        planets.clear();

        planets.add(new Planet("Merkurius", Color.parseColor("#B0B0B0"),
                80, 6, 2.0f,
                "Si Kecil Gesit", "0.4 AU", "88 hari", "4.879 km", "167°C", "0"));

        planets.add(new Planet("Venus", Color.parseColor("#E69F00"),
                120, 10, 1.5f,
                "Bintang Fajar", "0.7 AU", "225 hari", "12.104 km", "464°C", "0"));

        planets.add(new Planet("Bumi", Color.parseColor("#0077BE"),
                170, 11, 1.0f,
                "Rumah Kita", "1.0 AU", "365 hari", "12.742 km", "15°C", "1"));

        planets.add(new Planet("Mars", Color.parseColor("#D14035"),
                220, 8, 0.8f,
                "Planet Merah", "1.5 AU", "687 hari", "6.779 km", "-63°C", "2"));

        planets.add(new Planet("Jupiter", Color.parseColor("#C88B3A"),
                300, 25, 0.5f,
                "Raksasa Gas", "5.2 AU", "12 tahun", "139.820 km", "-108°C", "79"));

        planets.add(new Planet("Saturnus", Color.parseColor("#E3D081"),
                390, 22, 0.4f,
                "Cincin Indah", "9.5 AU", "29 tahun", "116.460 km", "-139°C", "82"));

        planets.add(new Planet("Uranus", Color.parseColor("#4FD0E7"),
                470, 18, 0.3f,
                "Raksasa Es", "19.2 AU", "84 tahun", "50.724 km", "-197°C", "27"));

        planets.add(new Planet("Neptunus", Color.parseColor("#3E54E8"),
                540, 17, 0.2f,
                "Si Pembuat Badai", "30.1 AU", "165 tahun", "49.244 km", "-201°C", "14"));
    }
}