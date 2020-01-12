package com.example.nemonemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import android.graphics.Point;
import android.graphics.Typeface;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.graphics.Color.rgb;
import static android.media.CamcorderProfile.get;

public class NamoGame {

    final int min (int A, int B)  {if(A<B) return A; else return B; }
    final int max(int A, int B)    {if(B<A) return A; else return B; }
    final int abs(int A)  {if(A<0) return A * -1; else return A; }

    private String name;
    public  int mWidth ;
    public  int mHeight ;
    public  int mCodeWidth;
    public  int mCodeHeight;

    public Paint mPaint = new Paint();
    public Paint mPaintBotton = new Paint();

    public RectF mPrevBtn = new RectF();
    public RectF mNextBtn = new RectF();
    public RectF mHintBtn = new RectF();
    public RectF mMakerBtn = new RectF();
    public boolean bMarker;

    public String mStrGameDescription;
    public BoardTile[] pBoardHeader;
    public GameData pGameDataHeader;
    public GameData pCurrentGame;
    public Point mOffset;
    public Point mCursor = new Point();
    public  float StepX ;
    public  float StepY;
    public  int start_x, end_x;
    public  int start_y, end_y;
    public boolean bSuccess;
    public Node<FragNode> CurrentFL;
    Typeface tf;
    public NamoGame()
    {
        this(300,500);
    }

    public NamoGame(int width, int height)
    {

        name = new String("namogame");

        pGameDataHeader = null;
        pBoardHeader = null;

        mOffset = new Point(10,100);
        //LoadGameData();

        mStrGameDescription = "Test Game drawing";
        bMarker = true;
        CurrentFL = new Node<FragNode>();
        Paint paint = new Paint();
        paint.setTypeface(tf);
    }

    public int NextGame(){
        if(pCurrentGame.pNext!=null)
            pCurrentGame = pCurrentGame.pNext;
        else
            return  -1;

        if(CurrentFL!=null) {
            if(CurrentFL.mValue!=null) {
                CurrentFL.mValue.mStart = 0;
                CurrentFL.mValue.mEnd = 0;
            }
        }
        //CurrentGame.pNext;
        GameLoad();
        return  0;
    }

    public int PrevGame(){
        if(pCurrentGame.pPrev!=null)
            pCurrentGame = pCurrentGame.pPrev;
        else
            return  -1;

        if(CurrentFL!=null) {
            if(CurrentFL.mValue!=null) {
                CurrentFL.mValue.mStart = 0;
                CurrentFL.mValue.mEnd = 0;
            }
        }
        GameLoad();
        return  0;
    }


    public Point substringPointXY(Point xy)
    {
        Point ret = new Point();


        ret.x = (int)(((double)(xy.x - mOffset.x - (mCodeWidth * StepX))) / StepX);
        ret.y = (int)(((double)(xy.y - mOffset.y - (mCodeHeight * StepY))) / StepY);

        if ((xy.x - mOffset.x - (mCodeWidth * StepX))<0)
            ret.x -= 1;
        if ((xy.y- mOffset.y - (mCodeHeight * StepY))<0)
            ret.y -= 1;
        return ret;
    }


    public int GameGetHint()    //Canvas canvas)
    {
        int i, j;
        int k;

        GuidePannel pGuide;
        int count = 1;
        pGuide = pCurrentGame.pHGuide;
        for (i = 0; i < mWidth; i++)
        {
            // 가로 패널 채크
            count += pGuide.PannelStatusCheck(mHeight, pBoardHeader[i], 1);
            pGuide.PannelAutoStatusCheck(mHeight, pBoardHeader[i], 1);
            pGuide.AI(mHeight, pBoardHeader[i], 1);
            pGuide = pGuide.pNext;
        }
        pGuide = pCurrentGame.pVGuide;
        for (i = 0; i < mHeight; i++)
        {
            //세로 패널 채크
            count += pGuide.PannelStatusCheck(mWidth, pBoardHeader[i*mWidth], 0);
            pGuide.PannelAutoStatusCheck(mWidth, pBoardHeader[i*mWidth], 0);
            pGuide.AI(mWidth, pBoardHeader[i*mWidth], 0);
            pGuide = pGuide.pNext;
        }

        return count;
    }

