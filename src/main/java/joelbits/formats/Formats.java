package joelbits.formats;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Formats {
    PDF("pdf"), TXT("txt"), DOC("doc"), HTML("html");
    private final String format;

    Formats(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public static List<String> getFormats() {
        return Stream.of(Formats.values()).map(Formats::getFormat).collect(Collectors.toList());
    }
}
