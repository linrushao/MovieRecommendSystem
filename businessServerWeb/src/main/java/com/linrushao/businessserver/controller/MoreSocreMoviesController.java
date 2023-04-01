package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.entity.movieEntity.Recommendation;
import com.linrushao.businessserver.service.MovieService;
import com.linrushao.businessserver.service.RateMoreRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author LRS
 * @Date 2022/10/5 15:37
 * Desc 离线推荐
 */
@Controller
public class MoreSocreMoviesController {
    /**
     * 评分最多
     */
    @Autowired
    private RateMoreRecommendationService rateMoreRecommendationService;
    @Autowired
    private MovieService movieService;
    @RequestMapping( "/morescoremovie")
    public String getRateMoreMovies( Model model) {
        List<Recommendation> socreRecommendations = rateMoreRecommendationService.getRateMoreRecommendations();
        model.addAttribute("MoreSocreMovies", movieService.getRecommendeMovies(socreRecommendations));
        return "MoreSocreMovies";
    }

}