    public int GameStatusCheck(Canvas canvas)
    {
        int i, j;
        int k;

        GuidePannel pGuide ;
        int count = 0;
        for (k = 0; k < 1; k++)
        {
            pGuide = pCurrentGame.pHGuide;
            for (i = 0; i < mWidth; i++)
            {
                // 가로 패널 채크
                count += pGuide.PannelStatusCheck(mHeight, pBoardHeader[i], 1);
                pGuide = pGuide.pNext;
            }

            pGuide = pCurrentGame.pVGuide;
            for (i = 0; i < mHeight; i++)
            {
                //세로 패널 채크
                count += pGuide.PannelStatusCheck(mWidth, pBoardHeader[i*mWidth], 0);
                pGuide = pGuide.pNext;
            }
        }
        return count;
    }

    public void GameLoad()
    {
        bSuccess = false;
        mWidth = pCurrentGame.mWidth;
        mHeight = pCurrentGame.mHeight;
        if (pBoardHeader!=null)
        {
            pBoardHeader = null;
        }
        pBoardHeader = new BoardTile[mWidth * mHeight];
        for(int k=0;k<mWidth * mHeight;k++) {
            pBoardHeader[k] = new BoardTile();
        }
        mCodeHeight = pCurrentGame.mCodeHeight;
        mCodeWidth = pCurrentGame.mCodeWidth;
        mStrGameDescription = pCurrentGame.mStrDesciption;

        for (int i = 0; i < mHeight; i++)
        {
            for (int j = 0; j < mWidth; j++)
            {
                if (i>0)            pBoardHeader[mWidth * i + j].pUp = pBoardHeader[mWidth * (i - 1) + j];
                if (j>0)            pBoardHeader[mWidth * i + j].pLeft = pBoardHeader[mWidth * (i)+j - 1];
                if (i<mHeight - 1)  pBoardHeader[mWidth * i + j].pDown = pBoardHeader[mWidth * (i + 1) + j];
                if (j<mWidth - 1)   pBoardHeader[mWidth * i + j].pRight = pBoardHeader[mWidth * (i)+j + 1];
            }
        }
    }

    public void Set(int start_x, int start_y, int end_x,int end_y)
    {
        int i, j;
        int left = min(start_x, end_x);
        int top = min(start_y, end_y);
        int right = max(start_x, end_x);
        int bottom = max(start_y, end_y);

        for (i = top; i <= bottom; i++)
        {
            for (j = left; j <= right; j++)
            {
                pBoardHeader[i * mWidth + j].SetOn();
            }
        }
    }
    
    public void Check(int start_x, int start_y, int end_x, int end_y){
        int i, j;
        int left = min(start_x, end_x);
        int top = min(start_y, end_y);
        int right = max(start_x, end_x);
        int bottom = max(start_y, end_y);

        for (i = top; i <= bottom; i++)
        {
            for (j = left; j <= right; j++)
            {
                pBoardHeader[i * mWidth + j].SetCheck();
            }
        }
    }
    public void AutoCheck(int start_x, int start_y, int end_x, int end_y){
        int i, j;
        int left = min(start_x, end_x);
        int top = min(start_y, end_y);
        int right = max(start_x, end_x);
        int bottom = max(start_y, end_y);


        GuidePannel pGuide;
        if(start_x == end_x)
        {
            pGuide = pCurrentGame.pHGuide;
            for (i = 0; i < mWidth; i++)
            {
                if (i == left)
                {
                    pGuide.PannelStatusCheck(mHeight, pBoardHeader[i], 1);
                    pGuide.PannelAutoStatusCheck(mHeight, pBoardHeader[i], 1);
                    pGuide.AI(mHeight, pBoardHeader[i], 1);
                }
                pGuide = pGuide.pNext;
            }
        }

        if (start_y == end_y)
        {
            pGuide = pCurrentGame.pVGuide;
            for ( i = 0; i < mHeight; i++)
            {
                if (i == top)
                {
                    pGuide.PannelStatusCheck(mWidth, pBoardHeader[i*mWidth], 0);
                    pGuide.PannelAutoStatusCheck(mWidth, pBoardHeader[i*mWidth], 0);
                    pGuide.AI(mWidth, pBoardHeader[i*mWidth], 0);
                }
                pGuide = pGuide.pNext;
            }
        }

    }
    
