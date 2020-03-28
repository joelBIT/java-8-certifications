package joelbits;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

class ConverterOptions {
    public static final String EXIT = "exit";
    public static final String HELP = "help";
    public static final String LANGUAGE = "lang";
    public static final String CONVERT = "convert";
    public static final String FORMAT = "format";

    static Options getConvertOptions() {
        Options options = new Options();
        options.addOption(new Option("formats", "Prints the supported formats."));
        options.addOption(new Option(HELP, "Prints this message."));
        options.addOption(new Option(EXIT, "Terminate the application."));
        options.addOption(Option.builder().longOpt(LANGUAGE).hasArg().argName("language").desc("Text will be printed in the chosen language.").build());
        options.addOption(Option.builder().longOpt(CONVERT).hasArg().argName("file").desc("The desired file to be converted.").build());
        options.addOption(Option.builder().longOpt(FORMAT).hasArg().argName("format").desc("Converts file into chosen format, if supported.").build());

        return options;
    }
}
