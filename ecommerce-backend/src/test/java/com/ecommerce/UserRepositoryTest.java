package com.ecommerce;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void testSaveAndFindUser(){
        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setName("test");
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        assertTrue(savedUser.getId() != null, "Saved user should have an ID");

    }
}
