package reportgenerator.services;

import jakarta.xml.bind.JAXBException;
import org.junit.Before;
import org.junit.Test;
import reportgenerator.domain.Column;
import reportgenerator.domain.Settings;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SettingsServiceTest {
    private SettingsService settingsService;

    @Before
    public void setUp() {
        settingsService = new SettingsService();
    }

    @Test
    public void testReadSettingsFromFile() throws IOException, JAXBException {
        var file = "src/test/resources/data/settings.xml";
        var actual = settingsService.readSettingsFromFile(file);
        assertResult(actual);
    }

    private void assertResult(Settings actual) {
        var expectedColumns = Arrays.asList(new Column("Номер", 8),
                new Column("Дата", 7), new Column("ФИО", 7));

        assertEquals(12, actual.getPage().getHeight());
        assertEquals(32, actual.getPage().getWidth());
        assertEquals(expectedColumns, actual.getColumns().getColumn());
    }

}