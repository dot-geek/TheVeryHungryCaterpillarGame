package com.positivestuff.theveryhungrycaterpillar;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class CaterpillarGameView extends PixelView {

    public CaterpillarGameView(Context context) {
        this(context, null);
    }
    public static final String TAG = "The Caterpillar Game : CaterpillarGameView";

    public int mMode = MODE_READY;
    public static final int MODE_PAUSE = 0;
    public static final int MODE_READY = 1;
    public static final int MODE_RUNNING = 2;
    public static final int MODE_LOST = 3;

    public int mDirection = DIRECTION_UP;
    public int mNextDirection = DIRECTION_UP;
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_RIGHT = 3;
    public static final int DIRECTION_LEFT = 4;

    public static final int PIXEL_FOOD = 1;
    public static final int PIXEL_CATERPILLAR_HEAD = 2;
    public static final int PIXEL_CATERPILLAR_BODY = 3;
    public static final int PIXEL_BORDER_1 = 4;
    public static final int PIXEL_BORDER_2 = 5;
    public static final int PIXEL_BORDER_3 = 6;
    public static final int PIXEL_BORDER_4 = 7;
    public static final int PIXEL_BORDER_5 = 8;
    public static final int PIXEL_TYPE_COUNT = 9;

    public long mScore = 0;
    public long mLevel = 0;
    public long mMoveDelay = 200;
    public long mLastMove;

    public TextView mStatusText, mScoreText, mLevelText;
    public Button mToggleGameStateButton;

    public ArrayList<Coordinate> mCaterpillarBody = new ArrayList<>();
    public ArrayList<Coordinate> mFoodList = new ArrayList<>();

    public static final Random RNG = new Random();

    public RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            CaterpillarGameView.this.update();
            CaterpillarGameView.this.invalidate();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };


    public CaterpillarGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCaterpillarGameView();
    }

    public CaterpillarGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initCaterpillarGameView();
    }

    public void initCaterpillarGameView() {
        setFocusable(true);

        Resources r = this.getContext().getResources();

        resetPixels(PIXEL_TYPE_COUNT);

        loadPixel(PIXEL_FOOD, r.getDrawable(R.drawable.pixel_square_yellow, getContext().getTheme()));
        loadPixel(PIXEL_CATERPILLAR_HEAD, r.getDrawable(R.drawable.pixel_round_red, getContext().getTheme()));
        loadPixel(PIXEL_CATERPILLAR_BODY, r.getDrawable(R.drawable.pixel_round_dark_green, getContext().getTheme()));

        loadPixel(PIXEL_BORDER_1, r.getDrawable(R.drawable.pixel_round_pink, getContext().getTheme()));
        loadPixel(PIXEL_BORDER_2, r.getDrawable(R.drawable.pixel_round_orange, getContext().getTheme()));
        loadPixel(PIXEL_BORDER_3, r.getDrawable(R.drawable.pixel_round_yellow, getContext().getTheme()));
        loadPixel(PIXEL_BORDER_4, r.getDrawable(R.drawable.pixel_round_light_green, getContext().getTheme()));
        loadPixel(PIXEL_BORDER_5, r.getDrawable(R.drawable.pixel_round_blue, getContext().getTheme()));
    }

    public void initNewGame() {
        mCaterpillarBody.clear();
        mFoodList.clear();

        mCaterpillarBody.add(new Coordinate(20, 20));
        mCaterpillarBody.add(new Coordinate(19, 20));
        mCaterpillarBody.add(new Coordinate(18, 20));
        mCaterpillarBody.add(new Coordinate(17, 20));
        mCaterpillarBody.add(new Coordinate(16, 20));
        mCaterpillarBody.add(new Coordinate(15, 20));
        mNextDirection = DIRECTION_UP;

        addRandomFood();
        addRandomFood();

        // TODO: For testing
//        for (int i = 0; i < 600; i++) {
//            addRandomFood();
//        }

        mLevel = 0;
        mScore = 0;
    }


    private int[] coordArrayListToArray(ArrayList<Coordinate> coordList) {
        int count = coordList.size();
        int[] rawArray = new int[count * 2];
        for (int index = 0; index < count; index++) {
            Coordinate c = coordList.get(index);
            rawArray[2 * index] = c.x;
            rawArray[2 * index + 1] = c.y;
        }
        return rawArray;
    }


    private ArrayList<Coordinate> coordArrayToArrayList(int[] rawArray) {
        ArrayList<Coordinate> coordArrayList = new ArrayList<>();

        int coordCount = rawArray.length;
        for (int index = 0; index < coordCount; index += 2) {
            Coordinate c = new Coordinate(rawArray[index], rawArray[index + 1]);
            coordArrayList.add(c);
        }
        return coordArrayList;
    }


    public Bundle saveState() {
        Bundle map = new Bundle();

        map.putIntArray("mFoodList", coordArrayListToArray(mFoodList));
        map.putInt("mDirection", Integer.valueOf(mDirection));
        map.putInt("mNextDirection", Integer.valueOf(mNextDirection));
        map.putLong("mMoveDelay", Long.valueOf(mMoveDelay));
        map.putLong("mScore", Long.valueOf(mScore));
        map.putIntArray("mCaterpillarBody", coordArrayListToArray(mCaterpillarBody));

        return map;
    }


    public void restoreState(Bundle icicle) {
        setMode(MODE_PAUSE);

        mFoodList = coordArrayToArrayList(icicle.getIntArray("mFoodList"));
        mDirection = icicle.getInt("mDirection");
        mNextDirection = icicle.getInt("mNextDirection");
        mMoveDelay = icicle.getLong("mMoveDelay");
        mScore = icicle.getLong("mScore");
        mCaterpillarBody = coordArrayToArrayList(icicle.getIntArray("mCaterpillarBody"));
    }


    public void setStatusTextView(TextView newView) {
        mStatusText = newView;
    }

    public void setScoreTextView(TextView newView) {
        mScoreText = newView;
    }

    public void setLevelTextView(TextView newView) {
        mLevelText = newView;
    }

    public void setToggleGameStateButton(Button toggleButton) {
        mToggleGameStateButton = toggleButton;
    }

    public void setMode(int newMode) {
        int oldMode = mMode;
        mMode = newMode;

        if (newMode == MODE_RUNNING & oldMode != MODE_RUNNING) {
            mStatusText.setVisibility(View.INVISIBLE);
            mToggleGameStateButton.setText(R.string.pause_game);
            update();
            return;
        }

        Resources res = getContext().getResources();
        CharSequence str = "";
        if (newMode == MODE_PAUSE) {
            str = res.getText(R.string.mode_pause);
            hideCaterpillarAndFood();
        }
        if (newMode == MODE_READY) {
            str = res.getText(R.string.mode_ready);
            //updateBorder();
        }
        if (newMode == MODE_LOST) {
            str = res.getString(R.string.mode_lost);
            clearFood();
        }

        mStatusText.setText(str);
        mToggleGameStateButton.setText(R.string.start_game);
        mStatusText.setVisibility(View.VISIBLE);
        updateGameStats();
    }

    private void addRandomFood() {
        Coordinate newCoord = null;
        boolean found = false;
        while (!found) {
            int newX = 1 + RNG.nextInt(mXPixelCount - 2);
            int newY = 1 + RNG.nextInt(mYPixelCount - 2);
            newCoord = new Coordinate(newX, newY);

            boolean collision = false;
            int caterpillarlength = mCaterpillarBody.size();
            for (int index = 0; index < caterpillarlength; index++) {
                if (mCaterpillarBody.get(index).equals(newCoord)) {
                    collision = true;
                }
            }
            found = !collision;
        }
        mFoodList.add(newCoord);
    }

    public void update() {
        if (mMode == MODE_RUNNING) {
            long now = System.currentTimeMillis();

            if (now - mLastMove > mMoveDelay) {
                clearPixels();
                updateBorder();
                updateCaterpillar();
                updateFood();
                updateGameStats();
                mLastMove = now;
            }
            mRedrawHandler.sleep(mMoveDelay);
        }
    }

    private void updateBorder() {
        int x = 4;
        for (; x < mXPixelCount; x+=5) {
            // Top Border
            setPixel(PIXEL_BORDER_1, x-4, 0);
            setPixel(PIXEL_BORDER_2, x-3, 0);
            setPixel(PIXEL_BORDER_3, x-2, 0);
            setPixel(PIXEL_BORDER_4, x-1, 0);
            setPixel(PIXEL_BORDER_5, x, 0);
            // Bottom Border
            setPixel(PIXEL_BORDER_5, x-4, mYPixelCount - 1);
            setPixel(PIXEL_BORDER_4, x-3, mYPixelCount - 1);
            setPixel(PIXEL_BORDER_3, x-2, mYPixelCount - 1);
            setPixel(PIXEL_BORDER_2, x-1, mYPixelCount - 1);
            setPixel(PIXEL_BORDER_1, x, mYPixelCount - 1);
        }
        // Sort out any leftover pixels that were not filled
        x-=5;
        int diff = mXPixelCount - x;
        if (diff > 0) {
            if (diff >= 1) {
                setPixel(PIXEL_BORDER_5, x, 0);
                setPixel(PIXEL_BORDER_1, x, mYPixelCount - 1);
            }
            x++;
            if (diff >= 2) {
                setPixel(PIXEL_BORDER_4, x, 0);
                setPixel(PIXEL_BORDER_2, x, mYPixelCount - 1);
            }
            x++;
            if (diff >= 3) {
                setPixel(PIXEL_BORDER_3, x, 0);
                setPixel(PIXEL_BORDER_3, x, mYPixelCount - 1);
            }
            x++;
            if (diff >= 4) {
                setPixel(PIXEL_BORDER_2, x, 0);
                setPixel(PIXEL_BORDER_4, x, mYPixelCount - 1);
            }
        }

        int y = 5; // Starts at pixel 1 not 0
        for (; y < mYPixelCount - 1; y+=5) {
            // Left Border
            setPixel(PIXEL_BORDER_5, 0, y-4);
            setPixel(PIXEL_BORDER_4, 0, y-3);
            setPixel(PIXEL_BORDER_3, 0, y-2);
            setPixel(PIXEL_BORDER_2, 0, y-1);
            setPixel(PIXEL_BORDER_1, 0, y);
            // Right Border
            setPixel(PIXEL_BORDER_1, mXPixelCount - 1, y-4);
            setPixel(PIXEL_BORDER_2, mXPixelCount - 1, y-3);
            setPixel(PIXEL_BORDER_3, mXPixelCount - 1, y-2);
            setPixel(PIXEL_BORDER_4, mXPixelCount - 1, y-1);
            setPixel(PIXEL_BORDER_5, mXPixelCount - 1, y);
        }
        // Sort out any leftover pixels that were not filled
        y-=5;
        diff = mYPixelCount - 1 - y;
        if (diff > 0) {
            if (diff >= 1) {
                setPixel(PIXEL_BORDER_1, 0, y);
                setPixel(PIXEL_BORDER_5, mXPixelCount - 1, y);
            }
            y++;
            if (diff >= 2) {
                setPixel(PIXEL_BORDER_2, 0, y);
                setPixel(PIXEL_BORDER_4, mXPixelCount - 1, y);
            }
            y++;
            if (diff >= 3) {
                setPixel(PIXEL_BORDER_3, 0, y);
                setPixel(PIXEL_BORDER_3, mXPixelCount - 1, y);
            }
            y++;
            if (diff >= 4) {
                setPixel(PIXEL_BORDER_4, 0, y);
                setPixel(PIXEL_BORDER_2, mXPixelCount - 1, y);
            }
        }
    }

    private void updateFood() {
        for (Coordinate c : mFoodList) {
            setPixel(PIXEL_FOOD, c.x, c.y);
        }
    }

    private void clearFood() {
        for (Coordinate c : mFoodList) {
            setPixel(0, c.x, c.y);
        }
        mFoodList.clear();
    }

    private void hideCaterpillarAndFood() {
        for (Coordinate f : mFoodList) {
            setPixel(0, f.x, f.y);
        }
        for (Coordinate s : mCaterpillarBody) {
            setPixel(0, s.x, s.y);
        }
    }

    private void updateCaterpillar() {
        boolean growCaterpillar = false;

        Coordinate head = mCaterpillarBody.get(0);
        Coordinate newHead = new Coordinate(1, 1);

        mDirection = mNextDirection;

        switch (mDirection) {
            case DIRECTION_RIGHT: {
                newHead = new Coordinate(head.x + 1, head.y);
                break;
            }
            case DIRECTION_LEFT: {
                newHead = new Coordinate(head.x - 1, head.y);
                break;
            }
            case DIRECTION_UP: {
                newHead = new Coordinate(head.x, head.y - 1);
                break;
            }
            case DIRECTION_DOWN: {
                newHead = new Coordinate(head.x, head.y + 1);
                break;
            }
        }

        // Check if caterpillar hit the border
        if ((newHead.x < 1) || (newHead.y < 1) || (newHead.x > mXPixelCount - 2) || (newHead.y > mYPixelCount - 2)) {
            setMode(MODE_LOST);
            return;
        }

        // Check if the caterpillar hit itself
        int caterpillarlength = mCaterpillarBody.size();
        for (int caterpillarindex = 0; caterpillarindex < caterpillarlength; caterpillarindex++) {
            Coordinate c = mCaterpillarBody.get(caterpillarindex);
            if (c.equals(newHead)) {
                setMode(MODE_LOST);
                return;
            }
        }

        // Check if it hit the food
        int foodcount = mFoodList.size();
        for (int foodindex = 0; foodindex < foodcount; foodindex++) {
            Coordinate c = mFoodList.get(foodindex);
            if (c.equals(newHead)) {
                mFoodList.remove(c);
                addRandomFood();

                mScore++;
                // Increase speed every 5 eats
                if (mScore % 5 == 0) {
                    mMoveDelay *= 0.9;
                    mLevel++;
                }

                growCaterpillar = true;
            }
        }

        // Update new head coordinate
        mCaterpillarBody.add(0, newHead);
        if (!growCaterpillar) {
            // Caterpillar is not growing, remove last pixel
            mCaterpillarBody.remove(mCaterpillarBody.size() - 1);
        }

        int index = 0;
        for (Coordinate c : mCaterpillarBody) {
            if (index == 0) {
                setPixel(PIXEL_CATERPILLAR_HEAD, c.x, c.y);
            }
            else {
                setPixel(PIXEL_CATERPILLAR_BODY, c.x, c.y);
            }
            index++;
        }
    }

    private void updateGameStats() {
        Resources res = getContext().getResources();
        String scoreStr = res.getString(R.string.score_title) + mScore;
        mScoreText.setText(scoreStr);
        String levelStr = res.getString(R.string.level_title) + mLevel;
        mLevelText.setText(levelStr);
    }

    private class Coordinate {
        public int x;
        public int y;

        public Coordinate(int newX, int newY) {
            x = newX;
            y = newY;
        }

        public boolean equals(Coordinate other) {
            if (x == other.x && y == other.y) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Coordinate: [" + x + "," + y + "]";
        }
    }

}