package TravelJournal.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Relation(collectionRelation = "descriptions")
public class PhotoDescriptionResponse extends RepresentationModel<PhotoDescriptionResponse> {
    private String id;
    private String photoId;
    private String description;
    private String date;
    private String country;
    private Double latitude;
    private Double longitude;
    private int rotateAngle;


    public PhotoDescriptionResponse(String id, String photoId,String description, String date, String country, int rotateAngle) {
        this.id = id;
        this.photoId = photoId;
        this.description = description;
        this.date = date;
        this.country = country;
        this.latitude = null;
        this.longitude = null;
        this.rotateAngle = rotateAngle;
    }
}
