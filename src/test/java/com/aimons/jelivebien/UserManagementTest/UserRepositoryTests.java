package com.aimons.jelivebien.UserManagementTest;

import com.aimons.jelivebien.model.User;
import com.aimons.jelivebien.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserRepositoryTests {



    @Autowired
    private UserRepository userRepository;



    @Test
    public void testCreatedUser(){

        User user = new User();
        user.setFirstName("dex");
        user.setLastName("dex");
        user.setEmail("dex@dex.com");
        user.setPhoneNumber(695695695);
        user.setCivility("Ledex");
        user.setPassword("dex");
        user.setEnabled(true);

        User savedUser = userRepository.save(user);



        Assertions.assertSame("dex@dex.com",savedUser.getEmail());
    }


}
