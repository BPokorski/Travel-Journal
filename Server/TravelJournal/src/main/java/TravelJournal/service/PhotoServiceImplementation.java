package TravelJournal.service;

import TravelJournal.model.photo.PhotoDescription;
import TravelJournal.payload.response.PhotoDescriptionResponse;
import TravelJournal.repository.cloudStorage.GoogleDriveRepository;
import TravelJournal.repository.photo.PhotoDescriptionRepository;
import TravelJournal.utils.location.GeocodingAPI;
import TravelJournal.utils.photo.PhotoEXIF;
import TravelJournal.utils.randomGenerator.RandomIntGenerator;
import com.drew.lang.GeoLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class handling business logic of whole application. Allows to add new photos,
 * display requested resource, get all files or specific folder from Cloud Storage
 * update already existing resources.
 */
@Service
public class PhotoServiceImplementation implements PhotoService{

    @Value("${traveljournal.app.geocodingAPIKey}")
    private String apiKey;

    @Autowired
    private GoogleDriveRepository googleDriveRepository;
    @Autowired
    private PhotoDescriptionRepository photoDescriptionRepository;
    @Autowired
    private RandomIntGenerator randomIntGenerator;
    @Autowired
    private GeocodingAPI geocodingAPI;
    @Autowired
    private PhotoEXIF photoEXIF;

    /**
     * {@inheritDoc}
     */
    @Override
    public PhotoDescription addPhoto(File photo, String user, String parentId) throws GeneralSecurityException, IOException {

        String countryName;

        GeoLocation geoLocation = photoEXIF.getPhotoLocation(photo);
        String date = photoEXIF.getPhotoDate(photo);

        if (geoLocation != null) {
            countryName = geocodingAPI.coordinatesToCountry(geoLocation.getLatitude(), geoLocation.getLongitude());
        } else {
            countryName = "Other"; // Photos without location
        }

//        String applicationFolderId = googleDriveRepository.searchFolder("application", "root").getId();
        com.google.api.services.drive.model.File userFolder = googleDriveRepository.searchFolder(user, parentId);
        com.google.api.services.drive.model.File countryFolder = googleDriveRepository.searchFolder(countryName, userFolder.getId());

        // Folder of given country was not found, so it needs to be created.
        if (countryFolder == null ) {
            googleDriveRepository.createFolder(countryName, userFolder.getId());
            countryFolder = googleDriveRepository.searchFolder(countryName, userFolder.getId());
        }

        // Save photo in cloud storage
        com.google.api.services.drive.model.File googlePhoto = googleDriveRepository.addPhoto(countryFolder.getId(), photo, photo.getName());

        // Generate random value to rotate displayed photo of given angle
        int rotateAngle = randomIntGenerator.generateRandomOfTwoInts(
                randomIntGenerator.generateRandomIntWithinRange(1, 9),
                randomIntGenerator.generateRandomIntWithinRange(352,359)); // Random value between 1-9 and 352-359 degrees

        TravelJournal.model.photo.PhotoDescription photoDescription = new PhotoDescription()
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PhotoDescription> getPhotosInCountry(String user, String country) {
        return photoDescriptionRepository.findByOwnerAndCountry(user, country);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhotoDescription updateDescription(String photoId, String description) {
        PhotoDescription photoDescription = photoDescriptionRepository.findByPhotoId(photoId);
        photoDescription.setDescription(description);
        return photoDescriptionRepository.save(photoDescription);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getPhoto(String photoId) throws IOException, GeneralSecurityException {
        return googleDriveRepository.downloadPhoto(photoId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhotoDescription getPhotoDescription(String photoId) {
        return photoDescriptionRepository.findByPhotoId(photoId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public com.google.api.services.drive.model.File getFolder(String folderName, String parentId) throws GeneralSecurityException, IOException {
        return googleDriveRepository.searchFolder(folderName, parentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createFolder(String folderName, String parentId) throws GeneralSecurityException, IOException {
        googleDriveRepository.createFolder(folderName, parentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PhotoDescriptionResponse> getUserPhotoDescriptions(String user, int page, int pageSize) {
        Pageable paging = PageRequest.of(page, pageSize);

        return photoDescriptionRepository.findByOwner(user, paging);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAvailableCountries(String user)  {

        Set<String> countries = new HashSet<>();
        List<PhotoDescriptionResponse> allUserDescriptions = photoDescriptionRepository.findByOwner(user);

        for (PhotoDescriptionResponse photoDescriptionResponse : allUserDescriptions) {
            countries.add(photoDescriptionResponse.getCountry());
        }
        return countries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFile(String fileId) throws GeneralSecurityException, IOException {
        googleDriveRepository.deleteFile(fileId);
        photoDescriptionRepository.deleteByPhotoId(fileId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteResources(String login, String applicationFolder) throws GeneralSecurityException, IOException {
        photoDescriptionRepository.deleteByOwner(login);

        String applicationFolderId = googleDriveRepository.searchFolder(applicationFolder, "root").getId();
        String userFolderId = googleDriveRepository.searchFolder(login, applicationFolderId).getId();

        googleDriveRepository.deleteFile(userFolderId);
    }
}
