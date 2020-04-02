package joelbits.tasks;

import joelbits.converters.Converter;
import joelbits.converters.ConverterFactory;
import joelbits.entities.ConvertedFile;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class ConverterTask extends RecursiveTask<List<ConvertedFile>> {
    private final List<String> filePaths;
    private final String format;
    private final List<ConvertedFile> convertedFiles;

    public ConverterTask(List<String> filePaths, String format) {
        this(filePaths, format, Collections.emptyList());
    }

    public ConverterTask(List<String> filePaths, String format, List<ConvertedFile> convertedFiles) {
        this.filePaths = Collections.synchronizedList(filePaths);
        this.format = format;
        this.convertedFiles = Collections.synchronizedList(convertedFiles);
    }

    @Override
    protected List<ConvertedFile> compute() {
        synchronized (filePaths) {
            try {
                if (filePaths.isEmpty()) {
                    return convertedFiles;
                }
                String filePath = filePaths.get(0);
                filePaths.remove(0);

                Converter converter = ConverterFactory.getConverter(getExtension(filePath));
                converter.convert(filePath, format);

            } catch (Exception e) {
                System.out.println("Failed to convert file due to: " + e.getMessage());
            }

            int middle = filePaths.size()/2;
            ConverterTask task = new ConverterTask(filePaths.subList(0, middle), format, convertedFiles);
            task.fork();

            return new ConverterTask(filePaths.subList(middle, filePaths.size()), format, task.join()).compute();
        }
    }

    private String getExtension(String filePath) {
        return filePath.substring(filePath.lastIndexOf(".")+1).toLowerCase();
    }
}
