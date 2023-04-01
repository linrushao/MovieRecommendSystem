package com.linrushao.businessserver.entity.movieEntity;

public class TopGenresRecommendation {

    private String genres;

    public TopGenresRecommendation(String genres, int sum) {
        this.genres = genres;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}
