
package pages;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

    // Search input
    private final By searchField = By.cssSelector(
        "input[type='search'][placeholder='Enter your search'], " +
        "input[type='search']"
    );

    // Possible search panel triggers
    private final List<By> searchTriggers = Arrays.asList(
        By.cssSelector("[aria-label*='search' i]"),
        By.cssSelector("[title*='search' i]"),
        By.cssSelector("button[class*='search' i]"),
        By.cssSelector("a[class*='search' i]"),
        By.cssSelector("[data-testid*='search' i]"),
        By.cssSelector("[role='button'][class*='search' i]")
    );

    // Product links
    private final By productLinks = By.cssSelector(
        "a[href*='/product'], " +
        "a[href*='/products'], " +
        ".product-item a, " +
        ".product-card a, " +
        "[data-testid*='product'] a"
    );

    // Product containers
    private final By productContainers = By.cssSelector(
        "[class*='product-card'], " +
        "[class*='product-item'], " +
        "[data-testid*='product']"
    );

    public HomePage(WebDriver driver) {
        super(driver);
    }

    // Check whether homepage is loaded
    public boolean isHomeLoaded() {

        wait.until(ExpectedConditions.or(
            ExpectedConditions.titleContains("Fossil"),
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("header, main, [role='main']")
            )
        ));

        return !driver.findElements(
            By.tagName("body")
        ).isEmpty();
    }

    // Find visible search input
    private WebElement findVisibleSearchField() {

        return wait.until(d -> {

            for (WebElement element : d.findElements(searchField)) {

                try {
                    if (element.isDisplayed()
                            && element.isEnabled()) {
                        return element;
                    }

                } catch (StaleElementReferenceException ignored) {
                }
            }

            return null;
        });
    }

    // Check search input visibility
    private boolean isSearchFieldVisible() {

        for (WebElement element : driver.findElements(searchField)) {

            try {
                if (element.isDisplayed()
                        && element.isEnabled()) {
                    return true;
                }

            } catch (StaleElementReferenceException ignored) {
            }
        }

        return false;
    }

    // Open collapsed search panel if necessary
    private void openSearchPanelIfNeeded() {

        if (isSearchFieldVisible()) {
            return;
        }

        for (By triggerLocator : searchTriggers) {

            for (WebElement trigger :
                    driver.findElements(triggerLocator)) {

                try {
                    if (trigger.isDisplayed()
                            && trigger.isEnabled()) {

                        trigger.click();

                        try {
                            findVisibleSearchField();
                            return;

                        } catch (TimeoutException ignored) {
                        }
                    }

                } catch (StaleElementReferenceException ignored) {
                }
            }
        }
    }

    // Print search diagnostics if search field is not found
    private void printSearchDiagnostics() {

        System.out.println(
            "===== FOSSIL SEARCH DIAGNOSTICS ====="
        );

        System.out.println("URL: " + driver.getCurrentUrl());
        System.out.println("Title: " + driver.getTitle());

        System.out.println("--- Search inputs ---");

        for (WebElement input :
                driver.findElements(By.tagName("input"))) {

            try {
                System.out.println(
                    "HTML: " + input.getAttribute("outerHTML")
                    + " | Displayed: " + input.isDisplayed()
                    + " | Enabled: " + input.isEnabled()
                );

            } catch (StaleElementReferenceException ignored) {
            }
        }

        System.out.println("--- Visible buttons and links ---");

        for (WebElement element : driver.findElements(
                By.cssSelector("button, a, [role='button']"))) {

            try {
                if (element.isDisplayed()) {

                    System.out.println(
                        "Tag: " + element.getTagName()
                        + " | Text: " + element.getText()
                        + " | Aria: " + element.getAttribute("aria-label")
                        + " | Title: " + element.getAttribute("title")
                        + " | Class: " + element.getAttribute("class")
                    );
                }

            } catch (StaleElementReferenceException ignored) {
            }
        }

        System.out.println(
            "===================================="
        );
    }

    // Search for a product
    public void search(String term) {

        System.out.println("Searching product: " + term);
        System.out.println(
            "Current URL: " + driver.getCurrentUrl()
        );

        WebElement field;

        try {

            if (!isSearchFieldVisible()) {
                openSearchPanelIfNeeded();
            }

            field = findVisibleSearchField();

        } catch (TimeoutException e) {

            printSearchDiagnostics();

            throw new NoSuchElementException(
                "Search input could not be made visible.",
                e
            );
        }

        field.click();
        field.clear();
        field.sendKeys(term);
        field.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("search"),
            ExpectedConditions.presenceOfElementLocated(productLinks),
            ExpectedConditions.presenceOfElementLocated(
                productContainers
            )
        ));

        System.out.println(
            "Search submitted for: " + term
        );
    }

    // Check whether product results are displayed
    public boolean hasProductResults() {

        return wait.until(d -> {

            for (WebElement element :
                    d.findElements(productLinks)) {

                try {
                    if (element.isDisplayed()) {
                        return true;
                    }

                } catch (StaleElementReferenceException ignored) {
                }
            }

            for (WebElement element :
                    d.findElements(productContainers)) {

                try {
                    if (element.isDisplayed()) {
                        return true;
                    }

                } catch (StaleElementReferenceException ignored) {
                }
            }

            return false;
        });
    }

    // Open the first visible product
    public void openFirstProduct() {

        WebElement product = wait.until(d -> {

            for (WebElement element :
                    d.findElements(productLinks)) {

                try {

                    String href = element.getAttribute("href");

                    if (element.isDisplayed()
                            && element.isEnabled()
                            && href != null
                            && !href.isBlank()
                            && element.getRect().getWidth() > 0
                            && element.getRect().getHeight() > 0) {

                        return element;
                    }

                } catch (StaleElementReferenceException ignored) {
                }
            }

            return null;
        });

        String productUrl = product.getAttribute("href");

        System.out.println(
            "Selected product URL: " + productUrl
        );

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block:'center'});",
            product
        );

        try {

            wait.until(
                ExpectedConditions.elementToBeClickable(product)
            );

            product.click();

        } catch (ElementNotInteractableException e) {

            System.out.println(
                "Normal click failed. Using product URL fallback."
            );

            if (productUrl == null || productUrl.isBlank()) {
                throw e;
            }

            driver.navigate().to(productUrl);
        }

        System.out.println(
            "Current URL after opening product: "
            + driver.getCurrentUrl()
        );

        System.out.println("First product opened.");
    }
}
