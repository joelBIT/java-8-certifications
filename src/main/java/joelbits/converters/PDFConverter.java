package joelbits.converters;

import joelbits.entities.ConvertedFile;
import joelbits.exception.ConverterNotFoundException;
import joelbits.formats.Formats;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.fit.pdfdom.PDFDomTree;
import sun.misc.IOUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static joelbits.formats.Formats.HTML;
import static joelbits.formats.Formats.TXT;

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
            case BMP:
            case GIF:
            case JPG:
            case PNG:
            case JPEG:
                toImage(file, format);
                break;
            case PDF:
                throw new ConverterNotFoundException("The file is already of type PDF");
            default:
                throw new ConverterNotFoundException("Converting PDF to format " + format + " is not supported");
        }

        return new ConvertedFile(file.getName(), format, 0.0, LocalDateTime.now());
    }

    private void toHTML(File file) throws IOException, ParserConfigurationException {
        try (PDDocument pdf = PDDocument.load(file); Writer output =
                new PrintWriter(getDestinationFilePath(file, HTML.name()), "utf-8")) {

            new PDFDomTree().writeText(pdf, output);
        }
    }

    private void toTXT(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            PDFParser parser = new PDFParser(new RandomAccessBuffer(IOUtils.readAllBytes(inputStream)));
            parser.parse();

            try (COSDocument document = parser.getDocument(); PrintWriter writer =
                    new PrintWriter(getDestinationFilePath(file, TXT.name())); PDDocument pdf = new PDDocument(document)) {

                writer.print(new PDFTextStripper().getText(pdf));
            }
        }
    }

    private void toImage(File file, String format) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file); OutputStream outputStream =
                new FileOutputStream(getDestinationFilePath(file, format))) {

            PDDocument pdf = PDDocument.load(IOUtils.readAllBytes(inputStream));
            PDFRenderer pdfRenderer = new PDFRenderer(pdf);

            for (int page = 0; page < pdf.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                ImageIOUtil.writeImage(bim, format, outputStream, 300);
            }
        }
    }

    private String getDestinationFilePath(File file, String format) {
        return Paths.get(System.getProperty("user.dir") + CONVERSION_DIRECTORY + getFileName(file) + "." + format)
                .toAbsolutePath().toString();
    }

    private String getFileName(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }
}
