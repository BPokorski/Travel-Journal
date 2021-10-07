package TravelJournal.service;

import TravelJournal.model.photo.PhotoData;
import TravelJournal.payload.response.PhotoDataResponse;
import TravelJournal.repository.cloudStorage.GoogleDriveRepository;
import TravelJournal.repository.photo.PhotoDataRepository;
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
    private PhotoDataRepository photoDataRepository;
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
    public PhotoData addPhoto(File photo, String user, String parentId) throws GeneralSecurityException, IOException {

        String countryName;

        GeoLocation geoLocation = photoEXIF.getPhotoLocation(photo);
        String date = photoEXIF.getPhotoDate(photo);

        if (geoLocation != null) {
            countryName = geocodingAPI.coordinatesToCountry(geoLocation.getLatitude(), geoLocation.getLongitude());
        } else {
            countryName = "Other"; // Photos without location
        }

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

        PhotoData photoData = new PhotoData()
                .setOwner(user)
                .setPhotoId(googlePhoto.getId())
                .setDescription("")
                .setCountry(countryName)
                .setDate(date)
                .setRotateAngle(rotateAngle);
        if (geoLocation != null) {
            photoData
                    .setLatitude(geoLocation.getLatitude())
                    .setLongitude(geoLocation.getLongitude());
        }
        return photoDataRepository.save(photoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PhotoData> getPhotosInCountry(String user, String country) {
        return photoDataRepository.findByOwnerAndCountry(user, country);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhotoData updateDescription(String photoId, String description) {
        PhotoData photoData = photoDataRepository.findByPhotoId(photoId);
        photoData.setDescription(description);
        return photoDataRepository.save(photoData);
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
    public PhotoData getPhotoData(String photoId) {
        return photoDataRepository.findByPhotoId(photoId);
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
    public Page<PhotoDataResponse> getUserPhotoData(String user, int page, int pageSize) {
        Pageable paging = PageRequest.of(page, pageSize);

        return photoDataRepository.findByOwner(user, paging);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAvailableCountries(String user)  {

        Set<String> countries = new HashSet<>();
        List<PhotoDataResponse> allUserDescriptions = photoDataRepository.findByOwner(user);

        for (PhotoDataResponse photoDataResponse : allUserDescriptions) {
            countries.add(photoDataResponse.getCountry());
        }
        return countries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFile(String fileId) throws GeneralSecurityException, IOException {
        googleDriveRepository.deleteFile(fileId);
        photoDataRepository.deleteByPhotoId(fileId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteResources(String login, String applicationFolder) throws GeneralSecurityException, IOException {
        photoDataRepository.deleteByOwner(login);

        String applicationFolderId = googleDriveRepository.searchFolder(applicationFolder, "root").getId();
        String userFolderId = googleDriveRepository.searchFolder(login, applicationFolderId).getId();

        googleDriveRepository.deleteFile(userFolderId);
    }
}
