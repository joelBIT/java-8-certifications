package joelbits;

import joelbits.entities.ConvertedFile;
import joelbits.formats.Formats;
import joelbits.tasks.ConvertFile;
import joelbits.tasks.ConverterAction;
import joelbits.utils.DatabaseUtil;
import joelbits.visitors.FileFormatVisitor;
import org.apache.commons.cli.*;

import java.io.Console;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

import static joelbits.ConverterOptions.*;
import static joelbits.converters.Converter.CONVERSION_DIRECTORY;
import static joelbits.utils.DatabaseUtil.createInsertQuery;

public class Main {
    private static final String BUNDLE_PROPERTIES = "AppText";
    private static final String BUNDLE_JAVA = "joelbits.properties." + BUNDLE_PROPERTIES;

    public static void main(String[] args) {
        DatabaseUtil databaseUtil = null;
        try {
            databaseUtil = DatabaseUtil.getInstance();
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

        while (true) {
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
                if (cmd.hasOption(CONVERT) && !cmd.hasOption(FORMAT)) {
                    System.out.println("You must add a desired --format <format> for the converted file(s)");
                } else if (!cmd.hasOption(CONVERT) && cmd.hasOption(FORMAT)) {
                    System.out.println("You must type --convert <file/directory> to convert file(s) to the supplied format.");
                }
                if (cmd.hasOption(CONVERT) && cmd.hasOption(FORMAT)) {

                    try {
                        Formats.valueOf(cmd.getOptionValue(FORMAT).toUpperCase());
                    } catch (Exception e) {
                        System.out.println("Supplied format is not supported. Type -formats to see which formats are supported.");
                        continue;
                    }

                    String convertArgument = cmd.getOptionValue(CONVERT);
                    Path path = null;
                    try {
                        path = Paths.get(convertArgument);
                    } catch (InvalidPathException e) {
                        System.out.println(path + " is not a valid path");
                        continue;
                    }

                    if (!(Files.isRegularFile(path) || Files.isDirectory(path))) {
                        System.out.println(path.toAbsolutePath() + " is not a file nor a directory!");
                        continue;
                    }

                    if (!Files.exists(Paths.get(System.getProperty("user.dir") + CONVERSION_DIRECTORY))) {
                        Files.createDirectory(Paths.get(System.getProperty("user.dir") + CONVERSION_DIRECTORY));
                    }

                    if (Files.isRegularFile(path)) {
                        String filePath = path.toAbsolutePath().toString();
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        Future<ConvertedFile> result = executorService.submit(new ConvertFile(filePath, cmd.getOptionValue(FORMAT)));

                        databaseUtil.executeQuery(createInsertQuery(result.get()));
                        continue;
                    }

                    if (Files.isDirectory(path)) {
                        FileFormatVisitor visitor = new FileFormatVisitor(Formats.getFormats());
                        Files.walkFileTree(Paths.get(path.toAbsolutePath().toString()), visitor);
                        
                        ForkJoinPool forkJoinPool = new ForkJoinPool();
                        ConverterAction task = new ConverterAction(new ArrayList<>(visitor.getPaths()), cmd.getOptionValue(FORMAT));
                        forkJoinPool.invoke(task);
                        for (ConvertedFile file : task.getConvertedFiles()) {
                            databaseUtil.executeQuery(createInsertQuery(file));
                        }

                        continue;
                    }
                }
                if (cmd.hasOption(LIST)) {
                    databaseUtil.listAllFiles();
                    continue;
                }
                if (cmd.hasOption(FORMATS)) {
                    System.out.println(Formats.getFormats());
                }
            } catch (ParseException e) {
                System.out.println(resourceBundle.getString("parse_error") + e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
