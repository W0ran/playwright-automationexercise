package com.automationexercise.tests;

import com.automationexercise.pages.CartPage;
import com.automationexercise.pages.ProductsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Catalog")
@Feature("Products")
public class ProductTests extends BaseTest {

    @Test(description = "Каталог товаров открывается и отображает список товаров")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Переход в раздел Products, проверка заголовка и наличия карточек товаров")
    public void testAllProductsPageIsVisible() {
        ProductsPage productsPage = new ProductsPage(page).open();

        Assert.assertTrue(productsPage.isAllProductsPageVisible(),
                "Заголовок 'All Products' должен быть виден на странице каталога");
        Assert.assertTrue(productsPage.getVisibleProductsCount() > 0,
                "На странице каталога должна отображаться хотя бы одна карточка товара");
    }

    @Test(description = "Поиск товара по ключевому слову возвращает релевантные результаты")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Пользователь ищет 'Top' — ожидается заголовок 'Searched Products' и совпадающие позиции")
    public void testSearchProduct() {
        ProductsPage productsPage = new ProductsPage(page).open();
        productsPage.searchProduct("Top");

        Assert.assertTrue(productsPage.isSearchedProductsTitleVisible(),
                "После поиска должен отображаться заголовок 'Searched Products'");
        Assert.assertTrue(productsPage.getVisibleProductsCount() > 0,
                "Результаты поиска не должны быть пустыми для популярного запроса 'Top'");
    }

    @Test(description = "Поиск несуществующего товара возвращает пустой список")
    @Severity(SeverityLevel.NORMAL)
    @Description("Поиск по случайной бессмысленной строке не должен возвращать товары")
    public void testSearchNonExistentProduct() {
        ProductsPage productsPage = new ProductsPage(page).open();
        productsPage.searchProduct("zzzNonExistentProduct12345");

        Assert.assertTrue(productsPage.isSearchedProductsTitleVisible(),
                "Заголовок 'Searched Products' должен отображаться даже при пустом результате");
        Assert.assertEquals(productsPage.getVisibleProductsCount(), 0,
                "Для несуществующего товара список результатов должен быть пустым");
    }

    @Test(description = "Добавление товара в корзину со страницы каталога")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Пользователь добавляет первый товар из каталога в корзину и переходит в корзину через модалку")
    public void testAddProductToCartFromCatalog() {
        ProductsPage productsPage = new ProductsPage(page).open();
        productsPage.addProductToCartByIndex(0);

        Assert.assertTrue(productsPage.isCartModalVisible(),
                "После добавления товара должна появиться модалка 'Added!'");

        CartPage cartPage = productsPage.goToCartFromModal();

        Assert.assertTrue(cartPage.isCartPageVisible(),
                "Страница корзины должна открыться и отобразить таблицу с товарами");
        Assert.assertEquals(cartPage.getProductsCountInCart(), 1,
                "В корзине должен быть ровно 1 товар после одного добавления");
    }

    @Test(description = "Просмотр деталей товара")
    @Severity(SeverityLevel.NORMAL)
    @Description("Пользователь открывает страницу деталей товара и видит его название")
    public void testViewProductDetails() {
        ProductsPage productsPage = new ProductsPage(page).open();
        productsPage.viewProductDetailsByIndex(0);

        Assert.assertTrue(productsPage.isProductDetailPageVisible(),
                "Страница деталей товара должна отображать название товара");
        Assert.assertFalse(productsPage.getProductDetailName().isEmpty(),
                "Название товара на странице деталей не должно быть пустым");
    }
}