
package base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import config.ConfigReader;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {

        ChromeOptions options = new ChromeOptions();

        options.addArguments(
            "--start-maximized",
            "--disable-notifications"
        );

        if (Boolean.parseBoolean(
                System.getProperty("headless", "false"))) {

            options.addArguments(
                "--headless=new",
                "--window-size=1440,1000"
            );
        }

        // Selenium Manager automatically manages ChromeDriver
        driver = new ChromeDriver(options);

        driver.manage().timeouts()
            .implicitlyWait(Duration.ZERO);

        driver.manage().timeouts()
            .pageLoadTimeout(
                Duration.ofSeconds(
                    ConfigReader.getInt("pageLoadTimeoutSeconds")
                )
            );

        driver.get(ConfigReader.get("baseUrl"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        if (driver != null) {

            if (!result.isSuccess()) {
                captureScreenshot(result.getName());
            }

            driver.quit();
        }
    }

    private void captureScreenshot(String testName) {

        try {

            File dir = new File("test-output/screenshots");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File source = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.FILE);

            Files.copy(
                source.toPath(),
                new File(
                    dir,
                    testName + "_" +
                    System.currentTimeMillis() + ".png"
                ).toPath()
            );

        } catch (IOException | WebDriverException e) {

            System.err.println(
                "Screenshot capture failed: " + e.getMessage()
            );
        }
    }
}
