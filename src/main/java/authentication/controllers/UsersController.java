package authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import authentication.models.User;
import authentication.services.UserService;

@Controller
public class UsersController {
    private final UserService userService;
    
    public UsersController(UserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "users/registration.jsp";
    }
    @RequestMapping("/login")
    public String login() {
        return "users/login.jsp";
    }
    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
    	if(result.hasErrors()) {
    		return"redirect:/registration";
    	} else {
    		userService.registerUser(user);
    		session.setAttribute("user_id", user.getId());
    		return"users/home.jsp";
    	}

    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
    	if(userService.authenticateUser(email, password)){
    		User user = userService.findByEmail(email);
    		session.setAttribute("user_id", user.getId());
    		return"redirect:/home";
    	}else {
    		model.addAttribute("error", "Wrong credentials. Please enter your valid credentials");
    		return "redirect:/login";
    	}

    }
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
    	Long id = (Long) session.getAttribute("user_id");
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        return "users/home.jsp";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}