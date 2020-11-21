package posmy.interview.boot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import posmy.interview.boot.constant.Constant;
import posmy.interview.boot.dao.LibraryUserDao;
import posmy.interview.boot.data.LibraryUser;
import posmy.interview.boot.data.dto.LibraryUserDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private LibraryUserDao userDao;

    private final String userUrl = "/user";

    private final MediaType contentType = MediaType.APPLICATION_JSON;

    private final ObjectMapper mapper = new ObjectMapper();

    private final String LIBRARIAN_USERNAME = "LIBRARIAN";
    private final String LIBRARIAN_PASSWORD = "LIBRARIAN";
    private final List<String> LIBRARIAN_AUTHORITIES = Collections
            .singletonList(Constant.ROLE_LIBRARIAN);

    private final String MEMBER_USERNAME = "MEMBER";
    private final String MEMBER_PASSWORD = "MEMBER";
    private final List<String> MEMBER_AUTHORITIES = Collections
            .singletonList(Constant.ROLE_MEMBER);

    private final String ANNO_USER = "ANNO";
    private final String ANNO_PASSWORD = "ANNO";
    private final List<String> ANNO_AUTHORITIES = Collections.emptyList();

    private LibraryUserDto userToBeCreated;

    @BeforeEach
    public void setupBeforeEachTest() {
        LibraryUser librarianUser = new LibraryUser((long) 1,
                LIBRARIAN_USERNAME, encoder
                .encode(LIBRARIAN_PASSWORD), LIBRARIAN_AUTHORITIES);
        LibraryUser memberUser = new LibraryUser((long) 2, MEMBER_USERNAME,
                encoder
                        .encode(MEMBER_PASSWORD), MEMBER_AUTHORITIES);
        LibraryUser annoUser = new LibraryUser((long) 3, ANNO_USER, encoder
                .encode(ANNO_PASSWORD), ANNO_AUTHORITIES);

        userDao.save(librarianUser);
        userDao.save(memberUser);
        userDao.save(annoUser);

        this.userToBeCreated = new LibraryUserDto("user", "username",
                Collections
                        .emptyList());
    }

    @Test
    public void createUser_isOk_WithLibrarian() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(this.userUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userToBeCreated))
                .with(SecurityMockMvcRequestPostProcessors
                        .httpBasic(LIBRARIAN_USERNAME, LIBRARIAN_PASSWORD)))
                .andExpect(status().isCreated());
    }

    @Test
    public void createUser_isAccessDenied_WithMember() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(this.userUrl)
                .contentType(contentType)
                .content(mapper.writeValueAsString(this.userToBeCreated))
                .with(SecurityMockMvcRequestPostProcessors
                        .httpBasic(this.MEMBER_USERNAME, this.MEMBER_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createUser_isAccessDenied_WithNoRoles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(this.userUrl)
                .contentType(this.contentType)
                .content(mapper.writeValueAsString(this.userToBeCreated))
                .with(SecurityMockMvcRequestPostProcessors
                        .httpBasic(this.ANNO_USER, this.ANNO_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createUser_isUnauthorized_NoCreds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(this.userUrl)
                .contentType(this.contentType)
                .content(mapper.writeValueAsString(this.userToBeCreated)))
                .andExpect(status().isUnauthorized());
    }

}
