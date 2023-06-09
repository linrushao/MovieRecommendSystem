package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.entity.Movie;
import com.linrushao.businessserver.entity.User;
import com.linrushao.businessserver.entity.form.Recommendation;
import com.linrushao.businessserver.entity.form.MovieGenresForm;
import com.linrushao.businessserver.entity.form.UserRecommendationForm;
import com.linrushao.businessserver.service.*;
import com.linrushao.businessserver.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Random;

/**
 * @Author LRS
 * @Date 2022/10/5 9:23
 * Desc 获取推荐的电影
 */
// TODO:  bug 混合推荐结果中，基于内容的推荐，基于MID，而非UID

@Controller
public class RealTimeRecommendController {

    @Autowired
    private UserService userService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private MovieRecommendationService movieRecommendationService;


    /**
     * 实时推荐 + 内容推荐
     * @param session
     * @param model
     * @return
     */
    @RequestMapping( "/realTime")
    public String realTimeRecommend(HttpSession session, Model model) {
        Object username = session.getAttribute("username");
        User user = userService.findByUsername(String.valueOf(username));
        List<Recommendation> recommendations = movieRecommendationService.getHybridRecommendations(user.getUid());
        if (recommendations.size() == 0) {
            String randomGenres = user.getPrefGenres().get(new Random().nextInt(user.getPrefGenres().size()));
            recommendations = movieRecommendationService.getTopGenresRecommendations(new MovieGenresForm(randomGenres.split(" ")[0], Constant.REDIS_MOVIE_RATING_QUEUE_SIZE));
        }
        List<Movie> hybirdRecommendeMovies = movieService.getHybirdRecommendeMovies(recommendations);

        model.addAttribute("realTimeRecommendeMovies",hybirdRecommendeMovies);
        return "realTimeRecommend";
    }
}


