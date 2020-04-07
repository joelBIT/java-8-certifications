package joelbits.converters;

import joelbits.entities.ConvertedFile;

import java.io.File;
import java.nio.file.Paths;

public interface Converter {
    String CONVERSION_DIRECTORY = "/converted/";

    ConvertedFile convert(String path, String format) throws Exception;

    default String getFileName(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

    default String getDestinationFilePath(File file, String format) {
        return Paths.get(System.getProperty("user.dir") + CONVERSION_DIRECTORY + getFileName(file) + "." + format)
                .toAbsolutePath().toString();
    }
}
