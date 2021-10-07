package TravelJournal.service;

import TravelJournal.payload.request.LoginRequest;
import TravelJournal.payload.request.SignupRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface UserService {
    /**
     * Register new user with given credentials.
     * Check if they are valid.
     * Create folder for storing images in Google Drive.
     * @param signUpRequest - Login, e-mail and password of new user.
     * @param applicationFolder - Folder in which create User Folder. "Application" is used basically.
     * @return ResponseEntity with status code 200 if successfully registered user, 400 status code otherwise.
     * @throws GeneralSecurityException - When authentication fails to Google Drive API.
     * @throws IOException - When something goes wrong with Google Drive.
     */
    ResponseEntity<?> signUpUser(SignupRequest signUpRequest, String applicationFolder) throws GeneralSecurityException, IOException;
    /**
     * Sign in User with given login and password. Check if given credentials are valid.
     * @param loginRequest - Login and password of the user, used for authentication.
     * @return ResponseEntity with status code 200 if successfully signed in, 400 status code otherwise.
     */
    ResponseEntity<?> signInUser(LoginRequest loginRequest);

    /**
     * Delete given user and all its resources.
     * @param login - User login.
     * @param applicationFolder - Folder in which User Folder was created. "Application" is used basically.
     * @throws GeneralSecurityException - When authentication fails to Google Drive API.
     * @throws IOException - When something goes wrong with Google Drive.
     */
    void deleteUser(String login, String applicationFolder) throws GeneralSecurityException, IOException;
}
