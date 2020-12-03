package com.example.dbproject.service;

import com.example.dbproject.domain.Role;
import com.example.dbproject.domain.User;
import com.example.dbproject.repos.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService  implements UserDetailsService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    public List<User> findAll() {
        return  userRepo.findAll();
    }

    public Optional<User> findById(Long userId) {
        return userRepo.findById(userId);
    }


    public void saveUser(Long userId, String username, Map<String, String> form) {
        User user = userRepo.findById(userId).get();
        user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepo.save(user);
    }

    public void updateProfile(User user, String username, String password) {
        if(!user.getPassword().equals(password) && !password.isEmpty()){
            user.setPassword(password);
        }
        if(!user.getUsername().equals(username)){
            user.setUsername(username);
        }
        userRepo.save(user);
    }
}
