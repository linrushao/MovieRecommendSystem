package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.entity.mainEntity.Tag;
import com.linrushao.businessserver.entity.mainEntity.User;
import com.linrushao.businessserver.service.RatingService;
import com.linrushao.businessserver.service.TagService;
import com.linrushao.businessserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @Author LRS
 * @Date 2022/10/12 8:19
 * Desc
 */
@RequestMapping("/tag")
@Controller
public class TagController {

    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;
    @Autowired
    private RatingService ratingService;

    @RequestMapping("/newtag")
    @ResponseBody
    public void newTag(@RequestParam("newtag[]") String[] newtag, Model model){
        System.out.println(Arrays.toString(newtag));
        model.addAttribute("success",true);
        model.addAttribute("message","接收成功");
    }


    @RequestMapping(value = "/tag/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getMovieTags(@PathVariable("mid") int mid, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("tags", tagService.findMovieTags(mid));
        return model;
    }


    @RequestMapping(value = "/mytag/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getMyTags(@PathVariable("mid") int mid, @RequestParam("username") String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("success", true);
        model.addAttribute("tags", tagService.findMyMovieTags(user.getUid(), mid));
        return model;
    }

    @RequestMapping(value = "/newtag/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model addMyTags(@PathVariable("mid") int mid, @RequestParam("tagname") String tagname, @RequestParam("username") String username, Model model) {
        User user = userService.findByUsername(username);
        Tag tag = new Tag(user.getUid(), mid, tagname);
        tagService.newTag(tag);
        model.addAttribute("success", true);
        model.addAttribute("tag", tag);
        return model;
    }

    @RequestMapping(value = "/stat", produces = "application/json", method = RequestMethod.GET)
    public Model getMyRatingStat(@RequestParam("username") String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("success", true);
        model.addAttribute("stat", ratingService.getMyRatingStat(user));
        return model;
    }


}
