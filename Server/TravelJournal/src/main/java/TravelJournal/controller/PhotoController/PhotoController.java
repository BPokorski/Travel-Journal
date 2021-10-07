package TravelJournal.controller.PhotoController;

import TravelJournal.model.photo.PhotoData;
import TravelJournal.payload.response.PhotoDataResponse;
import TravelJournal.service.PhotoService;
import TravelJournal.utils.photo.MultipartToFileConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.HashMap;
import java.util.List;
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

        // Application folder is root folder for main application
        String appFolderId = photoService.getFolder("Application", "root").getId();
        PhotoData photoData = photoService.addPhoto(photo, login, appFolderId);

        photo.delete();
        if(photoData.getLongitude() != null && photoData.getLatitude() !=null) {

            return ResponseEntity.ok(new PhotoDataResponse(
                    photoData.getId(),
                    photoData.getPhotoId(),
                    photoData.getDescription(),
                    photoData.getDate(),
                    photoData.getCountry(),
                    photoData.getLatitude(),
                    photoData.getLongitude(),
                    photoData.getRotateAngle()
            ));

        } else {
            return ResponseEntity.ok(new PhotoDataResponse(
                    photoData.getId(),
                    photoData.getPhotoId(),
                    photoData.getDescription(),
                    photoData.getDate(),
                    photoData.getCountry(),
                    photoData.getRotateAngle()));
        }
    }
    @PutMapping("/{photoId}/description")
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> updatePhotoDataDescription(
            @PathVariable String login,
            @PathVariable String photoId,
            @RequestParam("description") String description) {


        PhotoData updatedPhotoData = photoService.updateDescription(photoId, description);

        if(updatedPhotoData.getLongitude() != null && updatedPhotoData.getLatitude() !=null) {

            return ResponseEntity.ok(new PhotoDataResponse(
                    updatedPhotoData.getId(),
                    updatedPhotoData.getPhotoId(),
                    updatedPhotoData.getDescription(),
                    updatedPhotoData.getDate(),
                    updatedPhotoData.getCountry(),
                    updatedPhotoData.getLatitude(),
                    updatedPhotoData.getLongitude(),
                    updatedPhotoData.getRotateAngle()
            ));

        } else {
            return ResponseEntity.ok(new PhotoDataResponse(
                    updatedPhotoData.getId(),
                    updatedPhotoData.getPhotoId(),
                    updatedPhotoData.getDescription(),
                    updatedPhotoData.getDate(),
                    updatedPhotoData.getCountry(),
                    updatedPhotoData.getRotateAngle()));
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

    @GetMapping("/{photoId}/photodata")
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<?> getPhotoData(
            @PathVariable String login,
            @PathVariable String photoId) throws IOException, GeneralSecurityException {

        PhotoData photoData = photoService.getPhotoData(photoId);

        PhotoDataResponse photoDataResponse = new PhotoDataResponse();

        if (photoData.getLatitude() != null && photoData.getLongitude() != null) {
            photoDataResponse
                    .setDescription(photoData.getDescription())
                    .setCountry(photoData.getCountry())
                    .setDate(photoData.getDate())
                    .setId(photoData.getId())
                    .setPhotoId(photoId)
                    .setLatitude(photoData.getLatitude())
                    .setLongitude(photoData.getLongitude());
        } else {
            photoDataResponse
                    .setDescription(photoData.getDescription())
                    .setCountry(photoData.getCountry())
                    .setDate(photoData.getDate())
                    .setId(photoData.getId())
                    .setPhotoId(photoId);
        }
        Link photoLink = linkTo(methodOn(PhotoController.class)
                .getPhoto(login, photoId))
                .withRel("photo");
        Link selfLink = linkTo(methodOn(PhotoController.class)
                .getPhotoData(login,photoId))
                .withSelfRel();

        photoDataResponse.add(photoLink);
        photoDataResponse.add(selfLink);

        return ResponseEntity.ok(photoDataResponse);
    }

    @GetMapping("/photodata")
    public ResponseEntity<?> getAllPhotoData(@PathVariable String login,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "3") int size) throws IOException, GeneralSecurityException {

        Page<PhotoDataResponse> pagedPhotoDataResponse = photoService.getUserPhotoData(login, page, size);

        List<PhotoDataResponse> listPhotoDataResponse = pagedPhotoDataResponse.getContent();
        for (PhotoDataResponse photoDataResponse: listPhotoDataResponse) {
            Link link = linkTo(methodOn(PhotoController.class)
                    .getPhotoData(login, photoDataResponse.getPhotoId()))
                    .withSelfRel();

            Link photoLink = linkTo(methodOn(PhotoController.class)
                    .getPhoto(login,photoDataResponse.getPhotoId()))
                    .withRel("photo");
            photoDataResponse.add(link);
            photoDataResponse.add(photoLink);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("photoData", listPhotoDataResponse);
        response.put("currentPage", pagedPhotoDataResponse.getNumber());
        response.put("totalItems", pagedPhotoDataResponse.getTotalElements());
        response.put("totalPages", pagedPhotoDataResponse.getTotalPages());

        if(listPhotoDataResponse.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<?> deletePhoto(
            @PathVariable String login,
            @PathVariable String photoId) throws GeneralSecurityException, IOException {

        photoService.deleteFile(photoId);
        return ResponseEntity.ok("Photo deleted successfully");
    }
}
