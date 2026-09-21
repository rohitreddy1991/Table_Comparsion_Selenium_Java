package com.example.tablecompare;

import java.util.Map;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.example.tablecompare.model.TableData;
import com.example.tablecompare.utils.TableComparator;

public class TableComparisonTest extends BaseTest {

    @Test
    public void compareCustomerTables() {

        TableData table1 = readTable(
                By.cssSelector("table:nth-of-type(1)"),
                "Table 1",
                "Customer ID");

        TableData table2 = readTable(
                By.cssSelector("table:nth-of-type(2)"),
                "Table 2",
                "Customer ID");

        // Only specify columns whose names are different between the two tables.
        Map<String, String> columnAliases = Map.of(
                "Total Amount", "Amount"
        );

        TableComparator.compareTables(table1, table2, columnAliases);
    }
}
