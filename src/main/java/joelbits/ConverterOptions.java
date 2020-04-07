package joelbits;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class ConverterOptions {
    public static final String EXIT = "exit";
    public static final String HELP = "help";
    public static final String LIST = "list";
    public static final String LANGUAGE = "lang";
    public static final String CONVERT = "convert";
    public static final String FORMAT = "format";
    public static final String FORMATS = "formats";

    public static synchronized Options getConvertOptions() {
        Options options = new Options();
        options.addOption(new Option(FORMATS, "Prints the supported formats."));
        options.addOption(new Option(HELP, "Prints this message."));
        options.addOption(new Option(EXIT, "Terminate the application."));
        options.addOption(new Option(LIST, "List all converted files."));
        options.addOption(Option.builder().longOpt(LANGUAGE).hasArg().argName("language").desc("Text will be printed in the chosen language.").build());
        options.addOption(Option.builder().longOpt(CONVERT).hasArg().argName("file").desc("The desired file or directory to be converted. If it is a directory, all files in selected directory, including files in subdirectories, will be converted, if supported.").build());
        options.addOption(Option.builder().longOpt(FORMAT).hasArg().argName("format").desc("Converts file into chosen format, if supported.").build());

        return options;
    }
}
