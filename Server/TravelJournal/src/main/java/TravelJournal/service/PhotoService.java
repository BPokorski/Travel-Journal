package TravelJournal.service;

import TravelJournal.model.photo.PhotoDescription;
import TravelJournal.payload.response.PhotoDescriptionResponse;
import org.springframework.data.domain.Page;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PhotoService {
    TravelJournal.model.photo.PhotoDescription addPhoto(File photo, String user) throws GeneralSecurityException, IOException;
    TravelJournal.model.photo.PhotoDescription updateDescription(String photoId, String description);
    byte[] getPhoto(String photoId) throws IOException, GeneralSecurityException;
    TravelJournal.model.photo.PhotoDescription getPhotoDescription(String photoId);
    List<PhotoDescription> getPhotosInCountry(String user, String country);
    com.google.api.services.drive.model.File getFolder(String folderName, String parentId) throws GeneralSecurityException, IOException;
    void createFolder(String folderName, String parentId) throws IOException, GeneralSecurityException;
    Page<PhotoDescriptionResponse> getUserPhotoDescriptions(String user, int page, int pageSize) throws IOException, GeneralSecurityException;
    Set<String> getAvailableCountries(String user);


}
