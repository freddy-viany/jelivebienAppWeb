package com.aimons.jelivebien.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig  {


    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
    }

    /*

    .antMatchers("/home","/posts","/posts/page/**","/posts/search",
                        "/posts/search/page/**","/addpost","/posts/**",
                        "/posts/delete/**","/posts/update/**","/updatepost","/logout",
                        "/userpanel","/user/**","/paiement_vip","/verify_paiement",
                        "/paiement_premium","/paiement_standard","/verify_paiement_premium",
                        "/verify_paiement_standard").authenticated()
     */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //TODO: The first user should be the Admin. Not others thinks
        //TODO: after that we will remove User on hasAnyAuthority
        //Here, we should remove "User" after the first User signup.  hasAnyAuthority("Admin","Manager", "User")
        //Then we will reload the app on the server


        http.authorizeHttpRequests((requests)-> {
            try {
                requests

                                .requestMatchers("/users","/users/edit/**","/list_users_paiement","/admin","/manager").hasAnyAuthority("Admin","Manager")
                                .requestMatchers("/addpost",
                                "/posts/delete/**","/posts/update/**","/updatepost","/logout",
                                "/userpanel","/user/**","/paiement_vip","/verify_paiement",
                                "/paiement_premium","/paiement_standard","/verify_paiement_premium",
                                "/verify_paiement_standard").authenticated()
                                .anyRequest().permitAll()
                        .and()
                        .formLogin()
                            .loginPage("/login")
                            .usernameParameter("email")
                        .defaultSuccessUrl("/home")
                        .permitAll()
                        .and()
                        .logout()
                        .logoutUrl("/appLogout")
                        .logoutSuccessUrl("/");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return http.build();
    }




}
