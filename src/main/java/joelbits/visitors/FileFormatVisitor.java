package joelbits.visitors;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Adds files in the current directory, and its subdirectories, to the list of absolute paths when having the supplied
 * format(s) (or extension, that is).
 *
 */
public class FileFormatVisitor extends SimpleFileVisitor<Path> {
    private final List<String> formats;
    private final List<String> paths;

    {
        paths = new ArrayList<>();
        formats = new ArrayList<>();
    }

    public FileFormatVisitor(List<String> formats) {
        this.formats.addAll(formats);
        this.formats.replaceAll(String::toLowerCase);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
        String fileName = file.getFileName().toString();
        String extension = getExtension(fileName);
        if (formats.stream().anyMatch(f -> f.endsWith(extension))) {
            paths.add(file.toAbsolutePath().toString());
        }

        return FileVisitResult.CONTINUE;
    }

    public Collection<String> getPaths() {
        return Collections.unmodifiableCollection(paths);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
    }
}
