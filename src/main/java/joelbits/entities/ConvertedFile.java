package joelbits.entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

public class ConvertedFile {
    private final String fileName;
    private final String format;
    private final double size;
    private final Timestamp conversionDate;

    public ConvertedFile(String fileName, String format, double size, LocalDateTime conversionDate) {
        this.fileName = fileName;
        this.format = format;
        this.size = size;
        this.conversionDate = Timestamp.valueOf(conversionDate);
    }

    public String getFileName() {
        return fileName;
    }

    public String getFormat() {
        return format;
    }

    public double getSize() {
        return size;
    }

    public Timestamp getConversionDate() {
        return conversionDate;
    }

    @Override
    public String toString() {
        return fileName + " of size " + size + " was converted to format " + format + " at date and time " + conversionDate;
    }

    @Override
    public int hashCode() {
        return fileName.length() + conversionDate.getNanos();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ConvertedFile) {
            ConvertedFile file = (ConvertedFile) object;
            return Objects.equals(this.fileName, file.fileName) && Objects.equals(this.conversionDate.getNanos(), file.conversionDate.getNanos());
        }
        return false;
    }
}
