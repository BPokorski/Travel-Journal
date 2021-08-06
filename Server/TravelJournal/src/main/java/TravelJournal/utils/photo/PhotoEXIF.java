package TravelJournal.utils.photo;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;

@Service
public class PhotoEXIF {
    public String getPhotoDate(File photo) {

        String customDate = "2000 JANUARY 1";
        Metadata metadata = new Metadata();
        try {
            metadata = ImageMetadataReader.readMetadata(photo);

        } catch (IOException | ImageProcessingException e) {
            e.printStackTrace();
        }
        Collection<ExifSubIFDDirectory> exifDirectories = metadata.getDirectoriesOfType(ExifSubIFDDirectory.class);
        for (ExifSubIFDDirectory exifDirectory : exifDirectories) {
            Date date = exifDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            customDate =localDate.getYear() + " " + localDate.getMonth() + " " + localDate.getDayOfMonth();
            break;
        }
        return customDate;
    }
    public GeoLocation getPhotoLocation(File photo) {
        GeoLocation geoLocation = null;

        Metadata metadata = new Metadata();
        try {
            metadata = ImageMetadataReader.readMetadata(photo);

        } catch (IOException | ImageProcessingException e) {
            e.printStackTrace();
        }
        Collection<GpsDirectory> gpsDirectories = metadata.getDirectoriesOfType(GpsDirectory.class);

        for (GpsDirectory gpsDirectory : gpsDirectories) {
            // Try to read out the location, making sure it's non-zero
            geoLocation = gpsDirectory.getGeoLocation();
            if (geoLocation != null && !geoLocation.isZero()) {

                break;
            }
        }

        return geoLocation;
    }
}

