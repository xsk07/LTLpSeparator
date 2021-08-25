package params;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CommandLineManager {


    // create the command line parser
    CommandLineParser parser = new DefaultParser();

    // create the Options
    Options options = new Options();

    {
        options.addOption(new Option("d", "debug", false, "Turn on debug."));
        options.addOption(new Option("e", "extract", false, "Turn on extract."));
        options.addOption(new Option("o", "option", true, "Turn on option with argument."));
    }

}
