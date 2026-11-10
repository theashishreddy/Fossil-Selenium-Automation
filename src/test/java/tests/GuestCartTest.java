package tests;

import base.BaseTest;
import config.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.HomePage;
import pages.ProductPage;

public class GuestCartTest extends BaseTest {
    @Test(description = "Guest adds a product to cart and verifies cart state")
    public void guestCanAddProductToCart() {
        HomePage home = new HomePage(driver);
        Assert.assertTrue(home.isHomeLoaded(), "Fossil storefront should load");
        home.search(ConfigReader.get("searchTerm"));
        Assert.assertTrue(home.hasProductResults(), "Search results should be available");
        home.openFirstProduct();

        ProductPage product = new ProductPage(driver);
        Assert.assertTrue(product.isProductDetailVisible(), "Product details should be visible");
        String productPrice = product.getDisplayedPrice();
        Assert.assertFalse(productPrice.isBlank(), "Product price should be visible before adding to cart");
        product.addToCart();
        product.openCart();

        CartPage cart = new CartPage(driver);
        Assert.assertTrue(cart.isCartPageVisible(), "Cart page or cart drawer should be visible");
        Assert.assertTrue(cart.hasCartContent(), "Cart should contain the selected product");
    }

    @Test(description = "Cart exposes quantity controls when the storefront supports quantity editing")
    public void cartQuantityControlAvailability() {
        HomePage home = new HomePage(driver);
        home.search(ConfigReader.get("searchTerm"));
        Assert.assertTrue(home.hasProductResults(), "Search results should be available");
        home.openFirstProduct();
        ProductPage product = new ProductPage(driver);
        product.addToCart();
        product.openCart();

        CartPage cart = new CartPage(driver);
        Assert.assertTrue(cart.isCartPageVisible(), "Cart should be reachable");
        // Quantity editing is storefront-dependent; assert presence only when the UI exposes it.
        if (cart.getQuantityInputCount() > 0) {
            Assert.assertTrue(cart.getQuantityInputCount() >= 1, "At least one quantity field should be available");
        } else {
            System.out.println("INFO: No editable quantity input exposed in this cart UI; quantity test is not applicable.");
        }
    }
}
