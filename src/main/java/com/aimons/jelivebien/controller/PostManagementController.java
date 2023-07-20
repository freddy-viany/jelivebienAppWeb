package com.aimons.jelivebien.controller;

import com.aimons.jelivebien.model.FilePost;
import com.aimons.jelivebien.model.Post;
import com.aimons.jelivebien.model.User;
import com.aimons.jelivebien.repository.FilePostRepository;
import com.aimons.jelivebien.repository.PostRepository;
import com.aimons.jelivebien.repository.UserRepository;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.io.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class PostManagementController {



    private static final int DEFAULT_PAGE_NUMBER = 1;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilePostRepository filePostRepository;



    @GetMapping("/home")
    public String home(Model model){

         String selectCity ="";
         String selectCategory="";

        model.addAttribute("selectCity",selectCity);
        List<String> listPostCities = Arrays.asList("Douala","Yaounde","Bafoussam","Kribi","Limbe","Dschang");
        model.addAttribute("listPostCities",listPostCities);

        model.addAttribute("selectCategory",selectCategory);

        List<String> listPostCategories = Arrays.asList(
                "Massage aux pierres chaudes",
                "Massage californien & londonien & thailandais",
                "Massage corps à corps & sensuel",
                "Massage sportif ,réfléxologie ",
                "Soin du visage, de la peau, SPA",
                "Épilation, manucure et pédicure",
                "Huiles essentielles et Vitamines",
                "Diététique, Grossir ou Maigrir",
                "Parler pour se libérer d'un chagrin",
                "Coach sentimental - love coach ",
                "Coach de vie - life coach");

        model.addAttribute("listPostCategories",listPostCategories);

        model.addAttribute("post",new Post());


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());

        if(user != null){
            model.addAttribute("user",user);
        }
        else {

            model.addAttribute("user",new User());
        }




        return  "home_post";
    }

    @GetMapping("/posts")
    public String allPosts(Model model){

        return "redirect:/posts/page/"+DEFAULT_PAGE_NUMBER;
    }

    @GetMapping("/posts/page/{pageNo}")
    public String allPostsByPage(@PathVariable("pageNo") int pageNo,
                                 Model model){



        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        if(user != null)
            model.addAttribute("user",user);
        else{
            model.addAttribute("user",new User());
        }




        int pageSize = 9;

        Sort sortByStatus = Sort.by(Sort.Direction.ASC,"status");

        Pageable pageable = PageRequest.of((pageNo - 1),pageSize);

        Page<Post> postPage = postRepository.findByPostPublishedOrderByStatusAscPostIDDesc(true,pageable);



        // time: if post created is less dans 24 hours we call the format "HH:mm"
        //List<String> dateTimeList = new ArrayList<>();
        Map<Long,String > dateTimeList = new HashMap<>();

        List<Post> postPagePostList = postRepository.findByPostPublishedOrderByStatusAscPostIDDesc(true);
        Period period_today;
        Period period_yesterday;
        LocalDateTime dateTime = LocalDateTime.now();
        for(Post p :postPagePostList){
            period_today = Period.between(dateTime.toLocalDate(), p.getCreationPostDateTime().toLocalDate());
            period_yesterday =Period.between(dateTime.minusDays(1).toLocalDate(),p.getCreationPostDateTime().toLocalDate());

            if(period_today.getDays()==0){
               dateTimeList.put(p.getPostID(),"Aujourd'hui à " +p.getCreationPostDateTime().getHour()+"h:"+
                              p.getCreationPostDateTime().getMinute());


            }else if(period_yesterday.getDays()==0){
               dateTimeList.put(p.getPostID(),"Hier à " +p.getCreationPostDateTime().getHour() +"h:" +p.getCreationPostDateTime().getMinute());

            } else{

                dateTimeList.put(p.getPostID(),"le " + p.getCreationPostDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE )));


            }

        }
        model.addAttribute("dateTimeList",dateTimeList);


        model.addAttribute("postPage",postPage);
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",postPage.getTotalElements());
        model.addAttribute("totalItems",postPage.getTotalElements());
        return "all_posts";
    }

    @PostMapping("/posts/search")
    public ModelAndView allPostsBySearchRequest(
            @RequestParam(value ="searchSentence", required = false) String searchSentence,
            @RequestParam( value = "postCity", required = false) String citySearch,
            @RequestParam( value = "postCategory", required = false) String categorySearch,
            RedirectAttributes redirectAttributes){


        redirectAttributes.addAttribute("searchSentence",searchSentence);
        redirectAttributes.addAttribute("postCity",citySearch);
        redirectAttributes.addAttribute("postCategory",categorySearch);




        return new ModelAndView("redirect:/posts/search/page/"+DEFAULT_PAGE_NUMBER);
    }


    @GetMapping("/posts/search/page/{pageNo}")
    public String allPostsBySearchPage(@PathVariable("pageNo") int pageNo,
                                       @RequestParam(value ="searchSentence", required = false) String searchSentence,
                                       @RequestParam( value = "postCity", required = false) String citySearch,
                                       @RequestParam( value = "postCategory", required = false) String categorySearch,
                                       Model model){


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        if(user != null)
            model.addAttribute("user",user);
        else{
            model.addAttribute("user",new User());
        }



        int pageSize = 9;

        Sort sortByStatus = Sort.by(Sort.Direction.ASC,"status");
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,sortByStatus);



        //here when the search is only set - the sentence input(without category and city)

        if (((citySearch.toString().equals("0"))) && ((categorySearch.toString().equals("0")))) {
            if (searchSentence.length()!=0) {

                Page<Post> postPage = postRepository.findByPostDescriptionContainingAndPostPublishedOrderByStatusAscPostIDDesc(searchSentence, true, pageable);


                // time: if post created is less dans 24 hours we call the format "HH:mm"
                //List<String> dateTimeList = new ArrayList<>();
                Map<Long,String > dateTimeList = new HashMap<>();

                List<Post> postPagePostList = postRepository.findByPostDescriptionContainingAndPostPublishedOrderByStatusAscPostIDDesc(searchSentence, true);
                Period period_today;
                Period period_yesterday;
                LocalDateTime dateTime = LocalDateTime.now();
                for(Post p :postPagePostList){
                    period_today = Period.between(dateTime.toLocalDate(), p.getCreationPostDateTime().toLocalDate());
                    period_yesterday =Period.between(dateTime.minusDays(1).toLocalDate(),p.getCreationPostDateTime().toLocalDate());

                    if(period_today.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Aujourd'hui à " +p.getCreationPostDateTime().getHour()+"h:"+
                                p.getCreationPostDateTime().getMinute());

                    }else if(period_yesterday.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Hier à " +p.getCreationPostDateTime().getHour() +"h:" +p.getCreationPostDateTime().getMinute());

                    } else{

                        dateTimeList.put(p.getPostID(),"le " + p.getCreationPostDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE )));

                    }
                }
                model.addAttribute("dateTimeList",dateTimeList);

                //end time: if post created is less dans 24 hours we call the format "HH:mm"



                model.addAttribute("searchSentence",searchSentence);
                model.addAttribute("categorySearch",categorySearch);
                model.addAttribute("citySearch",citySearch);model.addAttribute(searchSentence);
                model.addAttribute(categorySearch);
                model.addAttribute(citySearch);

                model.addAttribute("postPage", postPage);
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("totalPages", postPage.getTotalElements());
                model.addAttribute("totalItems", postPage.getTotalElements());
                return "all_posts_by_search";
            }
        }

        //here when the city is set on only -the sentence input(without search and category empty)

        if ((searchSentence.length()==0) && ((categorySearch.toString().equals("0")))) {
            if (!(citySearch.toString().equals("0"))) {



                Page<Post> postPage = postRepository.findByPostCityAndPostPublishedOrderByStatusAscPostIDDesc(citySearch, true, pageable);

                // time: if post created is less dans 24 hours we call the format "HH:mm"

                Map<Long,String > dateTimeList = new HashMap<>();

                List<Post> postPagePostList = postRepository.findByPostCityAndPostPublishedOrderByStatusAscPostIDDesc(citySearch, true);
                Period period_today;
                Period period_yesterday;
                LocalDateTime dateTime = LocalDateTime.now();
                for(Post p :postPagePostList){
                    period_today = Period.between(dateTime.toLocalDate(), p.getCreationPostDateTime().toLocalDate());
                    period_yesterday =Period.between(dateTime.minusDays(1).toLocalDate(),p.getCreationPostDateTime().toLocalDate());

                    if(period_today.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Aujourd'hui à " +p.getCreationPostDateTime().getHour()+"h:"+
                                p.getCreationPostDateTime().getMinute());

                    }else if(period_yesterday.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Hier à " +p.getCreationPostDateTime().getHour() +"h:" +p.getCreationPostDateTime().getMinute());

                    } else{

                        dateTimeList.put(p.getPostID(),"le " + p.getCreationPostDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE )));

                    }
                }
                model.addAttribute("dateTimeList",dateTimeList);

                //end time: if post created is less dans 24 hours we call the format "HH:mm"

                model.addAttribute("searchSentence",searchSentence);
                model.addAttribute("categorySearch",categorySearch);
                model.addAttribute("citySearch",citySearch);

                model.addAttribute("postPage", postPage);
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("totalPages", postPage.getTotalElements());
                model.addAttribute("totalItems", postPage.getTotalElements());

                return "all_posts_by_search";
            }
        }

        //here when the category exists  only - (without search and city)
        if ((searchSentence.length()==0) && (citySearch.toString().equals("0"))) {
            if (!(categorySearch.toString().equals("0"))) {

                Page<Post> postPage = postRepository.findByPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(categorySearch,true, pageable);

                // time: if post created is less dans 24 hours we call the format "HH:mm"
                //List<String> dateTimeList = new ArrayList<>();
                Map<Long,String > dateTimeList = new HashMap<>();

                List<Post> postPagePostList = postRepository.findByPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(categorySearch,true);
                Period period_today;
                Period period_yesterday;
                LocalDateTime dateTime = LocalDateTime.now();
                for(Post p :postPagePostList){
                    period_today = Period.between(dateTime.toLocalDate(), p.getCreationPostDateTime().toLocalDate());
                    period_yesterday =Period.between(dateTime.minusDays(1).toLocalDate(),p.getCreationPostDateTime().toLocalDate());

                    if(period_today.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Aujourd'hui à " +p.getCreationPostDateTime().getHour()+"h:"+
                                p.getCreationPostDateTime().getMinute());

                    }else if(period_yesterday.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Hier à " +p.getCreationPostDateTime().getHour() +"h:" +p.getCreationPostDateTime().getMinute());

                    } else{

                        dateTimeList.put(p.getPostID(),"le " + p.getCreationPostDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE )));

                    }
                }
                model.addAttribute("dateTimeList",dateTimeList);

                //end time: if post created is less dans 24 hours we call the format "HH:mm"

                model.addAttribute("searchSentence",searchSentence);
                model.addAttribute("categorySearch",categorySearch);
                model.addAttribute("citySearch",citySearch);

                model.addAttribute("postPage", postPage);
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("totalPages", postPage.getTotalElements());
                model.addAttribute("totalItems", postPage.getTotalElements());

                return "all_posts_by_search";
            }
        }

        //here when the search sentence is empty and the others not empty
        if ((searchSentence.length()==0) ) {
            if ( (!(categorySearch.toString().equals("0"))) && (!(citySearch.toString().equals("0")))){

                Page<Post> postPage = postRepository.findByPostCategoryAndPostCityAndPostPublishedOrderByStatusAscPostIDDesc(categorySearch,citySearch,true, pageable);


                // time: if post created is less dans 24 hours we call the format "HH:mm"
                //List<String> dateTimeList = new ArrayList<>();
                Map<Long,String > dateTimeList = new HashMap<>();

                List<Post> postPagePostList = postRepository.findByPostCategoryAndPostCityAndPostPublishedOrderByStatusAscPostIDDesc(categorySearch,citySearch,true);
                Period period_today;
                Period period_yesterday;
                LocalDateTime dateTime = LocalDateTime.now();
                for(Post p :postPagePostList){
                    period_today = Period.between(dateTime.toLocalDate(), p.getCreationPostDateTime().toLocalDate());
                    period_yesterday =Period.between(dateTime.minusDays(1).toLocalDate(),p.getCreationPostDateTime().toLocalDate());

                    if(period_today.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Aujourd'hui à " +p.getCreationPostDateTime().getHour()+"h:"+
                                p.getCreationPostDateTime().getMinute());

                    }else if(period_yesterday.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Hier à " +p.getCreationPostDateTime().getHour() +"h:" +p.getCreationPostDateTime().getMinute());

                    } else{

                        dateTimeList.put(p.getPostID(),"le " + p.getCreationPostDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE )));

                    }
                }
                model.addAttribute("dateTimeList",dateTimeList);

                //end time: if post created is less dans 24 hours we call the format "HH:mm"


                model.addAttribute("searchSentence",searchSentence);
                model.addAttribute("categorySearch",categorySearch);
                model.addAttribute("citySearch",citySearch);

                model.addAttribute("postPage", postPage);
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("totalPages", postPage.getTotalElements());
                model.addAttribute("totalItems", postPage.getTotalElements());

                return "all_posts_by_search";
            }
        }

        //here when the city search is empty and the others not empty
        if ((citySearch.toString().equals("0")) ) {
            if ( (searchSentence.length()!=0) && (!(categorySearch.toString().equals("0")))){

                Page<Post> postPage = postRepository.findByPostDescriptionContainingAndPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(searchSentence,categorySearch,true, pageable);


                // time: if post created is less dans 24 hours we call the format "HH:mm"
                //List<String> dateTimeList = new ArrayList<>();
                Map<Long,String > dateTimeList = new HashMap<>();

                List<Post> postPagePostList = postRepository.findByPostDescriptionContainingAndPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(searchSentence,categorySearch,true);
                Period period_today;
                Period period_yesterday;
                LocalDateTime dateTime = LocalDateTime.now();
                for(Post p :postPagePostList){
                    period_today = Period.between(dateTime.toLocalDate(), p.getCreationPostDateTime().toLocalDate());
                    period_yesterday =Period.between(dateTime.minusDays(1).toLocalDate(),p.getCreationPostDateTime().toLocalDate());

                    if(period_today.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Aujourd'hui à " +p.getCreationPostDateTime().getHour()+"h:"+
                                p.getCreationPostDateTime().getMinute());

                    }else if(period_yesterday.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Hier à " +p.getCreationPostDateTime().getHour() +"h:" +p.getCreationPostDateTime().getMinute());

                    } else{

                        dateTimeList.put(p.getPostID(),"le " + p.getCreationPostDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE )));

                    }
                }
                model.addAttribute("dateTimeList",dateTimeList);

                //end time: if post created is less dans 24 hours we call the format "HH:mm"


                model.addAttribute("searchSentence",searchSentence);
                model.addAttribute("categorySearch",categorySearch);
                model.addAttribute("citySearch",citySearch);

                model.addAttribute("postPage", postPage);
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("totalPages", postPage.getTotalElements());
                model.addAttribute("totalItems", postPage.getTotalElements());

                return "all_posts_by_search";
            }
        }

        //here when the category search is empty and the others not empty
        if (((categorySearch.toString().equals("0"))) ) {
            if ( (searchSentence.length()!=0) && (!(citySearch.toString().equals("0")))){

                Page<Post> postPage = postRepository.findByPostDescriptionContainingAndPostCityAndPostPublishedOrderByStatusAscPostIDDesc(searchSentence,citySearch,true, pageable);


                // time: if post created is less dans 24 hours we call the format "HH:mm"
                //List<String> dateTimeList = new ArrayList<>();
                Map<Long,String > dateTimeList = new HashMap<>();

                List<Post> postPagePostList = postRepository.findByPostDescriptionContainingAndPostCityAndPostPublishedOrderByStatusAscPostIDDesc(searchSentence,citySearch,true);
                Period period_today;
                Period period_yesterday;
                LocalDateTime dateTime = LocalDateTime.now();
                for(Post p :postPagePostList){
                    period_today = Period.between(dateTime.toLocalDate(), p.getCreationPostDateTime().toLocalDate());
                    period_yesterday =Period.between(dateTime.minusDays(1).toLocalDate(),p.getCreationPostDateTime().toLocalDate());

                    if(period_today.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Aujourd'hui à " +p.getCreationPostDateTime().getHour()+"h:"+
                                p.getCreationPostDateTime().getMinute());

                    }else if(period_yesterday.getDays()==0){
                        dateTimeList.put(p.getPostID(),"Hier à " +p.getCreationPostDateTime().getHour() +"h:" +p.getCreationPostDateTime().getMinute());

                    } else{

                        dateTimeList.put(p.getPostID(),"le " + p.getCreationPostDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE )));

                    }
                }
                model.addAttribute("dateTimeList",dateTimeList);

                //end time: if post created is less dans 24 hours we call the format "HH:mm"


                model.addAttribute("searchSentence",searchSentence);
                model.addAttribute("categorySearch",categorySearch);
                model.addAttribute("citySearch",citySearch);

                model.addAttribute("postPage", postPage);
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("totalPages", postPage.getTotalElements());
                model.addAttribute("totalItems", postPage.getTotalElements());

                return "all_posts_by_search";
            }
        }


        //when the search sentence, city and category search are all empty

        if(((searchSentence.length() == 0)) && ((citySearch.toString().equals("0"))) && ((categorySearch.toString().equals("0")))){

            Page<Post> postPage = postRepository.findByPostPublishedOrderByStatusAscPostIDDesc(true,pageable);


            // time: if post created is less dans 24 hours we call the format "HH:mm"
            //List<String> dateTimeList = new ArrayList<>();
            Map<Long,String > dateTimeList = new HashMap<>();

            List<Post> postPagePostList = postRepository.findByPostPublishedOrderByStatusAscPostIDDesc(true);
            Period period_today;
            Period period_yesterday;
            LocalDateTime dateTime = LocalDateTime.now();
            for(Post p :postPagePostList){
                period_today = Period.between(dateTime.toLocalDate(), p.getCreationPostDateTime().toLocalDate());
                period_yesterday =Period.between(dateTime.minusDays(1).toLocalDate(),p.getCreationPostDateTime().toLocalDate());

                if(period_today.getDays()==0){
                    dateTimeList.put(p.getPostID(),"Aujourd'hui à " +p.getCreationPostDateTime().getHour()+"h:"+
                            p.getCreationPostDateTime().getMinute());

                }else if(period_yesterday.getDays()==0){
                    dateTimeList.put(p.getPostID(),"Hier à " +p.getCreationPostDateTime().getHour() +"h:" +p.getCreationPostDateTime().getMinute());

                } else{

                    dateTimeList.put(p.getPostID(),"le " + p.getCreationPostDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE )));

                }
            }
            model.addAttribute("dateTimeList",dateTimeList);

            //end time: if post created is less dans 24 hours we call the format "HH:mm"


            model.addAttribute("searchSentence","");
            model.addAttribute("categorySearch",0);
            model.addAttribute("citySearch",0);


            model.addAttribute("postPage", postPage);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", postPage.getTotalElements());
            model.addAttribute("totalItems", postPage.getTotalElements());

            return "all_posts_by_search";
            }

        //when the search sentence, city and category search are all not empty

        if(((searchSentence.length() != 0)) && (!(citySearch.toString().equals("0"))) && (!(categorySearch.toString().equals("0")))){

            Page<Post> postPage = postRepository.
                    findByPostDescriptionIsContainingAndPostCityAndPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(
                            searchSentence,citySearch,categorySearch,true,
                            pageable);


            // time: if post created is less dans 24 hours we call the format "HH:mm"
            //List<String> dateTimeList = new ArrayList<>();
            Map<Long,String > dateTimeList = new HashMap<>();

            List<Post> postPagePostList = postRepository.
                    findByPostDescriptionIsContainingAndPostCityAndPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(
                            searchSentence,citySearch,categorySearch,true);

            Period period_today;
            Period period_yesterday;
            LocalDateTime dateTime = LocalDateTime.now();
            for(Post p :postPagePostList){
                period_today = Period.between(dateTime.toLocalDate(), p.getCreationPostDateTime().toLocalDate());
                period_yesterday =Period.between(dateTime.minusDays(1).toLocalDate(),p.getCreationPostDateTime().toLocalDate());

                if(period_today.getDays()==0){
                    dateTimeList.put(p.getPostID(),"Aujourd'hui à " +p.getCreationPostDateTime().getHour()+"h:"+
                            p.getCreationPostDateTime().getMinute());

                }else if(period_yesterday.getDays()==0){
                    dateTimeList.put(p.getPostID(),"Hier à " +p.getCreationPostDateTime().getHour() +"h:" +p.getCreationPostDateTime().getMinute());

                } else{

                    dateTimeList.put(p.getPostID(),"le " + p.getCreationPostDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE )));

                }
            }
            model.addAttribute("dateTimeList",dateTimeList);

            //end time: if post created is less dans 24 hours we call the format "HH:mm"



            model.addAttribute("searchSentence","");
            model.addAttribute("categorySearch",0);
            model.addAttribute("citySearch",0);


            model.addAttribute("postPage", postPage);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", postPage.getTotalElements());
            model.addAttribute("totalItems", postPage.getTotalElements());

            return "all_posts_by_search";
        }






        return "all_posts_by_search";

    }

    @GetMapping("/addpost")
    public String addPost(Model model){
        Post post = new Post();
        model.addAttribute("post",post);

        List<String> listPostCities = Arrays.asList("Douala","Yaounde","Bafoussam","Kribi","Limbe","Dschang");
        model.addAttribute("listPostCities",listPostCities);

        List<String> listPostCategories = Arrays.asList(
                "Massage aux pierres chaudes",
                "Massage californien & londonien & thailandais",
                "Massage corps à corps & sensuel",
                "Massage sportif ,réfléxologie ",
                "Soin du visage, de la peau, SPA",
                "Épilation, manucure et pédicure",
                "Huiles essentielles et Vitamines",
                "Diététique, Grossir ou Maigrir",
                "Parler pour se libérer d'un chagrin",
                "Coach sentimental - love coach ",
                "Coach de vie - life coach");

        List<String> listPostMoveSituation = Arrays.asList("Reçois uniquement",
                "Se déplace uniquement",
                "Reçois et se déplace");

        model.addAttribute("listPostCategories",listPostCategories);
        model.addAttribute("listPostMoveSituation",listPostMoveSituation);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        model.addAttribute("user",user);

        return "add_post";
    }



    @PostMapping("/addpost")
    public  String savePost(Model model, @ModelAttribute("post") Post post,
                            @RequestParam("files") MultipartFile [] images, HttpServletRequest request) throws IOException {

        String URL_watermark = getSiteURL(request);

        Post newPost = new Post();

        newPost.setPostTitle(post.getPostTitle());
        newPost.setPostDescription(post.getPostDescription());
        newPost.setPostPhoneNumber(post.getPostPhoneNumber());
        newPost.setPostPublished(true);
        newPost.setPostCity(post.getPostCity());
        newPost.setPostCategory(post.getPostCategory());
        newPost.setPostMoveSituation(post.getPostMoveSituation());


        //TODO:IMPORTANT
        //
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userEmail = auth.getName();
        User userTypeAccount = userRepository.findByEmail(userEmail);
        //String userTypeAccount = userDetails.getTypeAccount();
        //newPost.setStatus(Post.PostStatus.STANDARD);

        if(userTypeAccount.getTypeAccount()== User.TypeAccount.VIP){
            newPost.setStatus(Post.PostStatus.VIP);
        }
        if(userTypeAccount.getTypeAccount() == User.TypeAccount.PREMIUM){
            newPost.setStatus(Post.PostStatus.PREMIUM);
        }
        if(userTypeAccount.getTypeAccount() == User.TypeAccount.STANDARD){
            newPost.setStatus(Post.PostStatus.STANDARD);
        }
        if(userTypeAccount.getTypeAccount() == User.TypeAccount.GRATUIT){
            newPost.setStatus(Post.PostStatus.GRATUIT);

            if(userTypeAccount.getNumberPostPermits()==1){
                userTypeAccount.setNumberPostPermits(userTypeAccount.getNumberPostPermits()-1);
                userTypeAccount.setSubscribe(false);
            }

            if(userTypeAccount.getNumberPostPermits()>1){
                userTypeAccount.setNumberPostPermits(userTypeAccount.getNumberPostPermits()-1);

            }



        }

        newPost.setUser(userTypeAccount);

        newPost.setPostCertifie(userTypeAccount.isCertified());


        newPost.setCreationPostDateTime(LocalDateTime.now());

        //TODO:IMPORTANT
        //TODO: here we will set the expire post time for 60days and the expire sponsoring date at 15 days

        newPost.setExpirePostDateTime(LocalDateTime.now().plusDays(60));

        //TODO: the setting the below should be null when the post is standard.
        //TODO: newPost.setExpireSponsoringPostDateTime(LocalDateTime.now().plusDays(15)); when post is vip or premium

        //TODO:Important!!
        //TODO: the delay of live of post is fonction on the type of user account for it's 5 days for all
        newPost.setExpireSponsoringPostDateTime(LocalDateTime.now().plusDays(5));

        //here if images are not null we put the first image in new post object
        if(images.length!=0){
            // get image

            // Enregistrer le contenu de l'image dans un objet File
            File uploadedFile = new File(images[0].getOriginalFilename());
            FileUtils.writeByteArrayToFile(uploadedFile, images[0].getBytes());

            // Ajouter un filigrane de texte au bas de l'image
            /**
            For example, to create thumbnails which are 50% the size of the original, the following code can be used:


            Thumbnails.of(image)
                    .scale(0.5)
                    .toFile(thumbnail);
             **/

            //ImageIO.read(getClass().getResource("/images/black.jpg"));

            /*Thumbnails.of(uploadedFile)
                    .scale(1)
                    .watermark(Positions.BOTTOM_CENTER, ImageIO.read(new File("src/main/resources/static/img/filigrane.png")), 0.5f)
                    .outputQuality(1)
                    .toFile(uploadedFile);*/



            /*Thumbnails.of(uploadedFile)
                    .scale(1)
                    .watermark(Positions.BOTTOM_CENTER, ImageIO.read(new URL(URL_watermark+"/img/filigrane.png")), 0.5f)
                    .outputQuality(1)
                    .toFile(uploadedFile);*/

            Thumbnails.of(uploadedFile)
                    .scale(1)
                    .watermark(Positions.CENTER, ImageIO.read(new URL(URL_watermark+"/img/filigrane-small.png")), 0.3f)
                    .outputQuality(1)
                    .toFile(uploadedFile);

            // Convertir l'image modifiée en base64
            byte[] modifiedFileContent = FileUtils.readFileToByteArray(uploadedFile);

            //String s = Base64.encodeBase64String(images[0].getBytes());
            String s = Base64.encodeBase64String(modifiedFileContent);
            newPost.setImageBase64(s);
            //String base64Encoded = new String(encodeBase64, "UTF-8");
            //base64List.add(base64Encoded);

            newPost.setImageType(images[0].getContentType());
            newPost.setImageContent(images[0].getBytes());
        }

        //saving images in his table
        List<FilePost> filePostList = new ArrayList<>();

        for(MultipartFile image:images){


            //Ecriture d'un filigrane pour les images a mettre en ligne
            //Start

            // Enregistrer le contenu de l'image dans un objet File
            File uploadedFile = new File(image.getOriginalFilename());
            FileUtils.writeByteArrayToFile(uploadedFile, image.getBytes());

            // Ajouter un filigrane de texte au bas de l'image
            Thumbnails.of(uploadedFile)
                    .scale(1)
                    .watermark(Positions.CENTER, ImageIO.read(new URL(URL_watermark+"/img/filigrane-small.png")), 0.3f)
                    .outputQuality(1)
                    .toFile(uploadedFile);

            // Convertir l'image modifiée en base64
            byte[] modifiedFileContent = FileUtils.readFileToByteArray(uploadedFile);

            //End

            /*
            String base64Encoded = Base64.getEncoder().encodeToString(modifiedFileContent);

            // Enregistrer l'image et le texte dans la base de données
            Annonce annonce = new Annonce(text, base64Encoded);
            return annonceRepository.save(annonce);

             */


            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
            String fileContentType = image.getContentType();
            Long fileSize = image.getSize();
            //byte [] data = image.getBytes();
            byte [] data = modifiedFileContent;
            // save file into DB
            FilePost filePost = new FilePost(fileName,fileContentType,fileSize,data,newPost);
            // Adding file into fileList
            filePostList.add(filePost);
        }

        // Save all the files into database
        for (FilePost filePost : filePostList)
            filePostRepository.save(filePost);

        // Save all the post into database
        newPost.setAttachments(filePostList);


        postRepository.save(newPost);

        model.addAttribute("success","cool,annonce enregistrée");

        return "redirect:/posts";
    }


    //here we display post by id


    @GetMapping("/show-posts/{postID}")
    public String showPost(@PathVariable("postID") Long postID, Model model, HttpServletRequest request) throws UnsupportedEncodingException {

        Post post = postRepository.findById(postID).get();

        //list of image by post id
        List<String> filePostConvertList = new ArrayList<>();
        if(post != null){
            List<FilePost> filePosts =filePostRepository.findAllFilleByPostId(Optional.of(post));

            for(FilePost file:filePosts){
                String s= Base64.encodeBase64String(file.getContent());
                filePostConvertList.add(s);
            }



        }


        model.addAttribute("filePostConvert",filePostConvertList);
        model.addAttribute("filePostConvert1",filePostConvertList.get(0));


        //encode de url to send message, phone call and whatsapp
        String urlencode = java.net.URLEncoder.encode("Je live bien App ** "+post.getPostTitle(),  java.nio.charset.StandardCharsets.UTF_8.toString());

        // time: if post created is less dans 24 hours we call the format "HH:mm"
        Period period = Period.between(LocalDateTime.now().toLocalDate(),post.getCreationPostDateTime().toLocalDate());

        if(period.isZero()){
            String timeFormatOfPublishedPost = post.getCreationPostDateTime().getHour()+":"+
                    post.getCreationPostDateTime().getMinute();
            model.addAttribute("timeFormatOfPublishedPost",timeFormatOfPublishedPost);
        }else {
            model.addAttribute("timeFormatOfPublishedPost","");
        }

        model.addAttribute("encodeURL",urlencode);
        model.addAttribute("post",post);

        //here we should the user who did the post


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        if(user != null)
            model.addAttribute("user",user);
        else{
            model.addAttribute("user",new User());
        }


        /*
        Here we count the number view of post view this URL
         */

        String theCurrentURL = getCurrentURL(request);

        if (theCurrentURL.contains("show-posts/")){
            String s = theCurrentURL.substring(12);
            int view = Integer.parseInt(s);
            if(post.getPostID()==view){
                post.setPostViewCount(post.getPostViewCount()+1);
                postRepository.save(post);
            }

        }




        return "show_post";
    }

    // here delete post by setting publisher by false


    @GetMapping("/posts/delete/{postID}")
    public String deletePost(@PathVariable("postID") Long postID, Model model) {

        Post post = postRepository.findById(postID).get();
        post.setPostPublished(false);
        postRepository.save(post);

        model.addAttribute("success","annonce supprimée");


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        model.addAttribute("user",user);



       return "redirect:/user/"+user.getId()+"/posts";


    }





    //TODO : update post by id

    @GetMapping("/posts/update/{postID}")
    public String updatePostForm(@PathVariable("postID") Long postID, Model model) {

        Post post = postRepository.findById(postID).get();

        //list of image by post id
        List<String> filePostConvertList = new ArrayList<>();
        if (post != null) {
            List<FilePost> filePosts = filePostRepository.findAllFilleByPostId(Optional.of(post));

            for (FilePost file : filePosts) {
                String s = Base64.encodeBase64String(file.getContent());
                filePostConvertList.add(s);
            }


        }


        model.addAttribute("filePostConvert", filePostConvertList);
        model.addAttribute("post",post);

        List<String> listPostCities = Arrays.asList("Douala","Yaounde","Bafoussam","Kribi","Limbe","Dschang");
        model.addAttribute("listPostCities",listPostCities);

        List<String> listPostCategories = Arrays.asList(
                "Massage aux pierres chaudes",
                "Massage californien & londonien & thailandais",
                "Massage corps à corps & sensuel",
                "Massage sportif ,réfléxologie ",
                "Soin du visage, de la peau, SPA",
                "Épilation, manucure et pédicure",
                "Huiles essentielles et Vitamines",
                "Diététique, Grossir ou Maigrir",
                "Parler pour se libérer d'un chagrin",
                "Coach sentimental - love coach ",
                "Coach de vie - life coach");
        model.addAttribute("listPostCategories",listPostCategories);

        return "update_post_form";
    }

    @PostMapping("/updatepost")
    @Transactional
    public  String updatePost(Model model, @ModelAttribute("post") Post post,
                            @RequestParam("files") MultipartFile [] images) throws IOException {


        post.setPostTitle(post.getPostTitle());
        post.setPostDescription(post.getPostDescription());
        post.setPostPhoneNumber(post.getPostPhoneNumber());
        post.setPostPublished(true);
        post.setPostCity(post.getPostCity());
        post.setPostCategory(post.getPostCategory());

        //IMPORTANT

        post.setStatus(post.getStatus());

        post.setCreationPostDateTime(post.getCreationPostDateTime());
        post.setExpirePostDateTime(post.getExpirePostDateTime());
        post.setExpireSponsoringPostDateTime(post.getExpireSponsoringPostDateTime());

        //here if images are not null we put the first image in new post object
        if(images.length!=0){
            // get image

            String image = Base64.encodeBase64String(images[0].getBytes());
            post.setImageBase64(image);
            //String base64Encoded = new String(encodeBase64, "UTF-8");
            //base64List.add(base64Encoded);

            post.setImageType(images[0].getContentType());
            post.setImageContent(images[0].getBytes());
        }

        //TODO: see have to update existing image into the post
        //TODO: update existing of which is child of another object is problem
        //saving images in his table


        List<FilePost> filePostList = new ArrayList<>();

        for(MultipartFile image:images){
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
            String fileContentType = image.getContentType();
            Long fileSize = image.getSize();
            byte [] data = image.getBytes();
            FilePost filePost = new FilePost(fileName,fileContentType,fileSize,data,post);

            filePostList.add(filePost);
        }

        // Save all the files into database
        for (FilePost filePost : filePostList){
            if(filePost.getOwner()==post){
                filePost.setOwner(post);
            }
            filePostRepository.save(filePost);
        }


        // Save all the post into database
        post.setAttachments(filePostList);

        postRepository.save(post);



        model.addAttribute("success","cool,annonce modifiee");


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        model.addAttribute("user",user);

        return "redirect:/user/"+user.getId()+"/posts";
    }



    public String getCurrentURL(HttpServletRequest request){

        String currentURL = request.getServletPath();

        return currentURL;
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }






}
