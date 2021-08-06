package TravelJournal.service;

import TravelJournal.model.photo.PhotoDescription;
import TravelJournal.payload.response.PhotoDescriptionResponse;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

public interface PhotoService {
//    void addPhoto(File photo);
    PhotoDescription addPhoto(File photo, String user) throws GeneralSecurityException, IOException;
    PhotoDescription updateDescription(String photoId, String description);
    byte[] getPhoto(String photoId) throws IOException, GeneralSecurityException;
    PhotoDescription getPhotoDescription(String photoId);
    List<PhotoDescriptionResponse> getPhotosInCountry(String user, String country) throws GeneralSecurityException, IOException;
    com.google.api.services.drive.model.File getFolder(String folderName, String parentId) throws GeneralSecurityException, IOException;
    List<com.google.api.services.drive.model.File> getAllFiles(String parentId, String fileType) throws IOException, GeneralSecurityException;
    Map<String, Object> getUserPhotoDescriptions(String user, int page, int pageSize) throws IOException, GeneralSecurityException;


}
