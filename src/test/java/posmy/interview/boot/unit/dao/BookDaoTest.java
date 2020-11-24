package posmy.interview.boot.unit.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import posmy.interview.boot.constant.BookStatus;
import posmy.interview.boot.dao.BookDao;
import posmy.interview.boot.data.Book;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BookDaoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookDao bookDao;

    private final static Logger logger = LoggerFactory.getLogger(BookDaoTest.class);

    @Test
    public void findAll_getAll() {
        Book book1 = new Book();
        book1.setName("Book1");
        Book book2 = new Book();
        book2.setName("Book2");
        entityManager.persist(book1);
        entityManager.persist(book2);

        List<Book> books = bookDao.findAll();
        assertEquals(Arrays.asList(book1, book2), books);
    }

    @Test
    public void findById_idExists() {
        Book book1 = new Book();
        book1.setName("Book1");
        Book book2 = new Book();
        book2.setName("Book2");

        Long idBook1 = entityManager.persistAndGetId(book1, Long.class);
        Long idBook2 = entityManager.persistAndGetId(book2, Long.class);

        Optional<Book> fetchedBook1 = bookDao.findById(idBook1);
        assertEquals(book1, fetchedBook1.get());
        Optional<Book> fetchedBook2 = bookDao.findById(idBook2);
        assertEquals(book2, fetchedBook2.get());
    }

    @Test
    public void findById_idNotExists() {
        Book book1 = new Book();
        book1.setName("Book1");
        Book book2 = new Book();
        book2.setName("Book2");
        entityManager.persist(book1);
        entityManager.persist(book2);

        Optional<Book> book = bookDao.findById(99999L);
        assertTrue(book.isEmpty());
        assertThrows(NoSuchElementException.class, () -> book.get());
    }

    @Test
    public void findByBookStatus_statusIsAvailable() {
        Book book1 = new Book();
        book1.setName("Book1");
        Book book2 = new Book();
        book2.setName("Book2");
        book2.setStatus(BookStatus.BORROWED);
        entityManager.persist(book1);
        entityManager.persist(book2);

        List<Book> books = bookDao.findByStatus(BookStatus.AVAILABLE);
        assertTrue(books.contains(book1));
        assertFalse(books.contains(book2));
    }


    @Test
    public void findByBookStatus_statusIsBorrowed() {
        Book book1 = new Book();
        book1.setName("Book1");
        Book book2 = new Book();
        book2.setName("Book2");
        book2.setStatus(BookStatus.BORROWED);
        entityManager.persist(book1);
        entityManager.persist(book2);

        List<Book> books = bookDao.findByStatus(BookStatus.BORROWED);
        assertTrue(books.contains(book2));
        assertFalse(books.contains(book1));
    }
}
