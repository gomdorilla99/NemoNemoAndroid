package com.example.nemonemo;

import java.util.Vector;

public class
CaseChecker  extends LinkList<CaseNode> {
    private final int MAX_TABLE = 100;
    private Node<FragNode> mpFl = new Node<FragNode>();
    private Node<Integer> mpValueHeader = new Node<Integer>();


      // 가능 한 경우 따져 보기(기존 Check 표시를 기반으로 Value의 갯수를 분배하기)
    public int IntDivideFuc(Vector<Integer> Value,  int nBuket, int mBall, int nThBuket)
    {
        if (nThBuket >= nBuket-1)
        {
            Value.set(nThBuket ,  mBall);
            // 중복없이 저장하면 된다.
            // 여기서 조건 Check가 들어가면됨,
            Vector<Integer> BuketBuffer = new Vector<>(); //new char[180];
            int FLSpace;
            int ValueSpace;
            Node<FragNode> pFlCur = mpFl;
            Node<Integer> pValue = mpValueHeader;
            boolean pass = false;
            BuketBuffer.add((int)';');
            fail_case:
            for (int k = 0; k < nBuket ; k++)
            {
                FLSpace = pFlCur.mValue.mEnd - pFlCur.mValue.mStart + 1;
                ValueSpace = 0;
                for (int v = 0; v < Value.get(k); v++)
                {
                    //Value space < FL space
                    ValueSpace += (ValueSpace > 0) ? pValue.mValue + 1 : pValue.mValue;
                    BuketBuffer.add((int)',');
                    pValue = pValue.pNext;
                } // for v
                if (ValueSpace <= FLSpace)
                {
                    //mustHave <= ValueSum; 인경우에 Case 등록
                    if (ValueSpace >= pFlCur.mValue.mMustHaveMin)   pass = true;
                    else
                    {
                        pass = false;
					    break fail_case;
                    }
                }
                else
                {
                    pass = false;
                    break fail_case;
                }

                if (Value.get(k)==0) // Value가 없을때
                {
                    if (pFlCur.mValue.mMustHaveMin > 0)
                    {
                        pass = false;
                        break fail_case;
                    }
                    else
                    {
                        pass = true;
                    }
                }
                BuketBuffer.add((int)';');
                pFlCur = pFlCur.pNext;
            }

            if (pass)
                AddToTail(BuketBuffer);
        }
        else
        {
            for (int i = 0; i <= mBall; i++)
            {
                if (nThBuket < nBuket)
                {
                    Value.set(nThBuket, i);
                    IntDivideFuc(Value,nBuket, mBall - i, nThBuket + 1);
                }
            }
        }
        return 0;
    }

    public int CaseGenerator(Node<FragNode> pFl, Node<Integer> pValueHeader, int depth, Vector<Integer> strChar)
    {
        Vector<Integer>  StrBuffer;
        StrBuffer = strChar;
        StrBuffer.add((int)';');	//시작을 표시, 구역에 대한 표시

        Node<FragNode> CurFl = pFl;
        Node<Integer> CurValue = pValueHeader;
        
        int nFl;
        int nValue;
        for (nFl=0; CurFl!=null ; CurFl = CurFl.pNext){nFl++;}	//Fl 개수 구하기
        for (nValue=0; CurValue!=null; CurValue = CurValue.pNext){nValue++;} // Value 개수구하기

        mpFl = pFl;
        mpValueHeader = pValueHeader;
        if (nValue>0)
        {
            Vector<Integer> pValue = new Vector<Integer>(30);
            for(int i=0;i<30;i++)
                pValue.add(0);
            if (pFl!=null)
                IntDivideFuc(pValue, nFl, nValue, 0);
        }
        return 0;
    }


    public void Prepare(Vector<Integer> Table)
    {
        Node<CaseNode> pCC = mpRootNode;
        while (pCC!=null)
        {
            for (int i = 0; i < Table.size(); i++)
                pCC.mValue.mTable.add(Table.get(i));	// 기본 Table설정
            pCC = pCC.pNext;
        }
    }

    public void Rendering(Fragmentation FL, Node<Integer> pValueHeader)
    {
        Node<CaseNode> pCaseChecker = mpRootNode;
        Node<FragNode> pFL=null;
        Node<Integer> pValue;
        Node<Integer> pStartValue;

        Node<Integer> pEndValue;
        int i;
        while (pCaseChecker!=null)
        {
            int count = 0;
            pFL = null;
            pValue = null;
            pStartValue = null;
            pEndValue = null;
            theNext:
            for (i = 0; i <pCaseChecker.mValue.mStrBuffer.size(); i++)
            {
                switch (pCaseChecker.mValue.mStrBuffer.get(i))
                {
                    case (int)'\0':
					break theNext;
                    case (int)';':
                    {
                        if (pFL == null) // 첫번째에 열고
                        {
                            pFL = FL.GetHeaderNode();
                            pStartValue = null;
                            pValue = null;
                        }
                        else // 2번째 이후 부터 FL에 대한 처리, ;로 끝나지 않으면 무시됨
                        {
                            //printf("<FL : [%d,%d]Must %d>\r\n", pFL.mValue.mStart, pFL.mValue.mEnd, pFL.mValue.mMustHaveMin);
                            //printf("value count = %d, (%d)\r\n",count,i);
                            if (count==0)
                            {
                                pCaseChecker.mValue.SetStatus(pFL.mValue.mStart, pFL.mValue.mEnd, 2);
                            }
                            else
                            {
                                if (pStartValue == null)
            						break theNext;
                                pCaseChecker.mValue.Fill(pFL, pStartValue, pEndValue, 3);
                            }

                            if (pValue!=null)
                                pStartValue = pValue.pNext;
                            else
                                pStartValue = null;

                            pFL = pFL.pNext;
                        }
                        count = 0;
                    }
                    break;
                    case (int)',':
                    {
                        if (pStartValue == null)
                        {
                            pStartValue = pValueHeader;
                            pValue = pStartValue;
                            pEndValue = pStartValue;
                        }
                        else
                        {
                            pEndValue = pValue = pValue.pNext;

                        }
                        count++;
                    }
                    break;
                    default:
                   }
            }
            pCaseChecker = pCaseChecker.pNext;
        }
        pCaseChecker = mpRootNode;
        while (pCaseChecker!=null)
        {
            pCaseChecker = pCaseChecker.pNext;
        }
    }

    public  Node<CaseNode> AddToTail(Vector<Integer> str)
    {
        Node<CaseNode> pNewNode = new Node<>();
        pNewNode.mValue = new CaseNode();
        pNewNode.mValue.mStrBuffer = str;

        super.AddToTail(pNewNode);
        return pNewNode;
    }
}
