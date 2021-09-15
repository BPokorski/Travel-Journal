package TravelJournal.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * Parameters necessary for user to create account.
 */
@Getter
@Setter
public class SignupRequest {
    @NotBlank
    private String login;

    @NotBlank
    @Email
    private String email;


    private Set<String> roles;

    @NotBlank
    private String password;
}
