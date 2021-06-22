package hu.szikorazoltan.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hu.szikorazoltan.entities.Role;
import hu.szikorazoltan.entities.Status;
import hu.szikorazoltan.entities.User;
import hu.szikorazoltan.repo.RoleRepository;
import hu.szikorazoltan.repo.UserRepository;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	private EmailService emailService;

	private static final String USER_ROLE = "ROLE_USER";

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return new UserDetailsImpl(user);
	}

	@Override
	public String registerUser(User userToRegister) {
		Role userRole = roleRepository.findByRole(USER_ROLE);
		if (userRole != null) {
			userToRegister.getRoles().add(userRole);
		} else {
			userToRegister.addRoles(USER_ROLE);
		}

		userToRegister.setEnabled(false);
		String code = generateKey();
		userToRegister.setActivation(code);
		userToRegister.setStatus(Status.A);
		userToRegister.setPassword(passwordEncoder.encode(userToRegister.getPassword()));
		userRepository.save(userToRegister);
		emailService.sendMessage(userToRegister.getEmail(), userToRegister.getFirstName(), code);
		return "ok";
	}

	private String generateKey() {
		Random random = new Random();
		char[] word = new char[16];
		for (int j = 0; j < word.length; j++) {
			word[j] = (char) ('a' + random.nextInt(26));
		}
		return new String(word);
	}

	@Override
	public String userActivation(String code) {
		User user = userRepository.findByActivation(code);
		if (user == null) return "noresult";
		user.setEnabled(true);
		user.setActivation(dateToString());
		userRepository.save(user);
		return "ok";
	}

	private String dateToString() {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		DateFormat df = new SimpleDateFormat(pattern);
		Date today = Calendar.getInstance().getTime();
		String todayAsString = df.format(today);
		return todayAsString;
	}

	@Override
	public List<User> findAll() {
		return (List<User>) userRepository.findAll();
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public String update(@Valid User user) {
		Optional<User> userInDb = userRepository.findById(user.getId());
		if (!(passwordEncoder.matches(user.getPassword(), userInDb.get().getPassword()))) {
			return "invalidPassword";
		}

		User userEmailExist = userRepository.findByEmail(user.getEmail());
		if (!userInDb.get().getEmail().equals(user.getEmail()) && userEmailExist != null) {
			return "alreadyExistEmail";
		}

		user.setPassword(userInDb.get().getPassword());
		user.setRoles(userInDb.get().getRoles());
		user.setStatus(userInDb.get().getStatus());
		user.setEnabled(true); 
		user.setActivation(userInDb.get().getActivation());
		userRepository.save(user);
		return "ok";
	}

	@Override
	public String setStatusPById(long id, String password) {
		Optional<User> currentUserData = userRepository.findById(id);
		if (!(passwordEncoder.matches(password, currentUserData.get().getPassword()))) {
			return "invalidPassword";
		}
		currentUserData.get().setStatus(Status.P);
		userRepository.save(currentUserData.get());
		return "ok";
	}

	@Override
	public void setStatusDeletedById(long id) {
		Optional<User> currentUserData = userRepository.findById(id);
		currentUserData.get().setStatus(Status.D);
		currentUserData.get().setEnabled(false);
		userRepository.save(currentUserData.get());
	}

	@Override
	public void addAdminRoleById(long id) {
		Optional<User> currentUserData = userRepository.findById(id);
		Set<Role> roles = currentUserData.get().getRoles();
		Role adminRole = roleRepository.findByRole("ROLE_ADMIN");
		roles.add(adminRole);
		currentUserData.get().setRoles(roles);
		userRepository.save(currentUserData.get());
	}

	@Override
	public boolean checkIfValidOldPassword(User user, String oldPassword) {
		return passwordEncoder.matches(oldPassword, user.getPassword());
	}

	@Override
	public void changeUserPassword(User user, String password) {
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
	}

	@Override
	public Page<User> findPaginated(Pageable pageable) {
		final List<User> users = findAll();

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<User> list;

		if (users.size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, users.size());
			list = users.subList(startItem, toIndex);
		}
		Page<User> userPage = new PageImpl<User>(list, PageRequest.of(currentPage, pageSize), users.size());
		return userPage;
	}
}
