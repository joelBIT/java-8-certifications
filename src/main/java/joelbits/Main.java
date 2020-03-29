package joelbits;

import joelbits.converters.Converter;
import joelbits.converters.ConverterFactory;
import joelbits.exception.ConverterNotFoundException;
import joelbits.properties.DatabaseProperties;
import org.apache.commons.cli.*;

import java.io.Console;
import java.io.File;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import static joelbits.ConverterOptions.*;

public class Main {
    private static final String BUNDLE_PROPERTIES = "AppText";
    private static final String BUNDLE_JAVA = "joelbits.properties." + BUNDLE_PROPERTIES;

    public static void main(String[] args) {
        try {
            Object clazz = Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            DatabaseProperties databaseProperties = new DatabaseProperties();
        } catch (Exception e) {
            System.out.println("Could not connect to database: " + e.getMessage());
        }

        Console console = System.console();
        if (console == null) {
            System.out.println("Console not available. Exiting...");
            System.exit(1);
        }

        CommandLineParser parser = new DefaultParser();
        Options options = ConverterOptions.getConvertOptions();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_JAVA);
        System.out.println(resourceBundle.getString("start"));

        while(true) {
            try {
                String[] input = console.readLine(resourceBundle.getString("input")).split("\\s+");
                cmd = parser.parse(options, input);

                if (cmd.hasOption(EXIT)) {
                    System.out.println(resourceBundle.getString("exiting"));
                    System.exit(0);
                }
                if (cmd.hasOption(HELP)) {
                    formatter.printHelp("fileConverter", options);
                }
                if (cmd.hasOption(LANGUAGE)) {
                    Locale locale = new Locale(cmd.getOptionValue(LANGUAGE));
                    resourceBundle = ResourceBundle.getBundle(BUNDLE_PROPERTIES, locale);
                }
                if (cmd.hasOption(CONVERT) && cmd.hasOption(FORMAT)) {

                    File file = new File(cmd.getOptionValue(CONVERT));

                    Converter converter = ConverterFactory.getConverter(file);
                    converter.convert(file, cmd.getOptionValue(FORMAT));
                }
            } catch (ParseException e) {
                System.out.println(resourceBundle.getString("parse_error") + e.getMessage());
            } catch (ConverterNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
