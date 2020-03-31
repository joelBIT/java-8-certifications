package joelbits.visitors;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FileFormatVisitor extends SimpleFileVisitor<Path> {
    private final String format;
    private final List<String> paths;

    public FileFormatVisitor(String format) {
        this.format = format;
        paths = new ArrayList<>();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
        if (file.getFileName().toString().endsWith("." + format)) {
            paths.add(file.toAbsolutePath().toString());
        }

        return FileVisitResult.CONTINUE;
    }

    public Collection<String> getPaths() {
        return Collections.unmodifiableCollection(paths);
    }
}
