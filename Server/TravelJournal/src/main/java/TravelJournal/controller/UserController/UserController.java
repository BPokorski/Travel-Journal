package TravelJournal.controller.UserController;

import TravelJournal.service.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/{login}")
public class UserController {
    @Autowired
    UserDetailsServiceImplementation userService;

    @DeleteMapping()
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> deleteUser(@PathVariable String login) throws GeneralSecurityException, IOException {
        userService.deleteUser(login, "Application");
        return ResponseEntity.ok("User deleted successfully");
    }

}
