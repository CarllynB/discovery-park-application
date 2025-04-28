package com.example.discoveryparkmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class RouteOverlayView extends View {

    private Point startPoint;
    private Point endPoint;

    private final Paint linePaint;
    private final Paint markerPaint;

    public RouteOverlayView(Context context) {
        super(context);

        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(8f);
        linePaint.setStyle(Paint.Style.STROKE);

        markerPaint = new Paint();
        markerPaint.setColor(Color.BLUE);
        markerPaint.setStyle(Paint.Style.FILL);
    }

    public void setRoute(Point start, Point end) {
        this.startPoint = start;
        this.endPoint = end;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (startPoint != null && endPoint != null) {
            canvas.drawLine((float) startPoint.x, (float) startPoint.y,
                    (float) endPoint.x, (float) endPoint.y, linePaint);

            canvas.drawCircle((float) endPoint.x, (float) endPoint.y, 18, markerPaint);
        }
    }
}
