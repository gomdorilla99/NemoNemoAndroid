package com.example.nemonemo;

public class GuideValue {

    public GuideValue pNext;
    public GuideValue pPrev;
    public int mValue;
    public int mAchieve;

    public GuideValue(){
        pNext = null;
    };

    public GuideValue  GetTail(){
        if (this == null)
            return null;
        if(pNext!=null){return(pNext.GetTail()); } else{ return this; }
    };
};
