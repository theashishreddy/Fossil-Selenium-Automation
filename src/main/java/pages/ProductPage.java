
package pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProductPage extends BasePage {

    private final By addToCart = By.xpath(
        "//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to cart') or " +
        "contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to bag') or " +
        "contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to cart')] | " +
        "//input[@type='submit' and contains(translate(@value,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add')]"
    );

    private final By price = By.cssSelector(
        "[class*='price'], [itemprop='price'], [data-testid*='price']"
    );

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public String getDisplayedPrice() {
        try {
            return firstVisible(price).getText().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public boolean isProductDetailVisible() {
        return exists(addToCart)
            || exists(price)
            || driver.getCurrentUrl().matches(".*(product|products|p/).*");
    }

    public void addToCart() {
        click(addToCart);
    }

    public void openCart() {
        clickFirst(
            By.cssSelector(
                "a[href*='/cart'], a[href*='cart'], " +
                "button[aria-label*='cart' i], [class*='cart-icon']"
            )
        );
    }

    // TEMPORARY DEBUG METHOD: identifies the actual product-page elements.
    public void debugProductPage() {

        System.out.println("\n========== PRODUCT PAGE DEBUG ==========");
        System.out.println("URL: " + driver.getCurrentUrl());
        System.out.println("TITLE: " + driver.getTitle());

        System.out.println("\n--- HEADINGS ---");

        for (WebElement element : driver.findElements(
                By.cssSelector("h1, h2, h3"))) {
            try {
                if (element.isDisplayed()) {
                    System.out.println("HEADING: " + element.getText());
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }

        System.out.println("\n--- VISIBLE BUTTONS ---");

        List<WebElement> buttons = driver.findElements(
            By.cssSelector("button, input[type='submit'], [role='button']")
        );

        for (WebElement element : buttons) {
            try {
                if (element.isDisplayed()) {
                    System.out.println(
                        "TAG: " + element.getTagName()
                        + " | TEXT: " + element.getText()
                        + " | ARIA: " + element.getAttribute("aria-label")
                        + " | ID: " + element.getAttribute("id")
                        + " | CLASS: " + element.getAttribute("class")
                        + " | VALUE: " + element.getAttribute("value")
                        + " | HTML: " + element.getAttribute("outerHTML")
                    );
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }

        System.out.println("\n--- PRODUCT LINKS ---");

        for (WebElement element : driver.findElements(
                By.cssSelector("a[href]"))) {
            try {
                if (element.isDisplayed()) {
                    System.out.println(
                        "TEXT: " + element.getText()
                        + " | HREF: " + element.getAttribute("href")
                    );
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }

        System.out.println("\n--- BODY TEXT ---");
        System.out.println(
            driver.findElement(By.tagName("body")).getText()
        );

        System.out.println("========== END DEBUG ==========\n");
    }
}
