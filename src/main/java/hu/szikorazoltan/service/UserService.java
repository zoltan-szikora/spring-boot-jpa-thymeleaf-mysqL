package hu.szikorazoltan.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.szikorazoltan.entities.User;

public interface UserService {

	User findByEmail(String email);

	String registerUser(User user);

	String userActivation(String code);

	User findByUsername(String username);

	List<User> findAll();

	Optional<User> findById(Long id);

	String update(@Valid User user);

	String setStatusPById(long id, String password);

	void setStatusDeletedById(long id);

	void addAdminRoleById(long id);

	boolean checkIfValidOldPassword(User user, String password);

	void changeUserPassword(User user, String password);

	Page<User> findPaginated(Pageable pageable);

}
