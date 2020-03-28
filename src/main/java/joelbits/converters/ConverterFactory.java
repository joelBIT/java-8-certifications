package joelbits.converters;

import joelbits.exception.ConverterNotFoundException;
import joelbits.formats.Formats;

import java.io.File;

public final class ConverterFactory {

    private ConverterFactory() {}

    static Converter getConverter(File file) throws ConverterNotFoundException {
        String extension = file.getName().substring(file.getName().lastIndexOf("."));
        switch (Formats.valueOf(extension)) {
            case PDF:
                return new PDFConverter();
            case HTML:
            case TXT:
            default:
                throw new ConverterNotFoundException("Could not find converter for " + extension);
        }
    }
}
