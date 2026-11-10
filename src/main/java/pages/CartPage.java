package pages;

import java.util.List;
import org.openqa.selenium.*;

public class CartPage extends BasePage {
    private final By cartLines = By.cssSelector("[class*='cart-item'], [class*='cart-product'], tr[class*='item'], [data-testid*='cart-item']");
    private final By quantityInputs = By.cssSelector("input[type='number'], input[name*='qty' i], input[name*='quantity' i]");
    private final By removeButtons = By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'remove') or contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'remove') or contains(translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'remove')]");
    private final By subtotal = By.xpath("//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'subtotal')]/following::*[self::span or self::div][1]");

    public CartPage(WebDriver driver) { super(driver); }
    public boolean isCartPageVisible() {
        return driver.getCurrentUrl().toLowerCase().contains("cart")
            || exists(cartLines) || exists(quantityInputs)
            || driver.getPageSource().toLowerCase().contains("your bag");
    }
    public int getQuantityInputCount() { return driver.findElements(quantityInputs).size(); }
    public String getSubtotalText() {
        try { return firstVisible(subtotal).getText().trim(); } catch (NoSuchElementException e) { return ""; }
    }
    public boolean hasCartContent() {
        return !driver.findElements(cartLines).isEmpty()
            || driver.getPageSource().toLowerCase().contains("checkout");
    }
    public void setQuantity(String quantity) {
        List<WebElement> inputs = driver.findElements(quantityInputs);
        if (inputs.isEmpty()) throw new NoSuchElementException("No editable quantity field found on cart page");
        WebElement input = inputs.get(0); input.clear(); input.sendKeys(quantity); input.sendKeys(Keys.TAB);
    }
    public void removeFirstItem() {
        List<WebElement> buttons = driver.findElements(removeButtons);
        if (buttons.isEmpty()) throw new NoSuchElementException("Remove control not found");
        buttons.get(0).click();
    }
}
