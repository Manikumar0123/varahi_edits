package com.varahiedits.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.varahiedits.model.Booking;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String whatsappFrom;

    @Value("${twilio.sms.from}")
    private String smsFrom;

    @Value("${app.whatsapp.business}")
    private String businessWhatsApp;
    @PostConstruct
    public void debug() {
        System.out.println("SID = " + accountSid);
        System.out.println("TOKEN = " + authToken);
        System.out.println("FROM = " + whatsappFrom);
    }
    @PostConstruct
    public void testTwilio() {
        try {
            Twilio.init(accountSid, authToken);

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:+919676633834"),
                    new PhoneNumber("whatsapp:+14783128121"),
                    "Test message"
            ).create();

            System.out.println("TEST SUCCESS: " + message.getSid());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() {
        if (!accountSid.startsWith("YOUR_") && !authToken.startsWith("YOUR_")) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully");
        } else {
            log.warn("Twilio not configured. Set credentials in application.properties");
        }
    }

    /**
     * Send WhatsApp alert to business owner when new booking arrives
     */
    @Async
    public void sendWhatsAppAlertToOwner(Booking booking) {
        if (accountSid.startsWith("YOUR_")) {
            log.warn("Twilio not configured – skipping WhatsApp alert");
            return;
        }
        try {
            String body = buildOwnerWhatsAppMessage(booking);
            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + businessWhatsApp),
                    new PhoneNumber(whatsappFrom),
                    body
            ).create();
            log.info("WhatsApp alert sent to owner. SID: {}", message.getSid());
        } catch (Exception e) {
            log.error("Failed to send WhatsApp alert to owner: {}", e.getMessage());
        }
    }

    /**
     * Send WhatsApp confirmation to customer
     */
    @Async
    public void sendWhatsAppConfirmationToCustomer(Booking booking) {
        if (accountSid.startsWith("YOUR_")) {
            log.warn("Twilio not configured – skipping WhatsApp confirmation");
            return;
        }
        // Only send if number looks like it has country code
        String phone = booking.getPhone().startsWith("+") ? booking.getPhone() : "+91" + booking.getPhone();
        try {
            String body = buildCustomerWhatsAppMessage(booking);
            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + phone),
                    new PhoneNumber(whatsappFrom),
                    body
            ).create();
            log.info("WhatsApp confirmation sent to customer: {}", phone);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp confirmation to customer: {}", e.getMessage());
        }
    }

    /**
     * Send SMS to business owner (fallback / additional alert)
     */
    @Async
    public void sendSmsAlertToOwner(Booking booking) {
        if (accountSid.startsWith("YOUR_")) {
            log.warn("Twilio not configured – skipping SMS alert");
            return;
        }
        try {
            String body = String.format(
                "VARAHI EDITS - New Booking!\nName: %s\nService: %s\nPhone: %s\nBooking ID: #%d",
                booking.getName(), booking.getService(), booking.getPhone(), booking.getId()
            );
            Message message = Message.creator(
                    new PhoneNumber(businessWhatsApp),
                    new PhoneNumber(smsFrom),
                    body
            ).create();
            log.info("SMS alert sent. SID: {}", message.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS: {}", e.getMessage());
        }
    }

    // ─── Message Templates ────────────────────────────────────────────────────

    private String buildOwnerWhatsAppMessage(Booking booking) {
        return String.format("""
            🎬 *VARAHI EDITS – New Booking!*
            
            📋 *Booking ID:* #%d
            👤 *Name:* %s
            📱 *Phone:* %s
            📧 *Email:* %s
            🎯 *Service:* %s
            💬 *Message:* %s
            
            ⏰ Please contact the customer within 24 hours.
            """,
            booking.getId(),
            booking.getName(),
            booking.getPhone(),
            booking.getEmail(),
            booking.getService(),
            booking.getMessage() != null ? booking.getMessage() : "—"
        );
    }

    private String buildCustomerWhatsAppMessage(Booking booking) {
        return String.format("""
            🎬 *VARAHI EDITS*
            
            ✅ Hi *%s*! Your booking has been received.
            
            📋 *Booking ID:* #%d
            🎯 *Service:* %s
            📊 *Status:* Pending Confirmation
            
            We will contact you at *%s* within 24 hours.
            
            📞 For queries: *+91 9676633834*
            """,
            booking.getName(),
            booking.getId(),
            booking.getService(),
            booking.getPhone()
        );
    }
}
