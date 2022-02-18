package nil.webparser.controller;

import nil.webparser.dao.UserRepository;
import nil.webparser.email.EmailService;
import nil.webparser.entity.Role;
import nil.webparser.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.ROLE_USER));

        userRepository.save(user);

        emailService.sendSimpleMessage(email, name);

        return "redirect:/login";
    }
}