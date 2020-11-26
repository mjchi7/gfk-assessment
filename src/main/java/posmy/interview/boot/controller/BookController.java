package posmy.interview.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import posmy.interview.boot.constant.Constant;
import posmy.interview.boot.dao.query.BookExample;
import posmy.interview.boot.data.Book;
import posmy.interview.boot.service.BookService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@Secured({Constant.ROLE_LIBRARIAN, Constant.ROLE_MEMBER})
public class BookController {

    private static final String path = "/books";

    @Autowired
    BookService bookService;

    @GetMapping(path)
    public List<Book> retrieve() {
        // TODO: Add filter capability
        return bookService.getAllBooks();
    }

    @PostMapping(path + "/search")
    public List<Book> search(@Valid @RequestBody BookExample query) {
        return bookService.searchBook(query);
    }

    @GetMapping(path + "/{id}")
    public Book retrieveById(@PathVariable Long id) {
        return bookService.getBookbyId(id).get();
    }

    @PostMapping(path)
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(Constant.ROLE_LIBRARIAN)
    public Book create(@Valid @RequestBody Book book) {
        return bookService.createBook(book);
    }

    @PatchMapping(path + "/{id}/borrow")
    @ResponseStatus(HttpStatus.OK)
    @Secured(Constant.ROLE_LIBRARIAN)
    public Book borrow(@PathVariable Long id) {
        return bookService.borrow(id);
    }

    @PatchMapping(path + "/{id}/return")
    @ResponseStatus(HttpStatus.OK)
    @Secured(Constant.ROLE_LIBRARIAN)
    public Book returnBook(@PathVariable Long id) {
        return bookService.returnBook(id);
    }

    @DeleteMapping(path + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured((Constant.ROLE_LIBRARIAN))
    public void delete(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

}
