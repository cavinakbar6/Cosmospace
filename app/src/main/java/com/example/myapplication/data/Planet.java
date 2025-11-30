package com.example.myapplication.data;

import java.io.Serializable;

public class Planet implements Serializable {

    // Info Visual
    private String name;
    private int color;
    private float orbitRadius; // Jarak dari matahari (visual)
    private float planetRadius; // Ukuran planet

    // Info Animasi (BARU)
    private float currentAngle; // Sudut posisi saat ini (0 - 360 derajat)
    private float orbitSpeed;   // Kecepatan putar

    // Variabel pembantu untuk deteksi sentuhan
    private float currentX, currentY;

    // Info Detail (Bottom Sheet)
    private String subtitle;
    private String distanceAU;
    private String orbitPeriod;
    private String diameter;
    private String temperature;
    private String moons;

    public Planet(String name, int color, float orbitRadius, float planetRadius, float orbitSpeed, String subtitle, String distanceAU, String orbitPeriod, String diameter, String temperature, String moons) {
        this.name = name;
        this.color = color;
        this.orbitRadius = orbitRadius;
        this.planetRadius = planetRadius;
        this.orbitSpeed = orbitSpeed; // Semakin besar, semakin cepat
        this.currentAngle = (float) (Math.random() * 360); // Posisi awal acak biar natural

        this.subtitle = subtitle;
        this.distanceAU = distanceAU;
        this.orbitPeriod = orbitPeriod;
        this.diameter = diameter;
        this.temperature = temperature;
        this.moons = moons;
    }

    // Getter & Setter
    public String getName() { return name; }
    public int getColor() { return color; }
    public float getOrbitRadius() { return orbitRadius; }
    public float getPlanetRadius() { return planetRadius; }

    public float getCurrentAngle() { return currentAngle; }
    public void setCurrentAngle(float angle) { this.currentAngle = angle; }

    public float getOrbitSpeed() { return orbitSpeed; }

    public float getCurrentX() { return currentX; }
    public void setCurrentX(float x) { this.currentX = x; }

    public float getCurrentY() { return currentY; }
    public void setCurrentY(float y) { this.currentY = y; }

    // Getter Data Detail
    public String getSubtitle() { return subtitle; }
    public String getDistanceAU() { return distanceAU; }
    public String getOrbitPeriod() { return orbitPeriod; }
    public String getDiameter() { return diameter; }
    public String getTemperature() { return temperature; }
    public String getMoons() { return moons; }
}