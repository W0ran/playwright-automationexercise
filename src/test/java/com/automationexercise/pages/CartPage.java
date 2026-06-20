package com.automationexercise.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CartPage extends BasePage {

    // ===== ЛОКАТОРЫ =====
    private static final String CART_TABLE        = "#cart_info_table";
    private static final String CART_ROWS          = "#cart_info_table tbody tr";
    private static final String PRODUCT_NAME_CELL   = ".cart_description h4 a";
    private static final String PRODUCT_PRICE_CELL  = ".cart_price p";
    private static final String PRODUCT_QUANTITY_CELL = ".cart_quantity button";
    private static final String PRODUCT_TOTAL_CELL  = ".cart_total p";
    private static final String DELETE_BUTTON       = ".cart_quantity_delete";

    private static final String EMPTY_CART_MESSAGE  = "#empty_cart";
    private static final String PROCEED_TO_CHECKOUT_BUTTON = "a:has-text('Proceed To Checkout')";

    // Модалка, которая показывается если в корзине нет зарегистрированного юзера
    private static final String CHECKOUT_REGISTER_LOGIN_LINK = "a:has-text('Register / Login')";

    public CartPage(Page page) {
        super(page);
    }

    // ===== НАВИГАЦИЯ =====
    public CartPage open() {
        navigateTo("https://www.automationexercise.com/view_cart");
        return this;
    }

    // ===== ПРОВЕРКИ =====
    public boolean isCartPageVisible() {
        return isVisible(CART_TABLE);
    }

    public boolean isCartEmpty() {
        return isVisible(EMPTY_CART_MESSAGE);
    }

    public int getProductsCountInCart() {
        return page.locator(CART_ROWS).count();
    }

    public boolean isProductInCart(String productName) {
        Locator names = page.locator(PRODUCT_NAME_CELL);
        int count = names.count();
        for (int i = 0; i < count; i++) {
            if (names.nth(i).innerText().trim().equalsIgnoreCase(productName.trim())) {
                return true;
            }
        }
        return false;
    }

    // ===== ДАННЫЕ ПО ТОВАРАМ В КОРЗИНЕ =====

    public String getProductNameByIndex(int index) {
        return page.locator(PRODUCT_NAME_CELL).nth(index).innerText().trim();
    }

    public String getProductPriceByIndex(int index) {
        return page.locator(PRODUCT_PRICE_CELL).nth(index).innerText().trim();
    }

    public String getProductQuantityByIndex(int index) {
        return page.locator(PRODUCT_QUANTITY_CELL).nth(index).innerText().trim();
    }

    public String getProductTotalByIndex(int index) {
        return page.locator(PRODUCT_TOTAL_CELL).nth(index).innerText().trim();
    }

    // ===== ДЕЙСТВИЯ =====

    /**
     * Удаляет товар из корзины по индексу строки.
     * После удаления строка анимированно исчезает — даём странице небольшую паузу.
     */
    public CartPage deleteProductByIndex(int index) {
        page.locator(DELETE_BUTTON).nth(index).click();
        page.waitForTimeout(500); // ожидание fade-out анимации удаления строки
        return this;
    }

    public CartPage deleteProductByName(String productName) {
        Locator rows = page.locator(CART_ROWS);
        int count = rows.count();
        for (int i = 0; i < count; i++) {
            String name = rows.nth(i).locator(PRODUCT_NAME_CELL).innerText().trim();
            if (name.equalsIgnoreCase(productName.trim())) {
                rows.nth(i).locator(DELETE_BUTTON).click();
                page.waitForTimeout(500);
                return this;
            }
        }
        throw new IllegalArgumentException("Товар '" + productName + "' не найден в корзине для удаления");
    }

    public CheckoutPage proceedToCheckout() {
        click(PROCEED_TO_CHECKOUT_BUTTON);
        return new CheckoutPage(page);
    }

    public boolean isRegisterLoginPromptVisible() {
        return isVisible(CHECKOUT_REGISTER_LOGIN_LINK);
    }

    public LoginPage goToLoginFromCheckoutPrompt() {
        click(CHECKOUT_REGISTER_LOGIN_LINK);
        return new LoginPage(page);
    }
}