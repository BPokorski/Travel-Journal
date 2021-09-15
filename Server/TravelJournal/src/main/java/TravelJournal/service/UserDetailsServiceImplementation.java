package TravelJournal.service;

import TravelJournal.model.user.EnumRole;
import TravelJournal.model.user.Role;
import TravelJournal.model.user.User;
import TravelJournal.payload.request.LoginRequest;
import TravelJournal.payload.request.SignupRequest;
import TravelJournal.payload.response.JwtResponse;
import TravelJournal.payload.response.MessageResponse;
import TravelJournal.repository.user.RoleRepository;
import TravelJournal.repository.user.UserRepository;
import TravelJournal.security.jwt.JwtUtils;
import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService, UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PhotoService photoService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username);
        if (user != null) {
            return UserDetailsImplementation.build(user);
        } else {
            throw new UsernameNotFoundException("Not found user with login: " + username);
        }
    }

    @Override
    public ResponseEntity<?> signUpUser(SignupRequest signUpRequest) throws GeneralSecurityException, IOException {
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
        if(signUpRequest.getPassword().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Password cannot be empty"));
        }
        if(signUpRequest.getEmail().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: E-mail cannot be empty"));
        }
        if(signUpRequest.getLogin().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Login cannot be empty"));
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
        File userFolder = photoService.getFolder(signUpRequest.getLogin(), "root");
        if (userFolder == null) {
            photoService.createFolder(signUpRequest.getLogin(), "root");
        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public ResponseEntity<?> signInUser(LoginRequest loginRequest) {
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
}
