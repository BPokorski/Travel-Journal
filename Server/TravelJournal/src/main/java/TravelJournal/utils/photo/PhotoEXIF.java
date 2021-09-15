package TravelJournal.utils.photo;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Class to get exif metadata from photo/picture
 */
@Service
public class PhotoEXIF {
    private Metadata metadata = new Metadata();

    /**
     * Method to get date of taking a picture
     * @param photo - picture to get date from
     * @return date in yyyy-mm-dd format
     */
    public String getPhotoDate(File photo) {

        try {
            metadata = ImageMetadataReader.readMetadata(photo);

        } catch (IOException | ImageProcessingException e) {
            e.printStackTrace();
        }
        Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        int dateTag = ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL;

        if (directory != null && directory.containsTag(dateTag)) {
            Date date = directory.getDate(dateTag, TimeZone.getDefault());
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        } else {
            return "1900-01-01";
        }
    }

    /**
     * Method to get location where picture was taken
     * @param photo - picture to get location from
     * @return GeoLocation object which stores coordinates
     */
    public GeoLocation getPhotoLocation(File photo) {
        GeoLocation geoLocation = null;
        try {
            metadata = ImageMetadataReader.readMetadata(photo);

        } catch (IOException | ImageProcessingException e) {
            e.printStackTrace();
        }

        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

        if (gpsDirectory != null) {
            geoLocation = gpsDirectory.getGeoLocation();
        }
        return geoLocation;
    }
}