    public void Mark(int start_x, int start_y, int end_x, int end_y) {
        for (int i = 0; i < mHeight; i++)
        {
            for (int j = 0; j < mWidth; j++)
            {
              pBoardHeader[mWidth * i + j].strMarking = null;
            }
        }
        int left = min(start_x, end_x);
        int top = min(start_y, end_y);
        int right = max(start_x, end_x);
        int bottom = max(start_y, end_y);

        String txt = new String();
        int Count;

        if (left == right && top == bottom) {
            txt.format(".");
            pBoardHeader[mWidth * top + left].strMarking = txt;
        } else if (left == right) {
            GuidePannel pGuide;
             pGuide = pCurrentGame.pHGuide;
            for (int i = 0; i < mWidth; i++)
            {
                if (i == left)
                {
                    pGuide.PannelStatusCheck(mHeight, pBoardHeader[i], 1);
                    pGuide.PannelAutoStatusCheck(mHeight, pBoardHeader[i], 1);
                    pGuide.AI(mHeight, pBoardHeader[i], 1);
                    pGuide.GetCurrentFL(mWidth,end_y, pBoardHeader[i], 1,  CurrentFL);

                }
                pGuide = pGuide.pNext;
            }

            if (end_y >= start_y) {

                for (int i = top; i <= bottom; i++)        //down
                {
                    Count = 0;
                    if (pBoardHeader[top * mWidth + left].pUp != null) {
                        Count = pBoardHeader[top * mWidth + left].pUp.CountUpper();
                    }
                    if (pBoardHeader[i * mWidth + left].pDown != null)
                        Count += pBoardHeader[i * mWidth + left].pDown.CountBelow();
                        pBoardHeader[mWidth * i + left].strMarking = txt.format("%d", Count + i - top + 1);
                }
            } else {
                for (int i = top; i <= bottom; i++)    // upper
                {
                    Count = 0;
                    if (pBoardHeader[bottom * mWidth + left].pDown != null) {
                        Count = pBoardHeader[bottom * mWidth + left].pDown.CountBelow();
                    }

                    if (pBoardHeader[i * mWidth + left].pUp != null)
                        Count += pBoardHeader[i * mWidth + left].pUp.CountUpper();
                    pBoardHeader[mWidth * i + left].strMarking = txt.format("%d", Count + bottom - i + 1);
                }
            }

        } else if (top == bottom)    //가로
        {
            GuidePannel pGuide;
            pGuide = pCurrentGame.pVGuide;
            for ( int i = 0; i < mHeight; i++)
            {
                if (i == top)
                {
                    pGuide.PannelStatusCheck(mWidth, pBoardHeader[i*mWidth], 0);
                    pGuide.PannelAutoStatusCheck(mWidth, pBoardHeader[i*mWidth], 0);
                    pGuide.AI(mWidth, pBoardHeader[i*mWidth], 0);
                    pGuide.GetCurrentFL(mWidth,end_x, pBoardHeader[i*mWidth], 0,  CurrentFL);
                }
                pGuide = pGuide.pNext;
            }

            if (end_x > start_x) {

                for (int i = left; i <= right; i++)    //오른쪽
                {
                    Count = 0;
                    if (pBoardHeader[top * mWidth + left].pLeft != null) {
                        Count = pBoardHeader[top * mWidth + left].pLeft.CountLeft();
                    }

                    if (pBoardHeader[top * mWidth + i].pRight != null)
                        Count += pBoardHeader[top * mWidth + i].pRight.CountRight();

                    pBoardHeader[mWidth * top + i].strMarking = txt.format("%d", Count + i - left + 1);
                }
            } else {

                for (int i = left; i <= right; i++)    //외쪽
                {
                    Count = 0;
                    if (pBoardHeader[top * mWidth + i].pLeft != null) {
                        Count = pBoardHeader[top * mWidth + i].pLeft.CountLeft();
                    }

                    if (pBoardHeader[top * mWidth + right].pRight != null)
                        Count += pBoardHeader[top * mWidth + right].pRight.CountRight();

                    pBoardHeader[mWidth * top + i].strMarking = txt.format("%d", Count + right - i + 1);;
                 }
            }

        } else {
            int j;
            for (int i = top; i <= bottom; i++) {
                for (j = left; j <= right; j++) {
                    if (i == top) {
                        pBoardHeader[mWidth * i + j].strMarking = txt.format("%d", j - left + 1);
                    } else if (j == left) {
                        pBoardHeader[mWidth * i + j].strMarking = txt.format("%d", i - top + 1);
                    } else {
                        if (i == bottom && j == right) {
                            pBoardHeader[mWidth * i + j].strMarking = txt.format("%dx%d", (right - left + 1) , (bottom - top + 1));
                        } else {
                            pBoardHeader[mWidth * i + j].strMarking = txt.format(".");
                        }
                    }
                }
            }
        }
    }



