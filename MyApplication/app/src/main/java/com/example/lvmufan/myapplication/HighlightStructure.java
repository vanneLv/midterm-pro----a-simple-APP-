package com.example.lvmufan.myapplication;

import java.util.ArrayList;

public class HighlightStructure {
    private ArrayList leftStart;
    private ArrayList leftEnd;
    private ArrayList rightStart;
    private ArrayList rightEnd;

    public HighlightStructure(){
        leftStart = new ArrayList();
        leftEnd = new ArrayList();
        rightStart = new ArrayList();
        rightEnd = new ArrayList();
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
}
