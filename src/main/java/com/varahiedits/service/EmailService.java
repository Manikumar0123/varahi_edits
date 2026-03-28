package com.varahiedits.service;

import com.varahiedits.model.Booking;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor

@Slf4j
public class EmailService {

	
	private final JavaMailSender mailSender;

    @Value("${app.email.business}")
    private String businessEmail;

    @Value("${app.email.from}")
    private String fromEmail;

    //System.out.println("MAIL USER = " + fromEmail);
    // 📧 CUSTOMER EMAIL
    @Async
    public void sendBookingConfirmationToCustomer(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Varahi Edits");
            helper.setTo(booking.getEmail());
            helper.setSubject("✅ Booking Confirmed – Varahi Edits");
            helper.setText(buildCustomerEmailHtml(booking), true);

            mailSender.send(message);
            log.info("✅ Customer email sent to {}", booking.getEmail());

        } catch (Exception e) {
            log.error("❌ Failed to send customer email: {}", e.getMessage());
        }
    }

    // 📧 ADMIN EMAIL
    @Async
    public void sendBookingAlertToOwner(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Varahi Edits System");
            helper.setTo(businessEmail);
            helper.setSubject("🔔 New Booking – " + booking.getName());

            helper.setText(buildOwnerEmailHtml(booking), true);

            mailSender.send(message);
            log.info("✅ Admin email sent");

        } catch (Exception e) {
            log.error("❌ Failed to send admin email: {}", e.getMessage());
        }
    }

    // 📧 STATUS UPDATE
    @Async
    public void sendStatusUpdateToCustomer(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Varahi Edits");
            helper.setTo(booking.getEmail());
            helper.setSubject("📋 Booking Update");

            helper.setText(buildStatusUpdateHtml(booking), true);

            mailSender.send(message);
            log.info("✅ Status email sent");

        } catch (Exception e) {
            log.error("❌ Failed to send status email: {}", e.getMessage());
        }
    }
    // ─── HTML Templates ───────────────────────────────────────────────────────

    private String buildCustomerEmailHtml(Booking booking) {
        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="margin:0;padding:0;background:#0a0a0a;font-family:'Segoe UI',Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#0a0a0a;padding:40px 20px;">
                <tr><td align="center">
                  <table width="600" cellpadding="0" cellspacing="0" style="background:#111;border:1px solid #2a2a2a;border-radius:4px;overflow:hidden;">
                    <!-- Header -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#8b0000,#c0392b);padding:40px 40px 30px;text-align:center;">
                        <h1 style="color:#fff;font-size:28px;margin:0;letter-spacing:3px;">VARAHI EDITS</h1>
                        <p style="color:rgba(255,255,255,0.8);margin:8px 0 0;font-size:13px;letter-spacing:2px;">VIDEO EDITING SERVICES</p>
                      </td>
                    </tr>
                    <!-- Body -->
                    <tr>
                      <td style="padding:40px;">
                        <h2 style="color:#e8a020;font-size:22px;margin:0 0 16px;">Booking Confirmed! 🎬</h2>
                        <p style="color:#c8bdb5;font-size:15px;line-height:1.7;margin:0 0 28px;">
                          Dear <strong style="color:#fff;">%s</strong>, thank you for choosing Varahi Edits!
                          We have received your booking and will contact you shortly to discuss the details.
                        </p>
                        <!-- Details -->
                        <table width="100%%" cellpadding="0" cellspacing="0" style="background:#1a1a1a;border-radius:4px;overflow:hidden;margin-bottom:28px;">
                          <tr><td style="padding:20px 24px;border-bottom:1px solid #2a2a2a;">
                            <p style="color:#9a8f85;font-size:11px;letter-spacing:2px;text-transform:uppercase;margin:0 0 4px;">Service</p>
                            <p style="color:#fff;font-size:16px;margin:0;font-weight:600;">%s</p>
                          </td></tr>
                          <tr><td style="padding:20px 24px;border-bottom:1px solid #2a2a2a;">
                            <p style="color:#9a8f85;font-size:11px;letter-spacing:2px;text-transform:uppercase;margin:0 0 4px;">Booking ID</p>
                            <p style="color:#e8a020;font-size:16px;margin:0;font-weight:600;">#%d</p>
                          </td></tr>
                          <tr><td style="padding:20px 24px;">
                            <p style="color:#9a8f85;font-size:11px;letter-spacing:2px;text-transform:uppercase;margin:0 0 4px;">Status</p>
                            <p style="color:#27ae60;font-size:16px;margin:0;font-weight:600;">PENDING CONFIRMATION</p>
                          </td></tr>
                        </table>
                        <p style="color:#c8bdb5;font-size:14px;line-height:1.7;">
                          📞 We will call you at <strong style="color:#fff;">%s</strong> within 24 hours.<br>
                          For urgent queries, contact us at <strong style="color:#e8a020;">+91 9676633834</strong>
                        </p>
                      </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                      <td style="background:#0a0a0a;padding:24px 40px;border-top:1px solid #2a2a2a;">
                        <p style="color:#555;font-size:12px;margin:0;text-align:center;">
                          © 2025 Varahi Edits · varahiedits@gmail.com · +91 9676633834
                        </p>
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(booking.getName(), booking.getService(), booking.getId(), booking.getPhone());
    }

    private String buildOwnerEmailHtml(Booking booking) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family:'Segoe UI',Arial,sans-serif;background:#f5f5f5;padding:30px;">
              <div style="max-width:600px;margin:0 auto;background:#fff;border-radius:8px;overflow:hidden;box-shadow:0 2px 10px rgba(0,0,0,0.1);">
                <div style="background:#c0392b;padding:24px 30px;">
                  <h2 style="color:#fff;margin:0;font-size:20px;">🔔 New Booking Received!</h2>
                </div>
                <div style="padding:30px;">
                  <table width="100%%" cellpadding="8" cellspacing="0" style="border-collapse:collapse;">
                    <tr style="background:#f9f9f9;"><td style="padding:12px;color:#666;font-size:13px;width:140px;"><strong>Booking ID</strong></td><td style="padding:12px;color:#c0392b;font-weight:bold;">#%d</td></tr>
                    <tr><td style="padding:12px;color:#666;font-size:13px;"><strong>Name</strong></td><td style="padding:12px;">%s</td></tr>
                    <tr style="background:#f9f9f9;"><td style="padding:12px;color:#666;font-size:13px;"><strong>Phone</strong></td><td style="padding:12px;"><a href="tel:%s" style="color:#c0392b;">%s</a></td></tr>
                    <tr><td style="padding:12px;color:#666;font-size:13px;"><strong>Email</strong></td><td style="padding:12px;"><a href="mailto:%s" style="color:#c0392b;">%s</a></td></tr>
                    <tr style="background:#f9f9f9;"><td style="padding:12px;color:#666;font-size:13px;"><strong>Service</strong></td><td style="padding:12px;font-weight:bold;color:#e8a020;">%s</td></tr>
                    <tr><td style="padding:12px;color:#666;font-size:13px;"><strong>Message</strong></td><td style="padding:12px;">%s</td></tr>
                  </table>
                  <div style="margin-top:24px;padding:16px;background:#fff3cd;border-radius:4px;border-left:4px solid #e8a020;">
                    <p style="margin:0;color:#856404;font-size:14px;">⚡ Please contact the customer within 24 hours to confirm the booking.</p>
                  </div>
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                booking.getId(), booking.getName(),
                booking.getPhone(), booking.getPhone(),
                booking.getEmail(), booking.getEmail(),
                booking.getService(),
                booking.getMessage() != null ? booking.getMessage() : "—"
            );
    }

    private String buildStatusUpdateHtml(Booking booking) {
        String statusColor = switch (booking.getStatus()) {
            case CONFIRMED -> "#27ae60";
            case IN_PROGRESS -> "#e8a020";
            case COMPLETED -> "#2980b9";
            case CANCELLED -> "#c0392b";
            default -> "#9a8f85";
        };
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family:'Segoe UI',Arial,sans-serif;background:#0a0a0a;padding:40px 20px;">
              <div style="max-width:600px;margin:0 auto;background:#111;border:1px solid #2a2a2a;border-radius:4px;">
                <div style="background:linear-gradient(135deg,#8b0000,#c0392b);padding:30px 40px;">
                  <h1 style="color:#fff;margin:0;font-size:24px;letter-spacing:3px;">VARAHI EDITS</h1>
                </div>
                <div style="padding:40px;">
                  <h2 style="color:#e8a020;margin:0 0 16px;">Booking Status Update</h2>
                  <p style="color:#c8bdb5;font-size:15px;">Dear <strong style="color:#fff;">%s</strong>, your booking status has been updated.</p>
                  <div style="background:#1a1a1a;padding:20px 24px;border-radius:4px;margin:24px 0;">
                    <p style="color:#9a8f85;font-size:11px;letter-spacing:2px;margin:0 0 4px;">BOOKING #%d · %s</p>
                    <p style="color:%s;font-size:20px;font-weight:bold;margin:0;">%s</p>
                  </div>
                  <p style="color:#9a8f85;font-size:13px;">Questions? Call us: <a href="tel:+919676633834" style="color:#e8a020;">+91 9676633834</a></p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                booking.getName(), booking.getId(), booking.getService(),
                statusColor, booking.getStatus().name()
            );
    }

	
	
}
