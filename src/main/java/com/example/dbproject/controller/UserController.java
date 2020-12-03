package com.example.dbproject.controller;

import com.example.dbproject.domain.Role;
import com.example.dbproject.domain.User;
import com.example.dbproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")

public class UserController {
    @Autowired
    UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "userList";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{userId}")
    public String userEditForm(@PathVariable Long userId, Model model) {
        model.addAttribute("user", userService.findById(userId).get());
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") Long userId
    ) {
        userService.saveUser(userId,username,form);

        return "redirect:/user";
    }
    @GetMapping("profile")
    public String profile(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("username",user.getUsername());
        return "profile";
    }
    @PostMapping("profile")
    public String updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam String password,
            @RequestParam String username
    ) {
        userService.updateProfile(user,username,password);
        return "redirect:/user/profile";
    }
}
