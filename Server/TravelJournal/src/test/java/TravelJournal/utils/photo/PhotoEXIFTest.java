package TravelJournal.utils.photo;

import com.drew.lang.GeoLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
public class PhotoEXIFTest {

    private PhotoEXIF photoEXIF;

    @BeforeEach
    void setUp() {
        photoEXIF = new PhotoEXIF();
    }

    @Test
    @DisplayName("Date from photo without location should be 2018-07-07")
    public void testGetPhotoDateFromPhotoWithoutLocation() {
        String expectedDate = "2018-07-07";
        File photo = new File("src/test/resources/noLocation.jpg");

        String actualDate = photoEXIF.getPhotoDate(photo);

        assertEquals(expectedDate,
                actualDate,
                "Actual date is different from expected");
    }

    @Test
    @DisplayName("Date from photo with location should be 2018-08-22")
    public void testGetPhotoDateFromPhotoWithLocation() {
        String expectedDate = "2018-08-22";
        File photo = new File("src/test/resources/withLocation.jpg");

        String actualDate = photoEXIF.getPhotoDate(photo);

        assertEquals(expectedDate,
                actualDate,
                "Actual date is different from expected");
    }

    @Test
    @DisplayName("Location from photo with location should not be null")
    public void testGetPhotoLocationFromPhotoWithLocation() {
        File photo = new File("src/test/resources/withLocation.jpg");

        GeoLocation actualLocation = photoEXIF.getPhotoLocation(photo);

        assertNotNull(actualLocation,
                "Location is null");
    }

    @Test
    @DisplayName("Location from photo without location should be null")
    public void testPhotoLocationFromPhotoWithoutLocationShouldBeNull() {
        File photo = new File("src/test/resources/noLocation.jpg");

        GeoLocation actualLocation = photoEXIF.getPhotoLocation(photo);

        assertNull(actualLocation,
                "Location is not null");
    }

    @Test
    @DisplayName("Rounded value of location should be 51.50411 degrees of latitude and -0.07458 degrees of Longitude")
    public void testGetPhotoLocationCoordinatesValue() {
        File photo = new File("src/test/resources/withLocation.jpg");
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.00000", decimalFormatSymbols);
        double expectedLatitude = 51.50411;
        double expectedLongitude = -0.07458;

        // values are round to 5th decimal place as it is millimeter precision.
        GeoLocation location = photoEXIF.getPhotoLocation(photo);
        String actualLatitudeString = decimalFormat.format(location.getLatitude());
        String actualLongitudeString = decimalFormat.format(location.getLongitude());

        assertTrue(Double.parseDouble(actualLatitudeString) == expectedLatitude &&
                Double.parseDouble(actualLongitudeString) == expectedLongitude,
                "Actual longitude and latitude are not equal expected");
    }
}
