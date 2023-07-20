package com.aimons.jelivebien.service;

import com.aimons.jelivebien.exception.UserNotFoundException;
import com.aimons.jelivebien.model.Role;
import com.aimons.jelivebien.model.User;
import com.aimons.jelivebien.repository.RoleRepository;
import com.aimons.jelivebien.repository.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service

public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private JavaMailSender mailSender;



    public void register(User user, Model model, String siteURL) throws Exception, UnsupportedEncodingException {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);

        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);
        user.setSubscribe(false);  //here by default the account is not able to post or read number phone of the ads
        user.setCreateUserDate(LocalDateTime.now());
        user.setExpireSubscribeDate(LocalDateTime.now());
        user.setExpireCertifiedDate(LocalDateTime.now());

        user.setTypeAccount(User.TypeAccount.GRATUIT);
        user.setNumberPostPermits(3);
        user.setSubscribe(true);

        //here the first User should be the Admin.
        List<User> userList = userRepository.findAll();
        Role roleUser;
        if(userList.size()==0){
            roleUser = roleRepository.findByName("Admin");
        } else{
            roleUser = roleRepository.findByName("User");
        }
        user.addRole(roleUser);


        userRepository.save(user);

        sendVerificationEmail(user, siteURL);

    }

    private void sendVerificationEmail(User user, String siteURL) throws Exception {
        String toAddress = user.getEmail();
        String fromAddress = "lefredexdev@gmail.com";
        String senderName = "JeliveBien";
        String subject = "Veuillez vérifier votre inscription";
        String content = "Cher(e) [[name]],<br>"
                + "Veuillez cliquer sur le lien ci-dessous pour vérifier votre inscription:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFIER</a></h3>"
                + "Merci,<br>"
                + " Je live bien.";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getCivility());

        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }

    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            Random random = new Random();
            int number = random.nextInt(10);
            String emailFail = "fail";
            user.setPhoneNumber(number);
            user.setEmail(emailFail+number+"@gmail.com");
            userRepository.save(user);
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            user.setCertified(false);
            userRepository.save(user);

            return true;
        }

    }


        public void updateResetPasswordToken(String token, String email) throws UserNotFoundException {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                user.setResetPasswordToken(token);
                userRepository.save(user);
            } else {
                throw new UserNotFoundException("Could not find any customer with the email " + email);
            }
        }

        public User getByResetPasswordToken(String token) {
            return userRepository.findByResetPasswordToken(token);
        }

        public void updatePassword(User user, String newPassword) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);

            user.setResetPasswordToken(null);
            userRepository.save(user);
        }
    }

