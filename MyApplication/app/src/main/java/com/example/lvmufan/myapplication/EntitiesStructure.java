package com.example.lvmufan.myapplication;

import java.util.ArrayList;

public class EntitiesStructure {
    private ArrayList EntityName;
    private ArrayList Start;
    private ArrayList End;
    private ArrayList NerTag;

    private String doc_id;
    private String sent_id;
    private String entitiesGroup;

    public EntitiesStructure(){
        EntityName = new ArrayList();
        Start = new ArrayList();
        End = new ArrayList();
        NerTag = new ArrayList();

    }


    public ArrayList getEntityName() {
        return EntityName;
    }
    public ArrayList getStart() {
        return Start;
    }
    public ArrayList getEnd() {
        return End;
    }
    public ArrayList getNerTag() {
        return NerTag;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }
    public String getDoc_id() {
        return doc_id;
    }
    public void setSent_id(String sent_id) {
        this.sent_id = sent_id;
    }
    public String getSent_id() {
        return sent_id;
    }

    public String getEntitiesGroup() {
        return entitiesGroup;
    }

    public int getSize(){
       return getEntityName().size();
    }
}
