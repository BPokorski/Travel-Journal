package TravelJournal.service;

import TravelJournal.payload.request.LoginRequest;
import TravelJournal.payload.request.SignupRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface UserService {
    ResponseEntity<?> signUpUser(SignupRequest signupRequest) throws GeneralSecurityException, IOException;
    ResponseEntity<?> signInUser(LoginRequest loginRequest);
}
