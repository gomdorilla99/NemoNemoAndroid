package com.example.nemonemo;

//import android.util.Log;

import java.util.Vector;

public class CaseNode
{
    private final int MAX_TABLE = 256;
    private Node<Integer> pValueStart;
    private Node<Integer> pValueEnd;
    private Vector<Integer> mStrOutputBuffer = new Vector<Integer>(MAX_TABLE);
    public Vector<Integer> mStrBuffer = new Vector<Integer>(MAX_TABLE);
    public Vector<Integer> mTable = new Vector<Integer>(MAX_TABLE);
    private boolean bStrOutputBufferEmpty;


    private Node<FragNode> mpFl;
    private Vector<Integer> mStrPartialBuffer = new Vector<Integer>(MAX_TABLE);

/*
    void Set(Node<Integer> start, Node<Integer> end)
    {
        pValueStart = start;
        pValueEnd = end;
    }

    void table_merge(char[] inTable1, char[] inTable2, char[] outTable,int Count)
    {
        int i;
        for (i = 0; i < Count; i++)
        {
            if (inTable1[i] == inTable2[i])
                outTable[i] = inTable2[i];
            else
                outTable[i] = 0;
        }
        return;
    }
*/
    void table_merge(Vector<Integer> inTable1, Vector<Integer>  inTable2, Vector<Integer> outTable,int Count)
    {
        int i;
        for (i = 0; i < Count; i++)
        {
            if (inTable1.get(i) == inTable2.get(i))
                outTable.set(i, inTable2.get(i));
            else
                outTable.set(i, 0);
        }
        return;
    }

    public int CaseGenerator(Node<FragNode> pFl, Node<Integer> StartNode, Node<Integer> EndNode, Vector<Integer> strChar)
    {
        int i ;
        for (i=0  ; i<strChar.size();i++)
        {
            mStrPartialBuffer.add(strChar.get(i));   // Default 조건들을 받아야함.  FL범위 안의 데이터만 복사해야 함.
        }
        for (; i<256;i++)
        {
            mTable.add(0);
            mStrPartialBuffer.add(0);   // Default 조건들을 받아야함.  FL범위 안의 데이터만 복사해야 함.
        }
        
        Node<FragNode> CurFl = pFl;
        Node<Integer> CurValue = StartNode;

        int FLSpace = pFl.mValue.mEnd - pFl.mValue.mStart+1;
        int nValue;
        int nBlank;
        int nSumValue=0;
        if (pFl.mValue.mMustHaveMin == 0)	// 값이 없으면 할 필요가 없음.
        {
            return -1;
        }

        for(i = 0;i<80;i++) {
            mStrPartialBuffer.set(i, 0);
        }
        bStrOutputBufferEmpty = true;

        for (int j = pFl.mValue.mStart; j <= pFl.mValue.mEnd; j++)
        {
            mStrPartialBuffer.set(j- pFl.mValue.mStart ,  mTable.get(j));	//구간의 Patten을 하나 복사
        }

        for (nValue = 0; ; CurValue = CurValue.pNext)
        {
            nValue++;
            nSumValue += CurValue.mValue;
            if (CurValue == EndNode)
                break;

        } // Value 개수구하기

        nBlank = FLSpace - nSumValue;
        mpFl = pFl;
       // mpValueHeader = StartIndex;
        if (nValue>0)
        {
            int[] BlankList = new int[80];
            // Blank개수는 Value보다 하나 더 많이
            IntDivideFuc1(BlankList, nValue+1, FLSpace, StartNode, EndNode, nBlank,0);
        }
        if (bStrOutputBufferEmpty == false)
        {
            for (int j = pFl.mValue.mStart; j <= pFl.mValue.mEnd; j++)
            {
                mTable.set(j, mStrOutputBuffer.get(j - pFl.mValue.mStart));
            }
        }
        return 0;
    }

