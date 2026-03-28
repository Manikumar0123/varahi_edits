# 🎬 Varahi Edits – Spring Boot Backend

Full backend for the Varahi Edits website with:
- ✅ Contact form → MySQL database
- ✅ Email notifications (Gmail SMTP)
- ✅ WhatsApp / SMS alerts (Twilio)
- ✅ Admin dashboard REST API
- ✅ JWT-secured admin auth

---

## 📁 Project Structure

```
varahi-backend/
├── pom.xml
└── src/main/
    ├── java/com/varahiedits/
    │   ├── VarahiEditsApplication.java       ← Main entry point
    │   ├── config/
    │   │   └── AppConfig.java                ← Async + Admin seeder
    │   ├── controller/
    │   │   ├── AuthController.java           ← POST /api/auth/login
    │   │   ├── BookingController.java        ← POST /api/bookings/submit (public)
    │   │   └── AdminController.java          ← /api/admin/** (protected)
    │   ├── dto/
    │   │   ├── ApiResponse.java
    │   │   ├── BookingRequest.java
    │   │   └── LoginRequest.java
    │   ├── exception/
    │   │   └── GlobalExceptionHandler.java
    │   ├── model/
    │   │   ├── Booking.java
    │   │   └── AdminUser.java
    │   ├── repository/
    │   │   ├── BookingRepository.java
    │   │   └── AdminUserRepository.java
    │   ├── security/
    │   │   ├── JwtUtil.java
    │   │   ├── JwtAuthFilter.java
    │   │   └── SecurityConfig.java
    │   └── service/
    │       ├── BookingService.java
    │       ├── EmailService.java
    │       └── NotificationService.java
    └── resources/
        └── application.properties
```

---

## ⚙️ Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |

---

## 🚀 Setup & Run

### Step 1 — Create MySQL Database

```sql
CREATE DATABASE varahi_edits;
```
> The app will auto-create tables on first run via `spring.jpa.hibernate.ddl-auto=update`

---

### Step 2 — Configure application.properties

Open `src/main/resources/application.properties` and fill in:

```properties
# MySQL
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Gmail (use App Password — NOT your regular password)
spring.mail.username=your_gmail@gmail.com
spring.mail.password=your_16_char_app_password
app.email.from=your_gmail@gmail.com

# Twilio (WhatsApp/SMS) — sign up at twilio.com
twilio.account.sid=ACxxxxxxxxxxxxxxxxxxxx
twilio.auth.token=your_auth_token
twilio.whatsapp.from=whatsapp:+14155238886   ← Twilio sandbox number
twilio.sms.from=+1xxxxxxxxxx                ← Your Twilio phone number

# Admin login credentials
app.admin.username=admin
app.admin.password=YourStrongPassword123!
```

#### 📧 Gmail App Password Setup
1. Go to Google Account → Security → 2-Step Verification → App Passwords
2. Generate password for "Mail"
3. Use the 16-character password in `spring.mail.password`

#### 📱 Twilio WhatsApp Setup
1. Sign up at [twilio.com](https://twilio.com)
2. Go to Messaging → Try it out → Send a WhatsApp message
3. Follow sandbox join instructions
4. Use sandbox number `whatsapp:+14155238886` as `twilio.whatsapp.from`

---

### Step 3 — Run the Application

```bash
# Navigate to project folder
cd varahi-backend

# Build and run
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

---

## 🔌 API Endpoints

### Public (No Auth Required)

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/bookings/submit` | Submit contact/booking form |
| POST | `/api/auth/login` | Admin login |

### Admin (JWT Token Required)
Add header: `Authorization: Bearer <token>`

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/admin/dashboard` | Stats overview |
| GET | `/api/admin/bookings` | All bookings |
| GET | `/api/admin/bookings?status=PENDING` | Filter by status |
| GET | `/api/admin/bookings/{id}` | Single booking |
| PATCH | `/api/admin/bookings/{id}/status` | Update status |
| DELETE | `/api/admin/bookings/{id}` | Delete booking |
| GET | `/api/admin/bookings/range?start=...&end=...` | Date range filter |

---

## 📋 API Usage Examples

### Submit a Booking
```bash
curl -X POST http://localhost:8080/api/bookings/submit \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ramesh Kumar",
    "phone": "+919876543210",
    "email": "ramesh@gmail.com",
    "service": "Wedding Highlights",
    "message": "Our wedding is on 15th May 2025"
  }'
```

### Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "Varahi@Admin2024"}'
```

### Get All Bookings (Admin)
```bash
curl http://localhost:8080/api/admin/bookings \
  -H "Authorization: Bearer <your_jwt_token>"
```

### Update Booking Status
```bash
curl -X PATCH http://localhost:8080/api/admin/bookings/1/status \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED", "adminNotes": "Called customer, confirmed for May 15"}'
```

---

## 🔗 Connect Frontend to Backend

In your `varahi_edits.html`, update the `submitForm()` function:

```javascript
async function submitForm() {
  const payload = {
    name: document.getElementById('f-name').value,
    phone: document.getElementById('f-phone').value,
    email: document.getElementById('f-email').value,
    service: document.getElementById('f-service').value,
    message: document.getElementById('f-msg').value
  };

  try {
    const res = await fetch('http://localhost:8080/api/bookings/submit', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    const data = await res.json();
    if (data.success) {
      document.getElementById('form-success').style.display = 'block';
    }
  } catch (err) {
    alert('Error submitting form. Please try again.');
  }
}
```

---

## 📊 Booking Status Flow

```
PENDING → CONFIRMED → IN_PROGRESS → COMPLETED
                   ↘ CANCELLED
```

---

## 🛡️ Security Notes

- All `/api/admin/**` routes are JWT-protected
- Passwords are BCrypt-hashed
- CORS is configured — update `app.cors.allowed-origins` for production
- Change default admin password before deploying!
