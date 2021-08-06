package TravelJournal.controller.UserController;

import TravelJournal.model.user.EnumRole;
import TravelJournal.model.user.Role;
import TravelJournal.model.user.User;
import TravelJournal.payload.request.LoginRequest;
import TravelJournal.payload.request.SignupRequest;
import TravelJournal.payload.response.JwtResponse;
import TravelJournal.payload.response.MessageResponse;
import TravelJournal.repository.cloudStorage.CloudStorageRepository;
import TravelJournal.repository.user.RoleRepository;
import TravelJournal.repository.user.UserRepository;
import TravelJournal.security.jwt.JwtUtils;
import TravelJournal.service.PhotoServiceImplementation;
import TravelJournal.service.UserDetailsImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    PhotoServiceImplementation photoServiceImplementation;

    @Autowired
    CloudStorageRepository cloudStorageRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        if(userRepository.existsByLogin(loginRequest.getLogin())) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImplementation userDetails = (UserDetailsImplementation) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid user or password"));
        }

    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws IOException, GeneralSecurityException {
        if (userRepository.existsByLogin(signUpRequest.getLogin())) {

            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {

            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getLogin(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(EnumRole.USER);
            if (userRole ==null) {
                throw new RuntimeException("Error: Role is not found");
            }
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {

                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(EnumRole.ADMIN);
                        if (adminRole ==null) {
                            throw new RuntimeException("Error: Role is not found");
                        }
                        roles.add(adminRole);

                        break;

                    default:
                        Role userRole = roleRepository.findByName(EnumRole.USER);
                        if (userRole ==null) {
                            throw new RuntimeException("Error: Role is not found");
                        }
                        roles.add(userRole);
                }
            });
        }
        cloudStorageRepository.createFolder(signUpRequest.getLogin(), "root");
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PutMapping("/signout")
    public ResponseEntity<?> signOut() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
        return ResponseEntity.ok(new MessageResponse("Signed out successfully"));
    }

}
