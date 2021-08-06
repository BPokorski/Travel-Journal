package TravelJournal.utils.photo;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MultipartToFileConverter {
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
