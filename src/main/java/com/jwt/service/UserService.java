package com.jwt.service;

import com.jwt.model.User;
import com.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<User> geUsers() {
        return userRepository.findAll();
    }

    public User createUsers(User user) {
       
        user.setUserPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
