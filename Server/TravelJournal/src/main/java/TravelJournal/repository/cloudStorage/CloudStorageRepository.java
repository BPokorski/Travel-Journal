package TravelJournal.repository.cloudStorage;

import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface CloudStorageRepository {
    File createFolder(String name, String rootFolderId) throws IOException, GeneralSecurityException;
    File addPhoto(String rootFolderId, java.io.File filePath, String photoName) throws IOException, GeneralSecurityException;
    File searchFolder(String name, String rootFolderId) throws GeneralSecurityException, IOException;
    List<File> getAllFilesInFolder(String rootFolderId, String fileType) throws IOException, GeneralSecurityException;
    byte[] downloadPhoto(String photoId) throws IOException, GeneralSecurityException;
    void deletePhoto(String photoId) throws GeneralSecurityException, IOException;
}
