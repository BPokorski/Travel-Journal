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

//    private final RandomIntGenerator randomIntGenerator = new RandomIntGenerator();
    /**
     * Add new photo for given user.
     * @param photo - Image to be added.
     * @param user - User which want to add photo.
     * @return PhotoDescription of newly created resource.
     * @throws GeneralSecurityException When authentication fails to Google Drive API.
     * @throws IOException When something goes wrong with Google Drive.
     */
    @Override
    public TravelJournal.model.photo.PhotoDescription addPhoto(File photo, String user) throws GeneralSecurityException, IOException {
        
        String countryName = "Ocean";

        GeoLocation geoLocation = photoEXIF.getPhotoLocation(photo);
        String date = photoEXIF.getPhotoDate(photo);

        if (geoLocation != null) {
            countryName = geocodingAPI.coordinatesToCountry(geoLocation.getLatitude(), geoLocation.getLongitude());
        } else {
            countryName = "Other"; // Photos without location
        }

        com.google.api.services.drive.model.File userFolder = googleDriveRepository.searchFolder(user, "root");
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
     * Get Descriptions(with ID of photo) in single country for given user.
     * @param user - Authenticated user.
     * @param country - Name of country to get available description.
     * @return List of PhotoDescription.
     */
    @Override
    public List<PhotoDescription> getPhotosInCountry(String user, String country) {
        return photoDescriptionRepository.findByOwnerAndCountry(user, country);
    }

    /**
     * Update Description of photo.
     * @param photoId - Id of photo to change description.
     * @param description - New description.
     * @return Updated PhotoDescription.
     */
    @Override
    public PhotoDescription updateDescription(String photoId, String description) {
        PhotoDescription photoDescription = photoDescriptionRepository.findByPhotoId(photoId);
        photoDescription.setDescription(description);
        return photoDescriptionRepository.save(photoDescription);
    }

    /**
     * Get Image with given Id.
     * @param photoId - Id of photo to be displayed.
     * @return Representation of image in bytes.
     * @throws GeneralSecurityException when authentication fails to Google Drive API.
     * @throws IOException when something goes wrong with Google Drive.
     */
    @Override
    public byte[] getPhoto(String photoId) throws IOException, GeneralSecurityException {
        return googleDriveRepository.downloadPhoto(photoId);
    }

    /**
     * Get single photoDescription with given id.
     * @param photoId - Id of Photo.
     * @return PhotoDescription.
     */
    @Override
    public PhotoDescription getPhotoDescription(String photoId) {
        return photoDescriptionRepository.findByPhotoId(photoId);
    }

    /**
     * Get single folder from Google Cloud Storage with given name and id of its parent.
     * @param folderName - Name of folder.
     * @param parentId - Id of parent folder. "root" if there is no-one.
     * @throws GeneralSecurityException When authentication fails to Google Drive API.
     * @throws IOException When something goes wrong with Google Drive.
     * @return Google Drive Folder
     */
    @Override
    public com.google.api.services.drive.model.File getFolder(String folderName, String parentId) throws GeneralSecurityException, IOException {
        return googleDriveRepository.searchFolder(folderName, parentId);
    }

    /**
     * Create folder in Google Cloud Storage with given name and id of its parent.
     * @param folderName - Name of folder to create.
     * @param parentId - Id of parent folder. "root" if there is no-one.
     * @return newly created Google Drive Folder.
     * @throws GeneralSecurityException - When authentication fails to Google Drive API.
     * @throws IOException - When something goes wrong with Google Drive.
     */
    @Override
    public void createFolder(String folderName, String parentId) throws GeneralSecurityException, IOException {
        googleDriveRepository.createFolder(folderName, parentId);
    }

    /**
     * Get pagable photo descriptions of user.
     * @param user - Owner(login) of photo descriptions.
     * @param page - Number of page.
     * @param pageSize - Size of page.
     * @return Map containing photo descriptions, current page number, total number of pages, total number of items.
     */
    @Override
    public Page<PhotoDescriptionResponse> getUserPhotoDescriptions(String user, int page, int pageSize) {
        Pageable paging = PageRequest.of(page, pageSize);

        return photoDescriptionRepository.findByOwner(user, paging);
    }

    /**
     * Get names of available countries for given user.
     * @param user - User to check available countries.
     * @return Set with names of countries.
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
}
