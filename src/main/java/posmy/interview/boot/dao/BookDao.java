package posmy.interview.boot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import posmy.interview.boot.constant.BookStatus;
import posmy.interview.boot.data.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao extends JpaRepository<Book, Long> {

    Optional<Book> findByName(String name);

    List<Book> findByStatus(BookStatus status);
}
