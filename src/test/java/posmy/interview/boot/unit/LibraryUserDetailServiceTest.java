package posmy.interview.boot.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import posmy.interview.boot.dao.LibraryUserDao;
import posmy.interview.boot.data.LibraryUser;
import posmy.interview.boot.service.LibraryUserDetailService;

// Import jupiter api for assertThrow;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibraryUserDetailServiceTest {

    String INVALID_USERNAME = "INVALID_USER";

    String VALID_USERNAME = "VALID_USER";

    @Mock
    LibraryUserDao libraryUserDao;

    @InjectMocks
    LibraryUserDetailService libraryUserDetailService;

    @Test
    public void loadUserByUsername_throwsException_invalidUser() {
        when(libraryUserDao.findByUsername(INVALID_USERNAME)).thenReturn(Optional.empty());

        assertNotNull(libraryUserDetailService);
        Exception exception = assertThrows(UsernameNotFoundException.class,
                () -> libraryUserDetailService.loadUserByUsername(INVALID_USERNAME));
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("User " + INVALID_USERNAME + " not found"));
    }

    @Test
    public void loadUserByUsername_hasUser_validUser() {
        LibraryUser userUnderTest = new LibraryUser(Long.valueOf(1), VALID_USERNAME, "", new ArrayList<>());
        when(libraryUserDao.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(userUnderTest));
        UserDetails user = libraryUserDetailService.loadUserByUsername(VALID_USERNAME);
        assertNotNull(user);
        assertEquals(userUnderTest, user);
    }
}
