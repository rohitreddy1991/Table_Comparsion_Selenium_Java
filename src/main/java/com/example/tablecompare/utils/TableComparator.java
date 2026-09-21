package com.example.tablecompare.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.asserts.SoftAssert;

import com.example.tablecompare.model.TableData;

/**
 * Matches rows by the key column and compares values by column name.
 */
public class TableComparator {

    private static final Logger log = LogManager.getLogger(TableComparator.class);

    public static void compareTables(TableData table1,
                                     TableData table2,
                                     Map<String, String> columnAliases) {

        SoftAssert softAssert = new SoftAssert();
        Set<String> matchedTable2Columns = new HashSet<>();
        matchedTable2Columns.add(table2.getKeyColumn());

        for (String customerId : table1.getRowsByKey().keySet()) {

            Map<String, String> row1 = table1.getRowsByKey().get(customerId);
            Map<String, String> row2 = table2.getRowsByKey().get(customerId);

            if (row2 == null) {
                softAssert.fail("Customer ID " + customerId
                        + " exists in Table 1 but was not found in Table 2");
                continue;
            }

            for (String table1Column : table1.getHeaders()) {

                String table2Column = columnAliases.getOrDefault(table1Column, table1Column);

                if (!table2.hasColumn(table2Column)) {
                    log.warn("Column '{}' exists in Table 1 but was not found in Table 2",
                            table1Column);
                    continue;
                }

                matchedTable2Columns.add(table2Column);

                String expected = row1.get(table1Column);
                String actual = row2.get(table2Column);

                softAssert.assertEquals(
                        actual,
                        expected,
                        "Customer ID " + customerId
                                + " | Column " + table1Column
                                + " -> " + table2Column);
            }
        }

        for (String table2Column : table2.getHeaders()) {
            if (!matchedTable2Columns.contains(table2Column)) {
                log.warn("Column '{}' exists in Table 2 but was not found in Table 1",
                        table2Column);
            }
        }

        for (String customerId : table2.getRowsByKey().keySet()) {
            if (!table1.getRowsByKey().containsKey(customerId)) {
                softAssert.fail("Customer ID " + customerId
                        + " exists in Table 2 but was not found in Table 1");
            }
        }

        softAssert.assertAll();
    }

    private TableComparator() {
        // Utility class
    }
}
