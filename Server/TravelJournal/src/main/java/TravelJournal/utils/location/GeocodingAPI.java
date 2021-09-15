package TravelJournal.utils.location;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageReverseRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Class containing geocoding API.
 * Geocoding is the process of converting addresses into geographic coordinates like latitude and longitude.
 * Reverse geocoding allows to convert given coordinates into addresses or places name.
 */
@Component
@Scope("singleton")
public class GeocodingAPI {

    private final JOpenCageGeocoder geocoder;
    
    public GeocodingAPI(@Value("${traveljournal.app.geocodingAPIKey}") String apiKey) {
        this.geocoder = new JOpenCageGeocoder(apiKey);
    }
    /**
     * Reverese geocoding method. Return country in which is place with given coordinates.
     * @param latitude - Geodetic Latitude, range between -90 to 90 degrees.
     * @param longitude - Geodetic Longitude, range between -180 to 180 degrees.
     * @return Country in which place with given coordinates exists.
     */
    public String coordinatesToCountry(double latitude, double longitude) {
        JOpenCageReverseRequest request = new JOpenCageReverseRequest(
                latitude,
                longitude);

        request.setLanguage("en");
        request.setNoDedupe(true);
        request.setLimit(2);
        request.setNoAnnotations(true);

        JOpenCageResponse response = geocoder.reverse(request);
        String country = response.getResults().get(0).getComponents().getCountry();
        if (country == null) {
            return "Ocean";
        } else {
            return country;
        }
    }
}
