package posmy.interview.boot.unit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import posmy.interview.boot.constant.BookStatus;
import posmy.interview.boot.constant.Constant;
import posmy.interview.boot.constant.Message;
import posmy.interview.boot.dao.BookDao;
import posmy.interview.boot.dao.query.BookExample;
import posmy.interview.boot.data.Book;
import posmy.interview.boot.exception.InvalidPayloadException;
import posmy.interview.boot.exception.InvalidTransitionException;
import posmy.interview.boot.service.BookService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookServiceTest extends BaseTest {

    @Mock
    BookDao bookDao;

    @InjectMocks
    BookService bookService;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    private List<Book> mockedBooks;

    @BeforeEach
    public void setupMock() {
        this.mockedBooks = new ArrayList<>();
        this.mockedBooks
                .add(new Book(1L, "Thinking, Fast and Slow",
                        BookStatus.AVAILABLE));
        this.mockedBooks
                .add(new Book(2L, "Computer Networking", BookStatus.BORROWED));
    }

    @Test
    public void getAllBooks_isOk() {
        when(bookDao.findAll()).thenReturn(this.mockedBooks);
        List<Book> books = bookService.getAllBooks();
        assertEquals(books, mockedBooks);
    }

    @Test
    public void getBookbyId_isOk() {
        when(bookDao.findById(1L)).thenReturn(Optional
                .of(this.mockedBooks.get(0)));
        Book book = bookService.getBookbyId(1L).get();
        assertEquals(book, mockedBooks.get(0));
    }

    @Test
    public void getBookById_notExist() {
        when(bookDao.findById(2L)).thenReturn(Optional.empty());
        NoSuchElementException exp =
                assertThrows(NoSuchElementException.class, () -> bookService
                        .getBookbyId((long) 2).get());
    }

    @Test
    public void borrow_isOk() {
        Book bookUnderTest = this.mockedBooks.get(0);
        when(bookDao.findById(1L)).thenReturn((Optional.of(bookUnderTest)));
        bookService.borrow(1L);
        verify(bookDao, times(1)).save(bookCaptor.capture());
        assertEquals(bookCaptor.getValue().getStatus(), BookStatus.BORROWED);
    }

    @Test
    public void borrow_isInvalidTransition_BookIsBorrowed() {
        Book bookUnderTest = this.mockedBooks.get(1);
        when(bookDao.findById(1L)).thenReturn(Optional.of(bookUnderTest));
        InvalidTransitionException exp =
                assertThrows(InvalidTransitionException.class, () -> bookService
                        .borrow(1L));
        assertTrue(exp.getMessage()
                .contains(Message.INVALID_TRANSIT_IS_BORROWED));
    }

    @Test
    public void return_isOk() {
        Book bookUnderTest = this.mockedBooks.get(1);
        when(bookDao.findById(1L)).thenReturn(Optional.of(bookUnderTest));
        bookService.returnBook(1L);
        verify(bookDao, times(1)).save(bookCaptor.capture());
        assertEquals(bookCaptor.getValue().getStatus(), BookStatus.AVAILABLE);
    }

    @Test
    public void return_isInvalidTransition_BookIsAvailable() {
        Book bookUnderTest = this.mockedBooks.get(0);
        when(bookDao.findById(1L)).thenReturn(Optional.of(bookUnderTest));

        InvalidTransitionException exp =
                assertThrows(InvalidTransitionException.class, () -> bookService
                        .returnBook(1L));
        assertTrue(exp.getMessage()
                .contains(Message.INVALID_TRANSIT_IS_RETURNED));
    }

    @Test
    public void create_isOk() {
        Book newBook = new Book();
        newBook.setName("Extreme Economies");
        bookService.createBook(newBook);

        verify(bookDao, times(1)).save(bookCaptor.capture());
        assertEquals(bookCaptor.getValue(), newBook);
    }

    @Test
    public void delete_isOk() {
        Book bookUnderTest = this.mockedBooks.get(0);
        when(bookDao.findById(1L)).thenReturn(Optional
                .ofNullable(bookUnderTest));
        bookService.deleteBook(1L);

        verify(bookDao, times(1)).delete(bookCaptor.capture());
        assertEquals(bookCaptor.getValue(), bookUnderTest);
    }

    @Test
    public void delete_isNotFound_BookNotExists() {
        when(bookDao.findById(1L)).thenReturn(Optional.ofNullable(null));
        assertThrows(NoSuchElementException.class, () -> bookService
                .deleteBook(1L));
    }

    /**
     * @Test public void search_isInvalidField_queryWithInvalidField() {
     * assertThrows(InvalidPayloadException.class, () -> bookService
     * .searchBook(Map .of("invalidField", "invalidValue"))); }
     */

    @Test
    public void search_shouldGetAll_emptyQuery() {
        when(bookDao.findAll(Example.of(new Book(null, null, null))))
                .thenReturn(this.mockedBooks);
        List<Book> returnedBooks = bookService
                .searchBook(new BookExample(null, null, null));

        assertEquals(this.mockedBooks, returnedBooks);
    }

    @Test
    public void search_shouldGetByName_queryWithName() {
        Book mockedBookList = this.mockedBooks.get(1);
        when(bookDao.findAll(Example
                .of(new Book(null, "Computer Networking", null))))
                .thenReturn(Collections.singletonList(mockedBookList));
        List<Book> returnedBooks = bookService
                .searchBook(new BookExample(null, "Computer Networking", null));

        assertEquals(Collections.singletonList(mockedBookList), returnedBooks);
    }

    @Test
    public void search_shouldGetByStatus_queryWithStatus() {
        Book mockedBookList = this.mockedBooks.get(1);
        when(bookDao.findAll(Example
                .of(new Book(null, null, BookStatus.BORROWED))))
                .thenReturn(Collections.singletonList(mockedBookList));
        List<Book> returnedBooks = bookService
                .searchBook(new BookExample(null, null, BookStatus.BORROWED));

        assertEquals(Collections.singletonList(mockedBookList), returnedBooks);
    }
}
