package TravelJournal.model.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Role")
@Data
public class Role {
    @Id
    private String id;

    private EnumRole name;

}
