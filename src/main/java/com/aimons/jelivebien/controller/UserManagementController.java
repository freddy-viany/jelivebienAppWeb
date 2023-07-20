package com.aimons.jelivebien.controller;

import com.aimons.jelivebien.model.Post;
import com.aimons.jelivebien.model.Role;
import com.aimons.jelivebien.model.User;
import com.aimons.jelivebien.repository.PostRepository;
import com.aimons.jelivebien.repository.RoleRepository;
import com.aimons.jelivebien.repository.UserRepository;
import com.aimons.jelivebien.service.UserService;
import com.aimons.jelivebien.utils.Utility;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class UserManagementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;


    @Autowired
    private JavaMailSender mailSender;



    @GetMapping("/")
    public  String viewHomePage(Model model){

        Long totalAds = postRepository.count();
        model.addAttribute("totalAds",totalAds);
        model.addAttribute("user",new User());
        return "index";
    }

    @GetMapping("/terms-conditions")
    public String termsConditionsPage(){

        return "terms_and_conditions";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model){
        model.addAttribute("user",new User());
        List<String> listCivilities = Arrays.asList("M","Madame","Mlle","Coach","Institut");
        model.addAttribute("listCivilities",listCivilities);

        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegister(@ModelAttribute("user") User user, Model model, HttpServletRequest request)
            throws Exception, UnsupportedEncodingException {

        /*
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        user.setEnabled(true);

        Role roleUser = roleRepository.findByName("User");
        user.addRole(roleUser);

        userRepository.save(user);

         */


        Optional<User> userPhoneEntry = userRepository.findByPhoneNumber(user.getPhoneNumber());
        User userEmailEntry = userRepository.findByEmail(user.getEmail());


        String message="";

        if(userPhoneEntry.isPresent()){

           model.addAttribute("message","Ce numero de telephone est déjà utilisé");
           model.addAttribute("user",new User());

            return "signup_form";
        }

        if(userEmailEntry!=null){
            model.addAttribute("message","Cette adresse email existe déjà");
            model.addAttribute("user",new User());

            return "signup_form";

        }





        userService.register(user, model, getSiteURL(request));

        return "register_success";

    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }


    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (userService.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || authentication instanceof AnonymousAuthenticationToken){
            return "login";
        }
        return "redirect:/";
    }

    @GetMapping("/appLogout")
    public String appLogout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/";
    }



    //TODO: here admin should have access from this to set user
    //TODO: we user also the list of post of search post id to edit

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> listUsers = userRepository.findAll();
        model.addAttribute("listUsers", listUsers);

        return "users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).get();
        List<Role> listRoles = roleRepository.findAll();
        String[] account = new String[] { "VIP","PREMIUM","STANDARD"};
        List<String> listTypeAccount = Arrays.asList(account);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("listTypeAccount", listTypeAccount);
        return "user_edit_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user) {


       userRepository.save(user);

        return "redirect:/users";
    }



    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model)  {
        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            userService.updateResetPasswordToken(token, email);

            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;

            sendEmail(email, resetPasswordLink);
            model.addAttribute("message",
                    "Nous avons envoyé un lien de réinitialisation du mot de passe à " +
                            "votre adresse électronique. Veuillez vérifier.");

        } catch (Exception e) {
            model.addAttribute("error", "Error while sending email");
        }

        return "forgot_password_form_email_out";
    }


    public void sendEmail(String recipientEmail, String link)
            throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("lefredexdev@gmail.com", "Je live bien Support");
        helper.setTo(recipientEmail);

        String subject = "Voici le lien pour réinitialiser votre mot de passe";

        String content = "<p>Hello,</p>"
                + "<p>Vous avez demandé à réinitialiser votre mot de passe.</p>"
                + "<p>Cliquez sur le lien ci-dessous pour changer votre mot de passe :</p>"
                + "<p><a href=\"" + link + "\">Changer mon mot de passe</a></p>"
                + "<br>"
                + "<p>Ignorez cet e-mail si vous vous souvenez de votre mot de passe, "
                + "ou si vous n'avez pas fait la demande.</p>";
        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "message";
        }

        return "reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "message";
        } else {
            userService.updatePassword(user, password);

            model.addAttribute("message", "Vous avez changé votre mot de passe avec succès.");
        }

        return "reset_password_successful";
    }

    @GetMapping("/userpanel")
    public String userPanel(@RequestParam(value ="messagePaiement", required = false) String messagePaiement,
                            Model model){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userEmail = auth.getName();

        User user = userRepository.findByEmail(userEmail);

        boolean roleAdminFound = false;
        boolean roleManagerFound = false;

        for(Role role:user.getRoles()){
            if(role.getName().equals("Admin")){
                roleAdminFound = true;
            }

            if(role.getName().equals("Manager")){
                roleManagerFound = true;
            }
        }

        model.addAttribute("user",user);
        model.addAttribute("roleAdminFound",roleAdminFound);
        model.addAttribute("roleManagerFound",roleManagerFound);
        model.addAttribute("messagePaiement",messagePaiement);

        //TODO: we will return the pseudo of the user, the type of account
        return "user_panel";
    }

    @GetMapping("/user/{id}/posts")
    public String UserPosts(@PathVariable("id") Long id, Model model){

        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            List<Post> allMyPosts = postRepository.findByUserOrderByPostIDDesc(user);

            List<Post> allMyPostsPublished = new ArrayList<>();

            for(Post post:allMyPosts){
                if(post.isPostPublished()==true)
                    allMyPostsPublished.add(post);
            }

            model.addAttribute("allMyPosts", allMyPostsPublished);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User userSession = userRepository.findByEmail(authentication.getName());
            model.addAttribute("user",userSession);

            return "all_my_posts";
        }

        return "index";
    }

}
