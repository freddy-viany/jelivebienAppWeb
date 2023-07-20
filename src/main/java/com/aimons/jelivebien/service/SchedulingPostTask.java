package com.aimons.jelivebien.service;

import com.aimons.jelivebien.model.Post;
import com.aimons.jelivebien.model.User;
import com.aimons.jelivebien.repository.PostRepository;
import com.aimons.jelivebien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulingPostTask {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * each 12 hours this method is running. each post has 60 days for live
     */
    @Scheduled(fixedDelay = 43200000)
    public void disablePostAfterExpireDate(){

        LocalDateTime timeNow= LocalDateTime.now();
        Period period;

        List<Post> postList = postRepository.findAll();
        if(postList.size()!=0){
            for(Post p :postList){
                period = Period.between(timeNow.toLocalDate(), p.getExpirePostDateTime().toLocalDate());

                if(period.isNegative()){
                    if(p.isPostPublished()==true){
                        p.setPostPublished(false);
                        p.setPostCanBeDelete(true);
                        postRepository.save(p);
                    }
                    else{
                        p.setPostCanBeDelete(true);
                        postRepository.save(p);
                    }
                }

            }
            System.out.println("Method executed, disable Post After Expire Dates is running. Current time is :: "+ new Date());
        }
        System.out.println("Method executed , disable Post After Expire Date. Current time is :: "+ new Date());
    }

    /**
     *  each 4 hours this method is running. The sponsoring post have 5 days for live
     *  if post is vip or premium or stantard we set it to false-->Gratuit
     */
    @Scheduled(fixedDelay = 14400000)
    public void disableSponsoringPostAfterExpireDate(){
        LocalDateTime timeNow = LocalDateTime.now();
        Period period;

        List<Post> postList = postRepository.findAll();
        if(postList.size()!=0){
            for(Post p:postList){
                if(p.getExpireSponsoringPostDateTime()!=null){
                    period = Period.between(timeNow.toLocalDate(),p.getExpireSponsoringPostDateTime().toLocalDate());
                    if(period.isNegative()){
                        if(p.isPostPublished()==true){
                            p.setStatus(Post.PostStatus.GRATUIT);
                            postRepository.save(p);
                        }
                    }
                }
            }
            System.out.println("The sponsoring post expire date is updating :"+new Date());
        }

        System.out.println("The method of sponsoring post exipre date is running: " +new Date());

    }


    /**
     *  each 4 hours this method is running. The sponsoring post have 5 days for live
     *  if post is vip or premium or standard, we renewal its creation date
     *  as the current date
     */
    @Scheduled(fixedDelay = 14400000)
    public void AutoRenewalSponsoringPostAfterOneDay(){
        LocalDateTime timeNow = LocalDateTime.now();
        Period period;
        Period periodPostDateCreate;

        List<Post> postList = postRepository.findAll();
        if(postList.size()!=0){
            for(Post p:postList){

                        if((p.isPostPublished()==true) && (p.getStatus()!= Post.PostStatus.GRATUIT)){

                           periodPostDateCreate =  Period.between(timeNow.toLocalDate(),p.getCreationPostDateTime().toLocalDate());

                            if((periodPostDateCreate.getDays()<-1) && (p.getStatus()== Post.PostStatus.VIP)){
                                p.setCreationPostDateTime(LocalDateTime.now().minusMinutes(2L));
                                postRepository.save(p);
                            }

                            if((periodPostDateCreate.getDays()<=-2) && (p.getStatus()== Post.PostStatus.PREMIUM)){
                                p.setCreationPostDateTime(LocalDateTime.now().minusMinutes(5L));
                                postRepository.save(p);
                            }

                            if((periodPostDateCreate.getDays()<=-2) && (p.getStatus()== Post.PostStatus.STANDARD)){
                                p.setCreationPostDateTime(LocalDateTime.now().minusMinutes(10L));
                                postRepository.save(p);
                            }

                        }


            }
            System.out.println("The sponsoring post expire date is updating :"+new Date());
        }

        System.out.println("The method of sponsoring post exipre date is running: " +new Date());

    }

    /**
     *  each 4 hours this method is running. The subscribe user have 30 days for true or up

     */

    @Scheduled(fixedDelay = 14400000)
    public void unsubscribeUserAfterOneMonth(){
        LocalDateTime timeNow = LocalDateTime.now();
        Period period;

        List<User> userList = userRepository.findAll();
        if(userList.size()!=0) {
            for(User u :userList){
                period = Period.between(timeNow.toLocalDate(), u.getExpireSubscribeDate().toLocalDate());

                if(period.isNegative()){
                    if(u.isSubscribe()==true){
                        u.setSubscribe(false);
                        userRepository.save(u);
                    }
                }

            }
            System.out.println("The user subscribe expire is updating to false :"+new Date());
        }

        System.out.println("The method subscribe expire is running :"+new Date());
    }

    @Scheduled(fixedDelay = 86400000)
    public void uncertifiedUserAfterThreeMonth(){
        LocalDateTime timeNow = LocalDateTime.now();
        Period period;

        List<User> userList = userRepository.findAll();
        if(userList.size()!=0) {
            for(User u :userList){

                if(u.getExpireCertifiedDate()!=null){
                    period = Period.between(timeNow.toLocalDate(), u.getExpireCertifiedDate().toLocalDate());

                    if(period.isNegative()){
                        if(u.isCertified()==true){
                            u.setCertified(false);
                            userRepository.save(u);
                        }
                    }
                }



            }
            System.out.println("The user certified expire is updating to false :"+new Date());
        }

        System.out.println("The method certified expire is running :"+new Date());
    }
}
