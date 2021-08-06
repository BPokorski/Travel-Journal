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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CloudStorageImplementation implements CloudStorageRepository {
    private static final String APPLICATION_NAME = "TravelJournal";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = CloudStorageImplementation.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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


    public File createFolder(String name, String rootFolderId) throws IOException, GeneralSecurityException {
        Drive service = getDriveService();
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(rootFolderId));

        File folders = searchFolder(name, rootFolderId);

        // if folder already exists get it
        if (folders == null) {
            File newFolder = service.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            System.out.println("Folder ID: " + newFolder.getId());
            return newFolder;
        } else {
            System.out.println("Folder ID: " + folders.getId());
            return folders;
        }

    }

    public File addPhoto(String rootFolderId, java.io.File filePath, String photoName) throws IOException, GeneralSecurityException {
        Drive service = getDriveService();
        File fileMetadata = new File();
        fileMetadata.setName(photoName);
        fileMetadata.setParents(Collections.singletonList(rootFolderId));

        FileContent mediaContent = new FileContent("image/jpeg", filePath);

        File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute();
        System.out.println("File ID: " + file.getId());
        return file;
    }

    public File searchFolder(String name, String parentId) throws GeneralSecurityException, IOException {
        Drive service = getDriveService();
        String pageToken = null;
        FileList result = service.files().list()
                .setQ(String.format("mimeType = 'application/vnd.google-apps.folder' and name= '%s' and trashed=false and '%s' in parents", name, parentId))
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
                .setPageToken(pageToken)
                .execute();

        if (result.getFiles().size() == 0) {
            return null;
        } else {
            return result.getFiles().get(0);
        }
    }
    public List<File> getAllFilesInFolder(String parentId, String fileType) throws IOException, GeneralSecurityException {
        Drive service = getDriveService();
        String pageToken = null;
        String mimeType = "";
        List<File> photos= new ArrayList<>();
        if(fileType.equals("folder")) {
            mimeType = "application/vnd.google-apps.folder";
        } else {
            mimeType = "image/jpeg";
        }
        FileList result = service.files().list()
                .setQ(String.format("mimeType = '%s' and trashed=false and '%s' in parents",mimeType, parentId))
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
                .setPageToken(pageToken)
                .execute();
        if (result.getFiles().size() == 0) {
            return photos;
        } else {
            return result.getFiles();
        }
    }

    public byte[] downloadPhoto(String photoId) throws IOException, GeneralSecurityException {
        java.io.File photoDownloaded;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Drive service = getDriveService();
        service.files().get(photoId).executeMediaAndDownloadTo(outputStream);
        return outputStream.toByteArray();
    }

    public void deletePhoto(String photoId) throws GeneralSecurityException, IOException {
        Drive service = getDriveService();
        service.files().delete(photoId).execute();

    }

    private Drive getDriveService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }

}