    public void finalize()
    {
        pBoardHeader = null;
        pGameDataHeader.pPrev.pNext = null;
        pGameDataHeader = null;
    }

    public int DrawMini(Canvas canvas)
    {
        RectF rect;
        int i;
        int j;
        mPaint.setTextSize(30);
        for (i = 0; i < mHeight; i++)
        {
            for (j = 0; j < mWidth; j++)
            {

                if (pBoardHeader[i * mWidth + j].GetStatus() >= 3)
                {
                    pBoardHeader[i * mWidth + j].DrawTileFinal1(canvas);
                }
                rect = pBoardHeader[i * mWidth + j].GetPosition();
                if(pBoardHeader[i * mWidth + j].strMarking != null)
                    canvas.drawText(pBoardHeader[i * mWidth + j].strMarking,rect.centerX(),rect.centerY(),mPaint);
            }
        }

        return 0;
    }

    public Point GetPointXY(Point xy)
    {
        Point ret = new Point();
        ret.x = (int)(((double)(xy.x - mOffset.x - (mCodeWidth * StepX))) / StepX);
        ret.y = (int)(((double)(xy.y - mOffset.y - (mCodeHeight * StepX))) / StepY);
        if ((xy.x - mOffset.x - (mCodeWidth * StepX))<0)
        {
            ret.x -= 1;
        }
        if ((xy.y- mOffset.y - (mCodeHeight * StepY))<0)
        {
            ret.y -= 1;
        }
        return ret;

    }

