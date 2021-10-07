package TravelJournal.controller.UserController;

import TravelJournal.payload.request.LoginRequest;
import TravelJournal.payload.request.SignupRequest;
import TravelJournal.payload.response.MessageResponse;
import TravelJournal.service.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class AuthController {

    @Autowired
    UserDetailsServiceImplementation userService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        return userService.signInUser(loginRequest);
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws IOException, GeneralSecurityException {

        return userService.signUpUser(signUpRequest, "application");
    }

    @PutMapping("/signout")
    public ResponseEntity<?> signOut() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
        return ResponseEntity.ok(new MessageResponse("Signed out successfully"));
    }

}
