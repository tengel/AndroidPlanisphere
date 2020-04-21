package org.tengel.planisphere;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class DrawArea extends View {
    private TextPaint mTextPaint;
    private Paint mCirclePaint;

    public DrawArea(Context context) {
        super(context);
        init(null, 0);
    }

    public DrawArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DrawArea(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DrawArea, defStyle, 0);

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.BLUE);

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int contentWidth = getWidth() ;
        int contentHeight = getHeight();

        canvas.drawText("Foobar", 0, 20, mTextPaint);

        float cx = contentWidth / 2;
        float cy = contentHeight / 2;
        float s = Math.min(contentWidth, contentHeight) / 2;
        canvas.drawCircle(cx, cy, s, mCirclePaint);
    }
}
