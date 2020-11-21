package posmy.interview.boot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import posmy.interview.boot.data.Book;

import java.util.Optional;

public interface BookDao extends JpaRepository<Book, Long> {

    Optional<Book> findByName(String name);
}
