package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.entity.movieEntity.*;
import com.linrushao.businessserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author LRS
 * @Date 2022/9/24 14:14
 * Desc 首页电影推荐
 */

@Controller
public class HomeMoviesController {

    @Autowired
    private HomeHotRecommendationsService homeHotRecommendationsService;
    @Autowired
    private MovieService movieService;

    @RequestMapping("/")
    public  String  index(Model model) {
        /**
         * 获取热门推荐
         */
        List<Recommendation> hotRecommendations = homeHotRecommendationsService.getHotRecommendations();
        model.addAttribute("hotMovies", movieService.getRecommendeMovies(hotRecommendations));
        return "index";
    }



}
