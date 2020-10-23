package reportgenerator.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.*;

@Getter
@NoArgsConstructor
public class CommandLineService {
    private CommandLine cmdLineArgs;
    private String settingsFileName;
    private String inputFileName;
    private String outputFileName;

    public boolean parseCommandLine(String[] cmdLine) {
        var options = new Options();

        var settings = new Option("s", "settings", true, "settings file path");
        settings.setRequired(true);
        options.addOption(settings);

        var input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);

        var output = new Option("o", "output", true, "output file path");
        output.setRequired(true);
        options.addOption(output);

        var parser = new DefaultParser();
        var formatter = new HelpFormatter();

        try {
            cmdLineArgs = parser.parse(options, cmdLine);
            settingsFileName = cmdLineArgs.getOptionValue("settings");
            inputFileName = cmdLineArgs.getOptionValue("input");
            outputFileName = cmdLineArgs.getOptionValue("output");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java -jar report-generator-1.0.jar", options);
            return false;
        }
        return true;
    }
}