    public int DrawNaNoGram(Canvas canvas, RectF rectWindow){
        int i;
        int j;

        RectF rect = new RectF();

        mPaint.setColor(0xFF000000);
        //CFont pOldFont;

        for (i = 0; i < mHeight; i++) // 가로 격자
        {
		    canvas.drawLine(mOffset.x, mOffset.y + StepY * (i + mCodeHeight) + (i / 5),
                        mOffset.x + StepX * (mWidth + mCodeWidth) + (mWidth / 5), mOffset.y + StepY * (i + mCodeHeight) + (i / 5), mPaint);

            canvas.drawLine(mOffset.x, mOffset.y + StepY * (i + 1 + mCodeHeight) + (i / 5),
                        mOffset.x + StepX * (mWidth + mCodeWidth) + (mWidth / 5), mOffset.y + StepY * (i + 1 + mCodeHeight) + (i / 5), mPaint);

        }
        // Vertial Pannel

        for ( i = 0; i < mHeight; i++)
        {
            for ( j = 0; j < mWidth; j++)
            {
                rect.set(mOffset.x + (j + mCodeWidth) * StepX + (j / 5),
                        mOffset.y + (i + mCodeHeight) * StepY + (i / 5),
                        mOffset.x + (j + mCodeWidth + 1) * StepX + (j / 5) + 1,
                        mOffset.y + (i + mCodeHeight + 1) * StepY + (i / 5) + 1);
                 pBoardHeader[i * mWidth + j].SetPosition(rect);
                 pBoardHeader[i * mWidth + j].DrawTile(canvas);
            }
        }

        for (i = 0; i < mWidth; i++) // 세로 격자 그리기
        {
	
            canvas.drawLine(mOffset.x + StepX * (i + mCodeWidth) + (i / 5), mOffset.y,
                    mOffset.x + StepX * (i + mCodeWidth) + (i / 5), mOffset.y + StepY * (mHeight + mCodeHeight) + (mHeight / 5), mPaint);

            canvas.drawLine(mOffset.x + StepX*(i+1 + mCodeWidth) + (i / 5), mOffset.y,
                        mOffset.x + StepX*(i + 1 + mCodeWidth) + (i / 5), mOffset.y + StepY*(mHeight + mCodeHeight) + (mHeight / 5), mPaint);

        }

        DrawPannels(canvas, rectWindow);

        DrawMini(canvas);

        mPaint.setColor(0x805A4020);
        mPaint.setTextSize(20);
        canvas.drawText("(C)Copyright I don't know, Not for sale this Application",
        mOffset.x + StepX * (mWidth + mCodeWidth) - 300, mOffset.y + StepY * (mHeight + mCodeHeight) + (mHeight / 5) + 23, mPaint);

        mPaint.setTextSize(40);
        canvas.drawText(mStrGameDescription,
                mOffset.x , mOffset.y + StepY * (mHeight + mCodeHeight) + (mHeight / 5) + 40, mPaint);

        // Draw Circle

        mPaintBotton.setColor(0x10000000);
        mPaintBotton.setTextSize(50);
        canvas.drawCircle(20,mOffset.y + StepY * (mHeight + mCodeHeight)+150,100,mPaintBotton);
        canvas.drawCircle(rectWindow.width()*2/3,mOffset.y + StepY * (mHeight + mCodeHeight)+150,100,mPaintBotton);
        canvas.drawCircle(rectWindow.right-20,mOffset.y + StepY * (mHeight + mCodeHeight )+150,100,mPaintBotton);

        mPaintBotton.setColor(0xFF000000);
        mPaintBotton.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("<<",20,mOffset.y + StepY * (mHeight + mCodeHeight )+160,mPaintBotton);
        canvas.drawText("Hint",2*rectWindow.width()/3,mOffset.y + StepY * (mHeight + mCodeHeight )+160,mPaintBotton);
        canvas.drawText(">>",rectWindow.right-20,mOffset.y + StepY * (mHeight + mCodeHeight )+160,mPaintBotton);

        if(bMarker) {
            mPaintBotton.setColor(0x805A4020);
            canvas.drawCircle(rectWindow.width()/3,mOffset.y + StepY * (mHeight + mCodeHeight )+150,80,mPaintBotton);
            mPaintBotton.setColor(0xFF000000);
            canvas.drawText("Mark", rectWindow.width() / 3, mOffset.y + StepY * (mHeight + mCodeHeight ) + 160, mPaintBotton);
        }else {
            mPaintBotton.setColor(0x105A4020);
            canvas.drawCircle(rectWindow.width()/3,mOffset.y + StepY * (mHeight + mCodeHeight )+150,100,mPaintBotton);

            mPaintBotton.setColor(0xFF000000);
            canvas.drawLine(rectWindow.width()/3-40,mOffset.y + StepY * (mHeight + mCodeHeight )+150-40,
                    rectWindow.width()/3+40,mOffset.y + StepY * (mHeight + mCodeHeight )+150+40,mPaintBotton);
            canvas.drawLine(rectWindow.width()/3-40,mOffset.y + StepY * (mHeight + mCodeHeight )+150+40,
                    rectWindow.width()/3+40,mOffset.y + StepY * (mHeight + mCodeHeight )+150-40,mPaintBotton);

            canvas.drawText("Check", rectWindow.width() / 3, mOffset.y + StepY * (mHeight + mCodeHeight ) + 160, mPaintBotton);
        }

        // Tools

        RectF rectStart = new RectF();
        RectF rectEnd = new RectF();
        RectF rectRefStart = new RectF();
        RectF rectRefEnd = new RectF();
        RectF rectEnd1 = new RectF();

        //DrawNaNoGram(pDC, windowRect);


        int left = min(start_x, end_x);
        int top = min(start_y, end_y);
        int right = max(start_x, end_x);
        int bottom = max(start_y, end_y);

        rectStart = pBoardHeader[top*mWidth + left].GetPosition();
        rectEnd = pBoardHeader[bottom*mWidth + right].GetPosition();
        //rectEnd1 = pBoardHeader[end_y*mWidth + end_x].GetPosition();
        mPaint.setColor(rgb(100,0,50));
        mPaint.setStrokeWidth(3);
        if (left == right && top == bottom)
        {
            /*Nathing to draw*/
        }else if (left == right)
        {

           //CurrentFL
            if (end_y >= start_y)
            {
              //  for (i = top; i <= bottom; i++)		//down
                {
                    rectRefStart = pBoardHeader[CurrentFL.mValue.mStart*mWidth + left].GetPosition();
                    rectRefEnd = pBoardHeader[CurrentFL.mValue.mEnd*mWidth + right].GetPosition();
                    canvas.drawLine(rectRefStart.left, rectRefStart.top, rectRefEnd.left, rectEnd.bottom, mPaint);
                    canvas.drawText(String.format("%d", end_y - CurrentFL.mValue.mStart+1 ),rectEnd.left-20, rectEnd.centerY(), mPaint);

                }
            }
            else
            {
                //for (i = top; i <= bottom; i++)	// upper
                {
                    rectRefStart = pBoardHeader[CurrentFL.mValue.mStart*mWidth + left].GetPosition();
                    rectRefEnd = pBoardHeader[CurrentFL.mValue.mEnd*mWidth + right].GetPosition();
                    canvas.drawLine(rectRefStart.left, rectStart.top , rectRefEnd.left, rectRefEnd.bottom, mPaint);
                    canvas.drawText(String.format("%d", CurrentFL.mValue.mEnd - end_y + 1), rectStart.left-20, rectStart.centerY(), mPaint);

                }
            }

        }else if (top == bottom)	//가로
        {
            if (end_x > start_x)
            {

             //   for (i = left; i <= right; i++)	//오른쪽
                {
                 //  rectEnd1 = pBoardHeader[mWidth*top + i].GetPosition();
                    //canvas.Ellipse(rectEnd1);
                    rectRefStart = pBoardHeader[mWidth*top+CurrentFL.mValue.mStart].GetPosition();
                    rectRefEnd = pBoardHeader[mWidth*top + CurrentFL.mValue.mEnd].GetPosition();
                    canvas.drawLine( rectRefStart.left, rectRefStart.top, rectEnd.right, rectRefEnd.top, mPaint);
                    canvas.drawText(String.format("%d", end_x - CurrentFL.mValue.mStart + 1), rectEnd.centerX(), rectEnd.top - 20, mPaint);

			        }
            }
            else
            {
             //  for (i = left; i <= right; i++)	//왼쪽
                {
                    rectEnd1 = pBoardHeader[mWidth*top + i].GetPosition();
                    //canvas.Ellipse(rectEnd1);
                   rectRefStart = pBoardHeader[mWidth*top + CurrentFL.mValue.mStart].GetPosition();
                    rectRefEnd = pBoardHeader[mWidth*top + CurrentFL.mValue.mEnd].GetPosition();
                    canvas.drawLine( rectStart.left, rectRefStart.top,  rectRefEnd.right, rectRefEnd.top, mPaint);
                    canvas.drawText(  String.format("%d", CurrentFL.mValue.mEnd - end_x + 1),rectStart.centerX(), rectStart.top-20, mPaint);
                }
            }
        }
        mPaint.setStrokeWidth(1);
        return 0;
    }

