package com.example.nemonemo;

import java.util.Vector;

public class GameData {
    private char[] Name = new char[8];

    public GameData pNext;
    public GameData pPrev;
    
    public GuidePannel pHGuide;
    public GuidePannel pVGuide;
    
    public String mStrDesciption;
    public int mWidth;
    public int mHeight;
    public int mCodeWidth;
    public int mCodeHeight;

    public GuidePannel GetHorGuide(int index){
        GuidePannel pPannel;
        pPannel = pHGuide;
        if (pHGuide != null)
        {
            for (int i = 0; i < index; i++)
            {
                pPannel = pPannel.pNext;
                if (pPannel==null)
                    return null;
            }
            return pPannel;
        }
        else
        {
            return null;
        }
    }
    public GuidePannel GetVerGuide(int index)
    {
        GuidePannel pPannel;
        pPannel = pVGuide;
        if (pVGuide != null)
        {
            for (int i = 0; i < index; i++)
            {
                pPannel = pPannel.pNext;
                if (pPannel==null)
                    return null;
            }
        }
        else
        {
            return null;
        }
        return pPannel;
    }

    public int AddVerGuide(String strGuide){
        String Arry[] = strGuide.split(",",mWidth + mHeight);
        GuidePannel pPannel = new GuidePannel();
        if (pVGuide!=null)
        {
            pVGuide.GetTail().pNext = pPannel;
        }
        else
        {
            pVGuide = pPannel;
        }

        for(int i=0;i<Arry.length;i++) {

            int num = Integer.parseInt(Arry[i]);
            pPannel.AddValue(num);
        }
        pPannel.mNumOfValue = Arry.length;
        return Arry.length;
    }

    public int AddHorGuide(String strGuide){

        String Arry[] = strGuide.split(",",mWidth + mHeight);
        GuidePannel pPannel = new GuidePannel();
        if (pHGuide!=null)
        {
            pHGuide.GetTail().pNext = pPannel;
        }
        else
        {
            pHGuide = pPannel;
        }

        for(int i=0;i<Arry.length;i++) {

            int num = Integer.parseInt(Arry[i]);
            pPannel.AddValue(num);
        }
        pPannel.mNumOfValue = Arry.length;
        return Arry.length;

    }

    public int AddVerGuide(int nCount, Vector<Character> Values)
    {
        /*
        char pt;
        int num;
        int Count = 0;

        GuidePannel pPannel = new GuidePannel;
        if (pVGuide != null)
        {
            pVGuide.GetTail().pNext = pPannel;
        }
        else
        {
            pVGuide = pPannel;
        }


        strtok_s(strGuide, ",", pt);
        num = Integer.parseInt(strGuide);
        pPannel.AddValue(num);
        Count++;
        while (pt != null)
        {
            num = Integer.parseInt(pt);
            pPannel.AddValue(num);
            strtok_s(pt, ",", pt);
            Count++;
        }
        pPannel.mNumOfValue = Count;
        return Count;
        */
        return  0;
    }

    public int AddHorGuide(int nCount, Vector<Character> Values)
    {
        /*
        char pt;
        int num;
        int Count = 0;

        GuidePannel pPannel = new GuidePannel;
        if (pHGuide != null)
        {
            pHGuide.GetTail().pNext = pPannel;
        }
        else
        {
            pHGuide = pPannel;
        }


        strtok_s(strGuide, ",", pt);
        num = Integer.parseInt(strGuide);
        pPannel.AddValue(num);
        Count++;
        while (pt! = null)
        {
            num = Integer.parseInt(pt, null, 10);
            pPannel.AddValue(num);
            strtok_s(pt, ",", pt);
            Count++;
        }
        pPannel.mNumOfValue = Count;
        return Count;
        */
        return  0;
    }
    
    public GameData GetTail(){
        if (pNext!=null)
        {
            return(pNext.GetTail());
        }
        else
        {
            return this;
        }
    }
};

