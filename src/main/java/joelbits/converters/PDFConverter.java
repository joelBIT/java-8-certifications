package joelbits.converters;

import joelbits.entities.ConvertedFile;
import joelbits.exception.ConverterNotFoundException;
import joelbits.formats.Formats;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.fit.pdfdom.PDFDomTree;
import sun.misc.IOUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * Converts PDF documents to supplied format, if supported.
 */
public class PDFConverter implements Converter {
    @Override
    public ConvertedFile convert(String path, String format) throws Exception {
        File file = Paths.get(path).toFile();

        switch(Formats.valueOf(format.toUpperCase())) {
            case HTML:
                toHTML(file);
                break;
            case DOC:
            case TXT:
                toTXT(file);
                break;
            case PDF:
                throw new ConverterNotFoundException("The file is already of type PDF");
            default:
                throw new ConverterNotFoundException("Converting PDF to format " + format + " is not supported");
        }

        return new ConvertedFile(file.getName(), format, 0.0, LocalDateTime.now());
    }

    private void toHTML(File file) {
        try (PDDocument pdf = PDDocument.load(file); Writer output = new PrintWriter("src/output/pdf.html", "utf-8")) {
            new PDFDomTree().writeText(pdf, output);

        } catch (IOException | ParserConfigurationException e) {
            System.out.println("Conversion failed due to " + e.getMessage());
        }
    }

    private void toTXT(File file) throws IOException {
        System.out.println("In TXT");
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] fileData = IOUtils.readAllBytes(inputStream);
            PDFParser parser = new PDFParser(new RandomAccessBuffer(fileData));
            parser.parse();

            try (COSDocument document = parser.getDocument(); PrintWriter writer = new PrintWriter(Paths
                    .get(System.getProperty("user.dir") + CONVERSION_DIRECTORY + file.getName() + ".txt")
                    .toAbsolutePath().toString(), "utf-8"); PDDocument pdf = new PDDocument(document)) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String parsedText = pdfStripper.getText(pdf);
                writer.print(parsedText);
            }
        }
    }
}
