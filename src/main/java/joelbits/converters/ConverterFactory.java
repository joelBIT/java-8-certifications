package joelbits.converters;

import joelbits.exception.ConverterNotFoundException;
import joelbits.formats.Formats;

public final class ConverterFactory {

    private ConverterFactory() {}

    public static synchronized Converter getConverter(String extension) throws ConverterNotFoundException {
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
