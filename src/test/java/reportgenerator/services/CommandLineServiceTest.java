package reportgenerator.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandLineServiceTest {
    private CommandLineService commandLineService;

    @Before
    public void setUp() {
        commandLineService = new CommandLineService();
    }

    @Test
    public void testParseCommandLineGood() {
        String[] inputArgs = {"-s", "settings.xml", "-i", "input.txt", "-o", "output.txt"};
        var actual = commandLineService.parseCommandLine(inputArgs);
        assertTrue(actual);
        assertEquals("settings.xml", commandLineService.getSettingsFileName());
        assertEquals("input.txt", commandLineService.getInputFileName());
        assertEquals("output.txt", commandLineService.getOutputFileName());
    }
}