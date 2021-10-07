package TravelJournal.repository.cloudStorage;

import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleDriveRepository {
    /**
     * Create new folder in given folder or root hierarchy.
     * @param name - Name of folder.
     * @param rootFolderId - Id of root folder to be created in, "root" if to create folder in top of hierarchy.
     * @throws IOException If the credentials.json file cannot be found.
     */
    void createFolder(String name, String rootFolderId) throws IOException, GeneralSecurityException;
    /**
     * Add photo to given folder.
     * @param rootFolderId - Id of folder in which new photo will be placed. "Root" if there isn't any folder.
     * @param filePath - File containing image.
     * @param photoName - Name of file created in Google Drive.
     * @return newly created photo.
     * @throws IOException If the credentials.json file cannot be found.
     */
    File addPhoto(String rootFolderId, java.io.File filePath, String photoName) throws IOException, GeneralSecurityException;
    /**
     * Search for specific folder.
     * @param name - Name of folder to be found.
     * @param parentId - Id of parent folder or "root" if there isn't any.
     * @return Found folder or null if there is no such a folder.
     * @throws IOException If the credentials.json file cannot be found.
     */
    File searchFolder(String name, String parentId) throws GeneralSecurityException, IOException;
    /**
     * Get photo with given id.
     * @param photoId - Google Drive Id of photo.
     * @return Byte array representation of image.
     * @throws IOException If the credentials.json file cannot be found.
     */
    byte[] downloadPhoto(String photoId) throws IOException, GeneralSecurityException;
    /**
     * Delete photo with given id.
     * @param fileId - Google Drive Id of photo.
     * @throws IOException If the credentials.json file cannot be found.
     */
    void deleteFile(String fileId) throws GeneralSecurityException, IOException;
}
