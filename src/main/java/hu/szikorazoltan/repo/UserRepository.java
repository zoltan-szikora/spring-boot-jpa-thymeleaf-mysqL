package hu.szikorazoltan.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.szikorazoltan.entities.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	User findByEmail(String email);

	User findByUsername(String username);

	User findByActivation(String code);

	List<User> findAll();

	Optional<User> findById(long id);
}
