package joelbits.entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ConvertedFile {
    private String fileName;
    private String format;
    private double size;
    private Timestamp conversionDate;

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
}
