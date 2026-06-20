package com.automationexercise.pages;

import com.microsoft.playwright.Page;

public class HomePage extends BasePage {

    // ===== ЛОКАТОРЫ: ШАПКА / НАВИГАЦИЯ =====
    private static final String NAV_PRODUCTS     = "a[href='/products']";
    private static final String NAV_CART         = "a[href='/view_cart']";
    private static final String NAV_LOGIN         = "a[href='/login']";
    private static final String NAV_LOGOUT        = "a[href='/logout']";
    private static final String NAV_DELETE_ACCOUNT = "a[href='/delete_account']";
    private static final String NAV_TEST_CASES    = "a[href='/test_cases']";
    private static final String NAV_CONTACT_US    = "a[href='/contact_us']";

    private static final String LOGGED_IN_USER    = "li:has-text('Logged in as')";

    // ===== ЛОКАТОРЫ: ГЛАВНАЯ СТРАНИЦА =====
    private static final String FEATURED_ITEMS_TITLE = "h2:has-text('Features Items')";
    private static final String SLIDER               = "#slider";

    // ===== ЛОКАТОРЫ: ПОДПИСКА (футер) =====
    private static final String SUBSCRIBE_EMAIL_INPUT = "#susbscribe_email";
    private static final String SUBSCRIBE_BUTTON       = "#subscribe";
    private static final String SUBSCRIBE_SUCCESS_MSG  = "#success-subscribe";

    // ===== ЛОКАТОРЫ: СКРОЛЛ ВВЕРХ =====
    private static final String SCROLL_UP_BUTTON = "#scrollUp";

    public HomePage(Page page) {
        super(page);
    }

    // ===== НАВИГАЦИЯ =====
    public HomePage open() {
        navigateTo("https://www.automationexercise.com");
        return this;
    }

    public LoginPage goToLogin() {
        click(NAV_LOGIN);
        return new LoginPage(page);
    }

    public ProductsPage goToProducts() {
        click(NAV_PRODUCTS);
        return new ProductsPage(page);
    }

    public CartPage goToCart() {
        click(NAV_CART);
        return new CartPage(page);
    }

    public void logout() {
        click(NAV_LOGOUT);
    }

    public void deleteAccount() {
        click(NAV_DELETE_ACCOUNT);
    }

    // ===== ПОДПИСКА =====
    public void subscribe(String email) {
        scrollToElement(SUBSCRIBE_EMAIL_INPUT);
        fill(SUBSCRIBE_EMAIL_INPUT, email);
        click(SUBSCRIBE_BUTTON);
    }

    public boolean isSubscribeSuccessVisible() {
        return isVisible(SUBSCRIBE_SUCCESS_MSG);
    }

    // ===== ПРОВЕРКИ =====
    public boolean isHomePageVisible() {
        return isVisible(SLIDER) && isVisible(FEATURED_ITEMS_TITLE);
    }

    public boolean isLoggedIn() {
        return isVisible(LOGGED_IN_USER);
    }

    public String getLoggedInUsername() {
        return getText(LOGGED_IN_USER).replace("Logged in as ", "").trim();
    }

    // ===== СКРОЛЛ =====
    public void scrollToBottomOfPage() {
        scrollToBottom();
    }

    public void clickScrollUp() {
        click(SCROLL_UP_BUTTON);
    }
}