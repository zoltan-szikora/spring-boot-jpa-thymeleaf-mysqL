package hu.szikorazoltan.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hu.szikorazoltan.entities.User;
import hu.szikorazoltan.service.UserService;

@Controller
public class AdminController {

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/users")
	public String listUsersPaginate(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		final int currentPage = page.orElse(1);
		final int pageSize = size.orElse(5);

		Page<User> usersPage = userService.findPaginated(PageRequest.of(currentPage - 1, pageSize));
		model.addAttribute("usersPage", usersPage);

		int totalPages = usersPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
		return "auth/listUsers";
	}

	@GetMapping("/users/details/{id}")
	public String showUserdetails(@PathVariable("id") long id, HttpServletResponse response, Model model) {
		User user = userService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		model.addAttribute("user", user);
		return "auth/user";
	}

	@PostMapping("/users/status/{id}")
	public String setStatusDeleted(@PathVariable("id") long id) {
		userService.setStatusDeletedById(id);
		return "redirect:/users";
	}

	@PostMapping("/users/role/{id}")
	public String addAdminRole(@PathVariable("id") long id) {
		userService.addAdminRoleById(id);
		return "redirect:/users";
	}

}
