package com.example.nemonemo;

public class FragNode {
    int mStart;
    int mEnd;
    int mMustHaveMin;
    int mIndex;

    public void FragNode()
    {
        mStart = -1;
        mEnd = -1;
        mMustHaveMin=0;
    }

    public int Set(int start, int end, int mustHaveMin)
    {
        mStart = start;
        mEnd = end;
        mMustHaveMin = mustHaveMin;
        return 0;
    }
    
}
