package hu.szikorazoltan.controller;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import hu.szikorazoltan.dto.PasswordDto;
import hu.szikorazoltan.entities.Status;
import hu.szikorazoltan.entities.User;
import hu.szikorazoltan.service.UserService;

@Controller
public class UserController {

	private UserService userService;

	@Autowired
	private MessageSource messages;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/profile")
	public String dashboard(Model model) {
		final Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByUsername(loggedInUser.getName());
		model.addAttribute("user", user);
		return "auth/profile";
	}

	@GetMapping("/user/edit")
	public String showUserUpdateForm(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		return "auth/update-user";
	}

	@PostMapping("/user/{id}")
	public String updateUser(@PathVariable("id") long id, @Valid User user,	BindingResult result) {
		final Locale locale = LocaleContextHolder.getLocale();
		if (result.hasErrors()) return "auth/update-user";

		String res = userService.update(user);
		if (res.equals("invalidPassword")) {
			result.rejectValue("password", null, messages.getMessage("user.password.invalid", null, locale));
			return "auth/update-user";
		}

		if (res.equals("alreadyExistEmail")) {
			result.rejectValue("email", null, messages.getMessage("user.email.alreadyExist", null, locale));
			return "auth/update-user";
		}
		return "redirect:/profile";
	}

	@GetMapping("/user/change-password")
	public String showPasswordUpdateForm(Model model) {
		model.addAttribute("newpassword", new PasswordDto());
		return "auth/update-password";
	}

	@PostMapping("/user/password")
	public String updatePassword(final HttpServletRequest request,
			@ModelAttribute("newpassword") @Valid PasswordDto passDto, BindingResult result) {
		final Locale locale = LocaleContextHolder.getLocale();
		final Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByUsername(loggedInUser.getName());
		if (result.hasErrors()) {
			return "auth/update-password";
		}

		if (!userService.checkIfValidOldPassword(user, passDto.getOldPassword())) {
			result.rejectValue("oldPassword", null, messages.getMessage("user.oldPassword.invalid", null, locale));
			return "auth/update-password";
		}

		if (passDto.getOldPassword().equals(passDto.getPassword())) {
			result.rejectValue("password", null, messages.getMessage("user.passwords.same", null, locale));
			return "auth/update-password";
		}

		userService.changeUserPassword(user, passDto.getPassword());
		return "redirect:/profile";
	}

	@GetMapping("/user/status")
	public String showAccountDeletePage(Model model) {
		final Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByUsername(loggedInUser.getName());
		model.addAttribute("user", user);
		return "auth/delete-profile";
	}

	@PostMapping("/user/status/{id}")
	public String setStatusProcessing(@PathVariable("id") long id, @ModelAttribute("user") User user,
			BindingResult result) {
		final Locale locale = LocaleContextHolder.getLocale();
		String res = userService.setStatusPById(id, user.getPassword());
		if (res.equals("invalidPassword")) {
			user.setStatus(Status.A);
			result.rejectValue("password", null, messages.getMessage("user.password.invalid", null, locale));
			return "auth/delete-profile";
		}
		return "redirect:/user/status?success";
	}
}
