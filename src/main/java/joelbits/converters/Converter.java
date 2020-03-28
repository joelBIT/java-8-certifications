package joelbits.converters;

import java.io.File;

public interface Converter {
    void convert(File file, String format);
}
