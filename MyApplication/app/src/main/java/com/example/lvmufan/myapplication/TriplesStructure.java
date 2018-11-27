package com.example.lvmufan.myapplication;

import java.util.ArrayList;

import javax.net.ssl.SSLEngineResult;

public class TriplesStructure {
    private ArrayList sub_id;
    private ArrayList leftStart;
    private ArrayList leftEnd;
    private ArrayList rightStart;
    private ArrayList rightEnd;
    private ArrayList relationStart;
    private ArrayList relationEnd;
    private ArrayList leftEntity;
    private ArrayList rightEntity;
    private ArrayList relationId;
    private String doc_id;
    private String sent_id;
    private String title;
    private String sent_ctx;
    private String triplesGroup;

    private ArrayList change;
    private ArrayList status;

    public TriplesStructure(){
        sub_id = new ArrayList();
        leftStart = new ArrayList();
        leftEnd = new ArrayList();
        rightStart = new ArrayList();
        rightEnd = new ArrayList();
        relationStart = new ArrayList();
        relationEnd = new ArrayList();
        leftEntity = new ArrayList();
        rightEntity = new ArrayList();
        relationId = new ArrayList();
        change = new ArrayList();   //改变实体间的关系
        status = new ArrayList();
    }

    public ArrayList getRelationStart() {
        return relationStart;
    }
    public ArrayList getRelationEnd() {
        return relationEnd;
    }
    public ArrayList getSub_id() {
        return sub_id;
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
    public ArrayList getChange() {
        return change;
    }
    public ArrayList getStatus() {
        return status;
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
    public void setSent_ctx(String sent_ctx) {
        this.sent_ctx = sent_ctx;
    }
    public String getSent_ctx() {
        return sent_ctx;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public void setTriplesGroup(String triplesGroup) {
        this.triplesGroup = triplesGroup;
    }
    public String getTriplesGroup() {
        return triplesGroup;
    }
    public void clearAllArrayList(){
        getLeftStart().clear();
        getLeftEnd().clear();
        getRightStart().clear();
        getRightEnd().clear();
        getLeftEntity().clear();
        getRightEntity().clear();
        getRelationId().clear();
        getRelationStart().clear();
        getRelationEnd().clear();
        getSub_id().clear();
        getChange().clear();
        getStatus().clear();
    }
    public int getSize(){
        return getSub_id().size();
    }
}
