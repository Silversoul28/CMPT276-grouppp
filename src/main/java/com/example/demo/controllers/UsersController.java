package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.models.User;
import com.example.demo.models.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UsersController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/users/view")
    public String getAllUsers(Model model){
        System.out.println("Getting all users");
        //TODO: get all users from database
        List<User> users = userRepo.findAll();
        //end of database call
        model.addAttribute("us", users);
        return "users/showAll";
    }

    @GetMapping("/")
    public RedirectView process(){
        return new RedirectView("login");
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String, String> newUser, HttpServletResponse response){
        System.out.println("ADD user");
        String newName = newUser.get("name");
        String newPwd = newUser.get("password");
        int newSize = Integer.parseInt(newUser.get("size"));
        userRepo.save(new User(newName, newPwd, newSize));
        response.setStatus(201);
        return "users/addedUser";
    }

    @GetMapping("/login")
    public String getLogin(Model model, HttpServletRequest request, HttpSession session){
        User user = (User) session.getAttribute("session_user");
        if (user == null){
            return "users/login";
        }
        else{
            model.addAttribute("user", user);
            return "users/protected";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request, HttpSession session){
        // processing logins
        String name = formData.get("name");
        String pwd = formData.get("password");
        List<User> userList = userRepo.findByNameAndPassword(name, pwd);
        if (userList.isEmpty()){
            return "users/login";
        }
        else{
            //sucess
            User user = userList.get(0);
            request.getSession().setAttribute("session_user", user);
            model.addAttribute("user", user);
            return "users/protected";
        }
    }

    @GetMapping("/logout")
    public String destroySession(HttpServletRequest request){
        request.getSession().invalidate();
        return "/users/login";
    }
}