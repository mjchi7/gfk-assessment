package posmy.interview.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import posmy.interview.boot.constant.BookStatus;
import posmy.interview.boot.dao.BookDao;
import posmy.interview.boot.data.Book;
import posmy.interview.boot.exception.InvalidTransitionException;

import java.util.List;
import java.util.Optional;

import static posmy.interview.boot.constant.Message.INVALID_TRANSIT_IS_BORROWED;
import static posmy.interview.boot.constant.Message.INVALID_TRANSIT_IS_RETURNED;

@Service
@RequiredArgsConstructor
public class BookService {

    // final, so @RequiredArgsConstructor will help us create Constructor that takes
    // BookDao as input
    private final BookDao bookDao;

    public Book createBook(Book book) {
        bookDao.save(book);
        return book;
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
