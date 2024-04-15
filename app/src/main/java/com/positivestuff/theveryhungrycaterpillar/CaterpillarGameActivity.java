package com.positivestuff.theveryhungrycaterpillar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import static com.positivestuff.theveryhungrycaterpillar.CaterpillarGameView.DIRECTION_UP;
import static com.positivestuff.theveryhungrycaterpillar.CaterpillarGameView.DIRECTION_DOWN;
import static com.positivestuff.theveryhungrycaterpillar.CaterpillarGameView.DIRECTION_RIGHT;
import static com.positivestuff.theveryhungrycaterpillar.CaterpillarGameView.DIRECTION_LEFT;
import static com.positivestuff.theveryhungrycaterpillar.CaterpillarGameView.MODE_PAUSE;
import static com.positivestuff.theveryhungrycaterpillar.CaterpillarGameView.MODE_READY;
import static com.positivestuff.theveryhungrycaterpillar.CaterpillarGameView.MODE_RUNNING;
import static com.positivestuff.theveryhungrycaterpillar.CaterpillarGameView.MODE_LOST;

public class CaterpillarGameActivity extends Activity {

    private CaterpillarGameView mCaterpillarGameView;
    private static final String ICICLE_KEY = "caterpillar-view";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_caterpillar_game);

        mCaterpillarGameView = findViewById(R.id.caterpillar);
        mCaterpillarGameView.setStatusTextView(findViewById(R.id.textView_status));
        mCaterpillarGameView.setScoreTextView(findViewById(R.id.textView_score));
        mCaterpillarGameView.setLevelTextView(findViewById(R.id.textView_level));
        mCaterpillarGameView.setToggleGameStateButton(findViewById(R.id.buttonToggleGameState));

        if (savedInstanceState == null) {
            mCaterpillarGameView.setMode(MODE_READY);
        }
        else {
            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
            if (map != null) {
                mCaterpillarGameView.restoreState(map);
            }
            else {
                mCaterpillarGameView.setMode(MODE_PAUSE);
            }
        }
    }

    public void buttonClicked(View view) {
        if (view.getId() == R.id.buttonToggleGameState) {
            if (mCaterpillarGameView.mMode == MODE_READY | mCaterpillarGameView.mMode == MODE_LOST) {
                mCaterpillarGameView.initNewGame();
                mCaterpillarGameView.setMode(MODE_RUNNING);
            }
            else if (mCaterpillarGameView.mMode == MODE_PAUSE) {
                mCaterpillarGameView.setMode(MODE_RUNNING);
            }
            else if (mCaterpillarGameView.mMode == MODE_RUNNING) {
                mCaterpillarGameView.setMode(MODE_PAUSE);
            }
            mCaterpillarGameView.update();
        }
        if (view.getId() == R.id.buttonUp) {
            if (mCaterpillarGameView.mDirection != DIRECTION_DOWN) {
                mCaterpillarGameView.mNextDirection = DIRECTION_UP;
            }
        }
        else if (view.getId() == R.id.buttonLeft) {
            if (mCaterpillarGameView.mDirection != DIRECTION_RIGHT) {
                mCaterpillarGameView.mNextDirection = DIRECTION_LEFT;
            }
        }
        else if (view.getId() == R.id.buttonRight) {
            if (mCaterpillarGameView.mDirection != DIRECTION_LEFT) {
                mCaterpillarGameView.mNextDirection = DIRECTION_RIGHT;
            }
        }
        else if (view.getId() == R.id.buttonDown) {
            if (mCaterpillarGameView.mDirection != DIRECTION_UP) {
                mCaterpillarGameView.mNextDirection = DIRECTION_DOWN;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaterpillarGameView.setMode(MODE_PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(ICICLE_KEY, mCaterpillarGameView.saveState());
    }

}
