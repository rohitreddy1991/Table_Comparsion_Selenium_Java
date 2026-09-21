# Table Comparison using Selenium + Java

This project came from a question I was asked in one of my client interviews.

In that interview, I was able to answer most of the questions, but I got stuck on this table comparison problem. I could not complete the solution the way I wanted during the interview, and I was rejected.

After that, I decided to build the solution myself so I could understand the problem properly and be ready if I get a similar question again.

What was the problem?

There are two HTML tables containing customer data.

The requirement is to compare the data between the two tables using Customer ID as the reference.

The tricky part is that:

rows may not be in the same order

columns may not be in the same order

column names may be different

one table may contain extra columns

the same customer can appear at a different row position in the second table

So the comparison should not depend on row number or column index.

Example

Table 1 can have:

Customer ID

Name

Total Orders

Total Amount

Table 2 can have:

Amount

Customer ID

Orders

Customer Name

Region

Here:

Name in Table 1 is the same as Customer Name in Table 2

Total Orders is the same as Orders

Total Amount is the same as Amount

Region is an extra column and is ignored

The row order can also be completely different.

How the comparison works

The logic is simple:

Read both tables using Selenium.

Use Customer ID as the unique key.

Take one Customer ID from Table 1.

Find the same Customer ID in Table 2.

Compare the values using column names instead of column positions.

Use aliases when the same column has a different name in Table 2.

Ignore extra columns that are not part of the comparison.

Fail the test if a Customer ID is missing or if the compared values do not match.

For example, if Table 1 has:

Customer ID: C003
Name: Carol Kim
Total Orders: 12
Total Amount: 2000.00

the code searches for C003 in Table 2 and then compares the corresponding values.

It does not matter if C003 is the first row in Table 1 and the last row in Table 2.

Column aliases

If column names are different between the tables, the test can define the mapping.

Example:

Map<String, String> columnAliases = Map.of(
        "Name", "Customer Name",
        "Total Orders", "Orders",
        "Total Amount", "Amount"
);

This keeps the comparison logic reusable.

Project structure

The project is kept intentionally simple.

src/main/java
└── com.example.tablecompare
    ├── model
    │   └── TableData.java
    └── utils
        ├── TableReader.java
        └── TableComparator.java

src/test/java
└── com.example.tablecompare
    ├── BaseTest.java
    └── TableComparisonTest.java

src/test/resources
└── testdata
    └── customer-tables.html

BaseTest

Handles browser setup and cleanup using TestNG @BeforeMethod and @AfterMethod.

TableComparisonTest

Contains the actual @Test.

It reads both tables, defines any column aliases, and calls the comparison method.

TableReader

Reads the table headers and rows using Selenium.

It stores each row using the Customer ID as the key.

TableData

Simple class used to store the table data.

TableComparator

Contains the main comparison logic.

It matches rows by Customer ID and compares the values by column name.

Technologies used

Java

Selenium WebDriver

TestNG

Maven

Log4j2

What should fail the test?

The test should fail when:

a Customer ID from Table 1 is missing in Table 2

a value does not match for the same customer

For example:

Customer ID C105
Total Orders: 9
Orders: 10

This is a real data mismatch, so the test should fail.

What should not fail the test?

An extra column should not fail the test.

For example, if Table 2 has:

Region

and Table 1 does not have it, the column is simply ignored or logged as informational.

The same applies when the column order is different.

Running the test

From the project root:

mvn clean test

You can also run TableComparisonTest directly from IntelliJ or Eclipse as a TestNG test.

Why I kept this project

I mainly created this project to close a gap from that interview.

Instead of just remembering the answer, I wanted to build it and understand why the approach works.

The main thing I learned from this problem is that table comparison becomes much easier when rows are matched by a unique key and columns are matched by header name instead of relying on their position.