package joelbits;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

class ConverterOptions {
    static Options getConvertOptions() {
        Options options = new Options();
        options.addOption(new Option("formats", "The supported formats are PDF, TXT, DOC"));
        options.addOption(Option.builder().longOpt("convert").hasArg().argName("file").desc("The desired file to be converted").build());
        options.addOption(Option.builder().longOpt("format").hasArg().argName("format").desc("Converts file into chosen format, if supported").build());

        return options;
    }
}
