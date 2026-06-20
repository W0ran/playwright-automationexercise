package com.automationexercise.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Хелпер для подготовки тестовых данных через API automationexercise.com.
 * Используется для setup/teardown в UI-тестах — регистрация и удаление
 * пользователя в обход интерфейса (быстрее и стабильнее, чем через UI).
 *
 * Документация API: https://automationexercise.com/api_list
 */
public class ApiTestDataHelper {

    private static final String BASE_URL = "https://automationexercise.com/api";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Создаёт пользователя через POST /api/createAccount.
     * Возвращает true, если аккаунт успешно создан (responseCode == 201).
     */
    public static boolean createAccount(String name, String email, String password) {
        Map<String, String> params = Map.ofEntries(
                Map.entry("name", name),
                Map.entry("email", email),
                Map.entry("password", password),
                Map.entry("title", "Mr"),
                Map.entry("birth_date", "10"),
                Map.entry("birth_month", "5"),
                Map.entry("birth_year", "1995"),
                Map.entry("firstname", name),
                Map.entry("lastname", "QA"),
                Map.entry("company", "QA Portfolio"),
                Map.entry("address1", "Test Address 1"),
                Map.entry("address2", "Test Address 2"),
                Map.entry("country", "United States"),
                Map.entry("zipcode", "010000"),
                Map.entry("state", "Astana"),
                Map.entry("city", "Astana"),
                Map.entry("mobile_number", "77071234567")
        );

        try {
            HttpResponse<String> response = sendForm("/createAccount", params, "POST");
            boolean success = response.statusCode() == 200 && bodyHasResponseCode(response.body(), 201);
            if (!success) {
                System.out.println("[ApiTestDataHelper] createAccount FAILED. HTTP status: "
                        + response.statusCode() + ", body: " + response.body());
            }
            return success;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать аккаунт через API: " + e.getMessage(), e);
        }
    }

    /**
     * Удаляет пользователя через DELETE /api/deleteAccount.
     * Полезно вызывать в @AfterSuite/@AfterClass для очистки тестовых данных.
     */
    public static boolean deleteAccount(String email, String password) {
        Map<String, String> params = Map.of(
                "email", email,
                "password", password
        );

        try {
            HttpResponse<String> response = sendForm("/deleteAccount", params, "DELETE");
            return response.statusCode() == 200 && bodyHasResponseCode(response.body(), 200);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось удалить аккаунт через API: " + e.getMessage(), e);
        }
    }

    /**
     * Проверяет, существует ли пользователь с такими данными — POST /api/verifyLogin.
     */
    public static boolean verifyLoginExists(String email, String password) {
        Map<String, String> params = Map.of(
                "email", email,
                "password", password
        );

        try {
            HttpResponse<String> response = sendForm("/verifyLogin", params, "POST");
            return response.statusCode() == 200 && bodyHasResponseCode(response.body(), 200);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось выполнить verifyLogin через API: " + e.getMessage(), e);
        }
    }

    // ===== ВНУТРЕННЯЯ КУХНЯ =====

    private static HttpResponse<String> sendForm(String path, Map<String, String> params, String method)
            throws IOException, InterruptedException {

        String formBody = toFormBody(params);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofSeconds(15))
                .method(method, BodyPublishers.ofString(formBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String toFormBody(Map<String, String> params) {
        return params.entrySet().stream()
                .map(e -> urlEncode(e.getKey()) + "=" + urlEncode(e.getValue()))
                .collect(Collectors.joining("&"));
    }

    /**
     * Проверяет наличие "responseCode": &lt;code&gt; в JSON-теле ответа,
     * устойчиво к наличию/отсутствию пробела после двоеточия.
     */
    private static boolean bodyHasResponseCode(String body, int expectedCode) {
        if (body == null) return false;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "\"responseCode\"\\s*:\\s*" + expectedCode
        );
        return pattern.matcher(body).find();
    }

    private static String urlEncode(String value) {
        return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}