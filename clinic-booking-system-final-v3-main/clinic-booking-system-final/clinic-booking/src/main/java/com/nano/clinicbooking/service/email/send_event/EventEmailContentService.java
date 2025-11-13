package com.nano.clinicbooking.service.email.send_event;

import com.nano.clinicbooking.model.Event;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.service.gemini.GeminiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EventEmailContentService implements EventEmailContentProvider {

    private final GeminiClient gemini;
    public EventEmailContentService(GeminiClient gemini) { this.gemini = gemini; }

    @Value("${clinic.name:ClinicBooking}")
    private String clinicName;

    public String buildEventEmailHtml(User user, Event ev, String subject, String ctaText, String ctaUrl) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", new Locale("vi","VN"));
        String timeStr = ev.getStartDate() != null ? ev.getStartDate().format(fmt) : "";
        if (ev.getEndDate() != null) timeStr += " – " + ev.getEndDate().format(fmt);

        String name  = safe(user.getFullName());
        String email = safe(user.getEmail());
        String title = safe(ev.getTitle());
        String desc  = (ev.getDescription() == null || ev.getDescription().isBlank())
                ? "Sự kiện đặc biệt tại phòng khám." : ev.getDescription().trim();
        String loc   = ev.getLocation() == null ? "—" : ev.getLocation();
        String subj  = (subject == null || subject.isBlank()) ? ("Sự kiện: " + title) : subject;

        // Prompt theo style của Register: 1 <div>, không <html>/<body>/<style>/<script>, không CTA
        String prompt = """
            Bạn là trợ lý soạn email cho phòng khám "%s".
            YÊU CẦU NGHIÊM NGẶT:
            - Trả về DUY NHẤT 1 THẺ <div> (KHÔNG có <html>, <body>, <style>, <script>, backticks).
            - Dùng inline style, mobile-friendly, max-width ~640px.
            - KHÔNG có nút bấm/CTA, KHÔNG chèn liên kết.
            - Giọng điệu ấm áp, rõ ràng. Viết mô tả sự kiện DÀI HƠN, chân thật nhưng không lan man.

            Bố cục:
            - Preheader ẩn: sử dụng giá trị Subject
            - Header: tên phòng khám + dòng nhỏ "Thông báo sự kiện"
            - Lời chào: tên người nhận
            - Tiêu đề sự kiện (đậm)
            - Đoạn mô tả dài về sự kiện
            - Thông tin: địa điểm, thời gian
            - Card "Tài khoản": hiển thị Email
            - Hỗ trợ, chữ ký đội ngũ
            - Footer nhỏ

            DỮ LIỆU:
            - Subject: %s
            - Họ tên: %s
            - Tiêu đề sự kiện: %s
            - Mô tả: %s
            - Địa điểm: %s
            - Thời gian: %s
            - Email: %s
        """.formatted(clinicName, subj, name, title, desc, loc,
                (timeStr.isBlank() ? "—" : timeStr), email);

        String html = gemini.generateHtml(prompt);
        if (isAcceptable(html)) return html.trim();

        // ===== FALLBACK ĐỘNG  =====
        return buildFallbackHtml(clinicName, subj, name, title, desc, loc, timeStr, email);
    }

    private String buildFallbackHtml(String clinicName, String subj, String name, String title,
                                     String desc, String loc, String timeStr, String email) {
        return String.format("""
          <div style="font-family:Arial,Helvetica,sans-serif;color:#111;line-height:1.6;max-width:640px;margin:0 auto;">
            <div style="display:none;visibility:hidden;opacity:0;height:0;overflow:hidden;color:transparent;">%s</div>
            <div style="padding:20px 16px;text-align:center;border-bottom:1px solid #eee;">
              <div style="font-size:20px;font-weight:700;">%s</div>
              <div style="font-size:12px;color:#666;margin-top:4px;">Thông báo sự kiện</div>
            </div>
            <div style="padding:20px 16px;">
              <h3 style="margin:0 0 8px;">Xin chào %s,</h3>
              <p style="margin:0 0 12px;"><b>%s</b></p>
              <p style="margin:0 0 12px;">%s</p>
              <p style="margin:0 0 12px;"><b>Địa điểm:</b> %s</p>
              <p style="margin:0 0 12px;"><b>Thời gian:</b> %s</p>
              <div style="border:1px solid #eee;border-radius:12px;padding:12px;margin:16px 0;background:#fafafa;">
                <div style="font-size:14px;color:#555;margin-bottom:8px;">Tài khoản</div>
                <div style="font-size:14px;"><b>Email:</b> %s</div>
              </div>
              <p style="font-size:13px;color:#666;margin:12px 0 0;">Nếu cần hỗ trợ, vui lòng phản hồi qua kênh CSKH.</p>
              <p style="margin:16px 0 0;">Trân trọng,<br><b>Đội ngũ %s</b></p>
            </div>
            <div style="border-top:1px solid #eee;padding:12px;text-align:center;font-size:12px;color:#777;">
              Email tự động – vui lòng không trả lời.
            </div>
          </div>
        """, subj, clinicName, name, title, desc,
                (loc == null ? "—" : loc),
                (timeStr == null || timeStr.isBlank() ? "—" : timeStr),
                email, clinicName);
    }

    // Kiểm tra output Gemini có hợp lệ: 1 thẻ <div>, không backticks, không html/body/style/script
    private static boolean isAcceptable(String html) {
        if (html == null) return false;
        String s = html.trim();
        if (s.isEmpty()) return false;
        String lower = s.toLowerCase();
        if (lower.contains("```")) return false;
        if (lower.contains("<html") || lower.contains("<body") || lower.contains("<style") || lower.contains("<script")) return false;
        if (!lower.startsWith("<div")) return false;
        if (!lower.endsWith("</div>")) return false;
        return true;
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
