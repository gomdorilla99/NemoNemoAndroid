package com.example.nemonemo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Vector;

public class GuidePannel {
    private final int MAX_TABLE = 256;
    public GuidePannel pNext;
    public GuideValue pHeader;
    public Paint mPaint = new Paint();
    public int mRemainValues;

    BoardTile NEXT_ITEM(BoardTile pTile, int direction)
    {
        if(direction==0)
            return pTile.pRight;
        else
            return pTile.pDown;
    };

    BoardTile PREV_ITEM(BoardTile pTile, int direction)
    {
        if(direction==0)
            return pTile.pLeft;
        else
            return pTile.pUp;

    };
    
    public GuideValue GetValue(int index) {
        GuideValue pValue;
        if (index < 0)
            return null;
        if (pHeader==null)
            return null;

        pValue = pHeader;
        for (int i = 0; i < index; i++)
        {
            if (pValue.pNext==null)
                return null;
            pValue = pValue.pNext;
        }
        return pValue;
    };
    
    public int PannelStatusCheck(int nCount, BoardTile pTile, int direction)
    {
        int i;
        int[] Normal_fixed_sequence = new int[nCount];
        int[] Backside_fixed_sequence = new int[nCount];
        int[] Total_Sequence = new int[nCount];
        boolean bOn;

        BoardTile pCurTile;
        BoardTile pTailTile;
        pCurTile = pTile;
        int itemCount;
        //initialize
        for (i = 0; i < nCount; i++)
        {
            Normal_fixed_sequence[i] = 0;
            Backside_fixed_sequence[i] = 0;
            Total_Sequence[i]=0;
        }

        //Full Searching
        bOn = false;
        for (i = 0, itemCount = 0; i < nCount; i++)
        {
            if (pCurTile.GetStatus() == 3)
            {
                bOn = true;
                Total_Sequence[itemCount]++;
            }
            else if (bOn)
            {
                bOn = false;
                itemCount++;
            }
            pCurTile = MoveToNext(direction, pCurTile);

        }
        pTailTile = pCurTile;	// 끝 타일


        //처음부터 확정타일만 검색
        pCurTile = pTile;
        bOn = false;
        for (i = 0, itemCount = 0; i < nCount; i++)
        {
            if (pCurTile.GetStatus()>0)	//뭔가라도있으면 진행
            {
                if (pCurTile.GetStatus() >= 3)
                {
                    bOn = true;
                    Normal_fixed_sequence[itemCount]++;
                }
                else if(bOn)
                {
                    bOn = false;
                    itemCount++;
                }
            }
            else
            {
                bOn = false;
                break;
            }
            pCurTile = MoveToNext(direction, pCurTile);
        }

        //끝부터 확정타일만 검색

        pCurTile = pTailTile;
        bOn = false;
        for (i = 0, itemCount = 0; i < nCount; i++)
        {
            if (pCurTile.GetStatus()>0)	//뭔가라도있으면 진행
            {
                if (pCurTile.GetStatus() >= 3)
                {
                    bOn = true;
                    Backside_fixed_sequence[itemCount]++;
                }
                else if (bOn)
                {
                    bOn = false;
                    itemCount++;
                }
            }
            else
            {
                bOn = false;
                break;
            }
     pCurTile = MoveToPrev(direction, pCurTile);
        }
        
        // 가이드와 비교
        GuideValue pValue = pHeader;

        for (i = 0; i < mNumOfValue; i++)
        {
            pValue.mAchieve = 0;
            pValue = pValue.pNext;
        }
        pValue = pHeader;

        int count = 0;
        int sum_of_value = 0;
        int sum_of_value_1 = 0;
        for (i = 0; i < mNumOfValue; )
        {
            if (pValue.mValue >0)
            {
                sum_of_value += pValue.mValue;
                sum_of_value_1 += Total_Sequence[i];
                if (pValue.mValue == Normal_fixed_sequence[i])
                {
                    if (Normal_fixed_sequence[i] > 0)
                    {
                        //count++;
                        pValue.mAchieve = 1;
                    }
                }
            }
            i++;
            pValue = pValue.pNext;
            if (pValue==null)
                break;
        }
        
        pValue = pHeader;

        if (sum_of_value == sum_of_value_1)
        {
            for (i = 0; i < mNumOfValue; i++)
            {
                if (pValue.mValue == Total_Sequence[i])
                    pValue.mAchieve = 1;
                else
                    break;
                pValue = pValue.pNext;
                if (pValue==null)
                    break;
            }
            //intf("\r\n");
        }


        pValue = pHeader.GetTail();
        for (i = 0; i < mNumOfValue; )
        {
            if (pValue.mValue == Backside_fixed_sequence[i])
            {
                pValue.mAchieve = 1;
                i++;
                if (Backside_fixed_sequence[i]==0)
                    break;
            }
            else
                break;
            pValue = pValue.pPrev;
            if (pValue==null)
                break;
        }
            pValue = pHeader;
        count = 0;
        for (i = 0; i < mNumOfValue; i++)
        {
            if (pValue.mAchieve != 0) count++;
            pValue = pValue.pNext;
        }

    //todo     delete[] Normal_fixed_sequence;
    //todo    delete[] Backside_fixed_sequence;
    //todo    delete[] Total_Sequence;
        mRemainValues = mNumOfValue - count;
        return mNumOfValue - count;
    }
    
