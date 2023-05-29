package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author LRS
 * @Date 2022/10/8 16:39
 * Desc
 */
@Controller
public class NewMovieController {

    @Autowired
    private MovieService movieService;
    /**
     * @param model
     * @return
     */
    @RequestMapping( "/newmovie")
    public String getNewMovies( Model model) {
        model.addAttribute("success", true);
        model.addAttribute("newmovies", movieService.getNewMovies());
        return "NewMovie";
    }
}
