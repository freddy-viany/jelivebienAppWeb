package com.aimons.jelivebien.controller;

import com.aimons.jelivebien.model.Post;
import com.aimons.jelivebien.model.User;
import com.aimons.jelivebien.repository.PostRepository;
import com.aimons.jelivebien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminManagementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/admin")
    public String adminPanel(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userEmail = auth.getName();

        User user = userRepository.findByEmail(userEmail);

        model.addAttribute("user",user);

        List<Post> oldPostListToDelete = postRepository.findByPostCanBeDeleteTrue();
        int number = oldPostListToDelete.size();
        model.addAttribute("number",number);


        return "admin_panel";
    }


    @GetMapping("/manager")
    public String managerPanel(Model model){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userEmail = auth.getName();

        User user = userRepository.findByEmail(userEmail);

        model.addAttribute("user",user);

        List<Post> oldPostListToDelete = postRepository.findByPostCanBeDeleteTrue();
        int number = oldPostListToDelete.size();
        model.addAttribute("number",number);


        return "manager_panel";
    }



    @GetMapping("/deleteOldPosts")
    public String deleteOldPosts(RedirectAttributes redirectAttributes){

        List<Post> oldPostListToDelete = postRepository.findByPostCanBeDeleteTrue();
        List<User> users = userRepository.findAll();



        String message="";

        if(oldPostListToDelete.size() >0){

            postRepository.deleteAll(oldPostListToDelete);

             message = " Tous les anciens posts ont ete supprimées avec success";

        }

        redirectAttributes.addFlashAttribute("message",message);




        return "redirect:/admin";
    }


    @GetMapping("/posts/remove/{postID}")
    public String removeFromDBPost(@PathVariable("postID") Long postID, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());


        Post post = postRepository.findById(postID).get();
        post.setPostPublished(false);

        List<Post> postList = postRepository.findAll();
        int indexPost;

        for(Post p: postList){
            if(p==post){
                indexPost = postList.indexOf(p);
                user.getPosts().set(indexPost,null);
                userRepository.save(user);
            }
        }



        postRepository.delete(post);


        model.addAttribute("user",user);
        model.addAttribute("success","annonce supprimée");






        return "redirect:/user/"+user.getId()+"/posts";


    }


}
