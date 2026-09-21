package com.example.tablecompare;

import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.example.tablecompare.model.TableData;
import com.example.tablecompare.utils.TableReader;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        URL testPage = getClass().getClassLoader()
                .getResource("testdata/customer-tables-different-columns.html");

        if (testPage == null) {
            throw new IllegalStateException("customer-tables.html not found");
        }

        driver.get(testPage.toString());
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected TableData readTable(By locator, String tableName, String keyColumn) {
        return TableReader.readTable(driver, locator, tableName, keyColumn);
    }
}
