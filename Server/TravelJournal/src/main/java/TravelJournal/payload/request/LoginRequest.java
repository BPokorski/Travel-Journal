package TravelJournal.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Parameters necessary for user to log in.
 */
@Getter
@Setter
public class LoginRequest {
    @NotBlank
    private String login;
    @NotBlank
    private String password;
}