    public int SetState(BoardTile pTile, int Start, int End, int StateValue,int direction)
    {
        BoardTile pCurTile = pTile;
        int index = 0;
        int status;
        while (pCurTile!=null)
        {
            if ((Start <= index) && (index<=End))
            {
                status = pCurTile.GetStatus();
                if (status == 0)
                    pCurTile.SetStatus(StateValue);
            }
            pCurTile = MoveToNextWithNoneProtect(direction, pCurTile);
            index++;
        }
        return 0;
    }
    public int PannelAutoStatusCheck(int nCount, BoardTile pTile, int direction)
    {
        GuideValue pValue = pHeader;
        BoardTile pCurTile;
        char[] debug_buffer = new char[256];

        int i,j;
        int	count = 0;
        int total_count = 0;
        int total;
        int empty_count = 0;
        int first_position = -1;
        int last_position = -1;
        int set_count = 0;
        int left_count=0;
        int right_count=0;
        while (NEXT_ITEM(pTile, direction)!=null)
        {
            if ((pTile.GetStatus() == 1) || (pTile.GetStatus() == 2))
                left_count++;
            else
            {
                pTile = pTile.MoveToTileEnd(direction);
                break;
            }
            pTile = NEXT_ITEM(pTile, direction);
        }

        while (PREV_ITEM(pTile, direction)!=null)
        {
            if ((pTile.GetStatus() == 1) || (pTile.GetStatus() == 2))
                right_count++;
            else
            {
                pTile = pTile.MoveToTileFront(direction);
                break;
            }
            pTile = PREV_ITEM(pTile, direction);
        }

        pValue = pHeader;
        for (i = 0; i < mNumOfValue; i++)
        {
            if (pValue.mAchieve > 0)
                count++;
            total_count += pValue.mValue;
            pValue = pValue.pNext;
        }
        total = total_count;
        total_count += (mNumOfValue - 1);
        pCurTile = pTile;
        i = 0;
        while (pCurTile !=null)
        {
            debug_buffer[i] = (char)pCurTile.GetStatus();
            if (pCurTile.GetStatus() == 0)
                empty_count++;
            if (pCurTile.GetStatus() > 2)
            {
                if (first_position < 0)
                    first_position = i;
                last_position = i;
                set_count++;
            }
            i++;
            pCurTile = NEXT_ITEM(pCurTile, direction);//// pCurTile.pRight;
        }

        //Item1 : 완성 행/열 나머지 체크

        if (mNumOfValue == count)
        {
            pCurTile = pTile;
            for (i = 0; i < nCount; i++)
            {
                if (pCurTile.GetStatus() == 0)
                    pCurTile.SetStatus(2);
                pCurTile = NEXT_ITEM(pCurTile, direction);
            }
        }
        //Item2 : 빈틈없이 이미 결정난경우
        pValue = pHeader;
        if (total_count == nCount)
        {
            pCurTile = pTile;
            for (i = 0; i < mNumOfValue; i++)
            {
                for (j = 0; j < pValue.mValue; j++)
                {
                    pCurTile.SetStatus(3);
                    pCurTile = NEXT_ITEM(pCurTile, direction);
                }
                if (pCurTile!=null)
                {
                    pCurTile.SetStatus(2);
                    pCurTile = NEXT_ITEM(pCurTile, direction);
                }
                if (pValue!=null)		pValue = pValue.pNext;
            }
        }

        //Item3 : 하나의 페널일때 빈공간의 크기가 단일값과 같을경우

        pValue = pHeader;
        if (mNumOfValue==1)
        {
            if (empty_count == total_count - set_count)
            {
                pCurTile = pTile;
                for (i = 0; i < mNumOfValue; i++)
                {
                    for (j = 0; j < pValue.mValue; j++)
                    {
                        if (pCurTile==null)
                            break;
                        if (pCurTile.GetStatus() == 0)
                            pCurTile.SetStatus(3);
                        else
                        {
                            j--;
                            pCurTile = NEXT_ITEM(pCurTile, direction);
                            continue;
                        }
                        pCurTile = NEXT_ITEM(pCurTile, direction);
                    }
                    if (pCurTile!=null)
                    {
                        pCurTile.SetStatus(2);
                        pCurTile = NEXT_ITEM(pCurTile, direction);
                    }
                    if (pValue!=null)		pValue = pValue.pNext;
                }
            }
        }
        //Item3 : 가정법
        int pre_count;
        int next_count;
        next_count = total;
        pre_count = 0;
        pValue = pHeader;
        pCurTile = pTile;
        next_count -= pValue.mValue;
        for (i = 0; i < mNumOfValue; i++)
        {
            if ((nCount -right_count - next_count - mNumOfValue  - i - pValue.mValue+1) <= pre_count + pValue.mValue-1+left_count)
            {
                SetState(pTile, ( nCount - right_count - next_count - mNumOfValue  - i - pValue.mValue+1), pre_count + pValue.mValue-1+left_count, 3, direction);	//가로
            }
            pre_count += pValue.mValue + 1;
            pValue = pValue.pNext;
            if (pValue!=null)
                next_count -= (pValue.mValue+2);
        }


            pValue = pHeader;
        if (first_position > -1)
        {
            if (first_position- left_count  < pValue.mValue)
            {
                SetState(pTile, first_position, pValue.mValue - 1, 3, direction);
                if(first_position==0)
                    SetState(pTile, pValue.mValue , pValue.mValue , 2, direction);
            }
        }
        pValue = pHeader.GetTail();
        if (last_position > -1)
        {
            if (last_position > nCount - pValue.mValue)
            {
                SetState(pTile, nCount - pValue.mValue, last_position, 3, direction);
                if (last_position == nCount-1)
                    SetState(pTile, nCount - pValue.mValue-1, nCount - pValue.mValue-1, 2, direction);
            }
        }

        if (mNumOfValue == 1)
        {
            if (first_position > -1 && last_position > -1)
                SetState(pTile, first_position, last_position, 3, direction);
        }

        //TRACE("\r\n");\
        //확정된 블럭다음 체크로 닫아주기 (정방향)


        return 0;
    }
    
