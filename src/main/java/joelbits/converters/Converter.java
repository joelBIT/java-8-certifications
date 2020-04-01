package joelbits.converters;

import java.nio.file.Path;

public interface Converter {
    void convert(Path file, String format) throws Exception;
}
