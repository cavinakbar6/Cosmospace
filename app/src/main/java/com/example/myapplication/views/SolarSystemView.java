package com.example.myapplication.views;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

import com.example.myapplication.data.Planet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SolarSystemView extends View {

    private Paint sunCorePaint, sunHaloPaint, sunGlowPaint;
    private Paint orbitPaint, planetPaint, textPaint, starPaint, shadowPaint;
    private Paint dateTextPaint, yearTextPaint;
    private List<Planet> planetList = new ArrayList<>();
    private OnPlanetClickListener listener;
    private boolean showNames = true;
    private float[] starX, starY, starSize;
    private int[] starAlpha;
    private int starCount = 150;
    private float[] asteroidX, asteroidY, asteroidAngle, asteroidRadius;
    private int asteroidCount = 300;
    private Random random = new Random();
    private float cx, cy;
    private Calendar simulationCalendar;
    private SimpleDateFormat dateFormat;
    private int currentYearCounter = 1;
    private float lastEarthAngle = 0;
    private Paint asteroidPaint;
    public interface OnPlanetClickListener {
        void onPlanetClick(Planet planet);
    }
    public void setOnPlanetClickListener(OnPlanetClickListener listener) {
        this.listener = listener;
    }

    public SolarSystemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        init();
    }

    private void init() {
        sunCorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sunCorePaint.setColor(Color.WHITE);
        sunCorePaint.setStyle(Paint.Style.FILL);

        sunHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sunHaloPaint.setColor(Color.parseColor("#FFD700"));
        sunHaloPaint.setStyle(Paint.Style.FILL);

        sunGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sunGlowPaint.setColor(Color.parseColor("#40FFC107"));
        sunGlowPaint.setStyle(Paint.Style.FILL);
        sunGlowPaint.setMaskFilter(new BlurMaskFilter(60, BlurMaskFilter.Blur.NORMAL));

        orbitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        orbitPaint.setColor(Color.parseColor("#20FFFFFF"));
        orbitPaint.setStyle(Paint.Style.STROKE);
        orbitPaint.setStrokeWidth(1.5f);

        planetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        planetPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setShadowLayer(5, 0, 0, Color.BLACK);

        starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setColor(Color.WHITE);

        asteroidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        asteroidPaint.setColor(Color.parseColor("#60888888"));
        asteroidPaint.setStyle(Paint.Style.FILL);

        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL);

        dateTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dateTextPaint.setColor(Color.parseColor("#00E5FF"));
        dateTextPaint.setTextSize(40f);
        dateTextPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        dateTextPaint.setShadowLayer(10, 0, 0, Color.BLUE);

        yearTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yearTextPaint.setColor(Color.LTGRAY);
        yearTextPaint.setTextSize(24f);

        simulationCalendar = Calendar.getInstance();
        simulationCalendar.set(Calendar.YEAR, 2025);
        dateFormat = new SimpleDateFormat("dd MMMM", new Locale("id", "ID"));
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
        generateSpaceObjects(w, h);
    }

    private void generateSpaceObjects(int w, int h) {
        starX = new float[starCount];
        starY = new float[starCount];
        starSize = new float[starCount];
        starAlpha = new int[starCount];

        for (int i = 0; i < starCount; i++) {
            starX[i] = random.nextFloat() * w;
            starY[i] = random.nextFloat() * h;
            starSize[i] = random.nextFloat() * 3f;
            starAlpha[i] = random.nextInt(255);
        }

        asteroidX = new float[asteroidCount];
        asteroidY = new float[asteroidCount];
        asteroidAngle = new float[asteroidCount];
        asteroidRadius = new float[asteroidCount];

        for (int i = 0; i < asteroidCount; i++) {
            asteroidAngle[i] = random.nextFloat() * 360;
            asteroidRadius[i] = 320 + random.nextFloat() * 50;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RadialGradient bgGradient = new RadialGradient(cx, cy, Math.max(cx, cy),
                new int[]{Color.parseColor("#0B0D17"), Color.BLACK},
                null, Shader.TileMode.CLAMP);
        Paint bgPaint = new Paint();
        bgPaint.setShader(bgGradient);
        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);

        if (starX != null) {
            for (int i = 0; i < starCount; i++) {
                if (random.nextInt(10) > 8) {
                    starAlpha[i] = random.nextInt(200) + 55;
                }
                starPaint.setAlpha(starAlpha[i]);
                canvas.drawCircle(starX[i], starY[i], starSize[i], starPaint);
            }
        }

        if (asteroidAngle != null) {
            for (int i = 0; i < asteroidCount; i++) {
                asteroidAngle[i] += 0.1f;
                float rad = (float) Math.toRadians(asteroidAngle[i]);
                float ax = cx + (float) (asteroidRadius[i] * Math.cos(rad));
                float ay = cy + (float) (asteroidRadius[i] * Math.sin(rad));
                canvas.drawCircle(ax, ay, random.nextFloat() * 2 + 1, asteroidPaint);
            }
        }

        canvas.drawCircle(cx, cy, 80, sunGlowPaint);
        canvas.drawCircle(cx, cy, 45, sunHaloPaint);
        canvas.drawCircle(cx, cy, 35, sunCorePaint);

        if (planetList == null) return;

        for (Planet planet : planetList) {

            if (planet.getName().equalsIgnoreCase("Bumi")) {
                calculateDateFromEarth(planet.getCurrentAngle());
            }

            float newAngle = planet.getCurrentAngle() + planet.getOrbitSpeed();
            if (newAngle >= 360) newAngle -= 360;
            planet.setCurrentAngle(newAngle);

            float angleRad = (float) Math.toRadians(newAngle);
            float planetX = cx + (float) (planet.getOrbitRadius() * Math.cos(angleRad));
            float planetY = cy + (float) (planet.getOrbitRadius() * Math.sin(angleRad));

            planet.setCurrentX(planetX);
            planet.setCurrentY(planetY);

            canvas.drawCircle(cx, cy, planet.getOrbitRadius(), orbitPaint);

            planetPaint.setColor(planet.getColor());
            canvas.drawCircle(planetX, planetY, planet.getPlanetRadius(), planetPaint);

            float lightX = planetX - (float)(Math.cos(angleRad) * planet.getPlanetRadius() * 0.5);
            float lightY = planetY - (float)(Math.sin(angleRad) * planet.getPlanetRadius() * 0.5);
            RadialGradient shadowGradient = new RadialGradient(
                    lightX, lightY,
                    planet.getPlanetRadius() * 1.8f,
                    new int[]{Color.TRANSPARENT, Color.parseColor("#AA000000")},
                    new float[]{0.2f, 1.0f},
                    Shader.TileMode.CLAMP);

            shadowPaint.setShader(shadowGradient);
            canvas.drawCircle(planetX, planetY, planet.getPlanetRadius(), shadowPaint);

            if (showNames) {
                textPaint.setAlpha(255);
                canvas.drawText(planet.getName(), planetX, planetY + planet.getPlanetRadius() + 40, textPaint);
            }
        }

        drawDateHUD(canvas);
        postInvalidateDelayed(16);
    }

    private void calculateDateFromEarth(float earthAngle) {
        if (lastEarthAngle > 300 && earthAngle < 50) {
            currentYearCounter++;
        }
        lastEarthAngle = earthAngle;
        float adjustedAngle = (earthAngle + 90) % 360;
        int dayOfYear = (int) ((adjustedAngle / 360f) * 365f);
        simulationCalendar.set(Calendar.DAY_OF_YEAR, dayOfYear + 1);
        simulationCalendar.set(Calendar.YEAR, 2025 + (currentYearCounter - 1));
    }

    private void drawDateHUD(Canvas canvas) {
        String dateString = dateFormat.format(simulationCalendar.getTime());
        String yearString = "Tahun Simulasi ke-" + currentYearCounter;
        canvas.drawText(dateString, 60, 100, dateTextPaint);
        canvas.drawText(yearString, 60, 140, yearTextPaint);
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