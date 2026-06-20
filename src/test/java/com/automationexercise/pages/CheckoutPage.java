package com.automationexercise.pages;

import com.microsoft.playwright.Page;

public class CheckoutPage extends BasePage {

    // ===== ЛОКАТОРЫ: СТРАНИЦА CHECKOUT (адрес + обзор заказа) =====
    private static final String ADDRESS_DELIVERY_TITLE = "h3:has-text('Your delivery address')";
    private static final String ADDRESS_BILLING_TITLE   = "h3:has-text('Your billing address')";
    private static final String REVIEW_ORDER_TITLE       = "h2:has-text('Review Your Order')";
    private static final String ORDER_COMMENT_TEXTAREA   = "textarea[name='message']";
    private static final String PLACE_ORDER_BUTTON        = "a:has-text('Place Order')";

    // ===== ЛОКАТОРЫ: СТРАНИЦА ОПЛАТЫ =====
    private static final String NAME_ON_CARD_INPUT = "input[name='name_on_card']";
    private static final String CARD_NUMBER_INPUT   = "input[name='card_number']";
    private static final String CVC_INPUT            = "input[name='cvc']";
    private static final String EXPIRY_MONTH_INPUT   = "input[name='expiry_month']";
    private static final String EXPIRY_YEAR_INPUT     = "input[name='expiry_year']";
    private static final String PAY_AND_CONFIRM_BUTTON = "#submit";

    // ===== ЛОКАТОРЫ: РЕЗУЛЬТАТ ЗАКАЗА =====
    private static final String ORDER_SUCCESS_MESSAGE = "p:has-text('order has been confirmed')";
    private static final String ACCOUNT_DELETED_MESSAGE = "h2:has-text('Account Deleted!')";
    private static final String DELETE_ACCOUNT_CONTINUE_BUTTON = "a:has-text('Continue')";

    public CheckoutPage(Page page) {
        super(page);
    }

    // ===== ПРОВЕРКИ: СТРАНИЦА CHECKOUT =====
    public boolean isAddressSectionVisible() {
        return isVisible(ADDRESS_DELIVERY_TITLE) && isVisible(ADDRESS_BILLING_TITLE);
    }

    public boolean isReviewOrderVisible() {
        return isVisible(REVIEW_ORDER_TITLE);
    }

    // ===== ДЕЙСТВИЯ: СТРАНИЦА CHECKOUT =====
    public CheckoutPage enterOrderComment(String comment) {
        fill(ORDER_COMMENT_TEXTAREA, comment);
        return this;
    }

    public CheckoutPage placeOrder() {
        click(PLACE_ORDER_BUTTON);
        return this;
    }

    // ===== ДЕЙСТВИЯ: ОПЛАТА =====

    /**
     * Заполняет тестовые платёжные данные. Это демо-сайт — реальная оплата
     * не производится, данные карты полностью фиктивные.
     */
    public CheckoutPage fillPaymentDetails(String nameOnCard, String cardNumber,
                                           String cvc, String expiryMonth, String expiryYear) {
        fill(NAME_ON_CARD_INPUT, nameOnCard);
        fill(CARD_NUMBER_INPUT, cardNumber);
        fill(CVC_INPUT, cvc);
        fill(EXPIRY_MONTH_INPUT, expiryMonth);
        fill(EXPIRY_YEAR_INPUT, expiryYear);
        return this;
    }

    public void confirmPayment() {
        click(PAY_AND_CONFIRM_BUTTON);
    }

    // ===== ПРОВЕРКИ: РЕЗУЛЬТАТ =====
    public boolean isOrderSuccessMessageVisible() {
        return isVisible(ORDER_SUCCESS_MESSAGE);
    }

    public boolean isAccountDeletedMessageVisible() {
        return isVisible(ACCOUNT_DELETED_MESSAGE);
    }

    public void clickContinueAfterAccountDeleted() {
        click(DELETE_ACCOUNT_CONTINUE_BUTTON);
    }
}