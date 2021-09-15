package TravelJournal.model.user;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

/**
 * User model. Contains id, login, password, e-mail and granted authorities.
 */
@Document(collection="User")
@Data
public class User {
    private String id;
    @NotBlank
    private String login;
    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;
    @DBRef
    private Set<Role> roles = new HashSet<>();
    public User(String login, String email, String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }
}
