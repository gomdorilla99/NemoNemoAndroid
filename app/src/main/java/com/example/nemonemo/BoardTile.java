package com.example.nemonemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.Random;

public class BoardTile {
    public String name;
    private RectF mRectPosition = new RectF();
    private int mState;
    private int mLastID;
    Paint mPaint = new Paint();
    Paint mBGPaint = new Paint();
    Paint mMarkPaint = new Paint();
    public  String strMarking;

    public PointF[]  mPatch = new PointF[8];
    protected BoardTile pUp;
    protected BoardTile pLeft;
    protected BoardTile pRight;
    protected BoardTile pDown;

    // mState : 0 : blank
    // mState : 1 : Check
    // mState : 2 : PC Check
    // mState : 3 : Set Mark

    public void SetOn(){
        mState = 3;
    };
    public void SetCheck()
    {
        switch(mState)
        {
            case 0:
                mState = 1;
                break;
            case 1:
                mState = 0;
                break;
            case 2:
                mState = 0;
                break;
            case 3:
                mState = 0;
                break;
        }
    }

    public void SetAutoCheck()
    {
        switch (mState)
        {
            case 0:
                mState = 2;
                break;
            case 1:
                mState = 1;
                break;
            case 2:
                mState = 2;
                break;
            case 3:
                mState = 3;
                break;
        }
    }

    public BoardTile(){

        pUp = null;
        pDown = null;
        pLeft = null;
        pRight = null;
        mState = 0;
        mLastID = 0;
        name =  "Tile";
        mBGPaint.setStyle(Paint.Style.FILL);
        mBGPaint.setColor(0x08FFFFFF);
        mMarkPaint.setStyle(Paint.Style.FILL);
        mMarkPaint.setStrokeWidth(2f);
        mMarkPaint.setColor(0xFF3A3A5A);
        for(int i=0;i<8;i++) {
            mPatch[i] = new PointF();
        }
    }

    @Override
    protected void finalize()
    {
        /*do Nothing */
    }

    protected BoardTile NEXT_ITEM(BoardTile Tile, int direction)
    {
        if(direction > 0) {
            return Tile.pDown;
        }else{
            return Tile.pRight;
        }
    }

    protected BoardTile PREV_ITEM(BoardTile Tile, int direction)
    {
        if(direction > 0) {
            return Tile.pUp;
        }else{
            return Tile.pLeft;
        }
    }

    public BoardTile  MoveToTileEnd(int direction)
    {
        BoardTile pTile = this;
        while (NEXT_ITEM(pTile, direction)!=null)
        {
            pTile = NEXT_ITEM(pTile, direction);
        }
        return  pTile;
    }
    public BoardTile  MoveToTileFront(int direction)
    {
        BoardTile pTile = this;
        while (PREV_ITEM(pTile, direction)!=null)
        {
            pTile = PREV_ITEM(pTile, direction);
        }
        return  pTile;
    }

    public int GetStatus(){ return mState; };
    public int SetStatus(int stat){ return mState=stat; };
    public int Update(int stat) { return mState = stat; };
    
    public int CountUpper()
    {
        int Count = 0;
        if (mState == 3)
        {
            if (pUp != null)  Count = pUp.CountUpper();
            return Count + 1;
        }else{return 0;}
    }
    public int CountBelow()
    {
        int Count = 0;
        if (mState == 3)
        {
            if (pDown != null) Count = pDown.CountBelow();
            return Count + 1;
        }else {return 0;}
    }

    public int CountLeft()
    {
        int Count = 0;
        if (mState == 3)
        {
            if (pLeft != null) Count = pLeft.CountLeft();
            return Count + 1;
        }else{return 0;}
    }

    public int CountRight()
    {
        int Count = 0;
        if (mState == 3)
        {
            if (pRight != null) Count = pRight.CountRight();
            return Count + 1;
        }else{return 0;}
    }

    public void SetPosition(RectF rect){
        mRectPosition.left = rect.left;
        mRectPosition.right = rect.right;
        mRectPosition.top = rect.top;
        mRectPosition.bottom = rect.bottom;
    };

    public RectF GetPosition(){ return mRectPosition; };

