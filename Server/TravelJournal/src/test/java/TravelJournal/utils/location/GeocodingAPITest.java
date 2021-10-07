package TravelJournal.utils.location;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GeocodingAPITest {

    @Autowired
    private GeocodingAPI geocodingAPI;

    @Test
    @DisplayName("Place with 0 degrees latitude and 0 degrees longitude should be ocean")
    public void testGetCountryWithZeroValuesOfCoordinates() {
        String expectedPlace = "Ocean";

        String actualPlace = geocodingAPI.coordinatesToCountry(0, 0);

        assertEquals(actualPlace,
                expectedPlace,
                "Place with 0 value of latitude and longitude is not ocean");
    }

    @Test
    @DisplayName("Place with 51 degrees latitude and 19 degrees longitude should be Poland")
    public void testGetCountryPoland() {
        String expectedCountry = "Poland";

        String actualCountry = geocodingAPI.coordinatesToCountry(51, 19);

        assertEquals(actualCountry,
                expectedCountry,
                "Place with 51 degrees latitude and 19 degrees longitude is "
                        .concat(actualCountry)
                        .concat(" while should be Poland"));
    }
}
