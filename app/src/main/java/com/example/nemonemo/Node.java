package com.example.nemonemo;

public class Node<T>
{
    T mValue; //the object information
    Node<T> pNext; //pointer to the next node element
    public Node() {
        pNext = null;
    }
}