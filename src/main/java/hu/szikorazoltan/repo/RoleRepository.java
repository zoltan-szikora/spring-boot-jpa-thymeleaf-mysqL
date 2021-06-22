package hu.szikorazoltan.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.szikorazoltan.entities.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

	Role findByRole(String role);
}
