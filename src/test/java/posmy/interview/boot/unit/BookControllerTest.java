package posmy.interview.boot.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import posmy.interview.boot.constant.BookStatus;
import posmy.interview.boot.constant.Constant;
import posmy.interview.boot.dao.BookDao;
import posmy.interview.boot.data.Book;
import posmy.interview.boot.service.BookService;
import posmy.interview.boot.service.LibraryUserDetailService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest will look for a @SpringBootConfiguration to build an application context
// with which it will used for testing. However, it doesn't seems to provide @AutoConfigureMockMvc (?)
@SpringBootTest
// @AutoConfigureMockMvc is needed to instantiate MockMvc.
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setupMockBeforeEach() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(Long.valueOf(1), "Thinking, Fast and Slow", BookStatus.AVAILABLE));
        books.add(new Book(Long.valueOf(2), "Computer Networking", BookStatus.BORROWED));
        when(bookService.getAllBooks()).thenReturn(books);
    }

    @Test
    @WithMockUser(username = "NO_ROLE_USER", password = "NO_ROLE_PASSWORD", authorities = {})
    void getBooks_isUnauthorized_unauthorized() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "MEMBER_ROLE_USER", password = "MEMBER_ROLE_PASSWORD", authorities = {Constant.ROLE_MEMBER})
    public void getBooks_isOk() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/books").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "NO_ROLE_USER", password = "NO_ROLE_PASSWORD", authorities = {})
    public void createBook_isUnauthorized_NoRoles() throws Exception {

        Book book = new Book();
        book.setName("Practical English Usage");
        book.setStatus(BookStatus.AVAILABLE);

        mockMvc.perform(MockMvcRequestBuilders.post("/books").content(this.mapper.writeValueAsString(book)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "MEMBER_ROLE_USER", password = "MEMBER_ROLE_PASSWORD", authorities = {Constant.ROLE_MEMBER})
    public void createBook_isUnauthorized_MemberRole() throws Exception {

        Book book = new Book();
        book.setName("Practical English Usage");
        book.setStatus(BookStatus.AVAILABLE);

        mockMvc.perform(MockMvcRequestBuilders.post("/books").content(this.mapper.writeValueAsString(book)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "LIBRARIAN_ROLE_USER", password = "LIBRARIAN_ROLE_PASSWORD", authorities = {Constant.ROLE_LIBRARIAN})
    public void createBook_isCreated_LibrarianRole() throws Exception {

        Book book = new Book();
        book.setName("Practical English Usage");
        book.setStatus(BookStatus.AVAILABLE);

        mockMvc.perform(MockMvcRequestBuilders.post("/books").content(this.mapper.writeValueAsString(book)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasProperty("id")))
                .andExpect(jsonPath("$", hasProperty("name")))
                .andExpect(jsonPath("$", hasProperty("status")));
    }
}
