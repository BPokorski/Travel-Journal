package TravelJournal.model.photo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "PhotoDescription")
@Accessors(chain = true)
@Relation(collectionRelation = "photo descriptions")
public class PhotoDescription {
    private String id;

    private String owner;
    private String photoId;
    private String description;
    private String date;
    private String country;
    private Double latitude;
    private Double longitude;
    private int rotateAngle;
}
