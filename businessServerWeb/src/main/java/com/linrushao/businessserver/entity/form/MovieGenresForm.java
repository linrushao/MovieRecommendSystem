package com.linrushao.businessserver.entity.form;

public class MovieGenresForm {

    private String genres;

    public MovieGenresForm(String genres, int sum) {
        this.genres = genres;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}
