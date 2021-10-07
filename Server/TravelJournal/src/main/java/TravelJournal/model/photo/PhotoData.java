package TravelJournal.model.photo;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.server.core.Relation;

/**
 * Model of photo data.
 * Contains data about owner, photo id, own description,
 * angle of rotation(to achieve effect in display of hand-glued photo),
 * date of taking a photo and if contains location data e.g. country and coordinates.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Document(collection = "PhotoData")
@Accessors(chain = true)
@Relation(collectionRelation = "photo data")
public class PhotoData {
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