    /*
    public void BucketSet( Vector<Integer> Bucket, int value, int num )
    {
        for(int i=0;i<num;i++)
            Bucket.set(i, value);
    }
    public void BucketCpy( Vector<Integer> Bucket_dest,Vector<Integer> Bucket_src, int num )
    {
        for(int i=0;i<num;i++)
            Bucket_dest.set(i, Bucket_src.get(i));
    }

    */
    public void BucketSetWithOffset(Vector<Integer> Bucket, int value, int num, int offset )
    {
        for(int i=0;i<num;i++)
            Bucket.set(i+offset, value);
    }


    public int IntDivideFuc1(int[] BlankList, int nBuket, int FLSpace,Node<Integer> StartNode, Node<Integer> EndNode,int RemainBlank, int nThBuket)
    {
        if (RemainBlank > -1)
        {
            // 정수분할,  n개 버켓에 m개볼.
            if (nThBuket >= nBuket - 1)
            {
                BlankList[nThBuket] = RemainBlank;
                Vector<Integer> StrBuffer = new Vector<Integer> (100);
                Node<Integer> pValue = StartNode;
                int k;
                int startAddr = 0;
                for (k = 0; k < 100; k++)
                {
                    StrBuffer.add(0);
                }
                for (k = 0; k < nBuket - 1; k++)
                {
                    BucketSetWithOffset(StrBuffer , 2, BlankList[k], startAddr);	// blank로 설정
                    BucketSetWithOffset(StrBuffer , 3, pValue.mValue,startAddr + BlankList[k]);	// blank로 설정
                    startAddr += (BlankList[k] + pValue.mValue);
                    pValue = pValue.pNext;
                }
                BucketSetWithOffset(StrBuffer, 2, BlankList[k],startAddr);
                boolean pass = false;
                for (k = 0; k < FLSpace; k++)
                {
                    if ((mStrPartialBuffer.get(k) == 3) || (mStrPartialBuffer.get(k) == 4))
                    {
                        if (StrBuffer.get(k) != 3)
                        {
                            pass = false;
                            break;
                        }
                        else if (StrBuffer.get(k) == 3)
                        {
                            pass = true;
                        }
                    }
                }
                if (pass)
                {
                    if (bStrOutputBufferEmpty)
                    {
                        mStrOutputBuffer = StrBuffer;
                        bStrOutputBufferEmpty = false;
                    }
                    else
                    {
                        for (int j = 0; j < FLSpace; j++)
                        {
                            if (StrBuffer.get(j) == 3 && mStrOutputBuffer.get(j)==3)
                                mStrOutputBuffer.set(j, 3);
                            else if (StrBuffer.get(j) ==2 && mStrOutputBuffer.get(j) == 2)
                                mStrOutputBuffer.set(j,2);
                            else
                                mStrOutputBuffer.set(j, 0);
                        }
                    }
                }
            }
            else
            {
                int i;
                i = (nThBuket == 0) ? 0 : 1;
                for (; i <= FLSpace; i++)
                {
                    if (nThBuket < nBuket)
                    {
                        BlankList[nThBuket] = (char)i;
                        //      (int BlankList, int nBuket, int FLSpace, Node<Integer> StartIndex, Node<Integer> EndIndex, int RemainBlank, int nThBuket = 0)
                        IntDivideFuc1(BlankList, nBuket, FLSpace, StartNode, EndNode, RemainBlank - i, nThBuket + 1);
                    }
                }	// for i
            } //
        }
        return 0;
    }

