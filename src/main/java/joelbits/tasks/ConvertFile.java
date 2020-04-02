package joelbits.tasks;

import joelbits.converters.Converter;
import joelbits.converters.ConverterFactory;
import joelbits.entities.ConvertedFile;

import java.util.concurrent.Callable;

public class ConvertFile implements Callable<ConvertedFile> {
    private final String filePath;
    private final String format;

    public ConvertFile(String filePath, String format) {
        this.filePath = filePath;
        this.format = format;
    }

    @Override
    public ConvertedFile call() throws Exception {
        Converter converter = ConverterFactory.getConverter(getExtension(filePath));
        return converter.convert(filePath, format);
    }

    private String getExtension(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".")+1).toLowerCase();
        System.out.println("Extension when retrieving converter is " + extension);
        return extension;
    }
}
