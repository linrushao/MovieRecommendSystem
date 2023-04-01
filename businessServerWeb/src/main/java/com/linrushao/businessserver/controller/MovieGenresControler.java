package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.entity.movieEntity.Recommendation;
import com.linrushao.businessserver.entity.movieEntity.SearchRecommendation;
import com.linrushao.businessserver.service.MovieGenresRecommendationService;
import com.linrushao.businessserver.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author LRS
 * @Date 2022/10/5 16:21
 * Desc 电影类别
 */
@RequestMapping("/genres")
@Controller
public class MovieGenresControler {

    @Autowired
    private MovieGenresRecommendationService movieGenresRecommendationService;
    @Autowired
    private MovieService movieService;

    /**
     * 查询类别电影
     *
     * @param Action
     * @param model
     * @return
     */

    //动作片
    @RequestMapping("/Action/{Action}")
    public String Action(@PathVariable("Action") String Action, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Action));
        model.addAttribute("success", true);
        model.addAttribute("Action", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Action";
    }


    //冒险经历
    @RequestMapping("/Adventure/{Adventure}")
    public String Adventure(@PathVariable("Adventure") String Adventure, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Adventure));
        model.addAttribute("success", true);
        model.addAttribute("Adventure", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Adventure";
    }


    //动画片
    @RequestMapping("/Animation/{Animation}")
    public String Animation(@PathVariable("Animation") String Animation, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Animation));
        model.addAttribute("success", true);
        model.addAttribute("Animation", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Animation";
    }

    //喜剧片
    @RequestMapping("/Comedy/{Comedy}")
    public String Comedy(@PathVariable("Comedy") String Comedy, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Comedy));
        model.addAttribute("success", true);
        model.addAttribute("Comedy", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Comedy";
    }


    //犯罪片
    @RequestMapping("/Crime/{Crime}")
    public String Crime(@PathVariable("Crime") String Crime, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Crime));
        model.addAttribute("success", true);
        model.addAttribute("Crime", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Crime";
    }

    //纪录片
    @RequestMapping("/Documentary/{Documentary}")
    public String Documentary(@PathVariable("Documentary") String Documentary, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Documentary));
        model.addAttribute("success", true);
        model.addAttribute("Documentary", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Documentary";
    }

    //喜剧文学片
    @RequestMapping("/Drama/{Drama}")
    public String Drama(@PathVariable("Drama") String Drama, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Drama));
        model.addAttribute("success", true);
        model.addAttribute("Drama", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Drama";
    }

    //魔幻片
    @RequestMapping("/Fantasy/{Fantasy}")
    public String Fantasy(@PathVariable("Fantasy") String Fantasy, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Fantasy));
        model.addAttribute("success", true);
        model.addAttribute("Fantasy", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Fantasy";
    }

    //恐怖片
    @RequestMapping("/Horror/{Horror}")
    public String Horror(@PathVariable("Horror") String Horror, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Horror));
        model.addAttribute("success", true);
        model.addAttribute("Horror", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Horror";
    }

    //悬疑片
    @RequestMapping("/Mystery/{Mystery}")
    public String Mystery(@PathVariable("Mystery") String Mystery, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Mystery));
        model.addAttribute("success", true);
        model.addAttribute("Mystery", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Mystery";
    }


    //爱情片
    @RequestMapping("/Romance/{Romance}")
    public String Romance(@PathVariable("Romance") String Romance, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Romance));
        model.addAttribute("success", true);
        model.addAttribute("Romance", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Romance";
    }

    //科幻片
    @RequestMapping("/ScienceFiction/{ScienceFiction}")
    public String ScienceFiction(@PathVariable("ScienceFiction") String ScienceFiction, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(ScienceFiction));
        model.addAttribute("success", true);
        model.addAttribute("ScienceFiction", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/ScienceFiction";
    }

    //惊悚片
    @RequestMapping("/Thriller/{Thriller}")
    public String Thriller(@PathVariable("Thriller") String Thriller, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Thriller));
        model.addAttribute("success", true);
        model.addAttribute("Thriller", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Thriller";
    }

    //战争片
    @RequestMapping("/War/{War}")
    public String War(@PathVariable("War") String War, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(War));
        model.addAttribute("success", true);
        model.addAttribute("War", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/War";
    }

    //西部片
    @RequestMapping("/Western/{Western}")
    public String Western(@PathVariable("Western") String Western, Model model) {
        List<Recommendation> recommendations = movieGenresRecommendationService.getContentBasedGenresRecommendations(new SearchRecommendation(Western));
        model.addAttribute("success", true);
        model.addAttribute("Western", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Western";
    }

}
