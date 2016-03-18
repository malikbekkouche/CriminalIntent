package com.example.malik.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by malik on 2/17/16.
 */
public class Crime {

    private String mTitle;
    private UUID mID;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;

    public String getSuspect() {
        return mSuspect;
    }
    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public UUID getID() {
        return mID;
    }

    public Crime(){
        this(UUID.randomUUID());

    }
    public Crime(UUID id) {
        mID = id;
        mDate = new Date();
    }

    @Override
    public String toString() {
        return  mTitle ;
    }
}
