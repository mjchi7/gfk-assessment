package posmy.interview.boot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import posmy.interview.boot.constant.BookStatus;
import posmy.interview.boot.constant.Constant;
import posmy.interview.boot.dao.BookDao;
import posmy.interview.boot.dao.LibraryUserDao;
import posmy.interview.boot.data.Book;
import posmy.interview.boot.service.BookService;

import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookDao bookDao;

    private final String bookUrl = "/books";

    private final String bookUrlById = bookUrl + "/1";

    private final String bookUrlDelete = bookUrl + "/1";

    private final String bookUrlBorrow_isAvailable = bookUrl + "/1" + "/borrow";

    private final String bookUrlBorrow_isBorrowed = bookUrl + "/2" + "/borrow";

    private final String bookUrlReturn_isAvailable = bookUrl + "/1" + "/return";

    private final String bookUrlReturn_isBorrowed = bookUrl + "/2" + "/return";

    private final String bookUrlSearch = bookUrl + "/search";

    private final MediaType contentType = MediaType.APPLICATION_JSON;

    private final ObjectMapper mapper = new ObjectMapper();

    private List<Book> books;

    private boolean initialized = false;

    private Book bookToBeCreated;

    @BeforeEach
    public void setupMock() {
        if (!initialized) {
            this.books = new ArrayList<>();
            this.books
                    .add(new Book(1L, "Thinking, Fast and Slow",
                            BookStatus.AVAILABLE));
            this.books
                    .add(new Book(2L, "Computer Networking",
                            BookStatus.BORROWED));
            this.initialized = true;
        }
        when(bookDao.findAll()).thenReturn(this.books);
        when(bookDao.findById(1L)).thenReturn(Optional.of(this.books.get(0)));
        when(bookDao.findById(2L)).thenReturn(Optional.of(this.books.get(1)));
        when(bookDao.findAll(Example
                .of(new Book(null, "Computer Networking", null))))
                .thenReturn(Collections.singletonList(this.books.get(1)));
        when(bookDao.findAll(Example.of(new Book(null, null, null))))
                .thenReturn(this.books);
    }

    private Book getBookToBeCreated() {
        if (bookToBeCreated == null) {
            this.bookToBeCreated = new Book();
            this.bookToBeCreated.setName("Book3");
        }
        return this.bookToBeCreated;
    }

    // Tests for BookController#retrieve()
    @Test
    public void getAllBook_isUnauthorized_isAnonymousUser() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.bookUrl).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {})
    public void getAllBook_isUnauthorized_noAuthorities() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.bookUrl).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_MEMBER})
    public void getAllBooks_isOk_isMember() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.bookUrl).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_LIBRARIAN})
    public void getAllBooks_isOk_isLibrarian() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.bookUrl).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // Tests for BookController#retrieveById
    @Test
    public void retrieveById_isUnauthorized_isAnonymousUser() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.bookUrlById).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {})
    public void retrieveById_isUnauthorized_noAuthorities() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.bookUrlById).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_MEMBER})
    public void retrieveById_isOk_isMember() throws Exception {

        Book targetBook = this.books.get(0);

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.bookUrlById).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(targetBook.getId()))
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.name").value(targetBook.getName()))
                .andExpect(jsonPath("$.status").hasJsonPath())
                .andExpect(jsonPath("$.status")
                        .value(targetBook.getStatus().toString()));
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_LIBRARIAN})
    public void retrieveById_isOk_isLibrarian() throws Exception {

        Book targetBook = this.books.get(0);

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.bookUrlById).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(targetBook.getId()))
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.name").value(targetBook.getName()))
                .andExpect(jsonPath("$.status").hasJsonPath())
                .andExpect(jsonPath("$.status")
                        .value(targetBook.getStatus().toString()));
    }

    // Tests for BookController#borrow
    @Test
    public void borrow_isUnauthorized_isAnonymousUser() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(this.bookUrlBorrow_isAvailable)
                .contentType(this.contentType);
        mockMvc.perform(req).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {})
    public void borrow_isForbidden_noAuthorities() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(this.bookUrlReturn_isAvailable)
                .contentType(this.contentType);
        mockMvc.perform(req).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_MEMBER})
    public void borrow_isForbidden_isMember() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(this.bookUrlBorrow_isAvailable)
                .contentType(this.contentType);
        mockMvc.perform(req).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_LIBRARIAN})
    public void borrow_isOk_isLibrarianAndBookAvailable() throws Exception {
        Book targetBook = this.books.get(0);

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(this.bookUrlBorrow_isAvailable)
                .contentType(this.contentType);
        mockMvc.perform(req).andExpect(status().isOk());

        verify(bookDao, times(1)).save(targetBook);
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_LIBRARIAN})
    public void borrow_isBadRequest_isLibrarianAndBookBorrowed() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(this.bookUrlBorrow_isBorrowed)
                .contentType(this.contentType);
        mockMvc.perform(req).andExpect(status().isBadRequest());

        // Make sure no 'save' invoked.
        verify(bookDao, never()).save(any());
    }

    // Tests for BookController#returnBook
    @Test
    public void returnBook_isUnauthorized_isAnonymousUser() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(this.bookUrlReturn_isAvailable)
                .contentType(this.contentType);
        mockMvc.perform(req).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {})
    public void returnBook_isForbidden_noAuthorities() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(this.bookUrlReturn_isAvailable)
                .contentType(this.contentType);
        mockMvc.perform(req).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_MEMBER})
    public void returnBook_isForbidden_isMember() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(this.bookUrlReturn_isAvailable)
                .contentType(this.contentType);
        mockMvc.perform(req).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_LIBRARIAN})
    public void returnBook_isOk_isLibrarianAndBookBorrowed() throws Exception {
        Book targetBook = this.books.get(1);

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(this.bookUrlReturn_isBorrowed)
                .contentType(this.contentType);
        mockMvc.perform(req).andExpect(status().isOk());

        verify(bookDao, times(1)).save(targetBook);
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_LIBRARIAN})
    public void returnBook_isBadRequest_isLibrarianAndBookAvailable() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(this.bookUrlReturn_isAvailable)
                .contentType(this.contentType);
        mockMvc.perform(req).andExpect(status().isBadRequest());

        // Make sure no 'save' invoked.
        verify(bookDao, never()).save(any());
    }

    // Tests for BookController#delete
    @Test
    public void delete_isUnauthorized_isAnonymousUser() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .delete(this.bookUrlDelete).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {})
    public void delete_isUnauthorized_noAuthorities() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .delete(this.bookUrlDelete).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_MEMBER})
    public void delete_isForbidden_isMember() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .delete(this.bookUrlDelete).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_LIBRARIAN})
    public void delete_isOk_isLibrarian() throws Exception {

        Book targetBook = this.books.get(0);

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .delete(this.bookUrlDelete).contentType(this.contentType);
        mockMvc.perform(req)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(bookDao, times(1)).delete(targetBook);
    }

    // Tests for BookController#create
    @Test
    public void create_isUnauthorized_isAnonymousUser() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.bookUrl).contentType(this.contentType)
                .content(mapper.writeValueAsString(getBookToBeCreated()));
        mockMvc.perform(req)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {})
    public void create_isUnauthorized_noAuthorities() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.bookUrl).contentType(this.contentType)
                .content(mapper.writeValueAsString(getBookToBeCreated()));
        mockMvc.perform(req)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_MEMBER})
    public void create_isForbidden_isMember() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.bookUrl).contentType(this.contentType)
                .content(mapper.writeValueAsString(getBookToBeCreated()));
        mockMvc.perform(req)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_LIBRARIAN})
    public void create_isOk_isLibrarian() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.bookUrl).contentType(this.contentType)
                .content(mapper.writeValueAsString(getBookToBeCreated()));
        mockMvc.perform(req)
                .andExpect(status().isCreated());

        verify(bookDao, times(1)).save(getBookToBeCreated());
    }

    @Test
    @WithMockUser(username = "username", password = "password", authorities =
            {Constant.ROLE_LIBRARIAN})
    public void create_isBadRequest_isLibrarianAndBookNameNull() throws Exception {

        Book nullBook = new Book();

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.bookUrl).contentType(this.contentType)
                .content(mapper.writeValueAsString(nullBook));
        mockMvc.perform(req)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details.data[0].field").value("name"));

        verify(bookDao, never()).save(any());
    }

    // Tests for BookController#search
    @Test
    @WithMockUser(authorities = {Constant.ROLE_MEMBER})
    public void search_isOk_isValidBody() throws Exception {

        ObjectNode body = mapper.createObjectNode();
        body.put("name", "Computer Networking");

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.bookUrlSearch).contentType(this.contentType)
                .content(mapper.writeValueAsString(body));

        mockMvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // Tests for BookController#search
    @Test
    @WithMockUser(authorities = {Constant.ROLE_MEMBER})
    public void search_isOk_hasNoBody() throws Exception {

        ObjectNode body = mapper.createObjectNode();

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.bookUrlSearch).contentType(this.contentType)
                .content(mapper.writeValueAsString(body));

        mockMvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // Tests for BookController#search
    @Test
    @WithMockUser(authorities = {Constant.ROLE_MEMBER})
    public void search_isBadRequest_hasInvalidFieldName() throws Exception {

        ObjectNode body = mapper.createObjectNode();
        body.put("invalidField", "invalidValue");

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.bookUrlSearch).contentType(this.contentType)
                .content(mapper.writeValueAsString(body));

        mockMvc.perform(req)
                .andExpect(status().isBadRequest());
    }
}
