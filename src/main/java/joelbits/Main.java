package joelbits;

import joelbits.parser.CommandParser;
import org.apache.commons.cli.*;

import java.io.Console;
import java.util.*;

import static joelbits.properties.AppText.BUNDLE_JAVA;

public class Main {

    public static void main(String[] args) {
        CommandParser commandParser = null;
        try {
            commandParser = new CommandParser();
        } catch (Exception e) {
            exitApplication(" Could not create command parser: " + e.getMessage());
        }

        Console console = System.console();
        if (console == null) {
            exitApplication("Console not available. Exiting...");
        }

        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_JAVA);
        System.out.println(resourceBundle.getString("start"));

        while (true) {
            try {
                String[] input = console.readLine(resourceBundle.getString("input")).split("\\s+");
                commandParser.parse(input);
            } catch (ParseException e) {
                System.out.println(resourceBundle.getString("parse_error") + e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void exitApplication(String message) {
        System.out.println(message);
        System.exit(1);
    }
}
