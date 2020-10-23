package reportgenerator;

import jakarta.xml.bind.JAXBException;
import reportgenerator.services.CommandLineService;
import reportgenerator.services.ReportService;
import reportgenerator.services.SettingsService;

import java.io.IOException;

public class ReportGenerator {
    public static void main(String[] args) {
        var commandLineService = new CommandLineService();
        var settingsService = new SettingsService();
        if (commandLineService.parseCommandLine(args)) {
            try {
                var settings = settingsService.readSettingsFromFile(commandLineService.getSettingsFileName());
                var stringGenerator = new ReportService(settings.getPage(), settings.getColumns());
                var inputFile = commandLineService.getInputFileName();
                var outputFile = commandLineService.getOutputFileName();
                stringGenerator.generateReport(inputFile, outputFile);
            } catch (IOException e) {
                System.out.println("Error reading settings file");
            } catch (JAXBException e) {
                System.out.println("Error parsing settings file");
            }
        }
    }
}
