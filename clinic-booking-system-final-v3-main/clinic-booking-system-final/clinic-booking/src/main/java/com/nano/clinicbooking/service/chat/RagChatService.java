package com.nano.clinicbooking.service.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nano.clinicbooking.service.gemini.GeminiClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.SearchPoints;
import io.qdrant.client.grpc.Points.WithPayloadSelector;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RagChatService {

    private final GeminiClient gemini;
    private final QdrantClient qdrant;
    private final String collection;

    // Gemini Embedding (HTTP)
    private final String geminiApiKey;
    private final String geminiApiBase;
    private final String embeddingModel;


    private final UnifiedClinicDataProvider dataProvider;          // NEW
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private final HttpClient http = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public RagChatService(
            GeminiClient geminiClient,
            @Value("${qdrant.host}") String qdrantHost,
            @Value("${qdrant.port:6334}") int qdrantPort,
            @Value("${qdrant.api-key}") String qdrantApiKey,
            @Value("${qdrant.collection:nano_knowledge_base}") String collectionName,
            @Value("${gemini.api.key}") String geminiApiKey,
            @Value("${gemini.api.base:https://generativelanguage.googleapis.com/v1beta}") String geminiApiBase,
            @Value("${gemini.embedding.model:text-embedding-004}") String embeddingModel,
                                 // NEW
            UnifiedClinicDataProvider dataProvider                  // NEW
    ) {
        this.gemini = geminiClient;
        this.collection = collectionName;

        this.dataProvider = dataProvider;                          // NEW

        QdrantGrpcClient grpc = QdrantGrpcClient.newBuilder(qdrantHost, qdrantPort, true)
                .withApiKey(qdrantApiKey)
                .build();
        this.qdrant = new QdrantClient(grpc);

        this.geminiApiKey = geminiApiKey;
        this.geminiApiBase = geminiApiBase;
        this.embeddingModel = embeddingModel;
    }

    @PostConstruct
    public void init() throws Exception {
        ensureCollection(768); // đổi 768 nếu model embedding khác kích thước
    }

    private void ensureCollection(int embeddingDim) throws Exception {
        try {
            qdrant.getCollectionInfoAsync(collection).get();
            log.info("Qdrant collection '{}' đã tồn tại.", collection);
        } catch (ExecutionException ex) {
            var cause = ex.getCause();
            if (cause instanceof StatusRuntimeException sre
                    && sre.getStatus().getCode() == Status.Code.NOT_FOUND) {
                log.warn("Chưa có collection '{}', tạo mới…", collection);
                qdrant.createCollectionAsync(
                        collection,
                        VectorParams.newBuilder()
                                .setSize(embeddingDim)
                                .setDistance(Distance.Cosine)
                                .build()
                ).get();
                log.info("Đã tạo collection '{}'.", collection);
            } else {
                throw ex;
            }
        }
    }

    /* =================== PUBLIC ENTRYPOINTS =================== */

    // NEW: giữ API cũ, forward sang bản đầy đủ
    public String ask(String question) {                            // NEW
        return ask(question, null);                                 // NEW
    }

    // NEW: bản đầy đủ có patientId để đặt lịch
    public String ask(String question, Long patientId) {            // NEW
        try {
            // 0) Intro rõ ràng → trả lời ngay
            if (isIntroIntent(question)) {
                String ai = tryIntroWithAI(question);
                if (ai != null && !ai.isBlank()) return ai.trim();
                return getClinicIntro();
            }





            // 2) Còn lại → trả lời theo unified data (events/doctors/shifts + RAG)
            return answerWithUnifiedData(question);

        } catch (Exception e) {
            log.error("ask() error", e);
            return "Đã xảy ra lỗi: " + e.getMessage();
        }
    }

    /* =================== CORE UNIFIED ANSWER =================== */

    // NEW: gom toàn bộ logic lấy JSON từ DB + RAG + prompt + call LLM
    private String answerWithUnifiedData(String question) throws Exception { // NEW
        // 1) Lấy data JSON từ DB
        String eventsJson  = dataProvider.getEventsJson();
        String doctorsJson = dataProvider.getDoctorsJson();
        String shiftsJson  = dataProvider.getShiftsJson(LocalDate.now());

        // 2) RAG context (nếu có)
        String ragContext = "";
        List<Float> queryVec = embed(question);
        if (queryVec != null && !queryVec.isEmpty()) {
            SearchPoints search = SearchPoints.newBuilder()
                    .setCollectionName(collection)
                    .addAllVector(queryVec)
                    .setLimit(5)
                    .setWithPayload(WithPayloadSelector.newBuilder().setEnable(true).build())
                    .build();
            List<ScoredPoint> hits = qdrant.searchAsync(search).get();

            ragContext = hits.stream()
                    .map(p -> {
                        var v = p.getPayloadMap().get("text");
                        return v != null ? v.getStringValue() : "";
                    })
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.joining("\n---\n"));
        }

        // 3) Build prompt hợp nhất
        String prompt = buildUnifiedPrompt(ragContext, eventsJson, doctorsJson, shiftsJson, question);

        // 4) Gọi model và trả lời
        String answer = gemini.generateHtml(prompt);
        return (answer == null || answer.isBlank())
                ? "Xin lỗi, hiện chưa thể trả lời câu hỏi này."
                : answer.trim();
    }

    /* =================== PROMPT BUILDER =================== */

    // NEW: tách ra method riêng, KHÔNG để lẫn trong ask()
    private String buildUnifiedPrompt(String ragContext, String eventsJson, String doctorsJson, String shiftsJson, String userQuestion) { // NEW
        return """
Bạn là trợ lý ảo của phòng khám Nano Clinic (Đà Nẵng).

DỮ LIỆU SỰ KIỆN (JSON, enable=true):
%s

DỮ LIỆU BÁC SĨ (JSON):
%s

DỮ LIỆU LỊCH KHÁM HÔM NAY (JSON):
%s

CÁCH DÙNG:
- Nếu câu hỏi liên quan "sự kiện/khuyến mãi/ưu đãi/voucher/event/gần đây/đang diễn ra/còn áp dụng không/...":
  → TỰ ĐỘNG liệt kê sự kiện phù hợp, định dạng:
  • **<Tiêu đề>**
  • Thời gian: dd/MM/yyyy → dd/MM/yyyy
  • (Nếu có) Địa điểm, mô tả (1–2 dòng)
  • (Nếu có) 🎁 Voucher: <tên — mã>
  • Ưu tiên đang diễn ra hoặc sắp tới gần nhất
- Nếu câu hỏi liên quan bác sĩ/chuyên khoa (ví dụ: "đau mắt", "khoa mắt", "tâm thần", "khám TMH", ...):
  → TỰ ĐỘNG đối sánh từ khóa triệu chứng/chuyên khoa với danh sách bác sĩ trong JSON để gợi ý 2–3 bác sĩ phù hợp (tên + chuyên khoa + vài chi tiết ngắn).
- Nếu hỏi lịch/khung giờ hôm nay:
  → Dựa vào JSON lịch, gợi ý khung giờ khả dụng và tên bác sĩ/chuyên khoa.
- Nếu không liên quan các dữ liệu trên, có thể dùng KB dưới; nếu vẫn không đủ, trả lời chung nhưng KHÔNG bịa.
NGUYÊN TẮC:
- Trả lời tiếng Việt tự nhiên, ngắn gọn, không bịa.
- KHÔNG in JSON ra màn hình; chỉ dùng JSON để suy luận.
- Nếu không có dữ liệu phù hợp: nói rõ "Hiện chưa có ..." thay vì bịa.

(Kiến thức tham khảo từ KB, có thể trống):
%s

CÂU HỎI NGƯỜI DÙNG:
"%s"
""".formatted(
                eventsJson == null ? "[]" : eventsJson,
                doctorsJson == null ? "[]" : doctorsJson,
                shiftsJson == null ? "[]" : shiftsJson,
                ragContext == null ? "" : ragContext,
                userQuestion
        );
    }

    /* =================== INTENT HELPERS =================== */

    private boolean isIntroIntent(String q) {
        if (q == null) return false;
        String s = q.toLowerCase();
        return s.contains("giới thiệu")
                || s.contains("phòng khám nano")
                || s.contains("nano clinic")
                || s.contains("về nano")
                || (s.contains("phòng khám") && (s.contains("ở đâu") || s.contains("thông tin")));
    }

    private boolean isTodayScheduleIntent(String q) {               // NEW
        if (q == null) return false;
        String s = q.toLowerCase();
        return (s.contains("lịch") || s.contains("khung giờ") || s.contains("giờ khám"))
                && (s.contains("hôm nay") || s.contains("today"));
    }

    private boolean isBookSlotIntent(String q) {                    // NEW
        if (q == null) return false;
        String s = q.toLowerCase();
        // các mẫu: “đặt slot 1058”, “book slot 123”, “chọn slot 321”
        return s.matches(".*(đặt|dat|book|chon|chọn)\\s*(slot)?\\s*\\d+.*");
    }

    /* =================== INTRO TEXT =================== */

    private String tryIntroWithAI(String userQuestion) {
        String prompt = """
Bạn là trợ lý của **Nano Clinic** (phòng khám đa khoa tại Đà Nẵng).
Viết đoạn **giới thiệu 4–6 câu, tiếng Việt tự nhiên**, nêu:
• Dịch vụ chính (nội tổng quát, nhi, sản, TMH, tim mạch, da liễu, xét nghiệm/siêu âm…)
• Địa chỉ/hotline: 032594011 / Giờ làm việc: Thứ 2 – Chủ nhật, 7:30–19:00
• Văn phong thân thiện, súc tích, không phóng đại.
Chỉ trả về đoạn giới thiệu (không kèm giải thích).
Câu hỏi người dùng: "%s"
""".formatted(userQuestion);
        try {
            String ans = gemini.generateHtml(prompt);
            return (ans == null || ans.isBlank()) ? null : ans;
        } catch (Exception e) {
            log.error("[INTRO-AI] error", e);
            return null;
        }
    }

    private String getClinicIntro() {
        return """
💙 **Phòng khám Nano Clinic** là phòng khám đa khoa hiện đại tại **Đà Nẵng**,
cung cấp các dịch vụ: nội tổng quát, nhi, sản, tai mũi họng, tim mạch, da liễu...
• Xét nghiệm, siêu âm, chẩn đoán hình ảnh
• Đặt lịch & quản lý hồ sơ sức khỏe trực tuyến
☎️ 032594011  • 🕐 7:30–19:00 (Thứ 2–CN)
📍 123 Nguyễn Văn Linh, Hải Châu, Đà Nẵng
""";
    }

    /* =================== EMBEDDING HTTP =================== */

    private List<Float> embed(String text) {
        try {
            if (geminiApiKey == null || geminiApiKey.isBlank()) return null;

            String base = geminiApiBase == null ? "" : geminiApiBase.replaceAll("/+$", "");
            String url = String.format("%s/models/%s:embedContent?key=%s", base, embeddingModel, geminiApiKey);

            String body = """
{
  "model": "models/%s",
  "content": { "parts": [ { "text": %s } ] }
}
""".formatted(embeddingModel, jsonQuote(text));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                log.warn("Embedding HTTP {}: {}", resp.statusCode(), resp.body());
                return null;
            }

            JsonNode root = mapper.readTree(resp.body());
            JsonNode values = root.path("embedding").path("values");
            if (!values.isArray() || values.isEmpty()) return null;

            List<Float> out = new ArrayList<>(values.size());
            for (JsonNode n : values) out.add((float) n.asDouble());
            return out;
        } catch (Exception e) {
            log.error("embed() error", e);
            return null;
        }
    }

    private static String jsonQuote(String s) {
        if (s == null) return "\"\"";
        return "\"" + s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "") + "\"";
    }
}