    public int DrawPannels(Canvas canvas, RectF rectWindow)
    {
        String text;
        RectF rect = new RectF();
        int i, j;

        for (i = 0; i < mHeight; i++)
        {
            for (j = 0; j < mCodeWidth; j++)
            {
                rect.set(mOffset.x + (j)*StepX, mOffset.y + (i + mCodeHeight)*StepY + (i / 5),
                        mOffset.x + (j + 1)*StepX, mOffset.y + (i + mCodeHeight + 1)*StepY + (i / 5));
                GuidePannel pPannel = pCurrentGame.GetVerGuide(i);

                if (pPannel!=null)
                {
                    GuideValue pValue = pPannel.GetValue(j - mCodeWidth + pPannel.mNumOfValue);
                    if (pValue!=null)
                    {
                        if (pValue.mAchieve>0)
                            mPaint.setColor(rgb(220, 220, 180));
                        else
                            mPaint.setColor(rgb(0, 0, 0));
                        text = String.format("%d", pValue.mValue);
                        mPaint.setTextAlign(Paint.Align.CENTER);
                        mPaint.setTextSize(StepX-2);
                        canvas.drawText(text, rect.centerX(),rect.bottom-1,mPaint);
                    }
                }

            }
        }

        for (i = 0; i < mWidth; i++)
        {

            for (j = 0; j < mCodeHeight; j++)
            {
                rect.set(mOffset.x + (mCodeWidth + i)*StepX + (i / 5), mOffset.y + j*StepY + (j / 5), mOffset.x + (mCodeWidth + i + 1)*StepX + (i / 5), mOffset.y + (j + 1)*StepY + (j / 5));
                int value;

                GuidePannel pPannel = pCurrentGame.GetHorGuide(i);

                if (pPannel!=null)
                {
                    GuideValue pValue = pPannel.GetValue(j - mCodeHeight + pPannel.mNumOfValue);
                    if (pValue!=null)
                    {
                        if (pValue.mAchieve>0)
                            mPaint.setColor(rgb(220, 220, 180));
                        else
                            mPaint.setColor(rgb(0, 0, 0));


                         mPaint.setTextSize(StepX-2);
                        text = String.format("%d", pValue.mValue);
                        mPaint.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText(text, rect.centerX(),rect.bottom-1,mPaint);
                    }
                }
            }
        }
        return 0;
    }

