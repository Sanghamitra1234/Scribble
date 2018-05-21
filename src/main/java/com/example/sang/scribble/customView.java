package com.example.sang.scribble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class customView extends View {
    private Path drawPath;
    private Paint drawPaint;
    private Paint canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float brushSize,lastBrushSize;

    private ArrayList<Path> paths = new ArrayList<Path>();

    private  void init(){
        brushSize=getResources().getInteger(R.integer.medium_size);
        lastBrushSize=brushSize;

        drawPath=new Path();
        drawPaint=new Paint();

        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

    }
    protected void onDraw(Canvas canvas){
        for (Path p : paths) {
            canvas.drawPath(p, drawPaint);
        }
        canvas.drawPath(drawPath,drawPaint);

    }
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //create canvas of certain device size.
        super.onSizeChanged(w, h, oldw, oldh);

        //create Bitmap of certain w,h
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //apply bitmap to graphic to start drawing.
        drawCanvas = new Canvas(canvasBitmap);
    }
    public boolean onTouchEvent(MotionEvent motionEvent){
      float touchX=motionEvent.getX();
      float touchY=motionEvent.getY();

      switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
              drawPath.moveTo(touchX, touchY);
              break;

          case MotionEvent.ACTION_MOVE:
              drawPath.lineTo(touchX, touchY);
              break;

          case MotionEvent.ACTION_UP:
              drawPath.lineTo(touchX, touchY);
              drawCanvas.drawPath(drawPath, drawPaint);
              drawPath.reset();
              //drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
              break;

              default:
                  return false;
      }
        invalidate();
        return true;
    }

    public customView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
}
