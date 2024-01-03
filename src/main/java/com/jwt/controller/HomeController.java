package com.jwt.controller;

import com.jwt.model.User;
import com.jwt.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private UserService userService;


    @GetMapping("/user")
    public List<User> getUser() {
        LOGGER.info("getting users");
        return userService.geUsers();
    }

    @GetMapping("/current-user")
    public String getLoggedInUser(Principal principal) {
        return principal.getName();
    }
}
