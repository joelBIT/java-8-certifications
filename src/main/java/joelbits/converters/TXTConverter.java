package joelbits.converters;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import joelbits.entities.ConvertedFile;
import joelbits.exception.ConverterNotFoundException;
import joelbits.formats.Formats;
import sun.misc.IOUtils;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static joelbits.formats.Formats.PDF;

/**
 * Converts TXT files to supplied format, if supported.
 */
public class TXTConverter implements Converter {
    @Override
    public ConvertedFile convert(String path, String format) throws Exception {
        File file = Paths.get(path).toFile();

        switch (Formats.valueOf(format.toUpperCase())) {
            case PDF:
                toPDF(file);
                break;
            default:
                throw new ConverterNotFoundException("Converting PDF to format " + format + " is not supported");
        }

        return new ConvertedFile(file.getName(), format, 0.0, LocalDateTime.now());
    }

    private void toPDF(File file) throws IOException, DocumentException {
        Document pdf = new Document(PageSize.A4);
        try (FileInputStream inputStream = new FileInputStream(file); OutputStream outputStream =
                new FileOutputStream(getDestinationFilePath(file, PDF.getFormat()))) {

            PdfWriter.getInstance(pdf, outputStream).setPdfVersion(PdfWriter.PDF_VERSION_1_7);
            pdf.open();
            pdf.add(new Paragraph("\n"));
            addTextToDocument(IOUtils.readAllBytes(inputStream), pdf, createFont());
        } finally {
            if (pdf.isOpen()) {
                pdf.close();
            }
        }
    }

    private Font createFont() throws DocumentException, IOException {
        BaseFont courier = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED);
        Font font = new Font(courier);
        font.setStyle(Font.NORMAL);
        font.setSize(11);

        return font;
    }

    private void addTextToDocument(byte[] fileData, Document pdf, Font font) throws IOException, DocumentException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileData)))) {
            String strLine;
            while ((strLine = reader.readLine()) != null) {
                Paragraph paragraph = new Paragraph(strLine + "\n", font);
                paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
                pdf.add(paragraph);
            }
        }
    }
}
