package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.entity.form.Recommendation;
import com.linrushao.businessserver.entity.form.MovieSearchForm;
import com.linrushao.businessserver.service.MovieRecommendationService;
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
public class MovieGenresController {

    @Autowired
    private MovieRecommendationService movieRecommendationService;
    @Autowired
    private MovieService movieService;

    /**
     * 查询类别电影
     * "动作"：Action,"动画"：Animation,"冒险"：Adventure,"喜剧"：Comedy,"犯罪"：Crime,
     * "奇幻"：Fantasy,"家庭"：Family,"传记"：Biography, "历史"：History,"音乐"：Music,
     * "恐怖"：Horror,"歌舞"：SingingAndDancing,"悬疑"：Mystery,"爱情"：Romance,
     * "古装"：Costume,"科幻"：ScienceFiction,"运动"：Sports,"惊悚"：Thriller,
     * "战争"：War,"武侠"：Swordsmen,"剧情"：Plot
     */

    //动作片
    @RequestMapping("/Action/{Action}")
    public String Action(@PathVariable("Action") String Action, Model model) {
        Action = "动作";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Action));
        model.addAttribute("success", true);
        model.addAttribute("Action", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Action";
    }

    //动画片
    @RequestMapping("/Animation/{Animation}")
    public String Animation(@PathVariable("Animation") String Animation, Model model) {
        Animation="动画";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Animation));
        model.addAttribute("success", true);
        model.addAttribute("Animation", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Animation";
    }

    //冒险经历
    @RequestMapping("/Adventure/{Adventure}")
    public String Adventure(@PathVariable("Adventure") String Adventure, Model model) {
        Adventure= "冒险";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Adventure));
        model.addAttribute("success", true);
        model.addAttribute("Adventure", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Adventure";
    }



    //喜剧片
    @RequestMapping("/Comedy/{Comedy}")
    public String Comedy(@PathVariable("Comedy") String Comedy, Model model) {
        Comedy="喜剧";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Comedy));
        model.addAttribute("success", true);
        model.addAttribute("Comedy", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Comedy";
    }


    //犯罪片
    @RequestMapping("/Crime/{Crime}")
    public String Crime(@PathVariable("Crime") String Crime, Model model) {
        Crime="犯罪";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Crime));
        model.addAttribute("success", true);
        model.addAttribute("Crime", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Crime";
    }

    //奇幻
    @RequestMapping("/Fantasy/{Fantasy}")
    public String Fantasy(@PathVariable("Fantasy") String Fantasy, Model model) {
        Fantasy="奇幻";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Fantasy));
        model.addAttribute("success", true);
        model.addAttribute("Fantasy", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Fantasy";
    }

    //家庭
    @RequestMapping("/Family/{Family}")
    public String Family(@PathVariable("Family") String Family, Model model) {
        Family="家庭";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Family));
        model.addAttribute("success", true);
        model.addAttribute("Family", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Family";
    }

    //传记
    @RequestMapping("/Biography/{Biography}")
    public String Biography(@PathVariable("Biography") String Biography, Model model) {
        Biography="传记";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Biography));
        model.addAttribute("success", true);
        model.addAttribute("Biography", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Biography";
    }

    //历史片
    @RequestMapping("/History/{History}")
    public String History(@PathVariable("History") String History, Model model) {
        History="历史";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(History));
        model.addAttribute("success", true);
        model.addAttribute("History", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/History";
    }
    //音乐
    @RequestMapping("/Music/{Music}")
    public String Music(@PathVariable("Music") String Music, Model model) {
        Music="音乐";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Music));
        model.addAttribute("success", true);
        model.addAttribute("Music", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Music";
    }
    //恐怖片
    @RequestMapping("/Horror/{Horror}")
    public String Horror(@PathVariable("Horror") String Horror, Model model) {
        Horror="恐怖";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Horror));
        model.addAttribute("success", true);
        model.addAttribute("Horror", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Horror";
    }

    //歌舞
    @RequestMapping("/SingingAndDancing/{SingingAndDancing}")
    public String SingingAndDancing(@PathVariable("SingingAndDancing") String SingingAndDancing, Model model) {
        SingingAndDancing="歌舞";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(SingingAndDancing));
        model.addAttribute("success", true);
        model.addAttribute("SingingAndDancing", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/SingingAndDancing";
    }
    //悬疑片
    @RequestMapping("/Mystery/{Mystery}")
    public String Mystery(@PathVariable("Mystery") String Mystery, Model model) {
        Mystery="悬疑";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Mystery));
        model.addAttribute("success", true);
        model.addAttribute("Mystery", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Mystery";
    }
    //爱情片
    @RequestMapping("/Romance/{Romance}")
    public String Romance(@PathVariable("Romance") String Romance, Model model) {
        Romance="爱情";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Romance));
        model.addAttribute("success", true);
        model.addAttribute("Romance", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Romance";
    }
    //古装
    @RequestMapping("/Costume/{Costume}")
    public String Costume(@PathVariable("Costume") String Costume, Model model) {
        Costume="古装";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Costume));
        model.addAttribute("success", true);
        model.addAttribute("Costume", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Costume";
    }
    //科幻片
    @RequestMapping("/ScienceFiction/{ScienceFiction}")
    public String ScienceFiction(@PathVariable("ScienceFiction") String ScienceFiction, Model model) {
        ScienceFiction="科幻";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(ScienceFiction));
        model.addAttribute("success", true);
        model.addAttribute("ScienceFiction", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/ScienceFiction";
    }
    //运动
    @RequestMapping("/Sports/{Sports}")
    public String Sports(@PathVariable("Sports") String Sports, Model model) {
        Sports="运动";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Sports));
        model.addAttribute("success", true);
        model.addAttribute("Sports", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Sports";
    }
    //惊悚片
    @RequestMapping("/Thriller/{Thriller}")
    public String Thriller(@PathVariable("Thriller") String Thriller, Model model) {
        Thriller="惊悚";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Thriller));
        model.addAttribute("success", true);
        model.addAttribute("Thriller", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Thriller";
    }
    //战争片
    @RequestMapping("/War/{War}")
    public String War(@PathVariable("War") String War, Model model) {
        War="战争";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(War));
        model.addAttribute("success", true);
        model.addAttribute("War", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/War";
    }
    //武侠
    @RequestMapping("/Swordsmen/{Swordsmen}")
    public String Documentary(@PathVariable("Swordsmen") String Swordsmen, Model model) {
        Swordsmen="武侠";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Swordsmen));
        model.addAttribute("success", true);
        model.addAttribute("Swordsmen", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Swordsmen";
    }
    //剧情
    @RequestMapping("/Plot/{Plot}")
    public String Plot(@PathVariable("Plot") String Plot, Model model) {
        Plot="剧情";
        List<Recommendation> recommendations = movieRecommendationService.getContentBasedGenresRecommendations(new MovieSearchForm(Plot));
        model.addAttribute("success", true);
        model.addAttribute("Plot", movieService.getRecommendeMovies(recommendations));
        return "movieGenres/Plot";
    }

}