    public int  Draw_FL_info(Canvas canvas, BoardTile pTile, int direction, Node<FragNode> pFL)
    {
        int i = 0;
        RectF rectStart = null;
        RectF rectEnd = null;
        String strtext;
        for (; pTile!=null;i++)
        {
            if (i == pFL.mValue.mStart)
                rectStart = pTile.GetPosition();
            if(i == pFL.mValue.mEnd)
                rectEnd = pTile.GetPosition();
            pTile =MoveToNextWithNoneProtect(direction, pTile);
        }
        canvas.drawLine(rectStart.centerX()+2, rectStart.top+2,rectEnd.centerX()-2, rectEnd.top+2, mPaint);
        
        return 0;
    }

    public void AddValue(int value)
    {
        GuideValue pTail;
        if (pHeader==null)
        {
            GuideValue pValue = new GuideValue();
            pValue.mValue = value;
            pHeader = pValue;
        }
        else
        {
            GuideValue pValue = new GuideValue();
            pValue.mValue = value;
            pTail = pHeader.GetTail();
            pTail.pNext = pValue;
            pValue.pPrev = pTail;
        }
    }
    public int mNumOfValue;
    
//	int AddGuide(char strGuide);
    GuidePannel  GetTail(){
        if (pNext!=null){return(pNext.GetTail());}else{return this;}
    };

