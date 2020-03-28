package joelbits;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

class ConverterOptions {
    static Options getConvertOptions() {
        Options options = new Options();
        options.addOption(new Option("formats", "Prints the supported formats."));
        options.addOption(new Option("help", "Prints this message."));
        options.addOption(new Option("exit", "Terminate the application."));
        options.addOption(Option.builder().longOpt("lang").hasArg().argName("language").desc("Text will be printed in the chosen language.").build());
        options.addOption(Option.builder().longOpt("convert").hasArg().argName("file").desc("The desired file to be converted.").build());
        options.addOption(Option.builder().longOpt("format").hasArg().argName("format").desc("Converts file into chosen format, if supported.").build());

        return options;
    }
}
