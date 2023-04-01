package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.entity.mainEntity.Movie;
import com.linrushao.businessserver.entity.movieEntity.MovieMidRecommendation;
import com.linrushao.businessserver.entity.movieEntity.Recommendation;
import com.linrushao.businessserver.service.MovieService;
import com.linrushao.businessserver.service.SimilarMovieRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author LRS
 * @Date 2022/10/6 9:08
 * Desc
 */
@Controller
public class SingleController {

    @Autowired
    private MovieService movieService;
    @Autowired
    private SimilarMovieRecommendationService similarMovieRecommendationService;

    /**
     * 获取单个电影的信息
     * @param mid
     * @param model
     * @return
     */
    @RequestMapping("/single/{mid}")
    public String single(@PathVariable("mid") int mid , Model model){

        //获取详细信息
        if (!movieService.movieExist(mid)) {
            return null;
        }
        Movie byMID = movieService.findByMID(mid);
        model.addAttribute("singlemovies",byMID);

        //获取详细页面的相似电影
        List<Recommendation> recommendations = similarMovieRecommendationService.getSimilarMovieRecommendations(new MovieMidRecommendation(mid));
        List<Movie> recommendeMovies = movieService.getRecommendeMovies(recommendations);
        model.addAttribute("samemovie",recommendeMovies);
        return "single";
    }

}
