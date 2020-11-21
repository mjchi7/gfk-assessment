package posmy.interview.boot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import posmy.interview.boot.constant.Constant;
import posmy.interview.boot.dao.LibraryUserDao;
import posmy.interview.boot.data.LibraryUser;
import posmy.interview.boot.data.dto.LibraryUserDto;
import posmy.interview.boot.service.LibraryUserDetailService;
import posmy.interview.boot.service.LibraryUserService;

import java.util.List;

import static posmy.interview.boot.constant.Constant.ROLE_LIBRARIAN;
import static posmy.interview.boot.constant.Constant.ROLE_MEMBER;

@RestController
@Secured({ROLE_LIBRARIAN, ROLE_MEMBER})
public class LibraryUserController {

    private final static Logger logger = LoggerFactory.getLogger(LibraryUserController.class);

    private final static String path = "/user";

    /** The following line causes circular dependencies between
     *
     * +->-+
     * |   LibraryUserDetailService
     * |   |
     * |   WebSecurityConfiguration
     * +-<-+
     *
     * Why? @TODO: Investigate
     *
    @Autowired
    LibraryUserDetailService libraryUserDetailsService;
     **/
    @Autowired
    LibraryUserService libraryUserService;

    @GetMapping(path)
    public List<LibraryUser> retrieveAll() {
        return libraryUserService.retrieveAll();
    }

    @PostMapping(value = path, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(ROLE_LIBRARIAN)
    public LibraryUserDto createUser(@RequestBody LibraryUserDto libraryUserDto) {
        return libraryUserService.createNewUser(libraryUserDto);
    }

    @DeleteMapping(value = path)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LibraryUser user = (LibraryUser) auth.getPrincipal();
        if (!user.getId().equals(id) && !auth.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_LIBRARIAN))) {
            throw new AccessDeniedException("Cannot delete account that doesn't belongs to you!");
        }
        libraryUserService.delete(id);
    }

}
