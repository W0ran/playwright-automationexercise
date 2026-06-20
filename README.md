# Playwright + Java Test Automation Framework

[![CI](https://github.com/W0ran/playwright-automationexercise/actions/workflows/ci.yml/badge.svg)](https://github.com/W0ran/playwright-automationexercise/actions/workflows/ci.yml)

End-to-end UI test automation framework for [automationexercise.com](https://automationexercise.com), built with **Java**, **Playwright**, and **TestNG**, following the **Page Object Model (POM)** design pattern.

This project was built as a portfolio piece to demonstrate practical test automation engineering skills: framework architecture, stable locator strategy, API-based test data setup, and CI-friendly configuration.

---

## Tech Stack

| Tool | Purpose |
|---|---|
| **Java 17** | Core language |
| **Playwright** | Browser automation |
| **TestNG** | Test runner, assertions, suite configuration |
| **Maven** | Build tool & dependency management |
| **Allure** | Test reporting |
| **Java HttpClient** | API calls for test data setup/teardown |

---

## What's covered

### Authentication (`AuthTests`)
- Successful login
- Login with invalid password
- Login with non-existent email
- Login with empty fields
- Signup with already-registered email
- Logout flow

### Catalog & Product Search (`ProductTests`)
- Catalog page loads and displays products
- Product search returns relevant results
- Search with no matches returns empty result set
- Add product to cart from catalog
- View product details page

### Checkout (`CheckoutTests`)
- Guest user is redirected to login/signup when attempting checkout
- Full purchase flow: login → add to cart → checkout → payment → order confirmation

**13 tests total**, all passing.

---

## Architecture highlights

- **Page Object Model** — every page is a class (`LoginPage`, `ProductsPage`, `CartPage`, `CheckoutPage`, etc.), inheriting shared utilities from `BasePage`.
- **API-based test data setup** — instead of registering test users through the UI (slow, flaky), `ApiTestDataHelper` creates and deletes accounts via the site's REST API (`/api/createAccount`, `/api/deleteAccount`) directly from Java's built-in `HttpClient`. This keeps UI tests focused on what they're meant to test.
- **Resilient interactions** — flaky third-party UI elements (hover-triggered overlays, animated modals) are handled with explicit waits and a retry-on-failure strategy rather than fixed sleeps.
- **Thread-safe Playwright lifecycle** — a single static `Playwright` instance is shared safely across parallel TestNG `<test>` blocks.
- **Allure annotations** (`@Epic`, `@Feature`, `@Severity`, `@Description`) on every test for structured, readable reports.

---

## Project structure

```
playwright-automationexercise/
├── pom.xml
├── testng.xml
└── src/
    └── test/
        ├── java/
        │   └── com/automationexercise/
        │       ├── pages/
        │       │   ├── BasePage.java
        │       │   ├── HomePage.java
        │       │   ├── LoginPage.java
        │       │   ├── ProductsPage.java
        │       │   ├── CartPage.java
        │       │   └── CheckoutPage.java
        │       ├── tests/
        │       │   ├── BaseTest.java
        │       │   ├── AuthTests.java
        │       │   ├── ProductTests.java
        │       │   └── CheckoutTests.java
        │       └── utils/
        │           └── ApiTestDataHelper.java
        └── resources/
```

---

## Running the tests

### Prerequisites
- Java 17+
- Maven 3.8+

### Install Playwright browsers (first run only)
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### Run all tests
```bash
mvn clean test
```

### Generate Allure report
```bash
mvn allure:report
mvn allure:serve
```

---

## Notes

- Tests run against the live public site [automationexercise.com](https://automationexercise.com), a demo site built specifically for automation practice.
- Test accounts are created and deleted via API for each test class run — no manual setup required.
- Browser runs in headed mode by default (`setHeadless(false)` in `BaseTest`) for demo visibility; switch to `true` for CI/headless environments.

---

## Author

**Yerkebulan Rakhymbay** — QA Engineer & Systems Analyst

- LinkedIn: [linkedin.com/in/yerkebulan-rakhymbay-29444336a](https://www.linkedin.com/in/yerkebulan-rakhymbay-29444336a/)
- GitHub: [github.com/W0ran](https://github.com/W0ran)
- Email: woran.96.kaz.kz@gmail.com