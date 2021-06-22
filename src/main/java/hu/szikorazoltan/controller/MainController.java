package hu.szikorazoltan.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import hu.szikorazoltan.dto.UserRegistrationDto;
import hu.szikorazoltan.service.UserService;

@Controller
public class MainController {

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/home")
	public String index() {
		return "index";
	}

	@GetMapping("/")
	public String homePage() {
		return "forward:/home";
	}

	@GetMapping("/registration")
	public String registrationForm(Model model) {
		model.addAttribute("user", new UserRegistrationDto());
		return "registration";
	}

	@PostMapping("/reg")
	public String regSubmit(@ModelAttribute("user") @Valid UserRegistrationDto userRegDto, BindingResult result) {
		if (result.hasErrors()) return "registration";
		userService.registerUser(userRegDto.convertToUser());
		return "redirect:/registration?success";
	}

	@GetMapping("/activation/{code}")
	public String activation(@PathVariable("code") String code, HttpServletResponse response) {
		userService.userActivation(code);
		return "/login";
	}

	@GetMapping("/logout")
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout";
	}

}
