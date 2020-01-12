package com.example.nemonemo;

public class Fragmentation extends  LinkList<FragNode>{

    public String name;
    public Fragmentation(){
        name = "Flagment";
    }
    public void finalize(){
        DeleteAllNode();
    }
}
