package reportgenerator.services;

import lombok.AllArgsConstructor;
import reportgenerator.domain.Column;
import reportgenerator.domain.Columns;
import reportgenerator.domain.Page;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
public class ReportService {
    private final static Character PAGE_DELIMITER = '~';
    private final static Character LINE_DELIMITER = '-';
    private final Page page;
    private final Columns columns;

    public void generateReport(String inputFile, String outputFile) {
        try {
            var tempBuffer = new StringBuilder();
            var header = generateHeader();
            header.forEach(tempBuffer::append);

            Files.writeString(Paths.get(outputFile), tempBuffer, StandardCharsets.UTF_16, StandardOpenOption.CREATE_NEW);

            AtomicInteger currentLinesOnPage = new AtomicInteger(header.size());
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_16))) {
                br.lines().forEach(line -> {
                    tempBuffer.delete(0, tempBuffer.length());
                    var rawRowElements = line.split("\\t");

                    Map<Integer, Map<Integer, String>> splitedRowElements = new HashMap<>();
                    // если количество элементов в строке отличается от количества колонок - игнорируем строку
                    if (rawRowElements.length != getColumnsCount()) {
                        System.out.println("Elements count != columns number. Line " + line + " skipped");
                    } else {
                        for (int i = 0; i < rawRowElements.length; i++) {
                            splitedRowElements.put(i, splitString(rawRowElements[i], getColumnsArray()[i].getWidth()));
                        }

                        int maxLinesCount = splitedRowElements.values().stream().map(Map::size)
                                .max(Comparator.comparing(Integer::valueOf)).get();
                        //если новая строка выходит за пределы страницы то начинаем новую страницу
                        if (currentLinesOnPage.get() + maxLinesCount + 1 > page.getHeight()) {
                            tempBuffer.append(getPageEnd());
                            var nextHeader = generateHeader();
                            nextHeader.forEach(tempBuffer::append);
                            currentLinesOnPage.set(nextHeader.size());
                        }

                        var rows = generateRows(splitedRowElements);
                        rows.forEach(tempBuffer::append);
                        currentLinesOnPage.getAndAdd(rows.size());
                        tempBuffer.append(getDataDelimiter()).append(System.lineSeparator());
                        currentLinesOnPage.getAndIncrement();
                    }
                    try {
                        Files.writeString(Paths.get(outputFile), tempBuffer, StandardCharsets.UTF_16, StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        System.out.println("Error write output file");
                    }
                });

            } catch (IOException e) {
                System.out.println("Error open input file");
            }
        } catch (IOException e) {
            System.out.println("Error open input file");
        }
    }

    String getDataDelimiter() {
        //ширина разделителя равно сумме ширин полей + два пробела по краям полей и | на каждое поле + финальный |
        var realWidth = (Integer) columns.getColumn().stream().mapToInt(Column::getWidth).sum();
        return String.valueOf(LINE_DELIMITER).repeat(realWidth + getColumnsCount() * 3 + 1);
    }

    String getPageEnd() {
        return String.valueOf(PAGE_DELIMITER).concat(System.lineSeparator());
    }

    int getColumnsCount() {
        return columns.getColumn().size();
    }

    Column[] getColumnsArray() {
        var columnsArray = new Column[getColumnsCount()];
        columns.getColumn().toArray(columnsArray);
        return columnsArray;
    }

    private String getRowFormatter() {
        var rowFormat = new StringBuilder();
        rowFormat.append("|");
        columns.getColumn().forEach(column -> rowFormat.append(" %-").append(column.getWidth() + 1).append("s|"));
        rowFormat.append(System.lineSeparator());
        return rowFormat.toString();
    }

    /* Описание работы метода:
    На вход у нас подается строка которую нужно разделить так, чтобы в каждой строке
    таблицы были только целые слова и разделители.
    Если слово не умещается в строку оно сначала переносится на новую строку, а затем
    разбивается на куски, в строке остается кусок максимальной длины.
    Если при переносе в качестве разделителя идет пробел то он подавляется, так как
    у нас и так есть отступы в виде пробелов в таблице
     */
    private Map<Integer, String> splitString(String string, int windowSize) {
        var str = new StringBuilder();
        str.append(string);
        var split = new ArrayList<String>();
        int lastSplitPoint = 0;
        int lastWordStart = 0;
        int lastWordSize = 0;

        for (int i = 0; i < str.length(); i++) {
            if (lastWordSize > windowSize) {
                if (lastWordStart < lastSplitPoint) {
                    split.add(str.substring(lastWordStart, lastSplitPoint));
                    if (Character.isSpaceChar(str.codePointAt(lastSplitPoint))) {
                        lastSplitPoint++;
                    }
                    lastWordStart = lastSplitPoint;
                    lastWordSize = i - lastWordStart;
                } else {
                    split.add(str.substring(lastWordStart, lastWordStart + windowSize));
                    lastWordStart = lastWordStart + windowSize;
                    lastWordSize = lastWordSize - windowSize;
                }
            }
            if (i == str.length() - 1) {
                split.add(str.substring(lastWordStart, str.length()));
            }
            if (!Character.isLetterOrDigit(str.codePointAt(i))) {
                lastSplitPoint = i;
            }
            lastWordSize++;
        }

        return IntStream.range(0, split.size())
                .boxed()
                .collect(Collectors.toMap(Function.identity(), split::get));
    }

    private List<String> generateRows(Map<Integer, Map<Integer, String>> splitedRowElements) {
        var rows = new ArrayList<String>();
        // находим количество строк таблицы которые будет занимать обработанная строка элементов
        int maxLinesCount = splitedRowElements.values().stream().map(Map::size)
                .max(Comparator.comparing(Integer::valueOf)).get();

        var forPrint = new String[getColumnsCount()];
        for (int lineNumber = 0; lineNumber < maxLinesCount; lineNumber++) {
            for (int columnNumber = 0; columnNumber < getColumnsCount(); columnNumber++) {
                forPrint[columnNumber] = splitedRowElements.get(columnNumber).getOrDefault(lineNumber, "");
            }
            //выводим строку в формат
            rows.add(String.format(getRowFormatter(), (Object[]) forPrint));
        }
        return rows;
    }

    private List<String> generateHeader() {
        Map<Integer, Map<Integer, String>> splitedHeaderElements = new HashMap<>();
        var result = new ArrayList<String>();
        result.add(getDataDelimiter().concat(System.lineSeparator()));
        var rawHeader = columns.getColumn().stream().map(Column::getTitle).toArray(String[]::new);
        for (int i = 0; i < rawHeader.length; i++) {
            splitedHeaderElements.put(i, splitString(rawHeader[i],
                    getColumnsArray()[i].getWidth()));
        }
        var headers = generateRows(splitedHeaderElements);
        result.addAll(headers);
        result.add(getDataDelimiter().concat(System.lineSeparator()));
        return result;
    }
}
