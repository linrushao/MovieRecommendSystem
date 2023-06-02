package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.entity.form.Recommendation;
import com.linrushao.businessserver.entity.form.MovieSearchForm;
import com.linrushao.businessserver.service.ElasticsearchSearchService;
import com.linrushao.businessserver.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author LRS
 * @Date 2022/10/8 15:48
 * Desc
 */
@RequestMapping("/movie")
@Controller
public class SearchMovieController {

    @Autowired
    private MovieService movieService;
    @Autowired
    private ElasticsearchSearchService elasticsearchSearchService;

    /**
     * 模糊查询电影
     * @param query
     * @param model
     * @return
     */
    @RequestMapping( "/search")
    public String getSearchMovies(@RequestParam("query") String query, Model model) {
        List<Recommendation> recommendations = elasticsearchSearchService.getContentBasedSearchRecommendations(new MovieSearchForm(query));
        model.addAttribute("success", true);
        model.addAttribute("key",query);
        model.addAttribute("SearchMovie", movieService.getRecommendeMovies(recommendations));
        return "searchMovie";
    }

}
