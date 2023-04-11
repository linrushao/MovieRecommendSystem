package com.linrushao.businessserver.entity.mainEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Rating {

    @JsonIgnore
    private String _id;
    private int rating_id;
    private String user_md5;
    private int uid;

    private int mid;

    private double score;

    private long  timestamp;

    public Rating() {
    }

    public Rating(int uid, int mid, double score) {
        this.uid = uid;
        this.mid = mid;
        this.score = score;
        this.timestamp = new Date().getTime();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getRating_id() {
        return rating_id;
    }

    public void setRating_id(int rating_id) {
        this.rating_id = rating_id;
    }

    public String getUser_md5() {
        return user_md5;
    }

    public void setUser_md5(String user_md5) {
        this.user_md5 = user_md5;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}