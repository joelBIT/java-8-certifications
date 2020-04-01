package joelbits.converters;

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
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Converts PDF documents to supplied format, if supported.
 */
public class PDFConverter implements Converter {
    @Override
    public void convert(Path file, String format) throws Exception {

        switch(Formats.valueOf(format.toUpperCase())) {
            case HTML:
                toHTML(file.toFile());
                break;
            case DOC:
            case TXT:
                toTXT(file);
                break;
            case PDF:
                System.out.println("The file is already of type PDF");
                break;
            default:
                System.out.println("Converting PDF to format " + format + " is not supported");

        }

    }

    private void toHTML(File file) {
        try (PDDocument pdf = PDDocument.load(file); Writer output = new PrintWriter("src/output/pdf.html", "utf-8")) {
            new PDFDomTree().writeText(pdf, output);

        } catch (IOException | ParserConfigurationException e) {
            System.out.println("Conversion failed due to " + e.getMessage());
        }
    }

    private void toTXT(Path file) throws IOException {
        System.out.println("In TXT");
        try (FileInputStream inputStream = new FileInputStream(file.toFile())) {
            byte[] fileData = IOUtils.readAllBytes(inputStream);
            PDFParser parser = new PDFParser(new RandomAccessBuffer(fileData));
            parser.parse();

            try (COSDocument document = parser.getDocument(); PrintWriter writer = new PrintWriter(Paths.get(System.getProperty("user.dir") + "/converted/testing.txt").toAbsolutePath().toString(), "utf-8"); PDDocument pdf = new PDDocument(document)) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String parsedText = pdfStripper.getText(pdf);
                writer.print(parsedText);
            }
        }
    }
}
