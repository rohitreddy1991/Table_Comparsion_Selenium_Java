# Table Comparison Framework

Compares two HTML tables on the same page using **Customer ID** as the unique
reference key. The comparison ignores **row order** and **column order** and
matches columns by **header name** (with an optional name mapping such as
`Total Amount` -> `Amount`).

Built to stay readable years from now: plain Java, one job per class,
step-by-step comments, no frameworks or patterns you need to look up.

## Tech stack

| Tool                          | Version |
|-------------------------------|---------|
| Java                          | 21      |
| Selenium WebDriver            | 4.26.0  |
| TestNG                        | 7.10.2  |
| Log4j2 (api / core / slf4j2)  | 2.24.3  |
| Maven Surefire Plugin         | 3.2.5   |

## Project structure

```
table-comparison/
├── pom.xml
├── testng.xml
├── README.md
└── src
    ├── main/java/com/example/tablecompare
    │   ├── model
    │   │   ├── TableData.java              # snapshot of one table (headers, rows, key index)
    │   │   └── TableComparisonResult.java  # failures (fail test) + reports (log only)
    │   └── utils
    │       ├── TableReader.java            # Selenium reader: locator in -> TableData out
    │       └── TableComparator.java        # the comparison algorithm
    └── test/java/com/example/tablecompare
        ├── BaseTest.java                   # Chrome + loading the local HTML page
        └── TableComparisonTest.java        # locators, key column, mapping, SoftAssert
```

The page under test is `src/test/resources/testdata/customer-tables.html`
(two `<table>` elements). It is loaded from the **classpath**, never from a
hard-coded Windows path.

## How to run

```bash
cd table-comparison

mvn clean test
# or explicitly:
mvn clean test -DsuiteXmlFile=testng.xml
```

Chrome must be installed; Selenium Manager fetches the matching chromedriver
automatically. In IntelliJ/Eclipse, import as *Existing Maven Project* and run
`testng.xml` directly if you prefer.

## How the comparison works (the algorithm)

```
1. READ      TableReader locates each <table> via the locator given by the test,
             reads "thead th" headers, and builds header name -> column index.
2. KEY       Each "tbody tr" row is stored twice:
             - as a plain list of cell values, and
             - indexed by Customer ID: Map<Customer ID, Map<header, value>>
3. MATCH     For every Customer ID in Table 1, look it up in Table 2.
             Not found -> failure (test fails).
4. COMPARE   For each mapped column (e.g. "Total Amount" -> "Amount"),
             compare Table 1 value vs Table 2 value by header name.
             Different -> failure (test fails).
5. REPORT    Extra columns on either side and Customer IDs only in Table 2
             are logged as reports - they do NOT fail the test.
6. ASSERT    All failures are pushed into a TestNG SoftAssert and reported
             together by softAssert.assertAll().
```

Rules enforced by the code:

- rows are matched by **Customer ID**, never by row number
- columns are matched by **header name**, never by index
- duplicate Customer IDs abort the run with a clear error (never overwritten)
- every problem is collected; the run never stops at the first mismatch

## What fails vs what is only logged

| Finding                                        | Result        |
|------------------------------------------------|---------------|
| Value mismatch (e.g. `1200.50` vs `1100.50`)   | **Fails test** |
| Customer ID missing in Table 2                 | **Fails test** |
| Customer ID only in Table 2                    | logged (report) |
| Extra/missing column on either side            | logged (report) |
| Mapped column missing in Table 2               | logged (report) |
| Duplicate Customer ID in either table          | run aborts with clear error |

## Expected result for the sample page

The sample page contains two known differences, so the test fails while
listing exactly them:

```
FAIL - Customer ID C004 | Column: Total Orders -> Total Orders | Expected: 5 | Actual: 4
FAIL - Customer ID C002 | Column: Total Amount -> Amount | Expected: 1200.50 | Actual: 1100.50
```

`C001` and `C003` match. The extra `surcharge` column is reported but does not
fail the test:

```
Column 'surcharge' exists in Table 2 but was not found in Table 1.
```

## Sample console output

```
[INFO ] TableReader - Reading Table 1
[INFO ] TableReader - Headers found: [Customer ID, Name, Total Orders, Total Amount]
[INFO ] TableReader - Reading Table 2
[INFO ] TableReader - Headers found: [Customer ID, Name, Total Orders, Amount, surcharge]
[INFO ] TableComparator - Comparing Table 1 vs Table 2 using key column 'Customer ID'
[WARN ] TableComparator - Column 'surcharge' exists in Table 2 but was not found in Table 1.
[INFO ] TableComparator - Comparing Customer ID C003
[INFO ] TableComparator - MATCHED: Customer ID C003 matches across all configured columns.
[ERROR] TableComparator - MISMATCH: Customer ID=C004, column=Total Orders -> Total Orders, expected=5, actual=4
[INFO ] TableComparator - Comparison finished: 2 matched key(s), 2 failure(s), 1 note(s)
```

## Reusing it for other tables

Only the test class knows anything about specific tables. To compare different
tables later, write a new test that supplies:

```java
// any locators, any key column, any column names
TableData t1 = readTable(By.id("ordersTable"), "Table 1", "Order No");
TableData t2 = readTable(By.id("backupTable"), "Table 2", "Order No");

TableComparisonResult result = TableComparator.compareTables(
        t1, t2, "Order No",
        Map.of("Order Total", "Total", "Status", "State"));
```

No utility class needs to change.
