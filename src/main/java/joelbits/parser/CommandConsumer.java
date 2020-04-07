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
import java.util.*;
import java.util.concurrent.*;
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
    private final Map<String, Consumer<String>> commands = new HashMap<>();
    private final Map<String, VoidCommand> voidCommands = new HashMap<>();
    private static final String MISSING_FORMAT = "missing_format";
    private static final String MISSING_CONVERT = "missing_convert";

    public CommandConsumer() throws Exception {
        databaseUtil = DatabaseUtil.getInstance();

        initializeCommands();
    }

    @Override
    public void accept(CommandLine cmd) {
        try {

            if (cmd.hasOption(CONVERT) && !cmd.hasOption(FORMAT)) {
                voidCommands.get(MISSING_FORMAT).execute();
            } else if (cmd.hasOption(FORMAT) && !cmd.hasOption(CONVERT)) {
                voidCommands.get(MISSING_CONVERT).execute();
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


            if (cmd.hasOption(LIST)) {
                voidCommands.get(LIST).execute();
            } else if (cmd.hasOption(FORMATS)) {
                voidCommands.get(FORMATS).execute();
            } else if (cmd.hasOption(EXIT)) {
                voidCommands.get(EXIT).execute();
            } else if (cmd.hasOption(HELP)) {
                voidCommands.get(HELP).execute();
            } else if (cmd.hasOption(LANGUAGE)) {
                commands.get(LANGUAGE).accept(cmd.getOptionValue(LANGUAGE));
            }

            } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void initializeCommands() {
        VoidCommand exitMessage = () -> System.out.println(resourceBundle.getString("exiting"));
        voidCommands.put(EXIT, exitMessage.andThen(() -> System.exit(0)));
        voidCommands.put(FORMATS, () -> System.out.println(Formats.getFormats()));
        voidCommands.put(HELP, () -> formatter.printHelp("fileConverter", options));
        voidCommands.put(LIST, databaseUtil::listAllFiles);
        voidCommands.put(MISSING_FORMAT, () -> System.out.println("You must add a desired --format <format> for the converted file(s)"));
        voidCommands.put(MISSING_CONVERT, () -> System.out.println("You must type --convert <file/directory> to convert file(s) to the supplied format."));

        commands.put(LANGUAGE, s -> resourceBundle = ResourceBundle.getBundle(BUNDLE_PROPERTIES, new Locale(s)));

    }
}