    //for solver
    void AI(int nCount, BoardTile pTile, int direction)
    {
        ValuesList ValueList = new ValuesList();
        GuideValue pGValue = pHeader;
        CaseChecker CaseChecker = new CaseChecker();
        Fragmentation FL = new Fragmentation();
        Node<CaseNode>  pCurCase;

        Vector<Integer>  StrBuffer = new Vector<Integer>(256);
        boolean bOpen = false;
        Vector<Integer> buffer =  new Vector<Integer>(MAX_TABLE);
        Vector<Integer> MustHave_buffer =  new Vector<Integer>(MAX_TABLE);
        Vector<Integer> output_buffer =  new Vector<Integer>(MAX_TABLE);

        int i;

        BoardTile pCurTile;

        pCurTile = pTile;
        for (i = 0; i < nCount; i++)
        {
          //  buffer.set(i, pCurTile.GetStatus());
            buffer.add(pCurTile.GetStatus());
            pCurTile = MoveToNextWithNoneProtect(direction, pCurTile);
        }

        Node<FragNode> pFL;

        // Test bench
        for( i=0;i<256;i++) {
            StrBuffer.add(0);
            output_buffer.add(0);
        }
        int start=0;
        int end;

        int mustHave = 0;
        // 사전 준비 Fragmentation을 작성	
        for (i = 0; i < mNumOfValue; i++)
        {
            Node<Integer> pValue = new Node<Integer>();
            pValue.mValue = pGValue.mValue;
            ValueList.AddToTail(pValue);
            pGValue = pGValue.pNext;
        }

        for (i = 0; i < nCount; i++)
        {
            if (buffer.get(i) == 1 || buffer.get(i) == 2) // 체크면 Flagment를 닫을수 있음
            {
                if (bOpen) // close Fragmentation
                {
                    end = i - 1;
                    pFL = new Node<FragNode>();
                    pFL.mValue = new FragNode();
                    pFL.mValue.Set(start, end, mustHave);
                    FL.AddToTail(pFL);
                }
                mustHave = 0;
                bOpen = false;
            }
            else // Empthy 이거나 확정이면 Flagement 를 시작할 수 있음 
            {
                if (buffer.get(i) == 3 || buffer.get(i) == 4)
                    {
                    mustHave++;
                }
                if (!bOpen)
                {
                    bOpen = true;
                    start = i;
                }
            }
        }
        if (bOpen) // 열린 상태에서 마친경우 close, 
        {
            end = i - 1;
            pFL = new Node<FragNode>();
            pFL.mValue = new FragNode();
            pFL.mValue.Set(start, end, mustHave);
            FL.AddToTail(pFL);
        }


        //Case generator
        CaseChecker.CaseGenerator(FL.GetHeaderNode(), ValueList.GetHeaderNode(), 0, StrBuffer);
        CaseChecker.Prepare(buffer);
        CaseChecker.Rendering(FL, ValueList.GetHeaderNode());
        int Nth = 0;
        for (pCurCase = CaseChecker.GetHeaderNode(); pCurCase!=null;Nth++, pCurCase = pCurCase.pNext)
        {
            int count_null_check = 0;
            for (i = 0; i < nCount; i++)
                count_null_check += pCurCase.mValue.mTable.get(i);

            if (count_null_check>0)
                pCurCase.mValue.table_merge(output_buffer, pCurCase.mValue.mTable, output_buffer, nCount);
       }


        pCurCase = CaseChecker.GetHeaderNode();

        if(pCurCase!=null)
        {
            for (i = 0; i < nCount; i++)
                output_buffer.set(i,pCurCase.mValue.mTable.get(i));

            Nth = 0;
            for (; pCurCase!=null;Nth++)
            {
                int count_null_check= 0;
                for (i = 0; i < nCount; i++)
                    count_null_check += pCurCase.mValue.mTable.get(i);

                if(count_null_check>0)
                {
                    // Display debug info
                    //DrawDebugInfor(pCurCase.mValue.mTable, Nth,direction);
                    pCurCase.mValue.table_merge(output_buffer, pCurCase.mValue.mTable, output_buffer, nCount);
                }
                pCurCase = pCurCase.pNext;
            } // if pCurCase
            pCurTile = pTile;
            for (i = 0; i < nCount; i++)
            {
                pCurTile.Update(output_buffer.get(i));
                pCurTile = MoveToNextWithNoneProtect(direction, pCurTile);
            }
        }	//pCurCase
   //     FL.DeleteAllNode();
   //     CaseChecker.DeleteAllNode();
   //     ValueList.DeleteAllNode();
    };


