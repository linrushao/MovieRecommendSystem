package com.linrushao.businessserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Movies")
public class Movie {

    @JsonIgnore
    private String _id;
    private int mid;
    private String name;
    private String actors;
    private String image;
    private String directors;
    private Double douban_score;
    private int douban_votes;
    private String genres;
    private String language;
    private String timelong;
    private String regions;
    private String issue;
    private String descri;
    private String tags;
    private String shoot;
    private String actor_ids;
    private String director_ids;
    private Double score;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDirectors() {
        return directors;
    }

    public void setDirectors(String directors) {
        this.directors = directors;
    }

    public Double getDouban_score() {
        return douban_score;
    }

    public void setDouban_score(Double douban_score) {
        this.douban_score = douban_score;
    }

    public int getDouban_votes() {
        return douban_votes;
    }

    public void setDouban_votes(int douban_votes) {
        this.douban_votes = douban_votes;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimelong() {
        return timelong;
    }

    public void setTimelong(String timelong) {
        this.timelong = timelong;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getShoot() {
        return shoot;
    }

    public void setShoot(String shoot) {
        this.shoot = shoot;
    }

    public String getActor_ids() {
        return actor_ids;
    }

    public void setActor_ids(String actor_ids) {
        this.actor_ids = actor_ids;
    }

    public String getDirector_ids() {
        return director_ids;
    }

    public void setDirector_ids(String director_ids) {
        this.director_ids = director_ids;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}