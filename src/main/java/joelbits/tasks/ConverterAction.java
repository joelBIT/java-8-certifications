package joelbits.tasks;

import joelbits.converters.Converter;
import joelbits.converters.ConverterFactory;
import joelbits.entities.ConvertedFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class ConverterAction extends RecursiveAction {
    private final List<String> filePaths;
    private final String format;
    private final List<ConvertedFile> convertedFiles;
    private static final int THRESHOLD = 1;

    public ConverterAction(List<String> filePaths, String format) {
        this(Collections.synchronizedList(filePaths), format, new ArrayList<>());
    }

    private ConverterAction(List<String> filePaths, String format, List<ConvertedFile> convertedFiles) {
        this.filePaths = filePaths;
        this.format = format;
        this.convertedFiles = convertedFiles;
    }

    @Override
    protected void compute() {
        if (filePaths.size() == THRESHOLD) {
            try {
                String filePath = filePaths.get(0);
                Converter converter = ConverterFactory.getConverter(getExtension(filePath));
                convertedFiles.add(converter.convert(filePath, format));
            } catch (Exception e) {
                System.out.println("Failed to convert file due to: " + e.getMessage());
            }
        } else {
            int middle = filePaths.size() / 2;
            invokeAll(new ConverterAction(filePaths.subList(0, middle), format, convertedFiles),
                    new ConverterAction(filePaths.subList(middle, filePaths.size()), format, convertedFiles));
        }
    }

    private String getExtension(String filePath) {
        return filePath.substring(filePath.lastIndexOf(".")+1).toLowerCase();
    }

    public List<ConvertedFile> getConvertedFiles() {
        return Collections.unmodifiableList(convertedFiles);
    }
}
