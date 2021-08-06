package TravelJournal.controller.MapController;

import TravelJournal.payload.response.CountryResponse;
import TravelJournal.payload.response.PhotoDescriptionResponse;
import TravelJournal.repository.photo.PhotoDescriptionRepository;
import TravelJournal.service.PhotoService;
import TravelJournal.utils.stringParser.StringUtills;
import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/{login}/map")
public class MapController {
    private final StringUtills stringUtills = new StringUtills();
    @Autowired
    PhotoDescriptionRepository descriptionRepository;

    @Autowired
    PhotoService photoService;

    @GetMapping()
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> getCountries(@PathVariable String login) throws GeneralSecurityException, IOException {
        List<CountryResponse> countries = new ArrayList<>();

        File userFolder = photoService.getFolder(login, "root");

                    List<File> availableCountries = photoService.getAllFiles(userFolder.getId(), "folder");
                    if (!availableCountries.isEmpty()) {

                        for (File availableCountry : availableCountries) {
                            if (!availableCountry.getName().equals("Other")) {
                                CountryResponse country = new CountryResponse(availableCountry.getName());
                                countries.add(country);

                                String countryLowercaseNameDash = stringUtills.connectorChanger(
                                        stringUtills.toLowerCaseConverter(
                                                availableCountry.getName()
                                        ),
                                        " ",
                                        "-");

                                Link link = linkTo(methodOn(MapController.class)
                                        .getCountryPhotoDescriptions(login, countryLowercaseNameDash))
                                        .withRel(availableCountry.getName());

                                country.add(link);
                            }
                        }
                    } else {
                        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
                    }
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/{country}")
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> getCountryPhotoDescriptions(@PathVariable String login,
                                                         @PathVariable String country) throws GeneralSecurityException, IOException {

        String countryCapitalWhitespace = stringUtills.connectorChanger(
                stringUtills.toCapitalFirstLetter(country),
                "-",
                " ");

        List<PhotoDescriptionResponse> descriptions = photoService.getPhotosInCountry(login, countryCapitalWhitespace);

        if(descriptions.isEmpty()) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(descriptions);
        }

    }

    @GetMapping("/ocean")
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> getOceanPhotoDescriptions(@PathVariable String login) throws GeneralSecurityException, IOException {

        List<PhotoDescriptionResponse> descriptions = photoService.getPhotosInCountry(login, "Ocean");

        if(descriptions.isEmpty()) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(descriptions);
        }

    }

}
