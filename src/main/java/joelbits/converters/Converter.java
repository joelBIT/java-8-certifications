package joelbits.converters;

import joelbits.entities.ConvertedFile;

public interface Converter {
    String CONVERSION_DIRECTORY = "/converted/";

    ConvertedFile convert(String path, String format) throws Exception;
}
