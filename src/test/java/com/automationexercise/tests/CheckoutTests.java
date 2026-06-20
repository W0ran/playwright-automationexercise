package com.automationexercise.tests;

import com.automationexercise.pages.CartPage;
import com.automationexercise.pages.CheckoutPage;
import com.automationexercise.pages.LoginPage;
import com.automationexercise.pages.ProductsPage;
import com.automationexercise.utils.ApiTestDataHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("Purchase Flow")
@Feature("Checkout")
public class CheckoutTests extends BaseTest {

    private static final String TEST_NAME      = "QA Checkout User";
    private static final String VALID_EMAIL    = "qa.checkout." + System.currentTimeMillis() + "@mailinator.com";
    private static final String VALID_PASSWORD = "TestPass123!";

    /**
     * Тестовый аккаунт создаётся через API один раз перед всеми checkout-тестами —
     * на сайте checkout доступен только залогиненным пользователям.
     */
    @BeforeClass
    public void createTestAccount() {
        boolean created = ApiTestDataHelper.createAccount(TEST_NAME, VALID_EMAIL, VALID_PASSWORD);
        Assert.assertTrue(created, "Тестовый аккаунт должен быть создан через API перед запуском checkout-тестов");
    }

    @AfterClass
    public void deleteTestAccount() {
        ApiTestDataHelper.deleteAccount(VALID_EMAIL, VALID_PASSWORD);
    }

    @Test(description = "Неавторизованный пользователь не может оформить заказ напрямую")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Гость добавляет товар в корзину и нажимает Proceed to Checkout — должен увидеть предложение войти/зарегистрироваться")
    public void testGuestCannotCheckoutDirectly() {
        ProductsPage productsPage = new ProductsPage(page).open();
        productsPage.addProductToCartByIndex(0);
        CartPage cartPage = productsPage.goToCartFromModal();

        cartPage.proceedToCheckout();

        Assert.assertTrue(cartPage.isRegisterLoginPromptVisible(),
                "Гостю должно быть предложено зарегистрироваться/войти перед оформлением заказа");
    }

    @Test(description = "Полный флоу покупки: логин -> добавление товара -> checkout -> оплата -> успех")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Авторизованный пользователь проходит весь путь от каталога до подтверждения заказа")
    public void testFullCheckoutFlow() {
        // Логин
        LoginPage loginPage = new LoginPage(page).open();
        loginPage.login(VALID_EMAIL, VALID_PASSWORD);
        Assert.assertTrue(loginPage.isLoggedIn(), "Предусловие: пользователь должен быть залогинен перед оформлением заказа");

        // Добавление товара в корзину
        ProductsPage productsPage = new ProductsPage(page).open();
        productsPage.addProductToCartByIndex(1);
        CartPage cartPage = productsPage.goToCartFromModal();

        Assert.assertEquals(cartPage.getProductsCountInCart(), 1,
                "В корзине должен быть ровно 1 товар перед переходом к оформлению");

        // Переход к оформлению заказа
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        Assert.assertTrue(checkoutPage.isAddressSectionVisible(),
                "На странице checkout должны отображаться адрес доставки и адрес для счёта");
        Assert.assertTrue(checkoutPage.isReviewOrderVisible(),
                "На странице checkout должен отображаться обзор заказа (Review Your Order)");

        checkoutPage.enterOrderComment("Тестовый заказ - автоматизация QA портфолио, доставка не требуется.");
        checkoutPage.placeOrder();

        // Оплата тестовыми (фиктивными) данными карты
        checkoutPage.fillPaymentDetails(
                "QA Test User",
                "4111111111111111",
                "123",
                "12",
                "2030"
        );
        checkoutPage.confirmPayment();

        Assert.assertTrue(checkoutPage.isOrderSuccessMessageVisible(),
                "После подтверждения оплаты должно отображаться сообщение об успешном оформлении заказа");
    }
}