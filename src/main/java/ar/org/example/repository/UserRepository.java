package ar.org.example.repository;

import ar.org.example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the User entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);
}
