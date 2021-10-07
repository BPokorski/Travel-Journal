package TravelJournal.repository.cloudStorage;

import com.google.api.services.drive.model.File;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GoogleDriveTest {

    private String testUserId = ""; // Delete folder with this id after every test

    @Autowired
    private GoogleDriveImplementation cloudStorage;

    @BeforeEach
    public void createTestUserFolder() throws IOException {
        String testFolderId = cloudStorage.searchFolder("Test", "root").getId();
        cloudStorage.createFolder("TestUser", testFolderId);
        testUserId = cloudStorage.searchFolder("TestUser", testFolderId).getId();
    }
    @AfterEach
    public void cleanUp() throws IOException {
        cloudStorage.deleteFile(testUserId);
    }

    @Test
    @DisplayName("Create Folder TestCountry in TestUser folder and get its id")
    public void testCreateAndGetFolder() throws IOException {
        String actualFolderName = "TestCountry";
        cloudStorage.createFolder(actualFolderName, testUserId);

        File testCountryFolder = cloudStorage.searchFolder(actualFolderName, testUserId);

        assertEquals(testUserId,
                testCountryFolder.getParents().get(0),
                "Folder was not created on parent directory TestUser but in directory "
                        .concat(testCountryFolder.getParents().get(0)));
        assertEquals(actualFolderName,
                testCountryFolder.getName(),
                "Created folder with name "
                        .concat(testCountryFolder.getName())
                        .concat(" whereas its name should be ")
                        .concat(actualFolderName));
    }

    @Test
    @DisplayName("Uploaded and downloaded photos should be equal")
    public void testUploadAndDownloadPhoto() throws IOException {
        java.io.File expectedPhotoFile = new java.io.File("src/test/resources/withLocation.jpg");

        File testPhotoFile = cloudStorage.addPhoto(testUserId,
                expectedPhotoFile,
                expectedPhotoFile.getName());
        byte[] testPhotoFileBytesArray = cloudStorage.downloadPhoto(testPhotoFile.getId());
        byte[] expectedPhotoFileByteArray = Files.readAllBytes(expectedPhotoFile.toPath());

        assertArrayEquals(expectedPhotoFileByteArray,
                testPhotoFileBytesArray,
                "Uploaded and downloaded photos are not equal");
    }
}