    void GetCurrentFL(int nCount, int currentPosition, BoardTile pTile, int direction, Node<FragNode> pCurrentFL)
    {
        Boolean bOpen = false;

        Vector<Integer> buffer =  new Vector<Integer>(MAX_TABLE);

        BoardTile pCurTile;
        int i;
        pCurTile = pTile;
        for (i = 0; i < nCount; i++)
        {
            //  buffer.set(i, pCurTile.GetStatus());
            buffer.add(pCurTile.GetStatus());
            pCurTile = MoveToNextWithNoneProtect(direction, pCurTile);
        }

        int start=0;
        int end=0;
        pCurrentFL.mValue = new FragNode();
         int fragIndex = 0;
        int mustHave = 0;


        for (i = 0; i < nCount; i++)
        {
            if (buffer.get(i) == 1 || buffer.get(i) == 2) // 체크면 Flagment를 닫을수 있음
            {
                if (bOpen) // close Flagmentation
                {
                    end = i - 1;

                    if (start <= currentPosition && currentPosition <= end)
                    {

                        pCurrentFL.mValue.Set(start, end, mustHave);
                        pCurrentFL.mValue.mIndex = fragIndex;
                    }
                    fragIndex++;
                }
                mustHave = 0;
                bOpen = false;
            }
            else // Empthy 이거나 확정이면 Flagement 를 시작할 수 있음
            {
                if (buffer.get(i) == 3 || buffer.get(i) == 4)
                {
                    mustHave++;
                }
                if (!bOpen)
                {
                    bOpen = true;
                    start = i;
                }
            }
        }
        if (bOpen) // 열린 상태에서 마친경우 close,
        {
            end = i - 1;
            if (start <= currentPosition && currentPosition <= end)
            {
                pCurrentFL.mValue.Set(start, end, mustHave);
                pCurrentFL.mValue.mIndex = fragIndex;
            }
            fragIndex++;
        }
    }


    public GuidePannel(){
        pNext = null;
        pHeader = null;
    };

	public void finalize()
    {

    }

    private BoardTile MoveToNext(int direction, BoardTile  pCurTile){
        if (direction>0)
        {
            if (pCurTile.pDown!=null)	//ver
                pCurTile = pCurTile.pDown;
        }
        else
        {
            if (pCurTile.pRight!=null)	//Hor
                pCurTile = pCurTile.pRight;
        }
        return pCurTile;
    }

    private BoardTile MoveToNextWithNoneProtect(int direction, BoardTile  pCurTile){
        if (direction>0)
            pCurTile = pCurTile.pDown;
        else
            pCurTile = pCurTile.pRight;
        return pCurTile;
    };

    private BoardTile MoveToPrev(int direction, BoardTile  pCurTile){
        if (direction == 0)	// Hor
        {
            if (pCurTile.pLeft!=null)
                pCurTile = pCurTile.pLeft;
        }
        else
        {
            if (pCurTile.pUp!=null)
                pCurTile = pCurTile.pUp;
        }
        return pCurTile;
    }
}

