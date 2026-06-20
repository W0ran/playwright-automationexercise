package com.automationexercise.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;

public class BasePage {

    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    // ===== НАВИГАЦИЯ =====

    protected void navigateTo(String url) {
        // DOMCONTENTLOADED вместо полной загрузки (load) — не ждём рекламу/трекеры,
        // достаточно того, что DOM и форма готовы к взаимодействию.
        page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }

    protected String getCurrentUrl() {
        return page.url();
    }

    protected String getTitle() {
        return page.title();
    }

    // ===== БАЗОВЫЕ ДЕЙСТВИЯ =====

    protected void click(String selector) {
        page.locator(selector).click();
    }

    protected void fill(String selector, String text) {
        page.locator(selector).clear();
        page.locator(selector).fill(text);
    }

    protected void selectOption(String selector, String value) {
        page.locator(selector).selectOption(value);
    }

    // ===== ОЖИДАНИЯ =====

    protected void waitForVisible(String selector) {
        page.locator(selector).waitFor(
                new Locator.WaitForOptions()
                        .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
        );
    }

    protected void waitForUrl(String urlPattern) {
        page.waitForURL(urlPattern);
    }

    // ===== ПРОВЕРКИ =====

    protected boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    protected String getText(String selector) {
        return page.locator(selector).innerText().trim();
    }

    protected boolean isDisplayed(String selector) {
        return page.locator(selector).count() > 0
                && page.locator(selector).isVisible();
    }

    // ===== СКРОЛЛ =====

    protected void scrollToElement(String selector) {
        page.locator(selector).scrollIntoViewIfNeeded();
    }

    protected void scrollToBottom() {
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
    }

    // ===== СКРИНШОТ (для Allure) =====

    public byte[] takeScreenshot() {
        return page.screenshot();
    }
}