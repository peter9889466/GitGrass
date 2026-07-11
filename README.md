# GitGrass 🌿

GitGrass is a lightweight, premium web application designed to monitor your GitHub repositories and ensure your daily contribution graph (grass) remains vibrant. It keeps track of repository health, warns you about inactive codebases, and sends optional reminder notifications to your Discord channel.

---

## 🌟 Core Features
1. **GitHub OAuth2 Authentication (Required)**: Secure login via GitHub. Automatically fetches and syncs your public/private repository metadata and access privileges.
2. **Repository Health Dashboard**: Monitors repository activity. Highlights branches (e.g., `feature/`) and repositories with no updates for over 3 months, providing clean-up and restructuring guides.
3. **Discord Reminder Scheduler (Optional)**: Set a target notification time (e.g., 10:00 PM). If you haven't committed to your monitored repositories by that time, GitGrass sends a notification via Discord Webhook. Users without Discord links are automatically bypassed.

---

## 🛠 Tech Stack

### Frontend
- **Framework**: React 18 (TypeScript)
- **Styling**: Tailwind CSS v4 (Glassmorphism & dark neon themes)
- **Build Tool**: Vite

### Backend
- **Language**: Kotlin 1.9.24
- **Framework**: Spring Boot 3.3.1 (MVC, Spring Security OAuth2 Client)
- **Database**: PostgreSQL (JPA/Hibernate)
- **JSON Processing & Cryptography**: JJWT 0.12.6, Java Cryptography Extension (JPA `AttributeConverter` utilizing AES-256-CBC)
- **HTTP Client**: Spring Boot 3.x `RestClient`

---

## 🔒 Security Architecture
- **JWT (JSON Web Token)**: Stateless authentication filter mapping backend services with the React frontend.
- **Token Encryption**: Access and Refresh tokens are automatically encrypted/decrypted via AES-256 when saved to/loaded from the PostgreSQL database, protecting user credentials from DB leakage risks.

---

## 📂 Project Directory Structure

```text
GitGrass/
├── backend/                  # Kotlin Spring Boot project
│   ├── src/main/kotlin/      # Domain entities, security configurations, client APIs
│   └── src/main/resources/   # Application properties (PostgreSQL and OAuth2 bindings)
└── frontend/                 # React Vite project
    ├── src/components/       # UI Views (Login, Dashboard, MyPage)
    └── src/index.css         # Tailwind v4 globals and glassmorphism styling
```

---

## 🚀 Getting Started

### Prerequisites
- Node.js (v18+)
- Java JDK 17+
- PostgreSQL Database

### Running Frontend
```bash
cd frontend
npm install
npm run dev
```

### Running Backend
```bash
cd backend
./gradlew bootRun
```
