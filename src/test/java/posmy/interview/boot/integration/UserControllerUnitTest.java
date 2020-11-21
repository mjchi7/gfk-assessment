package posmy.interview.boot.integration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import posmy.interview.boot.dao.LibraryUserDao;
import posmy.interview.boot.data.LibraryUser;
import posmy.interview.boot.data.dto.LibraryUserDto;
import posmy.interview.boot.service.LibraryUserDetailService;
import posmy.interview.boot.unit.BaseTest;

// Import MockMvcResultMatchers for "status()"
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerUnitTest extends BaseTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    BCryptPasswordEncoder encoder;

    @MockBean
    LibraryUserDao libraryUserDao;

    private static final String userUrl = "/api/user";

    private String payload;

    private ObjectMapper objectMapper = new ObjectMapper();
    

    @BeforeAll
    public void setupTest() throws Exception {
        // setup payload
        LibraryUserDto libraryUserDto = new LibraryUserDto();
        libraryUserDto.setUsername("admin");
        libraryUserDto.setPassword("admin");
        libraryUserDto.setRoles(Arrays.asList(new String[]{"MEMBER"}));
        this.payload = objectMapper.writer().withDefaultPrettyPrinter()
                .writeValueAsString(libraryUserDto);

        // setup NO_ROLE_USER

    }

    @Test
    public void create_AccessDenied_NoRoles() throws Exception {
        String NO_ROLE_USERNAME = "NO_ROLE_USERNAME";
        String NO_ROLE_PASSWORD = "NO_ROLE_PASSWORD";
        List<String> NO_ROLE_ROLES = new ArrayList<>();

        when(libraryUserDao.findByUsername(NO_ROLE_USERNAME))
                .thenReturn(Optional.of(new LibraryUser(Long.valueOf(1),
                        NO_ROLE_USERNAME, encoder
                        .encode(NO_ROLE_PASSWORD), NO_ROLE_ROLES)));

        mockMvc.perform(MockMvcRequestBuilders.post(this.userUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.payload)
                .with(SecurityMockMvcRequestPostProcessors
                        .httpBasic(NO_ROLE_USERNAME, NO_ROLE_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void create_AccessDenied_MemberRole() throws Exception {
        String MEMBER_USERNAME = "MEMBER_USERNAME";
        String MEMBER_PWD = "MEMBER_PWD";
        List<String> MEMBER_ROLES = Arrays.asList(new String[]{"ROLE_MEMBER"});

        when(libraryUserDao.findByUsername(MEMBER_USERNAME)).thenReturn(Optional
                .of(new LibraryUser(Long.valueOf(1), MEMBER_USERNAME, encoder
                        .encode(MEMBER_PWD), MEMBER_ROLES)));

        mockMvc.perform(MockMvcRequestBuilders.post(userUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.payload)
                .with(SecurityMockMvcRequestPostProcessors
                        .httpBasic(MEMBER_USERNAME, MEMBER_PWD)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void create_Ok_NewUser() throws Exception {
        String url = "/api/user";

        String LIBRARIAN_USERNAME = "LIBRARIAN_USERNAME";
        String LIBRARIAN_PWD = "LIBRARIAN_USERNAME";
        List<String> LIBRARIAN_ROLES = Arrays
                .asList(new String[]{"ROLE_LIBRARIAN"});

        when(libraryUserDao.findByUsername(LIBRARIAN_USERNAME))
                .thenReturn(Optional.of(new LibraryUser(Long.valueOf(1),
                        LIBRARIAN_PWD, encoder
                        .encode(LIBRARIAN_PWD), LIBRARIAN_ROLES)));

        mockMvc.perform(
                MockMvcRequestBuilders.post(this.userUrl)
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(
                                LIBRARIAN_USERNAME, LIBRARIAN_PWD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.payload)).andExpect(status().isCreated());
    }

    @Test
    public void delete_AccessDenied_NoRoles() {
        String NO_ROLES_USERNAME = "NO_ROLES_USERNAME";
        String NO_ROLES_PWD = "NO_ROLES_PWD";
        List<String> NO_ROLES_ROLES = Arrays.asList(new String[]{""});

        when(libraryUserDao.findByUsername(NO_ROLES_USERNAME));
    }
}
