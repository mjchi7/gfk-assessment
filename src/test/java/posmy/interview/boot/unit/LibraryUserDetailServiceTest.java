package posmy.interview.boot.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import posmy.interview.boot.constant.Constant;
import posmy.interview.boot.dao.LibraryUserDao;
import posmy.interview.boot.data.LibraryUser;
import posmy.interview.boot.data.dto.LibraryUserDto;
import posmy.interview.boot.service.LibraryUserDetailService;

// Import jupiter api for assertThrow;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import static org.mockito.Mockito.*;

public class LibraryUserDetailServiceTest extends BaseTest {

    String INVALID_USERNAME = "INVALID_USER";

    String VALID_USERNAME = "VALID_USER";

    List<LibraryUser> users;

    @Captor
    private ArgumentCaptor<LibraryUser> userCaptor;

    @Mock
    LibraryUserDao libraryUserDao;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    LibraryUserDetailService libraryUserDetailService;

    @BeforeEach
    public void setup() {
        this.users = new ArrayList<>();
        this.users.add(new LibraryUser(1L, "user1", "user1", Collections
                .emptyList()));
        this.users.add(new LibraryUser(2L, "user2", "user2", Collections
                .emptyList()));
    }

    @Test
    public void loadUserByUsername_throwsException_invalidUser() {
        when(libraryUserDao.findByUsername(INVALID_USERNAME))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class,
                () -> libraryUserDetailService
                        .loadUserByUsername(INVALID_USERNAME));
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage()
                .contains("User " + INVALID_USERNAME + " not found"));
    }

    @Test
    public void loadUserByUsername_hasUser_validUser() {
        LibraryUser userUnderTest = new LibraryUser(1L, VALID_USERNAME, "",
                new ArrayList<>());

        when(libraryUserDao.findByUsername(VALID_USERNAME))
                .thenReturn(Optional.of(userUnderTest));
        UserDetails user = libraryUserDetailService
                .loadUserByUsername(VALID_USERNAME);

        verify(libraryUserDao, times(1)).findByUsername(VALID_USERNAME);
        assertNotNull(user);
        assertEquals(userUnderTest, user);
    }

    @Test
    public void retrieveAll_isOk() {
        when(libraryUserDao.findAll()).thenReturn(this.users);

        List<LibraryUser> fetchedUsers = libraryUserDetailService.retrieveAll();

        assertEquals(this.users, fetchedUsers);
        verify(libraryUserDao, times(1)).findAll();
    }

    @Test
    public void delete_isOk() {
        LibraryUser userUnderTest = this.users.get(0);
        when(libraryUserDao.findById(1L)).thenReturn(Optional
                .of(userUnderTest));
        libraryUserDetailService.delete(1L);

        verify(libraryUserDao, times(1)).delete(userUnderTest);
    }

    @Test
    public void delete_NoSuchElementException_UserNotExists() {
        when(libraryUserDao.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> libraryUserDetailService
                        .delete(1L));
    }

    @Test
    public void create_isOk() {
        String password = "user1";
        String mockedEncPwd = "<encoded>" + password;
        when(bCryptPasswordEncoder.encode(password)).thenReturn(mockedEncPwd);
        LibraryUserDto libraryUserDto = new LibraryUserDto();
        libraryUserDto.setUsername("user1");
        libraryUserDto.setPassword(password);
        libraryUserDto.setRoles(Arrays
                .asList(Constant.ROLE_LIBRARIAN, Constant.ROLE_MEMBER));
        libraryUserDetailService.createNewUser(libraryUserDto);

        verify(libraryUserDao, times(1)).save(userCaptor.capture());
        LibraryUser userBeingSaved = userCaptor.getValue();
        assertEquals(libraryUserDto.getUsername(), userBeingSaved
                .getUsername());
        assertEquals(mockedEncPwd, userBeingSaved
                .getPassword());
        assertEquals(libraryUserDto.getRoles(), userBeingSaved.getRoles());
    }
}
