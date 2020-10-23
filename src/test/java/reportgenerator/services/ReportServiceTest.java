package reportgenerator.services;

import org.junit.Before;
import org.junit.Test;
import reportgenerator.domain.Column;
import reportgenerator.domain.Columns;
import reportgenerator.domain.Page;
import reportgenerator.domain.Settings;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ReportServiceTest {
    private ReportService reportService;

    @Before
    public void setUp() {
        reportService = new ReportService(getDefaultSettings().getPage(),
                getDefaultSettings().getColumns());
    }

    @Test
    public void testGetDataDelimiter() {
        String expected = String.valueOf('-').repeat(getDefaultSettings().getPage().getWidth());
        String actual = reportService.getDataDelimiter();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetPageEnd() {
        String expected = String.valueOf('~').concat(System.lineSeparator());
        String actual = reportService.getPageEnd();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetColumnsCount() {
        int expected = 3;
        int actual = reportService.getColumnsCount();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetColumnsArray() {
        int expectedLength = 3;
        Column[] actual = reportService.getColumnsArray();
        assertEquals(expectedLength, actual.length);
        assertEquals(new Column("Номер", 8), actual[0]);
        assertEquals(new Column("Дата", 7), actual[1]);
        assertEquals(new Column("ФИО", 7), actual[2]);
    }

    private Settings getDefaultSettings() {
        var settings = new Settings();
        settings.setPage(new Page(32, 12));
        settings.setColumns(new Columns(Arrays.asList(new Column("Номер", 8),
                new Column("Дата", 7), new Column("ФИО", 7))));
        return settings;
    }
}