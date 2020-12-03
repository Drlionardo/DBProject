package com.example.dbproject.controller;

import com.example.dbproject.domain.Message;
import com.example.dbproject.domain.User;
import com.example.dbproject.repos.MessageRepo;
import com.example.dbproject.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private UserRepo userRepo;
    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(Map<String,Object> model) {

        return "greeting";
    }
    @GetMapping("/main")
    public String main(Model model, @RequestParam(required = false) String filter){
        Iterable<Message> messages = messageRepo.findAll();
        if(filter!=null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);
        }
        else {
            messages = messageRepo.findAll();
        }
        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);
        return "main";
    }

    @PostMapping("/main")
    public String addMessage(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag,
            @RequestParam("file") MultipartFile file,
            Map<String, Object> model) throws IOException {
        Message m = new Message(text, tag, user);

        saveFile(m, file);
        messageRepo.save(m);
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);

        return "main";
    }
    @PostMapping("filter")
    public String filter(@RequestParam String filter, Map<String,Object> model){
        Iterable<Message> messages;
        if(filter!=null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);
        }
        else {
            messages = messageRepo.findAll();
        }
        model.put("messages", messages);

        return "main";
    }
    @GetMapping("/user-messages/{userId}")
    public String userMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId,
            Model model,
            @RequestParam(required = false) Long messageId
    ) {
        Message message;
        if (messageId != null) {
            message= messageRepo.findById(messageId).get();
        }
       else message=null;
        User user = userRepo.findById(userId).get();
        Set<Message> messages = user.getMessages();

        model.addAttribute("messages", messages);
        model.addAttribute("message", message);
        model.addAttribute("isCurrentUser", currentUser.equals(user));

        return "user-messages";
    }
    @PostMapping("/user-messages/{userId}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId,
            @RequestParam("id") Long messageId,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Message message = messageRepo.findById(messageId).get();
        if (message.getAuthor().equals(currentUser)) {
            if (!StringUtils.isEmpty(text)) {
                message.setText(text);
            }

            if (!StringUtils.isEmpty(tag)) {
                message.setTag(tag);
            }

            saveFile(message, file);

            messageRepo.save(message);
        }

        return "redirect:/user-messages/" + userId.toString();
    }
    private void saveFile(Message message, @RequestParam("file") MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            message.setFilename(resultFilename);
        }
    }
}