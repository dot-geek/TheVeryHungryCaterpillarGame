package com.positivestuff.theveryhungrycaterpillar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class PixelView extends View {

    protected static int mPixelSize;

    protected static int mXPixelCount;
    protected static int mYPixelCount;

    private static int mXOffset;
    private static int mYOffset;

    private Bitmap[] mPixelArray;

    private int[][] mPixelGrid;

    private final Paint mPaint = new Paint();

    private static final int mDefaultPixelSize = 36;

    public PixelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PixelView);
        mPixelSize = a.getInt(R.styleable.PixelView_pixelSize, mDefaultPixelSize);
        a.recycle();
    }

    public PixelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PixelView);
        mPixelSize = a.getInt(R.styleable.PixelView_pixelSize, mDefaultPixelSize);
        a.recycle();
    }


    public void resetPixels(int pixelcount) {
        mPixelArray = new Bitmap[pixelcount];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mXPixelCount = (int) Math.floor(w / mPixelSize);
        mYPixelCount = (int) Math.floor(h / mPixelSize);

        mXOffset = ((w - (mPixelSize * mXPixelCount)) / 2);
        mYOffset = ((h - (mPixelSize * mYPixelCount)) / 2);

        mPixelGrid = new int[mXPixelCount][mYPixelCount];
        clearPixels();
    }


    public void loadPixel(int key, Drawable pixel) {
        Bitmap bitmap = Bitmap.createBitmap(mPixelSize, mPixelSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        pixel.setBounds(0, 0, mPixelSize, mPixelSize);
        pixel.draw(canvas);

        mPixelArray[key] = bitmap;
    }


    public void clearPixels() {
        for (int x = 0; x < mXPixelCount; x++) {
            for (int y = 0; y < mYPixelCount; y++) {
                setPixel(0, x, y);
            }
        }
    }


    public void setPixel(int pixelindex, int x, int y) {
        mPixelGrid[x][y] = pixelindex;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < mXPixelCount; x += 1) {
            for (int y = 0; y < mYPixelCount; y += 1) {
                if (mPixelGrid[x][y] > 0) {
                    canvas.drawBitmap(
                            mPixelArray[mPixelGrid[x][y]],
                            mXOffset + x * mPixelSize,
                            mYOffset + y * mPixelSize,
                            mPaint
                    );
                }
            }
        }
    }

}