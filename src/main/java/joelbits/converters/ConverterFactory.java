package joelbits.converters;

import joelbits.exception.ConverterNotFoundException;
import joelbits.formats.Formats;

import java.nio.file.Path;

public final class ConverterFactory {

    private ConverterFactory() {}

    public static Converter getConverter(Path file) throws ConverterNotFoundException {
        String fileName = file.getFileName().toString();
        String extension = fileName.substring(fileName.lastIndexOf(".")+1);
        switch (Formats.valueOf(extension.toUpperCase())) {
            case PDF:
                return new PDFConverter();
            case HTML:
            case TXT:
            default:
                throw new ConverterNotFoundException("Could not find converter for " + extension);
        }
    }
}
