package com.example.dbproject.controller;

import com.example.dbproject.domain.Role;
import com.example.dbproject.domain.User;
import com.example.dbproject.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepo userRepo;
    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String,Object> model){
        User userFromDb = userRepo.findByUsername(user.getUsername());
        if(userFromDb!=null){
            model.put("message", "User exists!");
            return "registration";
        }
        else {
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.USER));
            user.setPassword(user.getPassword());
            userRepo.save(user);
        }
        return "redirect:/login";
    }


}
