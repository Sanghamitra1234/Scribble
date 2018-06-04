package com.example.sang.scribble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class customView extends View {
    private Path drawPath;
    private Paint drawPaint;
    public int selectedColor;
    private Paint canvasPaint;
    public int paintColor, prev_paintColor;
    private Canvas drawCanvas;
    private boolean erase = false;
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;

    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private ArrayList<Integer> colors = new ArrayList<Integer>();
    private ArrayList<Integer> colorsWith = new ArrayList<Integer>();
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;


    public void init() {
        selectedColor = getResources().getColor(R.color.col);
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        paintColor = 0xFF000000;

        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @SuppressLint("ResourceAsColor")
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        paths.add(drawPath);

    }

    public void setPaintColor(int color) {

        this.drawPaint.setColor(color);
        this.paintColor = color;
        canvasPaint.setColor(paintColor);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //create canvas of certain device size.
        super.onSizeChanged(w, h, oldw, oldh);

        //create Bitmap of certain w,h
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //apply bitmap to graphic to start drawing.
        drawCanvas = new Canvas(canvasBitmap);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(touchX, touchY);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                touch_move(touchX, touchY);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;

            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void touch_start(float x, float y) {
        undonePaths.clear();
        drawPath.reset();
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_up() {
        drawPath.lineTo(mX, mY);
        drawCanvas.drawPath(drawPath, drawPaint);
        paths.add(drawPath);
        drawPath = new Path();
        //eraseAll(false);
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            drawPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    public void onClickUndo() {
        if (paths.size() > 0) {
         
        } else {
            Toast.makeText(getContext(), "Undo unknown", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void eraseAll() {
        drawPath = new Path();
        paths.clear();
        drawCanvas.drawColor(Color.WHITE);
        invalidate();
    }

    public void erase(boolean isErase) {
        if (isErase)
            drawPaint.setColor(Color.WHITE);
        invalidate();
    }

    public void noterase(boolean notisErase) {
        if (notisErase)
            drawPaint.setColor(paintColor);
        invalidate();
    }

    public customView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

}
