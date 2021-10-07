package TravelJournal.repository.cloudStorage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Google Drive API class. Contains method to manage cloud storage files.
 */
@Service
public class GoogleDriveImplementation implements GoogleDriveRepository {
    private static final String APPLICATION_NAME = "TravelJournal";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private final Drive service;
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public GoogleDriveImplementation() throws GeneralSecurityException, IOException {
        this.service = getDriveService();
    }

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleDriveImplementation.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * {@inheritDoc}
     */
    public void createFolder(String name, String rootFolderId) throws IOException {
        File fileMetadata = new File();

        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(rootFolderId));

        service.files().create(fileMetadata)
                .setFields("id")
                .execute();
    }

    /**
     * {@inheritDoc}
     */
    public File addPhoto(String rootFolderId, java.io.File filePath, String photoName) throws IOException {

        File fileMetadata = new File();
        fileMetadata.setName(photoName);
        fileMetadata.setParents(Collections.singletonList(rootFolderId));

        FileContent mediaContent = new FileContent("image/jpeg", filePath);

        return service.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute();
    }

    /**
     * {@inheritDoc}
     */
    public File searchFolder(String name, String parentId) throws IOException {
        FileList result = service.files().list()
                .setQ(String.format("mimeType = 'application/vnd.google-apps.folder' and name= '%s' and trashed=false and '%s' in parents", name, parentId))
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name, parents)")
                .setPageToken(null)
                .execute();
        if (result.getFiles().size() == 0) {
            return null;
        } else {
            return result.getFiles().get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public byte[] downloadPhoto(String photoId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        service.files().get(photoId).executeMediaAndDownloadTo(outputStream);
        return outputStream.toByteArray();
    }

    /**
     * {@inheritDoc}
     */
    public void deleteFile(String fileId) throws IOException {
        service.files().delete(fileId).execute();
    }

    /**
     * {@inheritDoc}
     */
    private Drive getDriveService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
