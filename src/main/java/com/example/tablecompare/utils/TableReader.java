package com.example.tablecompare.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.example.tablecompare.model.TableData;

/**
 * Reads an HTML table without depending on row or column position.
 */
public class TableReader {

    public static TableData readTable(WebDriver driver,
                                      By tableLocator,
                                      String tableName,
                                      String keyColumn) {

        WebElement table = driver.findElement(tableLocator);

        List<String> headers = new ArrayList<>();
        for (WebElement header : table.findElements(By.cssSelector("thead th"))) {
            headers.add(header.getText().trim());
        }

        if (!headers.contains(keyColumn)) {
            throw new IllegalArgumentException(
                    "Column '" + keyColumn + "' not found in " + tableName);
        }

        Map<String, Map<String, String>> rowsByKey = new LinkedHashMap<>();

        for (WebElement row : table.findElements(By.cssSelector("tbody tr"))) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            Map<String, String> rowData = new LinkedHashMap<>();

            for (int i = 0; i < headers.size() && i < cells.size(); i++) {
                rowData.put(headers.get(i), cells.get(i).getText().trim());
            }

            String keyValue = rowData.get(keyColumn);

            if (rowsByKey.containsKey(keyValue)) {
                throw new IllegalArgumentException(
                        "Duplicate " + keyColumn + " '" + keyValue + "' found in " + tableName);
            }

            rowsByKey.put(keyValue, rowData);
        }

        return new TableData(tableName, keyColumn, headers, rowsByKey);
    }

    private TableReader() {
        // Utility class
    }
}
