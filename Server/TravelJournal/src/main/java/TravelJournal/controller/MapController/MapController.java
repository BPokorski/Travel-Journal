package TravelJournal.controller.MapController;

import TravelJournal.controller.PhotoController.PhotoController;
import TravelJournal.model.photo.PhotoDescription;
import TravelJournal.payload.response.CountryResponse;
import TravelJournal.payload.response.PhotoDescriptionResponse;
import TravelJournal.repository.photo.PhotoDescriptionRepository;
import TravelJournal.service.PhotoService;
import TravelJournal.utils.stringParser.StringUtils;
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
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/{login}/map")
public class MapController {
    private final StringUtils stringUtils = new StringUtils();
    @Autowired
    PhotoDescriptionRepository descriptionRepository;

    @Autowired
    PhotoService photoService;

    @GetMapping()
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> getCountries(@PathVariable String login) throws GeneralSecurityException, IOException {
        List<CountryResponse> countries = new ArrayList<>();

        Set<String> availableCountries = photoService.getAvailableCountries(login);

        if (!availableCountries.isEmpty()) {

            for (String availableCountry : availableCountries) {
                if (!availableCountry.equals("Other")) {
                    CountryResponse country = new CountryResponse(availableCountry);
                    countries.add(country);

                    String countryLowercaseNameDash = stringUtils.separatorChanger(
                            stringUtils.toLowerCaseConverter(
                                    availableCountry,
                                    " "
                            ),
                            " ",
                            "-");

                    Link link = linkTo(methodOn(MapController.class)
                            .getCountryPhotoDescriptions(login, countryLowercaseNameDash))
                            .withRel(availableCountry);

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

        List<PhotoDescriptionResponse> descriptions = new ArrayList<>();
        String countryCapitalWhitespace = stringUtils.separatorChanger(
                stringUtils.toCapitalFirstLetter(country, "-"),
                "-",
                " ");

        List<PhotoDescription> photos = photoService.getPhotosInCountry(login, countryCapitalWhitespace);
        if (!photos.isEmpty()) {

            for(TravelJournal.model.photo.PhotoDescription photoDescription: photos) {

                PhotoDescriptionResponse descriptionResponse = new PhotoDescriptionResponse()
                        .setId(photoDescription.getId())
                        .setPhotoId(photoDescription.getPhotoId())
                        .setDescription(photoDescription.getDescription())
                        .setDate(photoDescription.getDate())
                        .setCountry(photoDescription.getCountry())
                        .setLatitude(photoDescription.getLatitude())
                        .setLongitude(photoDescription.getLongitude())
                        .setRotateAngle(photoDescription.getRotateAngle());

                // link to self description
                Link link = linkTo(methodOn(PhotoController.class)
                        .getPhotoDescription(login, photoDescription.getPhotoId()))
                        .withSelfRel();

                // link to photo
                Link photoLink = linkTo(methodOn(PhotoController.class)
                        .getPhoto(login,photoDescription.getPhotoId()))
                        .withRel("photo");

                descriptionResponse.add(link);
                descriptionResponse.add(photoLink);
                descriptions.add(descriptionResponse);
            }
        }

        if(descriptions.isEmpty()) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(descriptions);
        }

    }

    @GetMapping("/ocean")
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> getOceanPhotoDescriptions(@PathVariable String login) throws GeneralSecurityException, IOException {
        List<PhotoDescriptionResponse> descriptions = new ArrayList<>();
        List<PhotoDescription> photos = photoService.getPhotosInCountry(login, "Ocean");

        if (!photos.isEmpty()) {

            for(PhotoDescription photoDescription: photos) {

                PhotoDescriptionResponse descriptionResponse = new PhotoDescriptionResponse()
                        .setId(photoDescription.getId())
                        .setPhotoId(photoDescription.getPhotoId())
                        .setDescription(photoDescription.getDescription())
                        .setDate(photoDescription.getDate())
                        .setCountry(photoDescription.getCountry())
                        .setLatitude(photoDescription.getLatitude())
                        .setLongitude(photoDescription.getLongitude())
                        .setRotateAngle(photoDescription.getRotateAngle());

                // link to self description
                Link link = linkTo(methodOn(PhotoController.class)
                        .getPhotoDescription(login, photoDescription.getPhotoId()))
                        .withSelfRel();

                // link to photo
                Link photoLink = linkTo(methodOn(PhotoController.class)
                        .getPhoto(login,photoDescription.getPhotoId()))
                        .withRel("photo");

                descriptionResponse.add(link);
                descriptionResponse.add(photoLink);
                descriptions.add(descriptionResponse);
            }
        }
        if(descriptions.isEmpty()) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(descriptions);
        }

    }

}
