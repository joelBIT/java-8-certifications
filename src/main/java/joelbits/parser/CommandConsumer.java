package joelbits.parser;

import joelbits.ConverterOptions;
import joelbits.entities.ConvertedFile;
import joelbits.formats.Formats;
import joelbits.tasks.ConvertFile;
import joelbits.tasks.ConverterAction;
import joelbits.utils.DatabaseUtil;
import joelbits.visitors.FileFormatVisitor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static joelbits.ConverterOptions.*;
import static joelbits.ConverterOptions.LANGUAGE;
import static joelbits.converters.Converter.CONVERSION_DIRECTORY;
import static joelbits.properties.AppText.BUNDLE_JAVA;
import static joelbits.properties.AppText.BUNDLE_PROPERTIES;
import static joelbits.utils.DatabaseUtil.createInsertQuery;

public class CommandConsumer implements Consumer<CommandLine> {
    private final DatabaseUtil databaseUtil;
    private final HelpFormatter formatter = new HelpFormatter();
    private final Options options = ConverterOptions.getConvertOptions();
    private ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_JAVA);

    public CommandConsumer() throws Exception {
        databaseUtil = DatabaseUtil.getInstance();
    }

    @Override
    public void accept(CommandLine cmd) {
        try {
            if (cmd.hasOption(LIST)) {
                try {
                    databaseUtil.listAllFiles();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (cmd.hasOption(FORMATS)) {
                System.out.println(Formats.getFormats());
            }
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
                    return;
                }

                String convertArgument = cmd.getOptionValue(CONVERT);
                Path path = null;
                try {
                    path = Paths.get(convertArgument);
                } catch (InvalidPathException e) {
                    System.out.println(path + " is not a valid path");
                    return;
                }

                if (!(Files.isRegularFile(path) || Files.isDirectory(path))) {
                    System.out.println(path.toAbsolutePath() + " is not a file nor a directory!");
                    return;
                }

                if (!Files.exists(Paths.get(System.getProperty("user.dir") + CONVERSION_DIRECTORY))) {
                    Files.createDirectory(Paths.get(System.getProperty("user.dir") + CONVERSION_DIRECTORY));
                }

                if (Files.isRegularFile(path)) {
                    String filePath = path.toAbsolutePath().toString();
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    Future<ConvertedFile> result = executorService.submit(new ConvertFile(filePath, cmd.getOptionValue(FORMAT)));

                    databaseUtil.executeQuery(createInsertQuery(result.get()));
                    return;
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

                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Could not consume command(s): " + e.getMessage());
        }
    }
}
