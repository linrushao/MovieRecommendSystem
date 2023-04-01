package com.linrushao.businessserver.entity.movieEntity;

public class SearchRecommendation {
    private String text;

    public SearchRecommendation(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
