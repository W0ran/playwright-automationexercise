package com.automationexercise.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.testng.annotations.*;

public class BaseTest {

    // static — один на всю JVM/suite, не пересоздаётся для каждого класса.
    // Инициализация синхронизирована, чтобы исключить гонку при параллельных <test> блоках.
    protected static Playwright playwright;
    private static final Object LOCK = new Object();

    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        synchronized (LOCK) {
            if (playwright == null) {
                playwright = Playwright.create();
            }
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        // Защитный фолбэк: если по какой-то причине @BeforeSuite ещё не отработал
        // в этом потоке (наблюдалось при нескольких параллельных <test> блоках в testng.xml),
        // инициализируем Playwright здесь же, лениво и потокобезопасно.
        synchronized (LOCK) {
            if (playwright == null) {
                playwright = Playwright.create();
            }
        }

        // Headless управляется переменной окружения CI (GitHub Actions выставляет CI=true
        // автоматически). Локально браузер остаётся видимым для удобства разработки,
        // на CI-раннере (где нет дисплея) запускается headless.
        boolean isCi = "true".equalsIgnoreCase(System.getenv("CI"));

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(isCi)
                        .setSlowMo(isCi ? 0 : 50)   // задержка для наглядности только локально
        );

        context = browser.newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(1920, 1080)
        );

        page = context.newPage();

        // Увеличиваем дефолтный таймаут для всех действий на странице —
        // automationexercise.com иногда грузится дольше стандартных 30 сек
        // из-за рекламных баннеров и сторонних скриптов.
        page.setDefaultTimeout(60000);
        page.setDefaultNavigationTimeout(60000);

        navigateWithRetry("https://www.automationexercise.com");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
        if (browser != null) browser.close();
    }

    @AfterSuite(alwaysRun = true)
    public void globalTearDown() {
        synchronized (LOCK) {
            if (playwright != null) {
                playwright.close();
                playwright = null;
            }
        }
    }

    /**
     * Переход на страницу с ожиданием DOMContentLoaded (а не полной загрузки
     * всех ресурсов вроде рекламы/трекеров) + одна повторная попытка при таймауте.
     * Это заметно снижает количество флейков на медленных страницах.
     */
    protected void navigateWithRetry(String url) {
        try {
            page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        } catch (PlaywrightException e) {
            System.out.println("[BaseTest] Навигация на " + url + " не удалась с первой попытки, повтор: " + e.getMessage());
            page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        }
    }
}