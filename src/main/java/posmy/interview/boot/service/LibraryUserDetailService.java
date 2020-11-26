package posmy.interview.boot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import posmy.interview.boot.constant.Message;
import posmy.interview.boot.dao.LibraryUserDao;
import posmy.interview.boot.data.LibraryUser;
import posmy.interview.boot.data.dto.LibraryUserDto;
import posmy.interview.boot.exception.PasswordMismatchException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
// @RequiredArgsConstructor
public class LibraryUserDetailService implements UserDetailsService {

    @Autowired
    private LibraryUserDao userDao;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Logger logger = LoggerFactory
            .getLogger(LibraryUserDetailService.class);

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        logger.info("In loadUserByUsername");
        Optional<LibraryUser> user = userDao.findByUsername(s);
        if (!user.isPresent()) {
            logger.info("User " + s + " does not exists in database");
            throw new UsernameNotFoundException("User " + s + " not found");
        }
        logger.info("User " + s + " exists in database");
        return user.get();
    }

    public List<LibraryUser> retrieveAll() {
        return userDao.findAll();
    }

    public LibraryUserDto createNewUser(LibraryUserDto libraryUserDto) {
        if (!libraryUserDto.getPassword().equals(libraryUserDto.getConfirmPassword())) {
            logger.info("Password mismatch");
            logger.info("password: " + libraryUserDto.getPassword());
            logger.info("confirmPassword: " + libraryUserDto.getConfirmPassword());
            throw new PasswordMismatchException(Message.PASSWORD_MISMATCH);
        }
        LibraryUser libraryUser = new LibraryUser();
        libraryUser.setUsername(libraryUserDto.getUsername());
        libraryUser.setPassword(bCryptPasswordEncoder
                .encode(libraryUserDto.getPassword()));
        libraryUser.setRoles(libraryUserDto.getRoles());
        userDao.save(libraryUser);
        return libraryUserDto;
    }

    public void delete(Long id) {
        Optional<LibraryUser> user = userDao.findById(id);
        LibraryUser u = user.get();
        userDao.delete(u);
    }

    @PostConstruct
    public void bootstrapRootUser() {
        logger.info("Bootstrapping root user");
        Optional<LibraryUser> rootUser = userDao.findByUsername("root");
        if (rootUser.isEmpty()) {
            logger.info("root user does not exists. Bootstrapping one...");
            LibraryUser newRootUser = new LibraryUser();
            newRootUser.setUsername("root");
            newRootUser.setPassword(bCryptPasswordEncoder.encode("root"));
            newRootUser.setRoles(new ArrayList<>());
            userDao.save(newRootUser);
        } else {
            logger.info("Bootstrapping root user skipped because username " +
                    "'root' already exists");
        }
    }
}
