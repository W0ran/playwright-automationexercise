package com.automationexercise.pages;

import com.microsoft.playwright.Page;

public class LoginPage extends BasePage {

    // ===== ЛОКАТОРЫ =====
    private static final String LOGIN_EMAIL    = "input[data-qa='login-email']";
    private static final String LOGIN_PASSWORD = "input[data-qa='login-password']";
    private static final String LOGIN_BUTTON   = "button[data-qa='login-button']";
    private static final String LOGIN_ERROR    = "p:has-text('Your email or password is incorrect!')";

    private static final String SIGNUP_NAME    = "input[data-qa='signup-name']";
    private static final String SIGNUP_EMAIL   = "input[data-qa='signup-email']";
    private static final String SIGNUP_BUTTON  = "button[data-qa='signup-button']";
    private static final String SIGNUP_ERROR   = "p:has-text('Email Address already exist!')";

    private static final String LOGGED_IN_USER = "li:has-text('Logged in as')";
    private static final String NAV_LOGOUT     = "a[href='/logout']";

    // ===== КОНСТРУКТОР =====
    public LoginPage(Page page) {
        super(page);
    }

    // ===== НАВИГАЦИЯ =====
    public LoginPage open() {
        navigateTo("https://www.automationexercise.com/login");
        return this;
    }

    // ===== ДЕЙСТВИЯ: ЛОГИН =====
    public void login(String email, String password) {
        fill(LOGIN_EMAIL, email);
        fill(LOGIN_PASSWORD, password);
        click(LOGIN_BUTTON);
    }

    // ===== ДЕЙСТВИЯ: РЕГИСТРАЦИЯ (первый шаг) =====
    public void enterSignupDetails(String name, String email) {
        fill(SIGNUP_NAME, name);
        fill(SIGNUP_EMAIL, email);
        click(SIGNUP_BUTTON);
    }

    // ===== ДЕЙСТВИЯ: ВЫХОД =====
    public void logout() {
        click(NAV_LOGOUT);
    }

    // ===== ПРОВЕРКИ =====
    public boolean isLoggedIn() {
        return isVisible(LOGGED_IN_USER);
    }

    public boolean isLoginErrorVisible() {
        return isVisible(LOGIN_ERROR);
    }

    public boolean isSignupErrorVisible() {
        return isVisible(SIGNUP_ERROR);
    }

    public String getLoggedInUsername() {
        return getText(LOGGED_IN_USER).replace("Logged in as ", "").trim();
    }
}