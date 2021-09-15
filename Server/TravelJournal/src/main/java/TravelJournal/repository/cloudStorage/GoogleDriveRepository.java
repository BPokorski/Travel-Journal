package TravelJournal.repository.cloudStorage;

import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface GoogleDriveRepository {
    void createFolder(String name, String rootFolderId) throws IOException, GeneralSecurityException;
    File addPhoto(String rootFolderId, java.io.File filePath, String photoName) throws IOException, GeneralSecurityException;
    File searchFolder(String name, String rootFolderId) throws GeneralSecurityException, IOException;
    byte[] downloadPhoto(String photoId) throws IOException, GeneralSecurityException;
    void deleteFile(String photoId) throws GeneralSecurityException, IOException;
}
