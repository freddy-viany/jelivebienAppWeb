package com.aimons.jelivebien;

import com.aimons.jelivebien.model.Role;
import com.aimons.jelivebien.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
//@PropertySource("classpath:application-${spring.profiles.active:dev}.properties")
public class JeLiveBienApplication implements ApplicationRunner {

    private final RoleRepository roleRepository;


    @Autowired
    public JeLiveBienApplication(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }



    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Create the "Admin" role if it doesn't exist
        if (roleRepository.findByName("Admin")==null) {
            Role adminRole = new Role("Admin");
            roleRepository.save(adminRole);
        }

        // Create the "Manager" role if it doesn't exist
        if (roleRepository.findByName("Manager")==null) {
            Role managerRole = new Role("Manager");
            roleRepository.save(managerRole);
        }

        // Create the "User" role if it doesn't exist
        if (roleRepository.findByName("User")==null) {
            Role userRole = new Role("User");
            roleRepository.save(userRole);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(JeLiveBienApplication.class, args);


    }
}
