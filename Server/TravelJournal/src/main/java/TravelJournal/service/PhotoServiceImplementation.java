package TravelJournal.service;

import TravelJournal.controller.PhotoController.PhotoController;
import TravelJournal.model.photo.PhotoDescription;
import TravelJournal.payload.response.PhotoDescriptionResponse;
import TravelJournal.repository.cloudStorage.CloudStorageRepository;
import TravelJournal.repository.photo.PhotoDescriptionRepository;
import TravelJournal.utils.photo.PhotoEXIF;
import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageReverseRequest;
import com.drew.lang.GeoLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PhotoServiceImplementation implements PhotoService{

//    private final cloudStorageImplementation cloudStorageImplementation = new cloudStorageImplementation();
    @Value("${traveljournal.app.geocodingAPIKey}")
    private String apiKey;

    @Autowired
    CloudStorageRepository cloudStorageRepository;
    @Autowired
    PhotoDescriptionRepository photoDescriptionRepository;

    @Autowired
    PhotoEXIF photoEXIF;

    @Override
    public PhotoDescription addPhoto(File photo, String user) throws GeneralSecurityException, IOException {

        String countryName = "Ocean";

        GeoLocation geoLocation = photoEXIF.getPhotoLocation(photo);
        String date = photoEXIF.getPhotoDate(photo);

        if (geoLocation != null) {

            JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder(apiKey);
            JOpenCageReverseRequest request = new JOpenCageReverseRequest(
                    geoLocation.getLatitude(),
                    geoLocation.getLongitude());

            request.setLanguage("en");
            request.setNoDedupe(true);
            request.setLimit(2);
            request.setNoAnnotations(true);

            JOpenCageResponse response = jOpenCageGeocoder.reverse(request);

            System.out.println(response.getResults().get(0).getComponents().getCountry());
            countryName = response.getResults().get(0).getComponents().getCountry();
        } else {
            countryName = "Other"; // Photos without location
        }

        com.google.api.services.drive.model.File userFolder = cloudStorageRepository.searchFolder(user, "root");


        cloudStorageRepository.createFolder(countryName, userFolder.getId());
        com.google.api.services.drive.model.File countryFolder = cloudStorageRepository.searchFolder(countryName, userFolder.getId());
        com.google.api.services.drive.model.File googlePhoto = cloudStorageRepository.addPhoto(countryFolder.getId(), photo, photo.getName());

        Random randomGenerator = new Random();
        int randomRotateAngle = randomGenerator.ints(1,1, 9).findFirst().getAsInt();
        int secondRandomRotateAngle = randomGenerator.ints(1, 352, 359).findFirst().getAsInt();

        int rotateAngle = randomGenerator.nextBoolean() ? randomRotateAngle: secondRandomRotateAngle; // Random value between 1-9 and 352-359 degrees

        PhotoDescription photoDescription = new PhotoDescription()
                .setOwner(user)
                .setPhotoId(googlePhoto.getId())
                .setDescription("")
                .setCountry(countryName)
                .setDate(date)
                .setRotateAngle(rotateAngle);
        if (geoLocation != null) {
            photoDescription
                    .setLatitude(geoLocation.getLatitude())
                    .setLongitude(geoLocation.getLongitude());
        }
        return photoDescriptionRepository.save(photoDescription);
    }

    @Override
    public List<PhotoDescriptionResponse> getPhotosInCountry(String user, String country) throws GeneralSecurityException, IOException {
        List<PhotoDescriptionResponse> descriptions = new ArrayList<>();

        List<PhotoDescription> photos = photoDescriptionRepository.findByOwnerAndCountry(user, country);

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
                        .getPhotoDescription(user, photoDescription.getPhotoId()))
                        .withSelfRel();

                // link to photo
                Link photoLink = linkTo(methodOn(PhotoController.class)
                        .getPhoto(user,photoDescription.getPhotoId()))
                        .withRel("photo");

                descriptionResponse.add(link);
                descriptionResponse.add(photoLink);
                descriptions.add(descriptionResponse);
            }
        }
        return descriptions;
    }

    @Override
    public PhotoDescription updateDescription(String photoId, String description) {
        PhotoDescription photoDescription = photoDescriptionRepository.findByPhotoId(photoId);
        photoDescription.setDescription(description);
        return photoDescriptionRepository.save(photoDescription);
    }


    @Override
    public byte[] getPhoto(String photoId) throws IOException, GeneralSecurityException {
        PhotoDescription photoDescription = photoDescriptionRepository.findByPhotoId(photoId);

        return cloudStorageRepository.downloadPhoto(photoId);

    }

    @Override
    public PhotoDescription getPhotoDescription(String photoId) {
        return photoDescriptionRepository.findByPhotoId(photoId);
    }

    @Override
    public com.google.api.services.drive.model.File getFolder(String folderName, String parentId) throws GeneralSecurityException, IOException {
        return cloudStorageRepository.searchFolder(folderName, parentId);
    }

    @Override
    public List<com.google.api.services.drive.model.File> getAllFiles(String parentId, String fileType) throws IOException, GeneralSecurityException {
        return cloudStorageRepository.getAllFilesInFolder(parentId, fileType);
    }

    @Override
    public Map<String, Object> getUserPhotoDescriptions(String user, int page, int pageSize) throws IOException, GeneralSecurityException {
        List<PhotoDescriptionResponse> descriptions = new ArrayList<PhotoDescriptionResponse>();

        Pageable paging = PageRequest.of(page, pageSize);
        Page<PhotoDescriptionResponse> pagedDescriptions;

        pagedDescriptions = photoDescriptionRepository.findByOwner(user, paging);

        // link to photo

        descriptions = pagedDescriptions.getContent();

        for (PhotoDescriptionResponse description: descriptions) {
            Link link = linkTo(methodOn(PhotoController.class)
                    .getPhotoDescription(user, description.getPhotoId()))
                    .withSelfRel();

            Link photoLink = linkTo(methodOn(PhotoController.class)
                    .getPhoto(user,description.getPhotoId()))
                    .withRel("photo");
            description.add(link);
            description.add(photoLink);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("descriptions", descriptions);
        response.put("currentPage", pagedDescriptions.getNumber());
        response.put("totalItems", pagedDescriptions.getTotalElements());
        response.put("totalPages", pagedDescriptions.getTotalPages());
        return response;
    }


}
