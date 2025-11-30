package com.example.myapplication.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

import com.example.myapplication.data.Planet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SolarSystemView extends View {

    // Tambahkan shadowPaint
    private Paint sunPaint, sunGlowPaint, orbitPaint, planetPaint, textPaint, starPaint, shadowPaint;
    private List<Planet> planetList = new ArrayList<>();
    private OnPlanetClickListener listener;

    // Variabel untuk Mode Game
    private boolean showNames = true;

    // Latar Belakang Bintang
    private float[] starX, starY;
    // [FITUR 1] Array untuk menyimpan ukuran acak tiap bintang
    private float[] starSizes;
    private int starCount = 100;
    private Random random = new Random(); // [PENTING] Randomizer

    // Pusat Layar
    private float cx, cy;

    public interface OnPlanetClickListener {
        void onPlanetClick(Planet planet);
    }

    public void setOnPlanetClickListener(OnPlanetClickListener listener) {
        this.listener = listener;
    }

    public SolarSystemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // ... (Cat Matahari & Orbit TETAP SAMA) ...
        sunPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sunPaint.setColor(Color.parseColor("#FFC107"));
        sunPaint.setStyle(Paint.Style.FILL);

        sunGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sunGlowPaint.setColor(Color.parseColor("#40FFC107"));
        sunGlowPaint.setStyle(Paint.Style.FILL);

        orbitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        orbitPaint.setColor(Color.parseColor("#33FFFFFF"));
        orbitPaint.setStyle(Paint.Style.STROKE);
        orbitPaint.setStrokeWidth(2f);

        planetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        planetPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Cat Bintang
        starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setColor(Color.WHITE);
        // Stroke width akan diatur dinamis nanti

        // [FITUR 2] Cat Bayangan (Shadow)
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setAlpha(100); // 0-255 (100 = Semi Transparan Gelap)
        shadowPaint.setStyle(Paint.Style.FILL);
    }

    public void setPlanetList(List<Planet> planets) {
        this.planetList = planets;
    }

    public void setShowNames(boolean show) {
        this.showNames = show;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = w / 2f;
        cy = h / 2f;
        generateStars(w, h);
    }

    private void generateStars(int w, int h) {
        starX = new float[starCount];
        starY = new float[starCount];
        starSizes = new float[starCount];

        for (int i = 0; i < starCount; i++) {
            starX[i] = random.nextFloat() * w;
            starY[i] = random.nextFloat() * h;
            starSizes[i] = random.nextFloat() * 3f + 1f; // Ukuran acak 1-4 pixel
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // A. Background
        canvas.drawColor(Color.parseColor("#0D0D1A"));

        // [FITUR 1: BINTANG BERKELIP]
        if (starX != null) {
            for (int i = 0; i < starCount; i++) {
                // Trik Kelip: Ubah Alpha (transparansi) secara acak setiap frame
                int randomAlpha = random.nextInt(255); // 0 (hilang) - 255 (terang)
                starPaint.setAlpha(randomAlpha);
                starPaint.setStrokeWidth(starSizes[i]);

                canvas.drawPoint(starX[i], starY[i], starPaint);
            }
        }

        // B. Matahari
        canvas.drawCircle(cx, cy, 60, sunGlowPaint);
        canvas.drawCircle(cx, cy, 35, sunPaint);

        if (planetList == null) return;

        // C. Planet & Bayangan
        for (Planet planet : planetList) {

            // 1. Update Posisi
            float newAngle = planet.getCurrentAngle() + planet.getOrbitSpeed();
            if (newAngle >= 360) newAngle -= 360;
            planet.setCurrentAngle(newAngle);

            float angleRad = (float) Math.toRadians(newAngle);
            float planetX = cx + (float) (planet.getOrbitRadius() * Math.cos(angleRad));
            float planetY = cy + (float) (planet.getOrbitRadius() * Math.sin(angleRad));

            planet.setCurrentX(planetX);
            planet.setCurrentY(planetY);

            // 2. Gambar Orbit
            canvas.drawCircle(cx, cy, planet.getOrbitRadius(), orbitPaint);

            // 3. Gambar Planet (Warna Asli)
            planetPaint.setColor(planet.getColor());
            canvas.drawCircle(planetX, planetY, planet.getPlanetRadius(), planetPaint);

            // [FITUR 2: EFEK FASE / BAYANGAN]
            // Konsep: Gambar lingkaran hitam transparan sedikit BERGESER menjauhi matahari.
            // Ini menciptakan ilusi bahwa sisi yang menghadap matahari itu terang.

            float shadowOffset = planet.getPlanetRadius() * 0.3f; // Geser dikit (30% radius)

            // Koordinat bayangan digeser menjauhi pusat (matahari)
            float shadowX = cx + (float) ((planet.getOrbitRadius() + shadowOffset) * Math.cos(angleRad));
            float shadowY = cy + (float) ((planet.getOrbitRadius() + shadowOffset) * Math.sin(angleRad));

            // Gambar lingkaran bayangan (lebih kecil sedikit biar tidak keluar jalur)
            canvas.drawCircle(shadowX, shadowY, planet.getPlanetRadius() * 0.9f, shadowPaint);

            // 4. Nama Planet
            if (showNames) {
                // Kembalikan alpha text jadi full (karena starPaint mengubah-ubah alpha global)
                textPaint.setAlpha(255);
                canvas.drawText(planet.getName(), planetX, planetY + planet.getPlanetRadius() + 40, textPaint);
            }
        }

        postInvalidateDelayed(16);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            for (Planet planet : planetList) {
                double dx = touchX - planet.getCurrentX();
                double dy = touchY - planet.getCurrentY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= planet.getPlanetRadius() + 40) {
                    if (listener != null) {
                        listener.onPlanetClick(planet);
                        return true;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }
}