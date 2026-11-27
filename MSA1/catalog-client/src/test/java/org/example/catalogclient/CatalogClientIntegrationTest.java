package org.example.catalogclient;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
class CatalogClientIntegrationTest {
    private static final String FILM = """
            {"id":7,"title":"Arrival","slug":"arrival","description":"First contact","genre":"Sci-fi",
             "releaseYear":2016,"durationMinutes":116,"rating":8.0,"createdAt":"2026-01-02","updatedAt":"2026-02-03"}
            """;

    private static HttpServer catalog;

    @LocalServerPort
    private int port;

    private final HttpClient http = HttpClient.newHttpClient();

    @DynamicPropertySource
    static void configureCatalog(DynamicPropertyRegistry registry) throws IOException {
        catalog = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        catalog.createContext("/api/films", CatalogClientIntegrationTest::serveFilms);
        catalog.start();
        registry.add("spring.cloud.openfeign.client.config.films.url",
                () -> "http://127.0.0.1:" + catalog.getAddress().getPort());
    }

    @AfterAll
    static void stopCatalog() {
        if (catalog != null) {
            catalog.stop(0);
            catalog = null;
        }
    }

    @Test
    void forwardsFilmRequestsAndHandlesCatalogFailure(CapturedOutput output) throws Exception {
        var list = get("/catalog/films");
        assertThat(list.statusCode()).isEqualTo(200);
        assertThat(list.body()).startsWith("[").contains("\"title\":\"Arrival\"");

        var film = get("/catalog/films/7");
        assertThat(film.statusCode()).isEqualTo(200);
        assertThat(film.body()).contains("\"id\":7", "\"title\":\"Arrival\"", "\"createdAt\":\"2026-01-02\"");

        assertThat(get("/catalog/films/999").statusCode()).isEqualTo(404);
        stopCatalog();
        assertThat(get("/catalog/films/7").statusCode()).isEqualTo(503);

        assertThat(output.getAll().lines().filter(line -> line.contains("Feign GET")).toList())
                .hasSize(4)
                .anyMatch(line -> line.matches(".*Feign GET /api/films/7 -> 200 \\(\\d+ ms\\)"))
                .anyMatch(line -> line.matches(".*Feign GET /api/films/7 -> ConnectException \\(\\d+ ms\\)"));
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path)).build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void serveFilms(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String body = switch (exchange.getRequestURI().getPath()) {
                case "/api/films" -> "[" + FILM + "]";
                case "/api/films/7" -> FILM;
                default -> null;
            };
            if (body == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
        }
    }
}
