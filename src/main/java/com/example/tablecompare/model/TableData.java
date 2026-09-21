package com.example.tablecompare.model;

import java.util.List;
import java.util.Map;

/**
 * Simple object that stores one table after Selenium reads it.
 */
public class TableData {

    private final String tableName;
    private final String keyColumn;
    private final List<String> headers;
    private final Map<String, Map<String, String>> rowsByKey;

    public TableData(String tableName,
                     String keyColumn,
                     List<String> headers,
                     Map<String, Map<String, String>> rowsByKey) {
        this.tableName = tableName;
        this.keyColumn = keyColumn;
        this.headers = headers;
        this.rowsByKey = rowsByKey;
    }

    public String getTableName() {
        return tableName;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public Map<String, Map<String, String>> getRowsByKey() {
        return rowsByKey;
    }

    public boolean hasColumn(String columnName) {
        return headers.contains(columnName);
    }
}
