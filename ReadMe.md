
# Auth Service - Spring Boot Microservice

This is a Spring Boot-based Authentication Microservice handling user registration, login, password management, and session management with JWT and OTP verification.

## 📂 Project Structure

```
src/main/java/com/thathsara/authservice/auth_service/
├── config
│   ├── checkstyle
│   ├── pmd
│   ├── spotbugs
│   └── SecurityConfig.java
├── controller
│   └── AuthController.java
├── dto
│   └── (All DTO classes)
├── exception
│   └── (All Exception classes)
├── middleware
│   └── (All middleware classes)
├── model
│   └── (All models classes)
├── repository
│   └── (All repository classes)
├── service
│   └── (All service classes)
├── utils
│   └── (All utilities classes)
└── AuthServiceApplication.java
```

## 📑 API Endpoints

### 🔐 Registration

**POST** `/api/auth/register`  
- Headers: `Tenant-ID`
- Request: `RegisterRequest` (form-data - username, email, password)
- Response: `RegisterResponse` (verify-token, OTP will send to the email , message)

### 🔑 Login

**POST** `/api/auth/login`
- Headers: `Tenant-ID`
- Request: `LoginRequest` (form-data - email , password)
- Response: `LoginResponse`(authToken , Refresh-Token, UserID, message)

### ✅ Verify Registration OTP

**POST** `/api/auth/verify-registration-otp`  
- Headers: `Tenant-ID` `Verify-Token`
- Request: `VerifyOtpRequest` (form-data - otp)
- Response: `VerifyOtpResponse` (authToken, Refresh-Token, message)

### 👤 Verify User (Me)

**GET** `/api/auth/me`  
- Headers: `Tenant-ID` `Authorization`, `Refresh-Token`
- Response: `AuthResponse` (authToken, Refresh-Token, UserID, message)

### 🚪 Logout

**GET** `/api/auth/logout`  
- Headers: `Tenant-ID` `Authorization`, `Refresh-Token`
- Response: `LogoutResponse` (message)

### 🔄 Resend Registration OTP

**GET** `/api/auth/resend-registration-otp`  
- Headers: `Tenant-ID` `Verify-Token`
- Response: `OtpResendResponse`(Verify-Token, OTP will Send)

### 📧 Forgot Password

**POST** `/api/auth/forgot-password`
- Headers: `Tenant-ID`
- Request: `ForgotPasswordRequest` (form-data - email)
- Response: `ForgotPasswordResponse` (Reset-Token, OTP Will Send)

### 🔒 Verify Reset Password OTP

**POST** `/api/auth/verify-reset-password-otp`  
- Headers: `Tenant-ID` `Reset-Token`
- Request: `ResetPasswordRequest` (form-data - otp )
- Response: `ResetPasswordResponse`(Reset-Token)

### 🔑 Change Password

**POST** `/api/auth/change-password`  
- Headers: `Tenant-ID` `Reset-Token`
- Request: `ChangePasswordRequest` (form-data)
- Response: `ChangePasswordResponse` (message)

## 📦 Tech Stack

- Java 24
- Spring Boot
- Spring Web
- Spring Security
- JWT (JSON Web Tokens)
- Redis (Optional, for caching tokens/OTPs)
- MySQL (or preferred RDBMS)
- Maven

## ⚙️ How to Run

1. Clone the repository:
```
git clone https://github.com/yourusername/auth-service.git
cd auth-service
```

2. Configure `application.properties` for your database, JWT secret, and SMTP email service.

3. Build and run:
```
./mvnw spring-boot:run
```

## 🤝 Contribution
### Contributions are welcome!

1. Fork this repository

2. Create a new branch git checkout -b feature/your-feature

3. Commit your changes git commit -m 'Add your feature'

4. Push to the branch git push origin feature/your-feature

5. Open a Pull Request

## 📞 Contact
### Thathsara Bandara
- 📧 [thathsaraBandara.dev](https://portfolio-v1-topaz-ten.vercel.app/)
- 🌐 [LinkedIn - Thathsara Bandara](https://www.linkedin.com/in/thathsara-bandara-b403582a7/)
- 💻 [Github](https://www.linkedin.com/in/thathsara-bandara-b403582a7/)
- ✉️ [Contant Developer](mailto:thathsaraarumapperuma@gmail.com?subject=Auth%20Service%20Support&body=Hello,%20I%20need%20help%20with...)

## 📄 License

This project is licensed under the MIT License.



