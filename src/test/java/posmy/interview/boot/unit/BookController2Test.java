package posmy.interview.boot.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import posmy.interview.boot.controller.BookController;
import posmy.interview.boot.data.Book;
import posmy.interview.boot.service.BookService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookController2Test extends BaseTest {

    @Mock
    BookService bookService;

    @InjectMocks
    BookController controller;

    @Test
    public void retrieve_isOk() {
        when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

        List<Book> books = controller.retrieve();
        assertEquals(books, Collections.emptyList());
    }

}
