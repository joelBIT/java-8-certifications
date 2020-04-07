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
            System.out.println(" Could not create command parser: " + e.getMessage());
            System.exit(1);
        }

        Console console = System.console();
        if (console == null) {
            System.out.println("Console not available. Exiting...");
            System.exit(1);
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
}
