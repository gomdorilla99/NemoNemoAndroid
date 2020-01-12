package com.example.nemonemo;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    private TextView mResult;
    Typeface tf;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mGameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private com.example.nemonemo.GameGroundView mGameView;
    private TextView mTitleView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private  NamoGame mGame;
    private  Point m_XY = new Point();
    private  Point m_endXY = new Point();

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);

        //  mResult  = findViewById(R.id.ResultView);
        // Set up the user interaction to manually show or hide the system UI.

        mGame = new NamoGame(400,400);
        LoadGameData(mGame);
        mGameView = findViewById(R.id.GameGroundView);
        mGameView.SetGame(mGame);
        mTitleView = findViewById(R.id.GameTitle);
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        tf = Typeface.createFromAsset(getAssets(),"fonts/pristina.ttf");
        mGame.mPaint.setTypeface(tf);
        mGameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //      FullscreenActivity.super.onTouchEvent(event);

                Point point = new Point();
                point.x = (int) event.getX();
                point.y = (int) event.getY();

                float height = (mGame.mHeight + mGame.mCodeHeight) * mGame.StepY;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Point tempPt  = mGame.GetPointXY(point);

                    if ((tempPt.x>-1) && (tempPt.y>-1) && (tempPt.x<mGame.mWidth) && (tempPt.y<mGame.mHeight)) {
                        m_XY = tempPt;
                    }
                    mGameView.invalidate();
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    m_endXY = mGame.GetPointXY(point);
                    if ((m_XY.x>-1) && (m_XY.y>-1) && (m_endXY.x<mGame.mWidth) && (m_endXY.y<mGame.mHeight)) {
                        if(m_endXY.x>-1 && m_endXY.y>-1 && (m_XY.x<mGame.mWidth) && (m_XY.y<mGame.mHeight) ) {
                            if (m_XY.x>-1 && m_XY.y>-1 && m_endXY.x<mGame.mWidth && m_endXY.y<mGame.mHeight) {
                                mGame.start_x = m_XY.x;
                                mGame.end_x  = m_endXY.x;
                                mGame.start_y = m_XY.y;
                                mGame.end_y = m_endXY.y;
                                mGame.Mark(m_XY.x, m_XY.y, m_endXY.x, m_endXY.y);
                            }
                            mGameView.invalidate();
                        }
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(!mGame.bSuccess) {
                        m_endXY = mGame.GetPointXY(point);
                        mGame.mCursor = m_endXY;
                        // if (m_XY.x>-1 && m_XY.y>-1 && m_endXY.x<mGame.mWidth && m_endXY.y<mGame.mHeight)
                        //    mGame.Check(m_XY.x, m_XY.y, m_endXY.x, m_endXY.y);

                        if ((m_XY.x > -1) && (m_XY.y > -1) && (m_endXY.x < mGame.mWidth) && (m_endXY.y < mGame.mHeight)) {
                            if (m_endXY.x > -1 && m_endXY.y > -1 && (m_XY.x < mGame.mWidth) && (m_XY.y < mGame.mHeight)) {

                                if ((m_XY.x == m_endXY.x) && (m_XY.y == m_endXY.y)) {
                                    if (mGame.bMarker) {
                                        mGame.Set(m_XY.x, m_XY.y, m_endXY.x, m_endXY.y);
                                    } else {
                                        mGame.Check(m_XY.x, m_XY.y, m_endXY.x, m_endXY.y);
                                    }
                                } else if ((m_XY.x == m_endXY.x) || (m_XY.y == m_endXY.y)) {
                                    //AutoCheck Already do something
                                    if (mGame.bMarker) {
                                        mGame.Set(m_XY.x, m_XY.y, m_endXY.x, m_endXY.y);
                                    } else {
                                        mGame.Check(m_XY.x, m_XY.y, m_endXY.x, m_endXY.y);
                                    }
                                } else {
                                    /*
                                    if (mGame.bMarker) {
                                              mGame.Set(m_XY.x, m_XY.y, m_endXY.x, m_endXY.y);
                                    } else {
                                             mGame.Check(m_XY.x, m_XY.y, m_endXY.x, m_endXY.y);
                                    }
                                    */
                                }
                            }
                            mGameView.invalidate();
                        }
                    }

                    // 이전 게임으로
                    if ((point.x < mGame.mPrevBtn.right) &&  (mGame.mPrevBtn.left < point.x))
                    {
                        if ((point.y < mGame.mPrevBtn.bottom) &&  (mGame.mPrevBtn.top < point.y))
                        {
                           if(mGame.PrevGame()<0) {
                             //  String message = new String("Fail to load previous Game data");
                             //  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                           }
                            mTitleView.setText(mGame.mStrGameDescription);
                            mGameView.invalidate();
                        }
                    }

                    // 다음 게임으로
                    if ((point.x < mGame.mNextBtn.right) &&  (mGame.mNextBtn.left < point.x))
                    {
                        if ((point.y < mGame.mNextBtn.bottom) && (mGame.mNextBtn.top < point.y))
                        {
                            if(mGame.NextGame()<0) {
                               // String message = new String("Fail to load next Game data");
                               // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                            mTitleView.setText(mGame.mStrGameDescription);
                            mGameView.invalidate();
                        }
                    }
                    // Game Hint
                    if ((point.x < mGame.mHintBtn.right) &&  (mGame.mHintBtn.left < point.x))
                    {
                        if(!mGame.bSuccess) {
                            if ((point.y < mGame.mHintBtn.bottom) && (mGame.mHintBtn.top < point.y)) {
                                //  CDC *pDC = &MemDC; //GetWindowDC();
                                if (mGame.GameGetHint() > 0)    // hint
                                {
                                    mGameView.invalidate();
                                }
                            }
                        }
                    }

                    //Set Mark option
                    if ((point.x < mGame.mMakerBtn.right) &&  (mGame.mMakerBtn.left < point.x))
                    {
                        if ((point.y < mGame.mMakerBtn.bottom) &&  (mGame.mMakerBtn.top < point.y))
                        {
                            if(mGame.bMarker)
                                mGame.bMarker= false;
                            else
                                mGame.bMarker = true;
                            mGameView.invalidate();
                            // mGame.NextGame();
                        }
                    }

                    if (m_endXY.x<0 && m_endXY.x>=0-mGame.mCodeWidth)
                    {
                        if (-1< m_endXY.y && m_endXY.y<mGame.mHeight)
                            mGame.AutoCheck(0, m_endXY.y, mGame.mWidth-1, m_endXY.y);
                    }
                    if (m_endXY.y<0 && m_endXY.y>=0-mGame.mCodeHeight)
                    {
                        if (-1<m_endXY.x && m_endXY.x<mGame.mWidth)
                            mGame.AutoCheck(m_endXY.x, 0, m_endXY.x, mGame.mHeight-1);
                    }
                }
                return true;
            }
        });

        mGameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGame.mCursor = m_endXY;
                if (m_XY.x>-1 && m_XY.y>-1 && m_endXY.x<mGame.mWidth && m_endXY.y<mGame.mHeight)
                    mGame.Check(m_XY.x, m_XY.y, m_endXY.x, m_endXY.y);

                mGameView.invalidate();
            }

        });

        mGame.GameLoad();
        mTitleView.setText(mGame.mStrGameDescription);
        mTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    toggle();
            }
        });
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.ExitGame).setOnTouchListener(mDelayHideTouchListener);

    }
    public void LoadGameData(NamoGame pGame)
    {
        StringBuffer result;
        StringBuffer data = new StringBuffer();
        try {
            InputStream is = getResources().openRawResource(R.raw.gamedata);
            BufferedReader buffer = new BufferedReader
                    (new InputStreamReader(is));

            String str = buffer.readLine(); // 파일에서 한줄을 읽어옴

            // 파일에서 읽은 데이터를 저장하기 위해서 만든 변수

            String strTemp;
            String strTemp1 = new String();
            GameData pTail;
            int end;
            while (str != null) {
                strTemp = str.substring(0,1);
                if(strTemp.compareTo("[")==0) {
                    end = str.indexOf("]");
                    strTemp = str.substring(1, end);
                    String[] array1 = strTemp.split(",",3);
                    if(array1.length==3) {
                       GameData pGameData = new GameData();
                        if (pGame.pGameDataHeader == null) {
                            pGame.pGameDataHeader = pGameData;
                        } else {
                            pTail = pGame.pGameDataHeader.GetTail();
                            pTail.pNext = pGameData;
                            pGameData.pPrev = pTail;
                        }
                        strTemp1 = String.format("width : %s, weight : %s %s", array1[0], array1[1], array1[2]);
                        data.append(strTemp1 + "\n");
                        pGameData.mStrDesciption = array1[2];
                        pGameData.mWidth = Integer.parseInt(array1[0]);
                        pGameData.mHeight = Integer.parseInt(array1[1]);
                        int i=0;
                        for (i=0; i < pGameData.mWidth;)
                        {
                            int num;
                            str = buffer.readLine();
                            String[] array2 = str.split(";",pGameData.mWidth);
                            for(int j= 0; j < array2.length; j++) {
                                num = pGameData.AddHorGuide(array2[j]);
                                if (pGameData.mCodeHeight < num)
                                    pGameData.mCodeHeight = num;
                            }
                            i+= array2.length;
                        }
                        for (i=0; i < pGameData.mHeight; )
                        {
                            int num;
                            str = buffer.readLine();
                            String[] array2 = str.split(";",pGameData.mHeight);
                            for (int j = 0; j < array2.length; j++) {
                                num = pGameData.AddVerGuide(array2[j]);
                                if (pGameData.mCodeWidth < num)
                                    pGameData.mCodeWidth = num;
                            }
                            i+= array2.length;
                        }
                    }
                }
                else if(strTemp.compareTo("#")==0) {
                    strTemp = str.substring(2);
                    String[] array1 = strTemp.split(" ",3);
                    if(array1.length==3) {
                        strTemp1 = String.format("Width : %s, Height : %s", array1[0], array1[1]);
                        data.append(strTemp1 + "\n");

                        GameData pGameData = new GameData();
                        if (pGame.pGameDataHeader == null) {
                            pGame.pGameDataHeader = pGameData;
                        } else {
                            pTail = pGame.pGameDataHeader.GetTail();
                            pTail.pNext = pGameData;
                            pGameData.pPrev = pTail;
                        }
                        strTemp1 = String.format("width : %s, weight : %s %s", array1[0], array1[1], array1[2]);
                        data.append(strTemp1 + "\n");
                        pGameData.mStrDesciption = array1[2];
                        pGameData.mHeight = Integer.parseInt(array1[0]);
                        pGameData.mWidth = Integer.parseInt(array1[1]);
                        for (int i = 0; i < pGameData.mWidth; i++) {
                            int num;
                            str = buffer.readLine();
                            String[] array2 = str.split(";", pGameData.mHeight);
                            for (int j = 0; j < array2.length; j++) {
                                num = pGameData.AddHorGuide(array2[j]);
                                if (pGameData.mCodeHeight < num)
                                    pGameData.mCodeHeight = num;
                            }
                        }
                        for (int i = 0; i < pGameData.mHeight; i++) {
                            int num;
                            str = buffer.readLine();
                            String[] array2 = str.split(";", pGameData.mWidth);
                            for (int j = 0; j < array2.length; j++) {
                                num = pGameData.AddVerGuide(array2[j]);
                                if (pGameData.mCodeWidth < num)
                                    pGameData.mCodeWidth = num;
                            }
                        }
                    }

                }
                else {
                    data.append(str + "\n");
                }

                str = buffer.readLine();
            }
            buffer.close();
            //mResult.setText(data);
        } catch (Exception e) {
          //  mResult.setText(data);
            //mResult.setText("Exception: raw resource file reading");
        }
        pGame.pGameDataHeader.pPrev = pGame.pGameDataHeader.GetTail();
        pGame.pGameDataHeader.GetTail().pNext = pGame.pGameDataHeader;
        pGame.pCurrentGame = pGame.pGameDataHeader;

    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mGameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
