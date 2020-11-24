package posmy.interview.boot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import posmy.interview.boot.data.LibraryUser;
import posmy.interview.boot.data.dto.LibraryUserDto;
import posmy.interview.boot.service.LibraryUserDetailService;

import java.util.List;

import static posmy.interview.boot.constant.Constant.ROLE_LIBRARIAN;
import static posmy.interview.boot.constant.Constant.ROLE_MEMBER;

@RestController
@Secured({ROLE_LIBRARIAN, ROLE_MEMBER})
public class LibraryUserController {

    private final static Logger logger = LoggerFactory.getLogger(LibraryUserController.class);

    private final static String path = "/user";

    @Autowired
    LibraryUserDetailService libraryUserDetailService;

    @GetMapping(path)
    public List<LibraryUser> retrieveAll() {
        return libraryUserDetailService.retrieveAll();
    }

    @PostMapping(value = path, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(ROLE_LIBRARIAN)
    public LibraryUserDto createUser(@RequestBody LibraryUserDto libraryUserDto) {
        return libraryUserDetailService.createNewUser(libraryUserDto);
    }

    @DeleteMapping(value = path + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LibraryUser user = (LibraryUser) auth.getPrincipal();
        if (!user.getId().equals(id) && !auth.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_LIBRARIAN))) {
            throw new AccessDeniedException("Cannot delete account that doesn't belongs to you!");
        }
        libraryUserDetailService.delete(id);
    }

}
