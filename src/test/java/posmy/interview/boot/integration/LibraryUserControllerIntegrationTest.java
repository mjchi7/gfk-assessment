package posmy.interview.boot.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.tomcat.jni.Library;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import posmy.interview.boot.constant.Constant;
import posmy.interview.boot.constant.Message;
import posmy.interview.boot.dao.LibraryUserDao;
import posmy.interview.boot.data.Book;
import posmy.interview.boot.data.LibraryUser;

import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LibraryUserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LibraryUserDao userDao;

    @MockBean
    BCryptPasswordEncoder encoder;

    private final String userUrl = "/user";

    private final String userUrlId1 = userUrl + "/1";

    private final MediaType contentType = MediaType.APPLICATION_JSON;

    private List<LibraryUser> users;

    private boolean isInitialized = false;

    private ObjectMapper mapper = new ObjectMapper();

    private ObjectNode userToBeCreatedComplete;

    private ObjectNode userToBeCreatedMissingConfirmPassword;

    private ObjectNode userToBeCreatedPasswordMismatch;

    private LibraryUser basicUser;

    private LibraryUser memberUser;

    private LibraryUser librarianUser;

    private Logger logger = LoggerFactory
            .getLogger(LibraryUserControllerIntegrationTest.class);

    @BeforeEach
    public void setupMock() {
        if (!isInitialized) {
            this.createUserToBeCreatedComplete();
            this.createUserToBeCreatedMissingConfirmPassword();
            this.createUserToBeCreatedPasswordMismatch();
            this.users = new ArrayList<>();
            this.basicUser = new LibraryUser(1L, "basic", "password",
                    new ArrayList<>());
            this.memberUser = new LibraryUser(2L, "member", "password",
                    Collections
                            .singletonList(Constant.ROLE_MEMBER));
            this.librarianUser = new LibraryUser(3L, "librarian", "password",
                    Collections
                            .singletonList(Constant.ROLE_LIBRARIAN));
            this.users.add(basicUser);
            this.users.add(memberUser);
            this.users.add(librarianUser);
            this.isInitialized = true;
        }
        when(userDao.findAll()).thenReturn(this.users);
        // librarianUser
        when(userDao.findByUsername(librarianUser.getUsername()))
                .thenReturn(Optional
                        .of(new LibraryUser(librarianUser.getId(), librarianUser
                                .getUsername(), librarianUser
                                .getPassword(), librarianUser.getRoles())));
        // memberUser
        when(userDao.findByUsername(memberUser.getUsername()))
                .thenReturn(Optional
                        .of(new LibraryUser(memberUser.getId(), memberUser
                                .getUsername(), memberUser
                                .getPassword(), memberUser.getRoles())));
        // basicUser
        when(userDao.findByUsername(basicUser.getUsername()))
                .thenReturn(Optional
                        .of(new LibraryUser(basicUser.getId(), basicUser
                                .getUsername(), basicUser
                                .getPassword(), basicUser.getRoles())));
        when(userDao.findById(any())).thenAnswer(i -> this.users.stream()
                .filter(user -> user.getId().equals(i.getArgument(0)))
                .findFirst());

        when(encoder.encode(any()))
                .thenAnswer(i -> { System.out.println(i.getArguments()); return
                        (String) i.getArgument(0); });

        when(encoder.matches(any(), any())).thenAnswer(i -> {
            logger.info("Argument 0: " + i.getArgument(0));
            logger.info("Argument 1: " + i.getArgument(1));
            if (i.getArgument(0).equals(i.getArgument(1))) {
                logger.info("Matches");
                return true;
            }
            return false;
        });
    }

    private void createUserToBeCreatedMissingConfirmPassword() {
        this.userToBeCreatedMissingConfirmPassword = mapper.createObjectNode();
        this.userToBeCreatedMissingConfirmPassword = mapper.createObjectNode();
        this.userToBeCreatedMissingConfirmPassword.put("username", "new_user");
        this.userToBeCreatedMissingConfirmPassword.put("password", "password");

        ArrayNode roles = this.userToBeCreatedMissingConfirmPassword.putArray("roles");
        roles.add(Constant.ROLE_LIBRARIAN.toString());
    }

    private void createUserToBeCreatedComplete() {
        this.userToBeCreatedComplete = mapper.createObjectNode();
        this.userToBeCreatedComplete.put("username", "new_user");
        this.userToBeCreatedComplete.put("password", "password");
        this.userToBeCreatedComplete.put("confirmPassword", "password");

        ArrayNode roles = this.userToBeCreatedComplete.putArray("roles");
        roles.add(Constant.ROLE_LIBRARIAN.toString());
    }

    private void createUserToBeCreatedPasswordMismatch() {
        this.userToBeCreatedPasswordMismatch = mapper.createObjectNode();
        this.userToBeCreatedPasswordMismatch.put("username", "new_user");
        this.userToBeCreatedPasswordMismatch.put("password", "password");
        this.userToBeCreatedPasswordMismatch.put("confirmPassword", "mismatch");

        ArrayNode roles = this.userToBeCreatedPasswordMismatch.putArray("roles");
        roles.add(Constant.ROLE_LIBRARIAN.toString());
    }

    private RequestPostProcessor getBasicUser() {
        return SecurityMockMvcRequestPostProcessors
                .httpBasic(this.basicUser.getUsername(), this.basicUser
                        .getPassword());
    }

    private RequestPostProcessor getMemberUser() {
        return SecurityMockMvcRequestPostProcessors
                .httpBasic(this.memberUser.getUsername(), this.memberUser
                        .getPassword());
    }

    private RequestPostProcessor getLibrarianUser() {
        return SecurityMockMvcRequestPostProcessors
                .httpBasic(this.librarianUser.getUsername(), this.librarianUser
                        .getPassword());
    }

    // Tests for LibraryUserController#retrieveAll()
    @Test
    public void getAll_isNotAuthed_thenReturnUnauthorized() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.userUrl).contentType(this.contentType);

        mockMvc.perform(req).andExpect(status().isUnauthorized());
    }

    @Test
    public void getAll_doNotHaveAuthorities_thenReturnForbidden() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.userUrl).contentType(this.contentType)
                .with(this.getBasicUser());

        mockMvc.perform(req).andExpect(status().isForbidden());
    }

    @Test
    public void getAll_isMember_thenReturnOk() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.userUrl).contentType(this.contentType)
                .with(this.getMemberUser());

        mockMvc.perform(req).andExpect(status().isOk());
    }

    @Test
    public void getAll_isLibrarian_thenReturnOk() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .get(this.userUrl).contentType(this.contentType)
                .with(this.getLibrarianUser());

        mockMvc.perform(req).andExpect(status().isOk());
    }

    // Tests for LibraryUserController#createUser()
    @Test
    public void createUser_isNotAuthed_thenReturnUnauthorized() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.userUrl).contentType(this.contentType)
                .content(mapper.writeValueAsString(this.userToBeCreatedComplete));

        mockMvc.perform(req).andExpect(status().isUnauthorized());
        verify(userDao, never()).save(any());
    }

    @Test
    public void createUser_doNotHaveAuthorities_thenReturnForbidden() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.userUrl).contentType(this.contentType)
                .content(mapper.writeValueAsString(this.userToBeCreatedComplete))
                .with(this.getBasicUser());

        mockMvc.perform(req).andExpect(status().isForbidden());
        verify(userDao, never()).save(any());
    }

    @Test
    public void createUser_isMember_thenReturnForbidden() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.userUrl).contentType(this.contentType)
                .content(mapper.writeValueAsString(this.userToBeCreatedComplete))
                .with(this.getMemberUser());

        mockMvc.perform(req).andExpect(status().isForbidden());
        verify(userDao, never()).save(any());
    }

    @Test
    public void createUser_isLibrarian_thenReturnOk() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.userUrl).contentType(this.contentType)
                .content(mapper.writeValueAsString(this.userToBeCreatedComplete))
                .with(this.getLibrarianUser());

        mockMvc.perform(req)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userDao, times(1)).save(any());
    }

    @Test
    public void createUser_isLibrarianPasswordMismatch_thenReturnBadRequest() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(this.userUrl).contentType(this.contentType)
                .content(mapper.writeValueAsString(this.userToBeCreatedPasswordMismatch))
                .with(this.getLibrarianUser());

        mockMvc.perform(req)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Message.PASSWORD_MISMATCH));

        verify(userDao, never()).save(any());
    }

    // Tests for LibraryUserController#delete()
    @Test
    public void delete_isNotAuthed_thenReturnUnauthorized() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .delete(this.userUrl + "/1").contentType(this.contentType)
                .content(mapper.writeValueAsString(this.userToBeCreatedComplete));

        mockMvc.perform(req).andExpect(status().isUnauthorized());
        verify(userDao, never()).save(any());
    }

    @Test
    public void delete_doNotHaveAuthorities_thenReturnForbidden() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .delete(this.userUrl + "/" + this.basicUser.getId())
                .contentType(this.contentType)
                .with(this.getBasicUser());

        mockMvc.perform(req).andExpect(status().isForbidden());
        verify(userDao, never()).save(any());
    }

    @Test
    public void delete_isMember_thenReturnNoContent() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .delete(this.userUrl + "/" + this.memberUser.getId())
                .contentType(this.contentType)
                .content(mapper.writeValueAsString(this.userToBeCreatedComplete))
                .with(this.getMemberUser());

        mockMvc.perform(req).andExpect(status().isNoContent());
        verify(userDao, times(1)).delete(any());
    }

    @Test
    public void delete_isMemberAndNotOwnAccount_thenReturnForbidden() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .delete(this.userUrl + "/" + 999)
                .contentType(this.contentType)
                .content(mapper.writeValueAsString(this.userToBeCreatedComplete))
                .with(this.getMemberUser());

        mockMvc.perform(req).andExpect(status().isForbidden());
        verify(userDao, never()).delete(any());
    }

    @Test
    public void delete_isLibrarian_thenReturnNoContent() throws Exception {
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .delete(this.userUrlId1).contentType(this.contentType)
                .content(mapper.writeValueAsString(this.userToBeCreatedComplete))
                .with(this.getLibrarianUser());

        mockMvc.perform(req)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userDao, times(1)).delete(any());
    }
}
