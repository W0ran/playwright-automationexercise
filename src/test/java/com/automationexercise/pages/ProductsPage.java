package com.automationexercise.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class ProductsPage extends BasePage {

    // ===== ЛОКАТОРЫ: ПОИСК =====
    private static final String SEARCH_INPUT  = "#search_product";
    private static final String SEARCH_BUTTON = "#submit_search";
    private static final String SEARCHED_PRODUCTS_TITLE = "h2:has-text('Searched Products')";

    // ===== ЛОКАТОРЫ: СПИСОК ТОВАРОВ =====
    private static final String ALL_PRODUCTS_TITLE = "h2:has-text('All Products')";
    private static final String PRODUCT_CARDS       = ".product-image-wrapper";
    private static final String PRODUCT_NAME_IN_CARD = ".productinfo p";
    private static final String PRODUCT_PRICE_IN_CARD = ".productinfo h2";

    // Кнопка "Add to cart", появляющаяся по ховеру на карточке (overlay)
    private static final String ADD_TO_CART_OVERLAY_BUTTON = ".product-overlay .add-to-cart";
    // Кнопка "Add to cart" под карточкой (всегда видна, второй дубль на странице)
    private static final String ADD_TO_CART_VISIBLE_BUTTON  = ".productinfo .add-to-cart";

    // ===== ЛОКАТОРЫ: МОДАЛКА "ДОБАВЛЕНО В КОРЗИНУ" =====
    private static final String CART_MODAL            = "#cartModal";
    private static final String CART_MODAL_VIEW_CART  = "#cartModal a:has-text('View Cart')";
    private static final String CART_MODAL_CONTINUE   = "#cartModal button:has-text('Continue Shopping')";

    // ===== ЛОКАТОРЫ: СТРАНИЦА ДЕТАЛЕЙ ТОВАРА =====
    private static final String PRODUCT_DETAIL_NAME      = ".product-information h2";
    private static final String PRODUCT_DETAIL_PRICE     = ".product-information span span";
    private static final String PRODUCT_DETAIL_QUANTITY  = "#quantity";
    private static final String PRODUCT_DETAIL_ADD_TO_CART = "button:has-text('Add to cart')";

    public ProductsPage(Page page) {
        super(page);
    }

    // ===== НАВИГАЦИЯ =====
    public ProductsPage open() {
        navigateTo("https://www.automationexercise.com/products");
        return this;
    }

    public boolean isAllProductsPageVisible() {
        return isVisible(ALL_PRODUCTS_TITLE);
    }

    // ===== ПОИСК =====
    public ProductsPage searchProduct(String productName) {
        fill(SEARCH_INPUT, productName);
        click(SEARCH_BUTTON);
        return this;
    }

    public boolean isSearchedProductsTitleVisible() {
        return isVisible(SEARCHED_PRODUCTS_TITLE);
    }

    public int getVisibleProductsCount() {
        return page.locator(PRODUCT_CARDS).count();
    }

    public boolean areAllVisibleProductsMatching(String keyword) {
        Locator names = page.locator(PRODUCT_NAME_IN_CARD);
        int count = names.count();
        if (count == 0) return false;

        for (int i = 0; i < count; i++) {
            String text = names.nth(i).innerText().toLowerCase();
            if (!text.contains(keyword.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    // ===== ДОБАВЛЕНИЕ В КОРЗИНУ (со страницы каталога, по индексу карточки) =====

    /**
     * Добавляет товар в корзину по индексу карточки (0 — первый товар на странице).
     * Использует overlay-кнопку, которая появляется при ховере на изображение товара.
     * Клик выполняется с force=true, так как оверлей анимированно проявляется
     * через CSS-transition и обычный actionability-чек Playwright иногда считает
     * элемент перекрытым в момент клика.
     * При неудаче (модалка не появилась за отведённое время — сайт периодически
     * подтормаживает) делаем одну повторную попытку с нуля.
     */
    public ProductsPage addProductToCartByIndex(int index) {
        boolean success = tryAddProductToCartByIndex(index);
        if (!success) {
            System.out.println("[ProductsPage] Повторная попытка добавления товара по индексу " + index);
            success = tryAddProductToCartByIndex(index);
        }
        if (!success) {
            System.out.println("[ProductsPage] Модалка корзины не появилась после 2 попыток. Текущий URL: " + page.url());
        }
        return this;
    }

    private boolean tryAddProductToCartByIndex(int index) {
        Locator card = page.locator(PRODUCT_CARDS).nth(index);
        card.scrollIntoViewIfNeeded();
        card.hover();
        page.waitForTimeout(400); // даём CSS-transition оверлея завершиться

        Locator addToCartBtn = card.locator(".product-overlay .add-to-cart");
        addToCartBtn.click(new Locator.ClickOptions().setForce(true));

        try {
            page.locator(CART_MODAL).waitFor(
                    new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000)
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ProductsPage closeCartModalAndContinueShopping() {
        waitForVisible(CART_MODAL_CONTINUE);
        click(CART_MODAL_CONTINUE);
        return this;
    }

    public CartPage goToCartFromModal() {
        waitForVisible(CART_MODAL_VIEW_CART);
        click(CART_MODAL_VIEW_CART);
        return new CartPage(page);
    }

    public boolean isCartModalVisible() {
        return isVisible(CART_MODAL);
    }

    // ===== ПРОСМОТР ДЕТАЛЕЙ ТОВАРА =====

    /**
     * Открывает страницу деталей товара по индексу карточки в каталоге.
     */
    public ProductsPage viewProductDetailsByIndex(int index) {
        Locator card = page.locator(PRODUCT_CARDS).nth(index);
        card.locator("a:has-text('View Product')").click();
        return this;
    }

    public String getProductDetailName() {
        return getText(PRODUCT_DETAIL_NAME);
    }

    public ProductsPage setProductQuantity(int quantity) {
        page.locator(PRODUCT_DETAIL_QUANTITY).fill(String.valueOf(quantity));
        return this;
    }

    public ProductsPage addCurrentProductToCart() {
        click(PRODUCT_DETAIL_ADD_TO_CART);
        return this;
    }

    public boolean isProductDetailPageVisible() {
        return isVisible(PRODUCT_DETAIL_NAME);
    }
}