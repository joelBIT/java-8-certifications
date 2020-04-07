package joelbits.parser;

import joelbits.ConverterOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class CommandParser {
    private CommandConsumer commandConsumer = null;
    private final CommandLineParser parser = new DefaultParser();
    private final Options options = ConverterOptions.getConvertOptions();

    public CommandParser() throws Exception {
        commandConsumer = new CommandConsumer();
    }

    public void parse(String[] commands) throws Exception {
        CommandLine cmd = parser.parse(options, commands);
        commandConsumer.accept(cmd);
    }
}