    public void DrawTile(Canvas canvas)
    {
        //srand(mRectPosition.left + mRectPosition.top);
        Random rnd = new Random();
        rnd.setSeed((long)(mRectPosition.left + mRectPosition.top));
        if (mState < 0 || mState>5)
            mState = 0;

        //// *oldPen;
        switch (mState)
        {
            case 0:
                canvas.drawRect((float)mRectPosition.left + 1f, (float)mRectPosition.top + 1f, (float)mRectPosition.right - 1f, (float)mRectPosition.bottom - 1f,mBGPaint);
                          break;
            case 1:
                canvas.drawLine(mRectPosition.left +3f, mRectPosition.top +3f,mRectPosition.right -3f, mRectPosition.bottom-3f,mPaint);
                canvas.drawLine(mRectPosition.left + 3f, mRectPosition.bottom-3f ,mRectPosition.right- 3f, mRectPosition.top +3f,mPaint);
                break;
            case 2:
                canvas.drawLine(mRectPosition.left, mRectPosition.top, mRectPosition.right, mRectPosition.bottom,mPaint);
                canvas.drawLine(mRectPosition.left, mRectPosition.bottom, mRectPosition.right, mRectPosition.top,mPaint);
                break;
            case 3:
            {
                mPaint.setColor(0xA0605000);
                canvas.drawRect(mRectPosition.left, mRectPosition.top, mRectPosition.right, mRectPosition.bottom,mPaint);
                mPaint.setColor(0xFF000000);
            }

            break;
            case 4:
            {
                mPaint.setColor(0xA0605000);
                canvas.drawRect(mRectPosition.left+1, mRectPosition.top+1, mRectPosition.right-1, mRectPosition.bottom-1,mPaint);
                mPaint.setColor(0xFF000000);
            }
            break;
            case 5:
            {
                Path path = new Path();
                path.moveTo(mRectPosition.left, mRectPosition.top);
                for (int i = 0; i < mRectPosition.right - mRectPosition.left; i += 2)
                {
                    path.lineTo(mRectPosition.left + i + rnd.nextInt(4), mRectPosition.top + rnd.nextInt(4));
                    path.lineTo(mRectPosition.left + rnd.nextInt(4), mRectPosition.top + i + rnd.nextInt(4));
                }
                for (int i = 0; i < mRectPosition.right - mRectPosition.left; i += 2)
                {
                    path.lineTo(mRectPosition.left + i + rnd.nextInt(4), mRectPosition.bottom - rnd.nextInt(4));
                    path.lineTo(mRectPosition.right - rnd.nextInt(8), mRectPosition.top + i + rnd.nextInt(4));
                }
                canvas.drawPath(path, mMarkPaint);
            }
            break;
            default:
        }
        String temp = new String();
        temp.format("%d", mLastID);
    }

    public void DrawTileFinal(Canvas canvas)
    {
        float center_x, center_y;
        float half;
        center_x = mRectPosition.centerX();
        center_y = mRectPosition.centerY();
        half = mRectPosition.height();
        float[] gravity = new float[8];
        for (int i = 0; i < 8; i++)
        {
            gravity[i] = 0;
        }
        if (pLeft!=null)
        {
            if (pLeft.mState > 2)
            {
                gravity[0] += 0.5;
                gravity[7] += 0.9;
                gravity[6] += 0.5;
            }
        }
        if (pUp!=null)
        {
            if (pUp.mState > 2)
            {
                gravity[0] += 0.5;
                gravity[1] += 0.9;
                gravity[2] += 0.5;
            }
        }
        if (pRight!=null)
        {
            if (pRight.mState > 2)
            {
                gravity[2] += 0.5;
                gravity[3] += 0.9;
                gravity[4] += 0.5;
            }
        }
        if (pDown!=null)
        {
            if (pDown.mState > 2)
            {
                gravity[4] += 0.5;
                gravity[5] += 0.9;
                gravity[6] += 0.5;
            }
        }
        float corner_weight = 2;

        if (pLeft!=null)
        {
            if (pLeft.mState > 2 )
            {
                if (pLeft.pUp != null)
                    if (pLeft.pUp.GetStatus()>2)	gravity[0] += corner_weight;
                if (pLeft.pDown != null)
                    if (pLeft.pDown.GetStatus()>2)	gravity[6] += corner_weight;
            }
        }
        if (pUp!=null)
        {
            if (pUp.mState > 2)
            {
                if (pUp.pLeft!=null)
                    if (pUp.pLeft.GetStatus()>2)	gravity[0] += corner_weight;
                if (pUp.pRight!=null)
                    if (pUp.pRight.GetStatus()>2)	gravity[2] += corner_weight;
            }
        }
        if (pRight!=null)
        {
            if (pRight.mState > 2)
            {
                if (pRight.pUp!=null)
                    if (pRight.pUp.GetStatus()>2)	gravity[2] += corner_weight;
                if (pRight.pDown!=null)
                    if (pRight.pDown.GetStatus()>2)	gravity[4] += corner_weight;
            }
        }
        if (pDown!=null)
        {
            if (pDown.mState > 2)
            {
                if (pDown.pRight!=null)
                    if (pDown.pRight.GetStatus()>2)	gravity[4] += corner_weight;
                if (pDown.pLeft!=null)
                    if (pDown.pLeft.GetStatus()>2)	gravity[6] += corner_weight;
            }
        }


        for (int i = 0; i < 48; i++)
        {
            gravity[i % 8] = (float)(gravity[(i - 1 + 8) % 8] + gravity[(i + 8) % 8] + gravity[(i + 1 + 8) % 8]) / 3.f;
        }

        for (int i = 0; i < 8; i++)
        {
            gravity[i] = (gravity[i] )/(float)4.;
        }

        for (int i = 0; i < 8; i++)
        {
            if (gravity[i] > 0.5) gravity[i] = .5f;
            if (gravity[i] < 0.0) gravity[i] = 0.2f;
        }

        Path path = new Path();
        path.reset();
        path.moveTo(center_x - gravity[0] * half, center_y - gravity[0] * half);
        mPatch[0].set(center_x - gravity[0] * half, center_y - gravity[0] * half);

        path.lineTo(center_x,                     center_y - gravity[1] * half);
        mPatch[1].set(center_x,                     center_y - gravity[1] * half);

        path.lineTo(center_x + gravity[2] * half, center_y - gravity[2] * half);
        mPatch[2].set(center_x + gravity[2] * half, center_y - gravity[2] * half);

        path.lineTo(center_x + gravity[3] * half, center_y );
        mPatch[3].set(center_x + gravity[3] * half, center_y );

        path.lineTo(center_x + gravity[4] * half, center_y + gravity[4] * half);
        mPatch[4].set(center_x + gravity[4] * half, center_y + gravity[4] * half);

        path.lineTo(center_x,                     center_y + gravity[5] * half);
        mPatch[5].set(center_x,                     center_y + gravity[5] * half);

        path.lineTo(center_x - gravity[6] * half, center_y + gravity[6] * half);
        mPatch[6].set(center_x - gravity[6] * half, center_y + gravity[6] * half);

        path.lineTo(center_x - gravity[7] * half, center_y);
        mPatch[7].set(center_x - gravity[7] * half, center_y);

        path.lineTo(center_x - gravity[0] * half, center_y - gravity[0] * half);

        path.close();
       //
        // canvas.drawPath(path,mPaint);
    }

