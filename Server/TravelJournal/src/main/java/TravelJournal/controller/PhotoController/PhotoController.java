package TravelJournal.controller.PhotoController;

import TravelJournal.model.photo.PhotoDescription;
import TravelJournal.payload.response.PhotoDescriptionResponse;
import TravelJournal.service.PhotoService;
import TravelJournal.utils.photo.MultipartToFileConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/{login}/photo")
public class PhotoController {
    @Autowired
    PhotoService photoService;
    @PostMapping()
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> addPhoto(@PathVariable String login,
                                      @RequestParam("image")MultipartFile image) throws IOException, GeneralSecurityException {


            MultipartToFileConverter multipartToFileConverter = new MultipartToFileConverter();

            File photo = multipartToFileConverter.convert(image);

            PhotoDescription photoDescription = photoService.addPhoto(photo, login);

            photo.delete();
            if(photoDescription.getLongitude() != null && photoDescription.getLatitude() !=null) {

                return ResponseEntity.ok(new PhotoDescriptionResponse(
                        photoDescription.getId(),
                        photoDescription.getPhotoId(),
                        photoDescription.getDescription(),
                        photoDescription.getDate(),
                        photoDescription.getCountry(),
                        photoDescription.getLatitude(),
                        photoDescription.getLongitude(),
                        photoDescription.getRotateAngle()
                ));

            } else {
                return ResponseEntity.ok(new PhotoDescriptionResponse(
                        photoDescription.getId(),
                        photoDescription.getPhotoId(),
                        photoDescription.getDescription(),
                        photoDescription.getDate(),
                        photoDescription.getCountry(),
                        photoDescription.getRotateAngle()));
            }
    }
    @PutMapping("/{photoId}/description")
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> updatePhotoDescription(
            @PathVariable String login,
            @PathVariable String photoId,
            @RequestParam("description") String description) {

            PhotoDescription updatedPhotoDescription = photoService.updateDescription(photoId, description);

        if(updatedPhotoDescription.getLongitude() != null && updatedPhotoDescription.getLatitude() !=null) {

            return ResponseEntity.ok(new PhotoDescriptionResponse(
                    updatedPhotoDescription.getId(),
                    updatedPhotoDescription.getPhotoId(),
                    updatedPhotoDescription.getDescription(),
                    updatedPhotoDescription.getDate(),
                    updatedPhotoDescription.getCountry(),
                    updatedPhotoDescription.getLatitude(),
                    updatedPhotoDescription.getLongitude(),
                    updatedPhotoDescription.getRotateAngle()
            ));

        } else {
            return ResponseEntity.ok(new PhotoDescriptionResponse(
                    updatedPhotoDescription.getId(),
                    updatedPhotoDescription.getPhotoId(),
                    updatedPhotoDescription.getDescription(),
                    updatedPhotoDescription.getDate(),
                    updatedPhotoDescription.getCountry(),
                    updatedPhotoDescription.getRotateAngle()));
        }
    }

    @GetMapping(value = "/{photoId}", produces = MediaType.IMAGE_JPEG_VALUE)
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> getPhoto(
            @PathVariable String login,
            @PathVariable String photoId)
            throws IOException, GeneralSecurityException {

        byte[] photo = photoService.getPhoto(photoId);

        return ResponseEntity.ok(photo);
    }

    @GetMapping("/{photoId}/description")
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> getPhotoDescription(
            @PathVariable String login,

            @PathVariable String photoId) throws IOException, GeneralSecurityException {

        PhotoDescription photoDescription = photoService.getPhotoDescription(photoId);

        PhotoDescriptionResponse descriptionResponse = new PhotoDescriptionResponse();
                if (photoDescription.getLatitude() != null && photoDescription.getLongitude() != null) {
                    descriptionResponse
                            .setDescription(photoDescription.getDescription())
                            .setCountry(photoDescription.getCountry())
                            .setDate(photoDescription.getDate())
                            .setId(photoDescription.getId())
                            .setPhotoId(photoId)
                            .setLatitude(photoDescription.getLatitude())
                            .setLongitude(photoDescription.getLongitude());
                } else {
                    descriptionResponse
                            .setDescription(photoDescription.getDescription())
                            .setCountry(photoDescription.getCountry())
                            .setDate(photoDescription.getDate())
                            .setId(photoDescription.getId())
                            .setPhotoId(photoId);
                }
        Link photoLink = linkTo(methodOn(PhotoController.class)
                .getPhoto(login, photoId))
                .withRel("photo");
        Link selfLink = linkTo(methodOn(PhotoController.class)
                .getPhotoDescription(login,photoId))
                .withSelfRel();

        descriptionResponse.add(photoLink);
        descriptionResponse.add(selfLink);

        return ResponseEntity.ok(descriptionResponse);

    }

    @GetMapping("/description")
    public ResponseEntity<?> getAllPhotoDescriptions(@PathVariable String login,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "3") int size) throws IOException, GeneralSecurityException {

        Map<String, Object> descriptions = photoService.getUserPhotoDescriptions(login, page, size);

        if(descriptions.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return ResponseEntity.ok(descriptions);
        }
    }

}
