package joelbits.converters;

import joelbits.entities.ConvertedFile;

public interface Converter {
    ConvertedFile convert(String path, String format) throws Exception;
}
