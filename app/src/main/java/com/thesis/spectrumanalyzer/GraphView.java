package com.thesis.spectrumanalyzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GraphView extends View {

    private long[] data;
    private Bitmap bitmap;
    private Canvas lineCanvas;
    private Matrix matrix = new Matrix();
    private Paint fadePaint = new Paint();
    private Paint paint = new Paint();
    private Rect screen = new Rect();
    private FormulasUtil.State state;
    private ArrayList<LabelView> labels;


    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        data = null;
        state = FormulasUtil.State.A_WEIGHTING;
        fadePaint.setColor(Color.argb(238, 255, 255, 255));
        fadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
        labels = new ArrayList<>();
        for(int iterator = 0 ; iterator < FormulasUtil._PERIOD_NUMBER_ ; iterator++){
            labels.add(new LabelView());
        }
    }

    public void invalidate(long[] data) {
        this.data = data;
        invalidate();
    }

    public void changeState(FormulasUtil.State state){
        this.state = state;
        invalidate();
    }

    private int getDamping(int numberOfTerce){
        switch (state) {
            case A_WEIGHTING:
                return FormulasUtil.weightingA(numberOfTerce);
            case C_WEIGHTING:
                return FormulasUtil.weightingC(numberOfTerce);
            default:
                return 0;
        }
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
        lineCanvas.drawPaint(fadePaint);
        matrix.reset();
        canvas.drawBitmap(bitmap, matrix, null);
    }

    public void drawComponents(Canvas canvas) {
        float[] drawingVector = new float[2 * data.length * 4];
        for (int iterator = 0; iterator < data.length / 2; iterator++) {
            drawingVector[iterator * 4] = iterator * 32  + 20;
            drawingVector[iterator * 4 + 2] = iterator * 32 + 20;
            int dbValue = (int) (10 * Math.log10(Math.max(1, (Math.pow(data[2 * iterator] , 2) + Math.pow(data[2 * iterator + 1] ,  2)))));
            dbValue = Math.max(0, getDamping(iterator) + dbValue);
            drawingVector[iterator * 4 + 1] = -150 + screen.height();
            drawingVector[iterator * 4 + 3] = -150 + screen.height() - (dbValue * 12 - 10);
            drawLabels(iterator, dbValue, drawingVector, canvas);
        }
        drawLines(drawingVector);
    }

    void drawLines(float[] vector){
        paint.setStrokeWidth(10f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(200, 227, 69, 53));
        lineCanvas.drawLines(vector, paint);
    }

    void drawLabels(int numberOfTerce, int dbValue, float[] vector, Canvas canvas){
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