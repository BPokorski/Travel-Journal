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

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> signUpUser(SignupRequest signUpRequest, String applicationFolder) throws GeneralSecurityException, IOException {
        ResponseEntity<?> result;
        if (userRepository.existsByLogin(signUpRequest.getLogin())) {
            result = ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        } else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            result = ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        } else if (signUpRequest.getPassword().isEmpty()) {
            result = ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Password cannot be empty"));
        } else if (signUpRequest.getEmail().isEmpty()) {
            result = ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: E-mail cannot be empty"));
        } else if (signUpRequest.getLogin().isEmpty()) {
            result = ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Login cannot be empty"));
        } else {// Create new user's account
            User user = new User(signUpRequest.getLogin(),
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()));
            Set<String> strRoles = signUpRequest.getRoles();
            Set<Role> roles = new HashSet<>();
            if (strRoles == null) {
                Role userRole = roleRepository.findByName(EnumRole.USER);
                if (userRole == null) {
                    throw new RuntimeException("Error: Role is not found");
                }
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {

                    switch (role) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(EnumRole.ADMIN);
                            if (adminRole == null) {
                                throw new RuntimeException("Error: Role is not found");
                            }
                            roles.add(adminRole);

                            break;

                        default:
                            Role userRole = roleRepository.findByName(EnumRole.USER);
                            if (userRole == null) {
                                throw new RuntimeException("Error: Role is not found");
                            }
                            roles.add(userRole);
                    }
                });
            }// Create folder in Google Drive for photos storage
            String applicationFolderId = photoService.getFolder(applicationFolder, "root").getId();
            File userFolder = photoService.getFolder(signUpRequest.getLogin(), applicationFolderId);
            if (userFolder == null) {
                photoService.createFolder(signUpRequest.getLogin(), applicationFolderId);
            }
            user.setRoles(roles);
            userRepository.save(user);
            result = ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
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
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid login"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(String login, String applicationFolder) throws GeneralSecurityException, IOException {
        userRepository.deleteByLogin(login);
        photoService.deleteResources(login, applicationFolder);
    }
}
