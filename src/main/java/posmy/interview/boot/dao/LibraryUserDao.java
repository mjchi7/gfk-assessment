package posmy.interview.boot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import posmy.interview.boot.data.LibraryUser;

import java.util.Optional;

public interface LibraryUserDao extends JpaRepository<LibraryUser, Long> {

    Optional<LibraryUser> findByUsername(String username);
}
