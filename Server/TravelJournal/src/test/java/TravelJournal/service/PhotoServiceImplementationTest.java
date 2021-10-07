package TravelJournal.service;

import TravelJournal.model.photo.PhotoData;
import TravelJournal.payload.response.PhotoDataResponse;
import TravelJournal.repository.cloudStorage.GoogleDriveImplementation;
import TravelJournal.repository.photo.PhotoDataRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"spring.data.mongodb.database=TestJournalDiary"})
@AutoConfigureDataMongo
public class PhotoServiceImplementationTest {

    @Autowired
    PhotoServiceImplementation photoService;

    @Autowired
    GoogleDriveImplementation cloudStorage;
    @Autowired
    PhotoDataRepository photoDataRepository;

    private com.google.api.services.drive.model.File testUserFolder = null; // Delete folder with this id after every test
    private String rootFolderId = "";

    @Nested
    public class PhotoServiceImplementationTestWithSetUp {
        @BeforeEach
        public void createTestUserFolder() throws IOException {
            String testFolderId = cloudStorage.searchFolder("Test", "root").getId();
            cloudStorage.createFolder("TestUser", testFolderId);
            testUserFolder = cloudStorage.searchFolder("TestUser", testFolderId);
            rootFolderId = testFolderId;
        }
        @AfterEach
        public void cleanUp() throws IOException {
            cloudStorage.deleteFile(testUserFolder.getId());
            photoDataRepository.deleteAll();
        }

        @Test
        @DisplayName("Get available countries for given user")
        public void testGetAvailableCountries() {
            PhotoData polandPhotoData = new PhotoData()
                    .setOwner("TestUser")
                    .setCountry("Poland");
            PhotoData germanyPhotoData = new PhotoData()
                    .setOwner("TestUser")
                    .setCountry("Germany");
            PhotoData unitedStatesPhotoData = new PhotoData()
                    .setOwner("TestUser")
                    .setCountry("United States");
            photoDataRepository.save(polandPhotoData);
            photoDataRepository.save(germanyPhotoData);
            photoDataRepository.save(unitedStatesPhotoData);
            Set<String> expectedCountries = new HashSet<>();
            expectedCountries.add("Poland");
            expectedCountries.add("Germany");
            expectedCountries.add("United States");

            Set<String> countries = photoService.getAvailableCountries("TestUser");

            assertEquals(countries.size(),
                    3,
                    "Available countries for Tester is not equal 3");
            assertEquals(expectedCountries,
                    countries,
                    "Received countries are not equal to inserted");
        }

        @Test
        @DisplayName("Add photo without location")
        public void testAddPhotoNoLocation() throws GeneralSecurityException, IOException {
            File photoNoLocation = new File("src/test/resources/noLocation.jpg");
            String testUser = "TestUser";
            String expectedCountry = "Other";

            PhotoData actualPhotoData = photoService.addPhoto(photoNoLocation, testUser, rootFolderId);
            com.google.api.services.drive.model.File expectedOtherFolder = photoService.getFolder(
                    "Other",
                    testUserFolder.getId());
            List<PhotoDataResponse> actualListPhotoData = photoDataRepository.findByOwner("TestUser");

            assertNull(actualPhotoData.getLatitude(),
                    "Latitude of photo with no location is not null");
            assertNull(actualPhotoData.getLongitude(),
                    "Longitude of photo with no location is not null");
            assertEquals(actualPhotoData.getCountry(),
                    expectedCountry,
                    "Photo without location should have Other as country value");
            assertNotNull(expectedOtherFolder,
                    "Did not find folder with no location photos");
            assertEquals(actualListPhotoData.size(),
                    1,
                    "Added more than one photo or photo data");
        }

        @Test
        @DisplayName("Add photo with location")
        public void testAddPhotoWithLocation() throws GeneralSecurityException, IOException {
            File photoNoLocation = new File("src/test/resources/withLocation.jpg");
            String testUser = "TestUser";
            String expectedCountry = "United Kingdom";

            PhotoData actualPhotoData = photoService.addPhoto(photoNoLocation, testUser, rootFolderId);
            com.google.api.services.drive.model.File expectedOtherFolder = photoService.getFolder(
                    "United Kingdom",
                    testUserFolder.getId());
            List<PhotoDataResponse> actualListPhotoData = photoDataRepository.findByOwner("TestUser");

            assertNotNull(actualPhotoData.getLatitude(),
                    "Latitude of photo with location is null");
            assertNotNull(actualPhotoData.getLongitude(),
                    "Longitude of photo with location is null");
            assertEquals(actualPhotoData.getCountry(),
                    expectedCountry,
                    "Photo without location should have Other as country value");
            assertNotNull(expectedOtherFolder,
                    "Did not find folder United Kingdom in TestUser folder");
            assertEquals(actualListPhotoData.size(),
                    1,
                    "Added more than one photo or photo data");
        }

