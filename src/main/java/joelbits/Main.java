package joelbits;

import org.apache.commons.cli.*;

import java.io.Console;

public class Main {
    private static final String EXIT = "exit";
    private static final String HELP = "help";

    public static void main(String[] args) {
        Console console = System.console();
        CommandLineParser parser = new DefaultParser();
        Options options = ConverterOptions.getConvertOptions();
        HelpFormatter formatter = new HelpFormatter();

        if (console == null) {
            System.out.println("Console not available. Exiting...");
            System.exit(1);
        }
        System.out.println("Welcome to fileConverter desktop version. Type \"help\" for a list of available commands.");

        while(true) {
            try {
                String[] input = console.readLine("Enter input: ").split("\\s+");
                switch(input[0].toLowerCase()) {
                    case EXIT:
                        System.out.println("Exiting...");
                        System.exit(0);
                    case HELP:
                        formatter.printHelp( "fileConverter", options );
                        break;
                    default:
                        CommandLine cmd = parser.parse( options, input);
                }
            } catch (ParseException e) {
                System.out.println("Could not parse input: " + e.getMessage());
            }
        }
    }
}
