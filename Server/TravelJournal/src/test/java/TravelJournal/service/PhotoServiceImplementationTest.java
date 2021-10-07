package TravelJournal.service;

import TravelJournal.model.photo.PhotoDescription;
import TravelJournal.payload.response.PhotoDescriptionResponse;
import TravelJournal.repository.cloudStorage.GoogleDriveImplementation;
import TravelJournal.repository.photo.PhotoDescriptionRepository;
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
    PhotoDescriptionRepository photoDescriptionRepository;

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
            photoDescriptionRepository.deleteAll();
        }

        @Test
        @DisplayName("Get available countries for given user")
        public void testGetAvailableCountries() {
            PhotoDescription polandPhotoDescription = new PhotoDescription()
                    .setOwner("TestUser")
                    .setCountry("Poland");
            PhotoDescription germanyPhotoDescription = new PhotoDescription()
                    .setOwner("TestUser")
                    .setCountry("Germany");
            PhotoDescription unitedStatesPhotoDescription = new PhotoDescription()
                    .setOwner("TestUser")
                    .setCountry("United States");
            photoDescriptionRepository.save(polandPhotoDescription);
            photoDescriptionRepository.save(germanyPhotoDescription);
            photoDescriptionRepository.save(unitedStatesPhotoDescription);
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

            PhotoDescription actualPhotoDescription = photoService.addPhoto(photoNoLocation, testUser, rootFolderId);
            com.google.api.services.drive.model.File expectedOtherFolder = photoService.getFolder(
                    "Other",
                    testUserFolder.getId());
            List<PhotoDescriptionResponse> expectedPhotoDescriptions = photoDescriptionRepository.findByOwner("TestUser");

            assertNull(actualPhotoDescription.getLatitude(),
                    "Latitude of photo with no location is not null");
            assertNull(actualPhotoDescription.getLongitude(),
                    "Longitude of photo with no location is not null");
            assertEquals(actualPhotoDescription.getCountry(),
                    expectedCountry,
                    "Photo without location should have Other as country value");
            assertNotNull(expectedOtherFolder,
                    "Did not find folder with no location photos");
            assertEquals(expectedPhotoDescriptions.size(),
                    1,
                    "Added more than one photo or photo description");
        }

        @Test
        @DisplayName("Add photo with location")
        public void testAddPhotoWithLocation() throws GeneralSecurityException, IOException {
            File photoNoLocation = new File("src/test/resources/withLocation.jpg");
            String testUser = "TestUser";
            String expectedCountry = "United Kingdom";

            PhotoDescription actualPhotoDescription = photoService.addPhoto(photoNoLocation, testUser, rootFolderId);
            com.google.api.services.drive.model.File expectedOtherFolder = photoService.getFolder(
                    "United Kingdom",
                    testUserFolder.getId());
            List<PhotoDescriptionResponse> expectedPhotoDescriptions = photoDescriptionRepository.findByOwner("TestUser");

            assertNotNull(actualPhotoDescription.getLatitude(),
                    "Latitude of photo with location is null");
            assertNotNull(actualPhotoDescription.getLongitude(),
                    "Longitude of photo with location is null");
            assertEquals(actualPhotoDescription.getCountry(),
                    expectedCountry,
                    "Photo without location should have Other as country value");
            assertNotNull(expectedOtherFolder,
                    "Did not find folder United Kingdom in TestUser folder");
            assertEquals(expectedPhotoDescriptions.size(),
                    1,
                    "Added more than one photo or photo description");
        }

        @Test
        @DisplayName("Get descriptions of user in single country")
        public void testGetPhotosInCountry() {
            PhotoDescription testUserPolandDescriptionFirst = new PhotoDescription()
                    .setCountry("Poland")
                    .setOwner("TestUser");
            PhotoDescription testUserPolandDescriptionSecond = new PhotoDescription()
                    .setCountry("Poland")
                    .setOwner("TestUser");
            PhotoDescription testUserPolandDescriptionThird = new PhotoDescription()
                    .setCountry("Poland")
                    .setOwner("TestUser");
            PhotoDescription expectedOtherUserPolandDescription = new PhotoDescription()
                    .setCountry("Poland")
                    .setOwner("OtherUser");
            PhotoDescription expectedTestUserGermanyDescription = new PhotoDescription()
                    .setCountry("Germany")
                    .setOwner("TestUser");
            photoDescriptionRepository.save(testUserPolandDescriptionFirst);
            photoDescriptionRepository.save(testUserPolandDescriptionSecond);
            photoDescriptionRepository.save(testUserPolandDescriptionThird);
            photoDescriptionRepository.save(expectedOtherUserPolandDescription);
            photoDescriptionRepository.save(expectedTestUserGermanyDescription);

            List<PhotoDescription> listTestUserPolandDescriptions = photoService.getPhotosInCountry(
                    "TestUser",
                    "Poland");
            List<PhotoDescription> listTestUserGermanyDescriptions = photoService.getPhotosInCountry(
                    "TestUser",
                    "Germany");
            List<PhotoDescription> listOtherUserPolandDescriptions = photoService.getPhotosInCountry(
                    "OtherUser",
                    "Poland");
            PhotoDescription actualTestUserGermanyDescription = listTestUserGermanyDescriptions.get(0);
            PhotoDescription actualOtherUserPolandDescription = listOtherUserPolandDescriptions.get(0);

            assertEquals(listTestUserPolandDescriptions.size(),
                    3,
                    "Should got 3 descriptions for Test User in Poland");
            assertEquals(listTestUserGermanyDescriptions.size(),
                    1,
                    "Should got 1 description for Test User in Germany");
            assertEquals(listOtherUserPolandDescriptions.size(),
                    1,
                    "Should got 1 description for Other User in Poland");
            assertThat(actualTestUserGermanyDescription)
                    .withFailMessage("Expected and actual Description for TestUser in Germany are not equal")
                    .isEqualTo(expectedTestUserGermanyDescription);
            assertThat(actualOtherUserPolandDescription)
                    .withFailMessage("Expected and actual Description for OtherUser in Poland are not equal")
                    .isEqualTo(expectedOtherUserPolandDescription);
        }

        @Test
        @DisplayName("Updating description")
        public void testUpdateDescription() {
            PhotoDescription description = photoDescriptionRepository.save(new PhotoDescription()
                    .setCountry("Poland")
                    .setOwner("TestUser")
                    .setPhotoId("1")
            );
            String updatedText = "Newly Updated Description";

            PhotoDescription updatedPhotoDescription = photoService.updateDescription(
                    description.getPhotoId(),
                    updatedText);

            assertEquals(updatedPhotoDescription.getDescription(),
                    updatedText,
                    "Should updated description");
        }

        @Test
        @DisplayName("Get single photo description")
        public void testGetPhotoDescription() {
            PhotoDescription expectedDescription = photoDescriptionRepository.save(new PhotoDescription()
                    .setCountry("Poland")
                    .setOwner("TestUser")
                    .setDate("2000-01-01")
                    .setDescription("Test Description")
                    .setLatitude(10d)
                    .setLongitude(10d)
                    .setPhotoId("1")
                    .setRotateAngle(5)
            );

            PhotoDescription actualDescription = photoService.getPhotoDescription(expectedDescription.getPhotoId());

            assertThat(actualDescription)
                    .withFailMessage("ExpectedDescription is not equal actualDescription")
                    .isEqualTo(expectedDescription);
        }

        @Test
        @DisplayName("Deleting single photo")
        public void testDeleteFileTestUserShouldHaveOnePhoto() throws GeneralSecurityException, IOException {
            File noLocationImage = new File("src/test/resources/noLocation.jpg");
            File withLocationImage = new File("src/test/resources/noLocation.jpg");
            PhotoDescription noLocationDescription = photoService.addPhoto(
                    noLocationImage,
                    "TestUser",
                    rootFolderId);
            PhotoDescription withLocationDescription = photoService.addPhoto(
                    withLocationImage,
                    "TestUser",
                    rootFolderId);

            photoService.deleteFile(noLocationDescription.getPhotoId());
            List<PhotoDescriptionResponse> testUserPhotoDescriptions = photoDescriptionRepository.findByOwner("TestUser");
            PhotoDescription notExistingDescription = photoDescriptionRepository.findByPhotoId(
                    noLocationDescription.getPhotoId());

            assertEquals(testUserPhotoDescriptions.size(),
                    1,
                    "Test User should have 1 description after delete");
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
        List<PhotoDescriptionResponse> descriptions = photoDescriptionRepository.findByOwner("TestUser");

        assertTrue(descriptions.isEmpty(),
                "User should not have any resources");
    }
}
