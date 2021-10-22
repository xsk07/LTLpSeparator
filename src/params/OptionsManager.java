package params;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class OptionsManager {

    public static Options initializeOptions(){

        Options options = new Options();

        Option t = Option.builder("t")
                .longOpt("tree")
                .desc("returns the tree representation of the formula got in input")
                .build();

        Option c = Option.builder("c")
                .longOpt("convert")
                .desc("converts the formula into an equivalent US form")
                .build();

        Option s= Option.builder("s")
                .longOpt("separate")
                .desc("separates the formula into a triple of pure past, pure present and pure future ones")
                .build();

        OptionGroup og = new OptionGroup();
        og.addOption(t);
        og.addOption(c);
        og.addOption(s);

        options.addOptionGroup(og);

        Option input = new Option(
                "iF",
                "inputFile",
                true,
                "use given .txt file as input"
        );
        input.setArgName("FILE");
        options.addOption(input);

        Option output = new Option(
                "oF",
                "outputFile",
                true,
                "filename of the output"
        );
        output.setArgName("FILE");
        options.addOption(output);

        Option extension = new Option(
                "oE",
                "outputExtension",
                true,
                "format encoding of the output file"
        );

        extension.setArgName("extension");
        options.addOption(extension);

        options.addOption(
                new Option(
                        "h",
                        "help",
                        false,
                        "print this message"
                )
        );

        return options;
    }

}