    public int DrawFinal(Canvas canvas, RectF RectWindow)
    {
        int i;
        int j;

        // Draw Circle

        mPaintBotton.setColor(0x10000000);
        mPaintBotton.setTextSize(50);
        canvas.drawCircle(RectWindow.right-20,mOffset.y + StepY * (mHeight + mCodeHeight )+150,100,mPaintBotton);

        mPaintBotton.setColor(0xFF000000);
        mPaintBotton.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(">>",RectWindow.right-20,mOffset.y + StepY * (mHeight + mCodeHeight )+160,mPaintBotton);


        StepX = (RectWindow.width() - 30) / (float)(mWidth + mCodeWidth);
        StepY = (RectWindow.height() - 50) / (float)(mHeight+ mCodeHeight);
        if (StepX > StepY)  StepX = StepY;  else  StepY = StepX;

        mOffset.x = (int)((RectWindow.width() - (mWidth  * StepX))/2);
        mOffset.y = (int)((RectWindow.height() - (mHeight) *StepY-550));
        if(mOffset.y <10)
            mOffset.y=10;

        RectF rect = new RectF();
        for ( i = 0; i < mHeight; i++)
        {
            for ( j = 0; j < mWidth; j++)
            {
                rect.set(mOffset.x + (j ) * StepX ,
                        mOffset.y + (i ) * StepY ,
                        mOffset.x + (j+ 1) * StepX -1,
                        mOffset.y + (i + 1) * StepY - 1);
                pBoardHeader[i * mWidth + j].SetPosition(rect);
                if (pBoardHeader[i * mWidth + j].GetStatus()==3)
                    pBoardHeader[i * mWidth + j].DrawTileFinal(canvas);
            }
        }



        int  k, l, a, b;
        for (i = 0; i < mHeight; i++) {
            for (j = 0; j < mWidth; j++) {
                mPaint.setColor(rgb(120, i*90/mHeight, j *100/mWidth));

                for (k = i - 1; k <= i + 1; k++)    //세로
                {
                    for (l = j - 1; l <= j + 1; l++)    // 가로
                    {
                        if (k > -1 && l > -1 && k < mHeight && l < mWidth) {
                            if (l == j && k == i) {
                                for (a = 0; a < 7; a++) {
                                    canvas.drawLine(pBoardHeader[i * mWidth + j].mPatch[a].x, pBoardHeader[i * mWidth + j].mPatch[a].y,
                                            pBoardHeader[k * mWidth + l].mPatch[a + 1].x, pBoardHeader[k * mWidth + l].mPatch[a + 1].y, mPaint);
                                }
                                canvas.drawLine(pBoardHeader[i * mWidth + j].mPatch[a].x, pBoardHeader[i * mWidth + j].mPatch[a].y,
                                        pBoardHeader[k * mWidth + l].mPatch[0].x, pBoardHeader[k * mWidth + l].mPatch[0].y, mPaint);
                            } else {

                                if (pBoardHeader[k * mWidth + l].GetStatus() == 3)
                                {
                                    if (pBoardHeader[i * mWidth + j].GetStatus() == 3) {
                                        if (abs(k - j) + abs(l - j) == 2) {
                                            if (pBoardHeader[i * mWidth + j].GetStatus() == 3) {
                                                for (a = 0; a < 8; a++) {
                                                    for (b = 0; b < 8; b++) {
                                                        canvas.drawLine(pBoardHeader[i * mWidth + j].mPatch[a].x, pBoardHeader[i * mWidth + j].mPatch[a].y,
                                                                pBoardHeader[k * mWidth + l].mPatch[b].x, pBoardHeader[k * mWidth + l].mPatch[b].y, mPaint);
                                                    }
                                                }
                                            }
                                        } else {
                                            for (a = 0; a < 8; a++) {
                                                for (b = 0; b < 8; b++) {
                                                    canvas.drawLine(pBoardHeader[i * mWidth + j].mPatch[a].x, pBoardHeader[i * mWidth + j].mPatch[a].y,
                                                            pBoardHeader[k * mWidth + l].mPatch[b].x, pBoardHeader[k * mWidth + l].mPatch[b].y, mPaint);
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }
        return 0;
    }
};