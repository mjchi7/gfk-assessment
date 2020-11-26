package posmy.interview.boot.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import posmy.interview.boot.constant.BookStatus;
import posmy.interview.boot.constant.Message;
import posmy.interview.boot.dao.BookDao;
import posmy.interview.boot.dao.query.BookExample;
import posmy.interview.boot.data.Book;
import posmy.interview.boot.exception.InvalidPayloadException;
import posmy.interview.boot.exception.InvalidTransitionException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static posmy.interview.boot.constant.Message.INVALID_TRANSIT_IS_BORROWED;
import static posmy.interview.boot.constant.Message.INVALID_TRANSIT_IS_RETURNED;

@Service
@RequiredArgsConstructor
public class BookService {

    // final, so @RequiredArgsConstructor will help us create Constructor
    // that takes
    // BookDao as input
    private final BookDao bookDao;

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public Book createBook(Book book) {
        bookDao.save(book);
        return book;
    }

    public List<Book> searchBook(BookExample query) {
        Book bookExample = new Book(query.getId(), query.getName(), query.getStatus());
        /**
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            try {
                Field entityField = Book.class
                        .getDeclaredField(entry.getKey());
                // Note: setAccessible only affects the single field instance
                // . See https://stackoverflow
                // .com/questions/10638826/java-reflection-impact-of
                // -setaccessibletrue
                entityField.setAccessible(true);
                Class<?> type = entityField.getType();
                if (type.isEnum()) {
                    Method valueOf = entityField.getType().getMethod("valueOf", String.class);

                }
                entityField.set(bookExample, entry.getValue());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new InvalidPayloadException(Message.INVALID_PAYLOAD_EXCEPTION);
            }
        }
         */

        logger.info("The query: " + query);
        logger.info("Example of book to be queried: " + bookExample);
        return bookDao.findAll(Example.of(bookExample));
    }

    public void deleteBook(Long id) {
        Optional<Book> book = bookDao.findById(id);
        Book b = book.get();
        bookDao.delete(b);
    }

    public Book updateBook(Book book) {
        return book;
    }

    public List<Book> getAllBooks() {
        List<Book> books = bookDao.findAll();
        return books;
    }

    public Optional<Book> getBookbyId(Long id) {
        Optional<Book> book = bookDao.findById(id);
        return book;
    }

    public Book borrow(Long id) {
        Optional<Book> book = bookDao.findById(id);
        Book b = book.get();
        if (b.getStatus() == BookStatus.BORROWED) {
            throw new InvalidTransitionException(INVALID_TRANSIT_IS_BORROWED);
        }
        b.setStatus(BookStatus.BORROWED);
        bookDao.save(b);
        return b;
    }

    public Book returnBook(Long id) {
        Optional<Book> book = bookDao.findById(id);
        Book b = book.get();
        if (b.getStatus() == BookStatus.AVAILABLE) {
            throw new InvalidTransitionException(INVALID_TRANSIT_IS_RETURNED);
        }
        b.setStatus(BookStatus.AVAILABLE);
        bookDao.save(b);
        return b;
    }

}
