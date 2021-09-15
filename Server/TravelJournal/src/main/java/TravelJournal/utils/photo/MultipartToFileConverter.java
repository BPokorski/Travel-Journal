package TravelJournal.utils.photo;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class to convert Multipart files used as requests or responses of images to Java File Class.
 */
public class MultipartToFileConverter {

    /**
     * Convert Multipart file to Java File
     * @param file - requested image
     * @return image in Java File format
     */
    public File convert(MultipartFile file) throws IOException {

        File convertedFile = new File(file.getOriginalFilename());
        convertedFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        file.transferTo(convertedFile);

        return convertedFile;
    }
}