        @Test
        @DisplayName("Get PhotoData of user in single country")
        public void testGetPhotosInCountry() {
            PhotoData testUserPolandPhotoDataFirst = new PhotoData()
                    .setCountry("Poland")
                    .setOwner("TestUser");
            PhotoData testUserPolandPhotoDataSecond = new PhotoData()
                    .setCountry("Poland")
                    .setOwner("TestUser");
            PhotoData testUserPolandPhotoDataThird = new PhotoData()
                    .setCountry("Poland")
                    .setOwner("TestUser");
            PhotoData expectedOtherUserPolandPhotoData = new PhotoData()
                    .setCountry("Poland")
                    .setOwner("OtherUser");
            PhotoData expectedTestUserGermanyPhotoData = new PhotoData()
                    .setCountry("Germany")
                    .setOwner("TestUser");
            photoDataRepository.save(testUserPolandPhotoDataFirst);
            photoDataRepository.save(testUserPolandPhotoDataSecond);
            photoDataRepository.save(testUserPolandPhotoDataThird);
            photoDataRepository.save(expectedOtherUserPolandPhotoData);
            photoDataRepository.save(expectedTestUserGermanyPhotoData);

            List<PhotoData> listTestUserPolandPhotoData = photoService.getPhotosInCountry(
                    "TestUser",
                    "Poland");
            List<PhotoData> listTestUserGermanyPhotoData = photoService.getPhotosInCountry(
                    "TestUser",
                    "Germany");
            List<PhotoData> listOtherUserPolandPhotoData = photoService.getPhotosInCountry(
                    "OtherUser",
                    "Poland");
            PhotoData actualTestUserGermanyPhotoData = listTestUserGermanyPhotoData.get(0);
            PhotoData actualOtherUserPolandPhotoData = listOtherUserPolandPhotoData.get(0);

            assertEquals(listTestUserPolandPhotoData.size(),
                    3,
                    "Should got 3 PhotoData for Test User in Poland");
            assertEquals(listTestUserGermanyPhotoData.size(),
                    1,
                    "Should got 1 PhotoData for Test User in Germany");
            assertEquals(listOtherUserPolandPhotoData.size(),
                    1,
                    "Should got 1 PhotoData for Other User in Poland");
            assertThat(actualTestUserGermanyPhotoData)
                    .withFailMessage("Expected and actual PhotoData for TestUser in Germany are not equal")
                    .isEqualTo(expectedTestUserGermanyPhotoData);
            assertThat(actualOtherUserPolandPhotoData)
                    .withFailMessage("Expected and actual PhotoData for OtherUser in Poland are not equal")
                    .isEqualTo(expectedOtherUserPolandPhotoData);
        }

        @Test
        @DisplayName("Updating description of PhotoData")
        public void testUpdateDescription() {
            PhotoData photoData = photoDataRepository.save(new PhotoData()
                    .setCountry("Poland")
                    .setOwner("TestUser")
                    .setPhotoId("1")
            );
            String updatedDescription = "Newly Updated Description";

            PhotoData updatedPhotoData = photoService.updateDescription(
                    photoData.getPhotoId(),
                    updatedDescription);

            assertEquals(updatedPhotoData.getDescription(),
                    updatedDescription,
                    "Should updated photoData");
        }

        @Test
        @DisplayName("Get single photo data")
        public void testGetPhotoData() {
            PhotoData expectedPhotoData = photoDataRepository.save(new PhotoData()
                    .setCountry("Poland")
                    .setOwner("TestUser")
                    .setDate("2000-01-01")
                    .setDescription("Test Description")
                    .setLatitude(10d)
                    .setLongitude(10d)
                    .setPhotoId("1")
                    .setRotateAngle(5)
            );

            PhotoData actualPhotoData = photoService.getPhotoData(expectedPhotoData.getPhotoId());

            assertThat(actualPhotoData)
                    .withFailMessage("expectedPhotoData is not equal actualPhotoData")
                    .isEqualTo(expectedPhotoData);
        }

        @Test
        @DisplayName("Deleting single photo")
        public void testDeleteFileTestUserShouldHaveOnePhoto() throws GeneralSecurityException, IOException {
            File noLocationImage = new File("src/test/resources/noLocation.jpg");
            File withLocationImage = new File("src/test/resources/noLocation.jpg");
            PhotoData noLocationPhotoData = photoService.addPhoto(
                    noLocationImage,
                    "TestUser",
                    rootFolderId);
            PhotoData withLocationPhotoData = photoService.addPhoto(
                    withLocationImage,
                    "TestUser",
                    rootFolderId);

            photoService.deleteFile(noLocationPhotoData.getPhotoId());
            List<PhotoDataResponse> testUserPhotoDescriptions = photoDataRepository.findByOwner("TestUser");
            PhotoData notExistingDescription = photoDataRepository.findByPhotoId(
                    noLocationPhotoData.getPhotoId());

            assertEquals(testUserPhotoDescriptions.size(),
                    1,
                    "Test User should have 1 photoData document after delete");
            assertNull(notExistingDescription,
                    "Should not find deleted resource");
        }
    }

    @Test
    @DisplayName("Deleting all resources of TestUser")
    public void testDeleteResourcesTestUserShouldNotHaveResources() throws GeneralSecurityException, IOException {
        String testFolderId = cloudStorage.searchFolder("Test", "root").getId();
        cloudStorage.createFolder("TestUser", testFolderId);
        testUserFolder = cloudStorage.searchFolder("TestUser", testFolderId);
        rootFolderId = testFolderId;
        File noLocationImage = new File("src/test/resources/noLocation.jpg");
        photoService.addPhoto(
                noLocationImage,
                "TestUser",
                rootFolderId);

        photoService.deleteResources("TestUser", "Test");
        List<PhotoDataResponse> descriptions = photoDataRepository.findByOwner("TestUser");

        assertTrue(descriptions.isEmpty(),
                "User should not have any resources");
    }
}
