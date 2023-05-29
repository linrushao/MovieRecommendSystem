package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.entity.mainEntity.User;
import com.linrushao.businessserver.entity.movieEntity.MovieRating;
import com.linrushao.businessserver.service.MovieService;
import com.linrushao.businessserver.service.RatingService;
import com.linrushao.businessserver.service.UserService;
import com.linrushao.javamodel.Constant;
import com.linrushao.businessserver.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * @Author LRS
 * @Date 2022/10/9 11:16
 * Desc
 */
@Controller
@RequestMapping("/user")
public class UserRatingController {
    private static Logger logger = Logger.getLogger(UserRatingController.class.getName());
    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;
    @Autowired
    private MovieService movieService;

    /**
     * 用户评分
     * @param mid
     * @param score
     * @return
     */
    @RequestMapping("/rating/{mid}")
    @ResponseBody
    public JsonResult<Void> rateToMovie(@PathVariable("mid") int mid, @RequestParam("score") Double score,HttpSession session) {
        Object username = session.getAttribute("username");
        User user = userService.findByUsername(String.valueOf(username));
        MovieRating request = new MovieRating(user.getUid(), mid, score);
        boolean complete = ratingService.movieRating(request);
//        boolean complete = true;
        //埋点日志
        if (complete) {
            logger.info(Constant.MOVIE_RATING_PREFIX + ":" + user.getUid() + "|" + mid + "|" + request.getScore() + "|" + System.currentTimeMillis());
        }
        JsonResult<Void> voidJsonResult = new JsonResult<>();
        voidJsonResult.setState(200);
        voidJsonResult.setMessage("你的评分为："+request.getScore() );
        return voidJsonResult;
    }

    /**
     * 获取用户评分过得电影
     *
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/myrate")
    public String getMyRateMovies(HttpSession session, Model model) {
        Object username = session.getAttribute("username");
        User user = userService.findByUsername(String.valueOf(username));
        model.addAttribute("success", true);
        model.addAttribute("UserRating", movieService.getMyRateMovies(user.getUid()));
        return "UserRating";
    }


}
