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
@Relation(collectionRelation = "countries")
@Accessors(chain = true)
public class CountryResponse extends RepresentationModel<CountryResponse> {
    private String name;

}
