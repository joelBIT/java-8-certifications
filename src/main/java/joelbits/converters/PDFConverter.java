package joelbits.converters;

import joelbits.formats.Formats;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * Converts PDF documents to supplied format, if supported.
 */
public class PDFConverter implements Converter {
    @Override
    public void convert(File file, String format) {

        switch(Formats.valueOf(format)) {
            case HTML:
                toHTML(file);
            case DOC:
            case TXT:
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
}
