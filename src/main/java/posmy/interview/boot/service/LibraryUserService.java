package posmy.interview.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import posmy.interview.boot.dao.LibraryUserDao;
import posmy.interview.boot.data.LibraryUser;
import posmy.interview.boot.data.dto.LibraryUserDto;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryUserService {

    @Autowired
    LibraryUserDao userDao;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public List<LibraryUser> retrieveAll() {
        return userDao.findAll();
    }

    public LibraryUserDto createNewUser(LibraryUserDto libraryUserDto) {
        LibraryUser libraryUser = new LibraryUser();
        libraryUser.setUsername(libraryUserDto.getUsername());
        libraryUser.setPassword(bCryptPasswordEncoder.encode(libraryUserDto.getPassword()));
        return libraryUserDto;
    }

    public void delete(Long id) {
        Optional<LibraryUser> user = userDao.findById(id);
        LibraryUser u = user.get();
        userDao.delete(u);
    }
}
