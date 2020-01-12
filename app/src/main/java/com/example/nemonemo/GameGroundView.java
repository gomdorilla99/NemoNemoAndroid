package com.example.nemonemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Toast;

public class GameGroundView extends android.support.v7.widget.AppCompatImageView {
    private Paint mPaint = new Paint();
    private  NamoGame mGame;
    public void SetGame(NamoGame pNamoGame) {
        mGame = pNamoGame;
    }
    public GameGroundView(@NonNull Context context,
                          @Nullable AttributeSet attrs,
                          @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // inflateViews(context, attrs);
    }

    public GameGroundView(@NonNull Context context,
                          @Nullable AttributeSet attrs) {
        super(context, attrs);

        //  inflateViews(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        RectF windowRect = new RectF(0,0,width,height);

        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        canvas.drawRect(windowRect, mPaint);

        mGame.StepX = (windowRect.width() - 30) / (float)(mGame.mWidth + mGame.mCodeWidth);
        mGame.StepY = (windowRect.height() - 50) / (float)(mGame.mHeight + mGame.mCodeHeight);

        if (mGame.StepX > mGame.StepY)
        {
            mGame.StepX = mGame.StepY;
        }
        else
        {
            mGame.StepY = mGame.StepX;

        }
        if (mGame.StepY > 100)mGame.StepY = 100;
        if (mGame.StepX > 100)mGame.StepX = 100;

        mGame.mOffset.x = (int)((width - (mGame.mWidth + mGame.mCodeWidth ) * mGame.StepX)/2);
        mGame.mOffset.y = (int)((height - (mGame.mHeight + mGame.mCodeHeight ) * mGame.StepY-550));
        if(mGame.mOffset.y <10)
            mGame.mOffset.y=10;


        mGame.mPrevBtn.set(0,mGame.mOffset.y + mGame.StepY * (mGame.mHeight + mGame.mCodeHeight )+150 - 100,
                100,mGame.mOffset.y + mGame.StepY * (mGame.mHeight +mGame. mCodeHeight )+150+100);
        mGame.mMakerBtn.set(windowRect.width()/3-100,mGame.mOffset.y + mGame.StepY * (mGame.mHeight + mGame.mCodeHeight )+150 - 100,
                windowRect.width()/3+100,mGame.mOffset.y + mGame.StepY * (mGame.mHeight +mGame. mCodeHeight )+150+100);
        mGame.mHintBtn.set(windowRect.width()*2/3-100,mGame.mOffset.y + mGame.StepY * (mGame.mHeight + mGame.mCodeHeight )+150 - 100,
                windowRect.width()*2/3+100,mGame.mOffset.y + mGame.StepY * (mGame.mHeight + mGame.mCodeHeight )+150+100);
        mGame.mNextBtn.set(windowRect.width()-20-100,mGame.mOffset.y +mGame.StepY * (mGame.mHeight + mGame.mCodeHeight )+150 - 100,
                width,mGame.mOffset.y + mGame.StepY * (mGame.mHeight + mGame.mCodeHeight )+150+100);


        if (mGame.GameStatusCheck(canvas)==0)
        {
            mGame.DrawFinal(canvas, windowRect);
            if(mGame.bSuccess == false) {
                String message = new String(mGame.mStrGameDescription);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                mGame.bSuccess = true;
            }
        }
        else
        {
            mPaint.setColor(Color.BLACK);
            mGame.DrawNaNoGram(canvas, windowRect);
        }
    }
}