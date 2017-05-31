package com.xmx.tango.module.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by The_onE on 2017/5/30.
 */

public class FlickView extends View {
    private static final float BLOCK_WIDTH = 200;
    private static final float BLOCK_HEIGHT = 200;
    private static final float HALF_WIDTH = BLOCK_WIDTH / 2;
    private static final float HALF_HEIGHT = BLOCK_HEIGHT / 2;

    private static final float TEXT_SIZE = 80;

    private Paint blockPaint;
    private Paint textPaint;
    float deltaY;

    private float x;
    private float y;
    private String[] kanaArray;

    public FlickView(Context context) {
        super(context);
        init();
    }

    private void init() {
        blockPaint = new Paint();
        x = -BLOCK_WIDTH * 2;
        y = -BLOCK_HEIGHT * 2;
        blockPaint.setColor(Color.LTGRAY);
        blockPaint.setAntiAlias(true);
        blockPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        deltaY = fontHeight / 2 - fontMetrics.bottom;
    }

    public void show(float cx, float cy, String[] kana) {
        x = cx;
        y = cy;
        kanaArray = kana;
        postInvalidate();
    }

    public void remove() {
        x = -BLOCK_WIDTH * 2;
        y = -BLOCK_HEIGHT * 2;
        kanaArray = null;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (kanaArray != null && kanaArray.length == 5) {
            canvas.drawRect(x - HALF_WIDTH, y - HALF_HEIGHT, x + HALF_WIDTH, y + HALF_HEIGHT, blockPaint);
            canvas.drawRect(x - HALF_WIDTH, y - HALF_HEIGHT * 3, x + HALF_WIDTH, y - HALF_HEIGHT, blockPaint);
            canvas.drawRect(x - HALF_WIDTH, y + HALF_HEIGHT, x + HALF_WIDTH, y + HALF_HEIGHT * 3, blockPaint);
            canvas.drawRect(x - HALF_WIDTH * 3, y - HALF_HEIGHT, x - HALF_WIDTH, y + HALF_HEIGHT, blockPaint);
            canvas.drawRect(x + HALF_WIDTH, y - HALF_HEIGHT, x + HALF_WIDTH * 3, y + HALF_HEIGHT, blockPaint);

            canvas.drawText(kanaArray[0], x, y + deltaY, textPaint);
            canvas.drawText(kanaArray[1], x - BLOCK_WIDTH, y + deltaY, textPaint);
            canvas.drawText(kanaArray[2], x, y - BLOCK_HEIGHT + deltaY, textPaint);
            canvas.drawText(kanaArray[3], x + BLOCK_WIDTH, y + deltaY, textPaint);
            canvas.drawText(kanaArray[4], x, y + BLOCK_HEIGHT + deltaY, textPaint);
        }
    }
}