    public void DrawTileFinal1(Canvas canvas)
    {

        float[] gravity = new float[8];

        for (int i = 0; i < 8; i++)
        {
            gravity[i] = 0f;
        }
        if (pLeft != null)
        {
            if (pLeft.mState > 2)
            {
                gravity[0] += 0.3;
                gravity[7] += 0.9;
                gravity[6] += 0.3;
            }
        }
        if (pUp != null)
        {
            if (pUp.mState > 2)
            {
                gravity[0] += 0.3;
                gravity[1] += 0.9;
                gravity[2] += 0.3;
            }
        }
        if (pRight != null)
        {
            if (pRight.mState > 2)
            {
                gravity[2] += 0.3;
                gravity[3] += 0.9;
                gravity[4] += 0.3;
            }
        }
        if (pDown != null)
        {
            if (pDown.mState > 2)
            {
                gravity[4] += 0.3;
                gravity[5] += 0.9;
                gravity[6] += 0.3;
            }
        }



        for (int i = 0; i < 24; i++)
        {
            gravity[i%8] = (float)(gravity[(i - 1 + 8) % 8] + gravity[(i + 8) % 8]+gravity[(i + 1 + 8) % 8]) / 3.f;
        }

        if (pLeft != null)
        {
            if (pLeft.mState > 2)
            {
                if (pLeft.pUp!=null)	if (pLeft.pUp.GetStatus()>2)	gravity[0] += 0.1f;
                if (pLeft.pDown!=null)	if (pLeft.pDown.GetStatus()>2)	gravity[6] += 0.1f;
            }
        }
        if (pUp != null)
        {
            if (pUp.mState > 2)
            {
                if (pUp.pLeft!=null)	if (pUp.pLeft.GetStatus()>2)	gravity[0] += 0.1f;
                if (pUp.pRight!=null)	if (pUp.pRight.GetStatus()>2)	gravity[2] += 0.1f;
            }
        }
        if (pRight != null)
        {
            if (pRight.mState > 2)
            {
                if (pRight.pUp!=null)	if (pRight.pUp.GetStatus()>2)	gravity[2] += 0.1f;
                if (pRight.pDown!=null)	if (pRight.pDown.GetStatus()>2)	gravity[4] += 0.1f;
            }
        }
        if (pDown != null)
        {
            if (pDown.mState > 2)
            {
                if (pDown.pRight!=null)	if (pDown.pRight.GetStatus()>2)	gravity[4] += 0.1f;
                if (pDown.pLeft!=null)	if (pDown.pLeft.GetStatus()>2)	gravity[6] += 0.1f;
            }
        }

        for (int i = 0; i < 8; i++)
        {
            if (gravity[i] < 0.2) gravity[i] = 0.2f;
        }
    }
}

