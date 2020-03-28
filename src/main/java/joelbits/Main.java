package joelbits;

import org.apache.commons.cli.*;

import java.io.Console;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
    private static final String EXIT = "exit";
    private static final String HELP = "help";
    private static final String LANGUAGE = "lang";
    private static final String BUNDLE_PROPERTIES = "AppText";
    private static final String BUNDLE_JAVA = "joelbits.properties." + BUNDLE_PROPERTIES;

    public static void main(String[] args) {
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
                    Locale locale = new Locale(cmd.getOptionValue("lang"));
                    resourceBundle = ResourceBundle.getBundle(BUNDLE_PROPERTIES, locale);
                }
            } catch (ParseException e) {
                System.out.println(resourceBundle.getString("parse_error") + e.getMessage());
            }
        }
    }
}