    public int Fill(Node<FragNode> pFL, Node<Integer> StartIndex, Node<Integer> EndIndex, int state)
    {
        if (StartIndex==null)
            return -1;
        int ValueCount = StartIndex.mValue;
        int NumOfValue = 1;
        int nCount = pFL.mValue.mEnd - pFL.mValue.mStart + 1; // 구간의 크기
        int ValueMax=0; // 구간 값중 가장 큰값
        int ValueMin = 256;
        int ValueSum = 0;
        Node<Integer> pValue = StartIndex;

        //printf("[Fill]\r\n");


        if (EndIndex.pNext == StartIndex) // 불가해보이는데??
        {
            SetStatus(pFL.mValue.mStart, pFL.mValue.mEnd,2);
            return 1;
        }

        for( ;pValue != EndIndex.pNext; pValue = pValue.pNext)
        {
            if (pValue==null) // Value가 없는경우 렌더링 할 것도 없다.
                return -1;
            if (ValueMax < pValue.mValue)
                ValueMax = pValue.mValue;

            if (ValueMin > pValue.mValue)
                ValueMin = pValue.mValue;
            ValueSum += pValue.mValue;
            NumOfValue++;
        }
        NumOfValue--;
        int first = -1;	// 처음으로 정해진 값이 나오기 시작하는 지점
        int last = -1;	// 마지막으로 정해진 값이
        int last_blank = -1;	// 마지막으로 정해진 값이
        int first_blank = -1;	// 처음으로 정해진 값이 나오기 시작하는 지점


        int first_length = 0;
        int last_length = 0;
        int first_blank_length = 0;
        int last_blank_length = 0;
        int _last_blank_length = 0;
        int _last_length = 0;
        for (int j = pFL.mValue.mStart; j <= pFL.mValue.mEnd; j++)
        {

            if (mTable.get(j) > 2)	//선택된 것이 나오기 시작하는 점
            {
                if (first < 0)
                    first = j;	// 처음 나오면 first
                _last_length++;
                last_length = _last_length;

                last = j;
                _last_blank_length = 0;
            }

            else
            {
                if (mTable.get(j)>0 && first_blank < 0)
                {
                    first_blank = j;
                }
                if (mTable.get(j)>0)
                {
                    last_blank = j;
                    _last_blank_length++;
                    last_blank_length = _last_blank_length;
                }
                else
                {
                    if ((first_blank > -1) && first_blank_length == 0)
                        first_blank_length = last_blank - first_blank + 1;
                    _last_blank_length = 0;
                }
                if ((first > -1) && first_length == 0)
                    first_length = last - first + 1;
                _last_length = 0;
            }
        }
        if (_last_blank_length > 0 && last_length>0)
            last_length = _last_blank_length;
        pValue = StartIndex;
        if (first > 0)
        {
            if ((first_length == pValue.mValue) && (first - pFL.mValue.mStart < pValue.mValue + 1))
            {
                if (first < first_blank) // 공백없이 시작된 처음
                    //printf("첫 Value처리\n");
                    SetStatus(first + pValue.mValue, first + pValue.mValue, 2);
                else if (first_blank_length < pValue.mValue + 1)
                {
                    SetStatus(first - 1, first - 1, 2);
                    SetStatus(first + pValue.mValue, first + pValue.mValue, 2);
                }
            }
            if((first_length < pValue.mValue) && (first_blank< pValue.mValue ))
                SetStatus(first , pFL.mValue.mStart+pValue.mValue-1, 3);
        }

        if (last > 0)
        {
            if ((last_length == EndIndex.mValue) && (pFL.mValue.mEnd - last < EndIndex.mValue+1))
            {
                if (last > last_blank) // 공백보다 뒤에나온거면 끝난 마지막
                {
                    SetStatus(last - EndIndex.mValue, last - EndIndex.mValue, 2);
                }
                else if ((last_blank_length < EndIndex.mValue + 1 ))
                {
                    SetStatus(last + 1, last + 1, 2);
                    SetStatus(last - EndIndex.mValue, last - EndIndex.mValue, 2);
                }
            }
            if ((last_length < EndIndex.mValue) && (last_blank< EndIndex.mValue))
            {
                SetStatus(pFL.mValue.mEnd - EndIndex.mValue+1  ,last, 3);
            }
        }

        if (pValue.mValue == pFL.mValue.mEnd - pFL.mValue.mStart + 1)
             SetStatus(pFL.mValue.mStart, pFL.mValue.mEnd, 3);
        if (pFL.mValue.mEnd - pFL.mValue.mStart + 1 < ValueMin)
            SetStatus(pFL.mValue.mStart, pFL.mValue.mEnd , 2);

        if (NumOfValue>1) // value가 1이상일때
        {
            if (ValueSum + NumOfValue - 1 == pFL.mValue.mEnd - pFL.mValue.mStart + 1)	// 꽉차는경우
            {
                SetStatus(pFL.mValue.mStart, pFL.mValue.mStart, 3);	// 한점만찍으면 알아서 풀릴듯
                //SetStatus(pFL.mValue.mEnd, pFL.mValue.mEnd, 3);	//
            }

            if (first_length >1)
            {
                if (first_length == ValueMax)
                {
                    SetStatus(first-1, first-1, 2);
                    SetStatus(first + first_length , first + first_length , 2);
                }

            }

            if (last_length >1)
            {
                if (last_length == ValueMax)
                {
                    SetStatus(last + 1, last + 1, 2);
                    SetStatus(last - last_length, last - last_length, 2);
                }

            }
        }
        else if (NumOfValue==1)
        {
            if (last - first < ValueMax)
                SetStatus(first, last, 3);

            if (pFL.mValue.mMustHaveMin > 0)
            {
                if ((pFL.mValue.mEnd - pFL.mValue.mStart+1) == ValueCount)
                    SetStatus(pFL.mValue.mStart, pFL.mValue.mEnd, 3);


                if (first < 0 || last < 0)
                {
                    SetStatus(pFL.mValue.mEnd - pValue.mValue + 1, pFL.mValue.mStart + pValue.mValue - 1, 3);
                }
                else
                {

                    SetStatus( first, pFL.mValue.mStart + pValue.mValue-1, 3);
                    SetStatus( pFL.mValue.mEnd - pValue.mValue+1, last, 3);
                    SetStatus(pFL.mValue.mEnd - pValue.mValue + 1, first - 1, 3);

                    if (last - first  < pValue.mValue)
                    {
                        SetStatus(first, last, 3);
                        SetStatus(first + pValue.mValue, pFL.mValue.mEnd, 2);
                        SetStatus(pFL.mValue.mStart, last - pValue.mValue, 2);
                    }
                    if ((first_length == pValue.mValue) && (first - pFL.mValue.mStart < pValue.mValue + 1))
                    {
                        SetStatus(pFL.mValue.mStart, first, 2);
                        SetStatus(first + pValue.mValue, first + pValue.mValue, 2);
                    }

                }
            }
            else
            {
                SetStatus(pFL.mValue.mEnd - ValueCount + 1, pFL.mValue.mStart + ValueCount - 1, 3);
            }
        }
        else
        {
            SetStatus(pFL.mValue.mStart , pFL.mValue.mEnd , 2);
        }
        //Item3 : 가정법
        int pre_count;
        int next_count;
        next_count = ValueSum;
        pre_count = 0;
        pValue = StartIndex;
        next_count -= pValue.mValue;
        for (int i = 0; i < NumOfValue; i++)
        {
            if ((nCount - next_count - NumOfValue - i - pValue.mValue+1 ) <= pre_count + pValue.mValue -1 )
                SetStatus(pFL.mValue.mStart+nCount - next_count - NumOfValue - i - pValue.mValue+1 , pFL.mValue.mStart+pre_count + pValue.mValue-1 , 3);	//가로
            pre_count += pValue.mValue + 1;
            pValue = pValue.pNext;
            if (pValue!=null)
                next_count -= (pValue.mValue + 2);
        }
        // 마지막엔 무식한 방법으로
        //if(NumOfValue<8)
        CaseGenerator(pFL, StartIndex, EndIndex, mTable);

        return 0;
    }


    public int SetStatus(int start, int end, int state)
    {
        Vector<Integer> buffer = mTable;
        if (start > end)
            return -1;
        if(start<0 || end<0)
            return -1;
        if(end + 1 >buffer.size())
            end = buffer.size()-1;

        for (int i = start; i <=end; i++)
        {
            if (buffer.get(i) == 0)
                buffer.set(i, state);
         }
        return 0;
    }
}
