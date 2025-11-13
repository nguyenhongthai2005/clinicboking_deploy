package com.nano.clinicbooking.service.email.send_register;

import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.service.gemini.GeminiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RegisterEmailContentService {

    private final GeminiClient gemini;
    public RegisterEmailContentService(GeminiClient gemini) { this.gemini = gemini; }

    @Value("${clinic.name:ClinicBooking}")
    private String clinicName;

    public String generateRegisterSuccessEmail(User user) {
        final String name  = safe(user.getFullName());
        final String email = safe(user.getEmail());

        // Prompt theo style Event: 1 <div>, không <html>/<body>/<style>/<script>, không backticks, không link/nút
        String prompt = """
            Bạn là trợ lý soạn email cho phòng khám "%s".
            YÊU CẦU NGHIÊM NGẶT:
            - Trả về DUY NHẤT 1 THẺ <div> (KHÔNG có <html>, <body>, <style>, <script>, backticks).
            - Dùng inline style, mobile-friendly, max-width ~640px.
            - KHÔNG chèn liên kết, KHÔNG có nút bấm/CTA, KHÔNG ảnh nhúng.
            - Giọng điệu ấm áp, rõ ràng, ngắn gọn.
            - một đoạn dài
            - CHO thềm màu đẹp thân thiện người đăng ký
            Bố cục:
            - Preheader ẩn: "Hoàn tất đăng ký tài khoản %s."
            - Header: tên phòng khám + dòng nhỏ mô tả
            - Lời chào: tên người nhận
            - Xác nhận đăng ký thành công
            - Card "Thông tin tài khoản": hiển thị Email
            - Hỗ trợ, cảm ơn & chữ ký đội ngũ
            - Footer nhỏ: "© 2025 %s. Bảo lưu mọi quyền."

            DỮ LIỆU:
            - Họ tên: %s
            - Email: %s
        """.formatted(clinicName, clinicName, clinicName, name, email);

        String html = gemini.generateHtml(prompt);
        if (isAcceptable(html)) return html.trim();

        // ===== FALLBACK ĐỘNG (1 div, không link/nút/ảnh) =====
        return buildFallbackHtml(clinicName, name, email);
    }

    private String buildFallbackHtml(String clinicName, String name, String email) {
        return String.format("""
            <div style="font-family:Arial,Helvetica,sans-serif;color:#111;line-height:1.6;max-width:640px;margin:0 auto;">
              <div style="display:none;visibility:hidden;opacity:0;height:0;overflow:hidden;color:transparent;">
                Hoàn tất đăng ký tài khoản %s.
              </div>
              <div style="padding:24px 16px;text-align:center;border-bottom:1px solid #eee;">
                <div style="font-size:20px;font-weight:700;">%s</div>
                <div style="font-size:12px;color:#666;margin-top:4px;">Chăm sóc tận tâm – Đặt lịch nhanh chóng</div>
              </div>
              <div style="padding:24px 16px;">
                <h3 style="margin:0 0 8px;">Xin chào %s,</h3>
                <p style="margin:0 0 12px;">Chúc mừng bạn đã <b>đăng ký tài khoản thành công</b> tại %s.</p>
                <div style="border:1px solid #eee;border-radius:12px;padding:16px;background:#fafafa;margin:16px 0;">
                  <div style="font-size:14px;color:#555;margin-bottom:8px;">Thông tin tài khoản</div>
                  <div style="font-size:14px;"><b>Email đăng nhập:</b> %s</div>
                </div>
                <p style="font-size:13px;color:#666;margin:12px 0 0;">Nếu cần hỗ trợ, vui lòng liên hệ CSKH.</p>
                <p style="margin:16px 0 0;">Trân trọng,<br><b>Đội ngũ %s</b></p>
              </div>
              <div style="border-top:1px solid #eee;text-align:center;font-size:12px;color:#777;padding:16px;">
                © 2025 %s. Bảo lưu mọi quyền. — Đây là email tự động, vui lòng không trả lời trực tiếp.
              </div>
            </div>
        """, clinicName, clinicName, name, clinicName, email, clinicName, clinicName);
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
