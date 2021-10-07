package TravelJournal.service;

import TravelJournal.payload.request.LoginRequest;
import TravelJournal.payload.request.SignupRequest;
import TravelJournal.repository.cloudStorage.GoogleDriveRepository;
import TravelJournal.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(properties = {"spring.data.mongodb.database=TestJournalDiary"})
@AutoConfigureDataMongo
public class UserDetailsServiceImplementationTest {
    @Autowired
    UserService userService;
    @Autowired
    GoogleDriveRepository cloudStorage;
    @Autowired
    UserRepository userRepository;

    private String rootFolderId = "";

    @Test
    @DisplayName("Sign in to not existing account should failed")
    public void testSignInUserNotExistingAccount() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("TestUser");
        loginRequest.setPassword("12345");
        int expectedStatusCode = 400;

        ResponseEntity<?> response = userService.signInUser(loginRequest);
        assertEquals(expectedStatusCode,
                response.getStatusCodeValue(),
                "Should not log in to not existing account");
    }

    @Test
    @DisplayName("Login should not be empty")
    public void testSignUpUserWithEmptyLogin() throws GeneralSecurityException, IOException {
        SignupRequest signUpTestUser = new SignupRequest();
        signUpTestUser.setLogin("");
        signUpTestUser.setPassword("1234");
        signUpTestUser.setEmail("TestMail@mail.com");
        int expectedStatusCode = 400;

        ResponseEntity<?> response = userService.signUpUser(signUpTestUser, "Test");

        assertEquals(expectedStatusCode,
                response.getStatusCodeValue(),
                "Should not create account with empty login");
    }

    @Test
    @DisplayName("Password should not be empty")
    public void testSignUpUserWithEmptyPassword() throws GeneralSecurityException, IOException {
        SignupRequest signUpTestUser = new SignupRequest();
        signUpTestUser.setLogin("TestUser");
        signUpTestUser.setPassword("");
        signUpTestUser.setEmail("TestMail@mail.com");
        int expectedStatusCode = 400;

        ResponseEntity<?> response = userService.signUpUser(signUpTestUser, "Test");

        assertEquals(expectedStatusCode,
                response.getStatusCodeValue(),
                "Should not create account with empty Password");
    }

    @Test
    @DisplayName("E-mail should not be empty")
    public void testSignUpUserWithEmptyEmail() throws GeneralSecurityException, IOException {
        SignupRequest signUpTestUser = new SignupRequest();
        signUpTestUser.setLogin("TestUser");
        signUpTestUser.setPassword("12345");
        signUpTestUser.setEmail("");
        int expectedStatusCode = 400;

        ResponseEntity<?> response = userService.signUpUser(signUpTestUser, "Test");

        assertEquals(expectedStatusCode,
                response.getStatusCodeValue(),
                "Should not create account with empty Email");
    }

    @Nested
    class UserDetailsServiceImplementationTestWithSetUp {
        @BeforeEach
        public void registerUser() throws GeneralSecurityException, IOException {
            SignupRequest signUpTestMailUser = new SignupRequest();
            signUpTestMailUser.setEmail("TestUser@mail.com");
            signUpTestMailUser.setLogin("TestUser");
            signUpTestMailUser.setPassword("12345");
            userService.signUpUser(signUpTestMailUser, "Test");
            rootFolderId = cloudStorage.searchFolder("Test", "root").getId();
        }
        @AfterEach
        public void cleanUp() throws GeneralSecurityException, IOException {
            String testUserId = cloudStorage.searchFolder("TestUser", rootFolderId).getId();
            cloudStorage.deleteFile(testUserId);
            userRepository.deleteAll();
        }

        @Test
        @DisplayName("Should not create account with existing e-mail")
        public void testSignUpUserWithExistingEmailShouldFail() throws GeneralSecurityException, IOException {
            SignupRequest signUpTestUser = new SignupRequest();
            signUpTestUser.setLogin("TestUser2");
            signUpTestUser.setPassword("1234");
            signUpTestUser.setEmail("TestUser@mail.com");
            signUpTestUser.setRoles(new HashSet<>(Collections.singletonList("USER")));
            int expectedStatusCode = 400;

            ResponseEntity<?> response = userService.signUpUser(signUpTestUser, "Test");

            assertEquals(expectedStatusCode,
                    response.getStatusCodeValue(),
                    "Should be 400 status code");
        }

        @Test
        @DisplayName("Should not create account with existing login")
        public void testSignUpUserWithExistingLoginShouldFail() throws GeneralSecurityException, IOException {
            SignupRequest signUpTestUser = new SignupRequest();
            signUpTestUser.setLogin("TestUser");
            signUpTestUser.setPassword("1234");
            signUpTestUser.setEmail("TestMail@mail.com");
            signUpTestUser.setRoles(new HashSet<>(Collections.singletonList("USER")));
            int expectedStatusCode = 400;

            ResponseEntity<?> response = userService.signUpUser(signUpTestUser, "Test");

            assertEquals(expectedStatusCode,
                    response.getStatusCodeValue(),
                    "Should be 400 status code");
        }

        @Test
        @DisplayName("Logging into account")
        public void testSignInUser() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLogin("TestUser");
            loginRequest.setPassword("12345");
            int expectedStatusCode = 200;

            ResponseEntity<?> response = userService.signInUser(loginRequest);

            assertEquals(expectedStatusCode,
                    response.getStatusCodeValue(),
                    "Should sign in successfully");
        }
    }

    @Test
    @DisplayName("Delete user")
    public void testDeleteUser() throws GeneralSecurityException, IOException {
        SignupRequest signUpTestUser = new SignupRequest();
        signUpTestUser.setLogin("TestUser");
        signUpTestUser.setPassword("12345");
        signUpTestUser.setEmail("TestUser@mail.com");
        userService.signUpUser(signUpTestUser, "Test");

        userService.deleteUser("TestUser", "Test");
        boolean userExists = userRepository.existsByLogin("TestUser");

        assertFalse(userExists,
                "TestUser should not exist");
    }
}
