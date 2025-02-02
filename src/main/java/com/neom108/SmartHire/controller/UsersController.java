package com.neom108.SmartHire.controller;

import com.neom108.SmartHire.entity.Users;
import com.neom108.SmartHire.entity.UsersType;
import com.neom108.SmartHire.services.UsersService;
import com.neom108.SmartHire.services.UsersTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {

    private final UsersTypeService usersTypeService;
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersTypeService usersTypeService, UsersService usersService, UsersService usersService1) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService1;
    }

    @GetMapping("/register")
    public String register(Model model){
        List<UsersType> usersTypes = usersTypeService.getAll();

        model.addAttribute("getAllTypes", usersTypes);
        model.addAttribute("user", new Users());
        return "register";

    }

    @PostMapping("/register/new")
    public String userRegistration(@Valid Users users, Model model){

        Optional<Users> optionalUsers = usersService.getUserByEmail(users.getEmail());

        if (optionalUsers.isPresent()){
            model.addAttribute("error","User already exists, try to login or register with another email");
            List<UsersType> usersTypes = usersTypeService.getAll();

            model.addAttribute("getAllTypes", usersTypes);
            model.addAttribute("user", new Users());
            return "register";
        }
        usersService.addNew(users);
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            // built-in Spring Security class that handles the logout process.
            //It clears the SecurityContext and removes authentication from the session
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }
        return "redirect:/";
    }
}
