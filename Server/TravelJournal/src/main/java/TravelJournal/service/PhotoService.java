package TravelJournal.service;

import TravelJournal.model.photo.PhotoData;
import TravelJournal.payload.response.PhotoDataResponse;
import org.springframework.data.domain.Page;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Set;

public interface PhotoService {
    /**
     * Add new photo for given user.
     * @param photo - Image to be added.
     * @param user - User which want to add photo.
     * @param parentId - Id of parent folder in Google Drive. "root" if there is no-one.
     * @return PhotoData of newly created resource.
     * @throws GeneralSecurityException When authentication fails to Google Drive API.
     * @throws IOException When something goes wrong with Google Drive.
     */
    PhotoData addPhoto(File photo, String user, String parentId) throws GeneralSecurityException, IOException;

    /**
     * Update Description of photo.
     * @param photoId - Id of photo to change description.
     * @param description - New description.
     * @return Updated PhotoData.
     */
    PhotoData updateDescription(String photoId, String description);

    /**
     * Get Image with given Id.
     * @param photoId - Id of photo to be displayed.
     * @return Representation of image in bytes.
     * @throws GeneralSecurityException when authentication fails to Google Drive API.
     * @throws IOException when something goes wrong with Google Drive.
     */
    byte[] getPhoto(String photoId) throws IOException, GeneralSecurityException;

    /**
     * Get single PhotoData with given id.
     * @param photoId - Id of Photo.
     * @return PhotoData.
     */
    PhotoData getPhotoData(String photoId);

    /**
     * Get PhotoData in single country for given user.
     * @param user - Authenticated user.
     * @param country - Name of country to get available description.
     * @return List of PhotoData.
     */
    List<PhotoData> getPhotosInCountry(String user, String country);

    /**
     * Get single folder from Google Cloud Storage with given name and id of its parent.
     * @param folderName - Name of folder.
     * @param parentId - Id of parent folder in Google Drive. "root" if there is no-one.
     * @throws GeneralSecurityException When authentication fails to Google Drive API.
     * @throws IOException When something goes wrong with Google Drive.
     * @return Google Drive Folder
     */
    com.google.api.services.drive.model.File getFolder(String folderName, String parentId) throws GeneralSecurityException, IOException;

    /**
     * Create folder in Google Cloud Storage with given name and id of its parent.
     * @param folderName - Name of folder to create.
     * @param parentId - Id of parent folder in Google Drive. "root" if there is no-one.
     * @throws GeneralSecurityException - When authentication fails to Google Drive API.
     * @throws IOException - When something goes wrong with Google Drive.
     */
    void createFolder(String folderName, String parentId) throws IOException, GeneralSecurityException;

    /**
     * Get pagable PhotoData of user.
     * @param user - Owner(login) of PhotoData.
     * @param page - Number of page.
     * @param pageSize - Size of page.
     * @return Map containing PhotoData, current page number, total number of pages, total number of items.
     */
    Page<PhotoDataResponse> getUserPhotoData(String user, int page, int pageSize) throws IOException, GeneralSecurityException;

    /**
     * Get names of available countries for given user.
     * @param user - User to check available countries.
     * @return Set with names of countries.
     */
    Set<String> getAvailableCountries(String user);

    /**
     * Delete photo with given id.
     * @param fileId - Id of photo to be deleted.
     * @throws GeneralSecurityException - When authentication fails to Google Drive API.
     * @throws IOException - When something goes wrong with Google Drive.
     */
    void deleteFile(String fileId) throws GeneralSecurityException, IOException;

    /**
     * Delete all resources of given user.
     * @param login - User login.
     * @param applicationFolder - Folder in User folder was created. "Application" is used basically.
     * @throws GeneralSecurityException - When authentication fails to Google Drive API.
     * @throws IOException - When something goes wrong with Google Drive.
     */
    void deleteResources(String login, String applicationFolder) throws GeneralSecurityException, IOException;
}
