package com.example.lvmufan.myapplication;

import java.util.ArrayList;

public class HighlightStructure {
    private ArrayList leftStart;
    private ArrayList leftEnd;
    private ArrayList rightStart;
    private ArrayList rightEnd;
    private ArrayList leftEntity;
    private ArrayList rightEntity;
    private ArrayList relationId;
    private int change;

    public HighlightStructure(){
        leftStart = new ArrayList();
        leftEnd = new ArrayList();
        rightStart = new ArrayList();
        rightEnd = new ArrayList();
        leftEntity = new ArrayList();
        rightEntity = new ArrayList();
        relationId = new ArrayList();
        change = 0;   //改变实体间的关系
    }

    public ArrayList getLeftStart() {
        return leftStart;
    }
    public ArrayList getLeftEnd() {
        return leftEnd;
    }
    public ArrayList getRightStart() {
        return rightStart;
    }
    public ArrayList getRightEnd() {
        return rightEnd;
    }
    public ArrayList getLeftEntity() {
        return leftEntity;
    }
    public ArrayList getRightEntity() {
        return rightEntity;
    }
    public ArrayList getRelationId() {
        return relationId;
    }
    public int getChange() {
        return change;
    }
    public void setChange(int change) {
        this.change = change;
    }
}
