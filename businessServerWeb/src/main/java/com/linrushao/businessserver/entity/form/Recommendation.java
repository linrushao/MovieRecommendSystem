package com.linrushao.businessserver.entity.form;

/**
 * 推荐项目的包装
 */
public class Recommendation {

    // 电影ID
    private int mid;

    @Override
    public String toString() {
        return "Recommendation{" +
                "mid=" + mid +
                ", score=" + score +
                '}';
    }

    //电影评分
    private Double score;

    // 电影的推荐得分
    public Recommendation() {
    }

    public Recommendation(int mid, Double score) {
        this.mid = mid;
        this.score = score;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
