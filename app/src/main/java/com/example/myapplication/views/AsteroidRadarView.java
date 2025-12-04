package com.example.myapplication.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

import com.example.myapplication.data.NearEarthObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AsteroidRadarView extends View {

    private Paint radarPaint, earthPaint, safeDotPaint, dangerDotPaint, textPaint;
    private List<NearEarthObject> neoList = new ArrayList<>();
    private List<Float> randomAngles = new ArrayList<>();

    private float cx, cy;
    private float maxDistance = 0;

    public AsteroidRadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        radarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        radarPaint.setColor(Color.parseColor("#3300FF00"));
        radarPaint.setStyle(Paint.Style.STROKE);
        radarPaint.setStrokeWidth(2f);
        earthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        earthPaint.setColor(Color.parseColor("#0077BE"));
        earthPaint.setStyle(Paint.Style.FILL);
        safeDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        safeDotPaint.setColor(Color.parseColor("#69F0AE"));
        safeDotPaint.setStyle(Paint.Style.FILL);
        dangerDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dangerDotPaint.setColor(Color.parseColor("#FF5252"));
        dangerDotPaint.setStyle(Paint.Style.FILL);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(24f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setNeoList(List<NearEarthObject> list) {
        this.neoList = list;

        randomAngles.clear();
        maxDistance = 0;
        Random random = new Random();

        for (NearEarthObject neo : list) {
            randomAngles.add(random.nextFloat() * 360f);
            if (!neo.getCloseApproachData().isEmpty()) {
                String distStr = neo.getCloseApproachData().get(0).getMissDistance().getKilometers();
                float dist = Float.parseFloat(distStr);
                if (dist > maxDistance) maxDistance = dist;
            }
        }
        maxDistance = maxDistance * 1.1f;

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = w / 2f;
        cy = h / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float maxRadius = Math.min(cx, cy) - 40;

        canvas.drawCircle(cx, cy, maxRadius, radarPaint);
        canvas.drawCircle(cx, cy, maxRadius * 0.66f, radarPaint);
        canvas.drawCircle(cx, cy, maxRadius * 0.33f, radarPaint);

        canvas.drawCircle(cx, cy, 20, earthPaint);
        canvas.drawText("Bumi", cx, cy + 40, textPaint);

        if (neoList == null || neoList.isEmpty() || maxDistance == 0) return;

        for (int i = 0; i < neoList.size(); i++) {
            NearEarthObject neo = neoList.get(i);
            if (neo.getCloseApproachData().isEmpty()) continue;

            String distStr = neo.getCloseApproachData().get(0).getMissDistance().getKilometers();
            float distanceKm = Float.parseFloat(distStr);

            float scaledDistance = (distanceKm / maxDistance) * maxRadius;
            if (scaledDistance < 30) scaledDistance = 30;
            float angle = randomAngles.get(i);
            float angleRad = (float) Math.toRadians(angle);

            float asteroidX = cx + (float) (scaledDistance * Math.cos(angleRad));
            float asteroidY = cy + (float) (scaledDistance * Math.sin(angleRad));

            if (distanceKm < 10000000) {
                canvas.drawCircle(asteroidX, asteroidY, 12, dangerDotPaint);
            } else {
                canvas.drawCircle(asteroidX, asteroidY, 8, safeDotPaint);
            }
        }
    }
}