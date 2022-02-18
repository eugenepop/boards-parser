package nil.webparser.controller;

import nil.webparser.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import nil.webparser.security.UserService;

import java.security.Principal;

@Controller
public class PersonalAccount {
    @Autowired
    private UserService userService;

    @GetMapping("/lk")
    public String lk(Principal principal, Model model) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        model.addAttribute("user", user.getEmail());
        return "lk";
    }
}