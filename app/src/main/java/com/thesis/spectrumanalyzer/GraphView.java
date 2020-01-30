package com.thesis.spectrumanalyzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GraphView extends View {

    private int[] data;
    private Bitmap bitmap;
    private Canvas lineCanvas;
    private Matrix matrix = new Matrix();
    private Paint fade = new Paint();
    private Paint paint = new Paint();
    private Rect screen = new Rect();
    private ArrayList<LabelView> labels;


    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        data = null;
        fade.setColor(Color.argb(222, 255, 255, 255));
        fade.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
        labels = new ArrayList<>();
        for(int iterator = 0 ; iterator < FormulasUtil._THIRDS_NUMBER_ ; iterator++){
            labels.add(new LabelView());
        }
    }

    public void invalidate(int[] data) {
        this.data = data;
        invalidate();
    }

    public void changeState(){
        lineCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        screen.set(0, 0, getWidth(), getHeight());
        paint.setTextSize(35);
        if(bitmap == null)
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        if(lineCanvas == null)
            lineCanvas = new Canvas(bitmap);
        lineCanvas.drawColor(Color.TRANSPARENT);
        if (data != null)
            drawComponents(canvas);
        lineCanvas.drawPaint(fade);
        matrix.reset();
        canvas.drawBitmap(bitmap, matrix, null);
    }

    public void drawComponents(Canvas canvas) {
        float[] drawingVector = new float[2 * data.length * 4];
        for (int iterator = 0 ; iterator < data.length ; iterator++) {
            drawingVector[iterator * 4] = iterator * 32  + 20; //calibration of position on screen
            drawingVector[iterator * 4 + 2] = iterator * 32 + 20; //calibration of position on screen
            drawingVector[iterator * 4 + 1] = -150 + screen.height(); //calibration of position on screen
            drawingVector[iterator * 4 + 3] = -150 + screen.height() - (data[iterator] * 12 - 10); //calibration of position on screen
            drawLabel(iterator, data[iterator], drawingVector, canvas);
        }
        drawLines(drawingVector);
    }

    void drawLines(float[] vector){
        paint.setStrokeWidth(10f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(200, 120, 85, 228));
        lineCanvas.drawLines(vector, paint);
    }

    void drawLabel(int numberOfTerce, int dbValue, float[] vector, Canvas canvas){
        labels.get(numberOfTerce).onRefresh(dbValue);
        int shift = 100;
        if(numberOfTerce % 2 == 0) shift += 50;
        if(numberOfTerce % 4 == 1 || numberOfTerce % 4 == 0) paint.setColor(Color.GRAY);
        else paint.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(labels.get(numberOfTerce).getValue()), vector[numberOfTerce *4 ]-7, vector[numberOfTerce * 4 + 1]  + shift, paint);
    }

    class LabelView{
        static final int _DELAY_ = 20;
        private int counter = 0;
        private int value = 0;

        int getValue() { return value; }
        void onRefresh(int newValue){
            if(newValue > value || counter == 0){
                counter = _DELAY_;
                value = newValue;
            }
            else counter--;
        }
    }
}