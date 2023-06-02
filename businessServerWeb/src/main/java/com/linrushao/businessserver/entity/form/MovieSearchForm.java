package com.linrushao.businessserver.entity.form;

public class MovieSearchForm {
    private String text;

    public MovieSearchForm(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
