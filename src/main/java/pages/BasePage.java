
package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import config.ConfigReader;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;

        this.wait = new WebDriverWait(
            driver,
            Duration.ofSeconds(
                ConfigReader.getInt("explicitWaitSeconds")
            )
        );

        this.wait.ignoring(
            StaleElementReferenceException.class
        );
    }

    protected WebElement visible(By locator) {
        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    protected WebElement clickable(By locator) {
        return wait.until(
            ExpectedConditions.elementToBeClickable(locator)
        );
    }

    protected void click(By locator) {
        clickable(locator).click();
    }

    protected void type(By locator, String value) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected boolean exists(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected WebElement firstVisible(By... locators) {

        for (By locator : locators) {

            List<WebElement> elements =
                driver.findElements(locator);

            for (WebElement element : elements) {

                try {
                    if (element.isDisplayed()) {
                        return element;
                    }
                } catch (StaleElementReferenceException ignored) {
                }
            }
        }

        throw new NoSuchElementException(
            "No visible element found"
        );
    }

    protected void clickFirst(By... locators) {

        wait.until(driver -> {
            try {
                firstVisible(locators).click();
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        });
    }
}
