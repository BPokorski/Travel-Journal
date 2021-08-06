package TravelJournal.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String id;
    private String login;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, String id, String login, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.login = login;
        this.email = email;
        this.roles = roles;
    }


}
