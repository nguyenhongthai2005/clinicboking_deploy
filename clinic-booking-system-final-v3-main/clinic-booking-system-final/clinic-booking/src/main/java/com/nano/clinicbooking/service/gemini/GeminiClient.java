package com.nano.clinicbooking.service.gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;

@Component
public class GeminiClient {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String defaultModel;

    @Value("${gemini.api.base:https://generativelanguage.googleapis.com/v1beta}")
    private String apiBase;

    // Timeout mỗi request (ms). Quá mức này -> ném HttpTimeoutException -> return null.
    @Value("${gemini.timeout.ms:100000}")
    private int timeoutMs;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private String endpointFor(String mdl) {
        String base = apiBase == null ? "" : apiBase.replaceAll("/+$", "");
        return String.format("%s/models/%s:generateContent?key=%s", base, mdl, apiKey);
    }

    public String generateHtml(String prompt) {
        return generateHtml(prompt, List.of(defaultModel, "gemini-2.5-flash", "gemini-1.5-flash", "gemini-2.5-pro"));
    }

    public String generateHtml(String prompt, List<String> models) {
        if (apiKey == null || apiKey.isBlank()) return null;

        String body = """
            {"contents":[{"role":"user","parts":[{"text":%s}]}]}
        """.formatted(jsonQuote(prompt));

        for (String mdl : models) {
            if (mdl == null || mdl.isBlank()) continue;
            String ep = endpointFor(mdl);

            int attempts = 3;
            long backoffMs = 400;

            for (int attempt = 1; attempt <= attempts; attempt++) {
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(URI.create(ep))
                            .timeout(Duration.ofMillis(timeoutMs))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(body))
                            .build();

                    HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
                    int code = resp.statusCode();

                    if (code == 200) {
                        String html = extractFirstText(resp.body());
                        if (html != null && !html.isBlank()) return html.trim();
                        break; // rỗng -> thử model khác
                    }
                    if (code == 404) break;                 // model không tồn tại -> model khác
                    if (code == 401 || code == 403) return null; // key lỗi -> dừng, cho lớp gọi fallback

                    if (code == 429 || (code >= 500 && code < 600)) {
                        Thread.sleep(backoffMs);
                        backoffMs *= 2;
                        continue;
                    }
                    break; // lỗi khác -> bỏ model này

                } catch (HttpTimeoutException e) {
                    // TIMEOUT -> báo lớp gọi fallback
                    return null;

                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return null;

                } catch (Exception ex) {
                    // lỗi tạm -> retry
                    try { Thread.sleep(backoffMs); } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                    backoffMs *= 2;
                }
            }
        }
        return null; // hết cách -> fallback ở lớp gọi
    }

    private static String jsonQuote(String s) {
        if (s == null) return "\"\"";
        return "\"" + s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "") + "\"";
    }

    private String extractFirstText(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) return null;
            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (!parts.isArray() || parts.isEmpty()) return null;
            StringBuilder sb = new StringBuilder();
            for (JsonNode p : parts) {
                String t = p.path("text").asText(null);
                if (t != null) sb.append(t);
            }
            return sb.length() == 0 ? null : sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
