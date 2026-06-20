package com.automationexercise.tests;

import com.automationexercise.pages.LoginPage;
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

@Epic("Authentication")
@Feature("Login")
public class AuthTests extends BaseTest {

    // Уникальный email на каждый запуск — исключает конфликт "уже зарегистрирован"
    private static final String TEST_NAME      = "QA Portfolio User";
    private static final String VALID_EMAIL    = "qa.portfolio." + System.currentTimeMillis() + "@mailinator.com";
    private static final String VALID_PASSWORD = "TestPass123!";

    /**
     * Создаём тестового пользователя через API ДО запуска UI-тестов логина.
     * Это быстрее и стабильнее, чем регистрироваться через форму на сайте.
     */
    @BeforeClass
    public void createTestAccount() {
        boolean created = ApiTestDataHelper.createAccount(TEST_NAME, VALID_EMAIL, VALID_PASSWORD);
        Assert.assertTrue(created, "Тестовый аккаунт должен быть создан через API перед запуском тестов логина");
    }

    /**
     * Удаляем тестового пользователя после всех тестов класса — не оставляем мусор в системе.
     */
    @AfterClass
    public void deleteTestAccount() {
        ApiTestDataHelper.deleteAccount(VALID_EMAIL, VALID_PASSWORD);
    }

    @Test(description = "Успешный логин с валидными данными")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Пользователь вводит корректные email и пароль, ожидается успешный вход")
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(page).open();
        loginPage.login(VALID_EMAIL, VALID_PASSWORD);

        Assert.assertTrue(loginPage.isLoggedIn(),
                "Пользователь должен быть залогинен после ввода корректных данных");
    }

    @Test(description = "Логин с неверным паролем")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Пользователь вводит корректный email, но неверный пароль — ожидается ошибка")
    public void testLoginWithInvalidPassword() {
        LoginPage loginPage = new LoginPage(page).open();
        loginPage.login(VALID_EMAIL, "WrongPassword123");

        Assert.assertTrue(loginPage.isLoginErrorVisible(),
                "Должно отображаться сообщение об ошибке при неверном пароле");
    }

    @Test(description = "Логин с несуществующим email")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Пользователь вводит email, которого нет в системе — ожидается ошибка")
    public void testLoginWithNonExistentEmail() {
        LoginPage loginPage = new LoginPage(page).open();
        loginPage.login("nonexistent_" + System.currentTimeMillis() + "@test.com", "AnyPassword123");

        Assert.assertTrue(loginPage.isLoginErrorVisible(),
                "Должно отображаться сообщение об ошибке для незарегистрированного email");
    }

    @Test(description = "Логин с пустыми полями")
    @Severity(SeverityLevel.NORMAL)
    @Description("Пользователь отправляет форму логина без заполнения полей")
    public void testLoginWithEmptyFields() {
        LoginPage loginPage = new LoginPage(page).open();
        loginPage.login("", "");

        Assert.assertFalse(loginPage.isLoggedIn(),
                "Пользователь не должен быть залогинен с пустыми полями");
    }

    @Test(description = "Регистрация с уже существующим email")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Попытка регистрации с email, который уже используется в системе")
    public void testSignupWithExistingEmail() {
        LoginPage loginPage = new LoginPage(page).open();
        loginPage.enterSignupDetails("Test User", VALID_EMAIL);

        Assert.assertTrue(loginPage.isSignupErrorVisible(),
                "Должна отображаться ошибка о том, что email уже зарегистрирован");
    }

    @Test(description = "Логаут после успешного входа")
    @Severity(SeverityLevel.NORMAL)
    @Description("После логина пользователь нажимает Logout и возвращается на страницу логина")
    public void testLogout() {
        LoginPage loginPage = new LoginPage(page).open();
        loginPage.login(VALID_EMAIL, VALID_PASSWORD);
        Assert.assertTrue(loginPage.isLoggedIn(), "Предусловие: пользователь должен быть залогинен");

        loginPage.logout();

        Assert.assertTrue(page.url().contains("/login"),
                "После логаута пользователь должен быть перенаправлен на страницу логина");
    }
}