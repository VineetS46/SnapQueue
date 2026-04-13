# 📬 SnapQueue — Anonymous Office Feedback System

A full-stack Spring Boot web application that allows employees to anonymously submit workplace feedback with a structured approval workflow across three role-based dashboards.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.2.5, Spring Web, Spring Data JPA, Spring Security |
| Database | MySQL 8.x |
| Frontend | HTML5, Bootstrap 5, Vanilla JavaScript (fetch API) |
| Build Tool | Maven |
| Auth | Session-based (no JWT) with BCrypt password encoding |

---

## 👥 Roles & Access

| Role | Can Do |
|---|---|
| **EMPLOYEE** | Submit issues, view own issues and their status |
| **MANAGER** | View all pending issues, approve or reject with reason |
| **ADMIN** | View all approved issues, resolve them |

---

## 🔄 Feedback Status Flow

```
EMPLOYEE submits → PENDING
MANAGER reviews  → APPROVED or REJECTED
ADMIN acts       → RESOLVED
```

| Transition | Who |
|---|---|
| PENDING → APPROVED | MANAGER only |
| PENDING → REJECTED | MANAGER only (with reason) |
| APPROVED → RESOLVED | ADMIN only |

---

## 🗂️ Project Structure

```
com.snapqueue
├── controller
│   ├── AuthController.java
│   └── FeedbackController.java
├── service
│   ├── EmployeeService.java
│   └── FeedbackService.java
├── repository
│   ├── EmployeeRepository.java
│   └── FeedbackRepository.java
├── model
│   ├── Employee.java
│   ├── Feedback.java
│   ├── Category.java     (BUG, SUGGESTION, APPRECIATION)
│   ├── Status.java       (PENDING, APPROVED, REJECTED, RESOLVED)
│   └── Role.java         (EMPLOYEE, MANAGER, ADMIN)
├── exception
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
└── config
    ├── SecurityConfig.java
    └── DataLoader.java

static/
├── login.html
├── register.html
├── index.html                  ← role-based router
├── employee-dashboard.html
├── manager-dashboard.html
├── admin-dashboard.html
├── submit.html
└── manage.html                 ← redirects to manager-dashboard
```

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.x running on port **3308**

### 1. Clone the repository
```bash
git clone https://github.com/VineetS46/SnapQueue.git
cd SnapQueue
```

### 2. Install Maven
```bash
winget install Apache.Maven
```
Verify:
```bash
mvn -version
```

### 3. Configure MySQL
Open `src/main/resources/application.properties` and update your password:
```properties
spring.datasource.url=jdbc:mysql://localhost:3308/snapqueuedb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### 4. Run the application
```bash
mvn spring-boot:run
```

### 5. Open in browser
```
http://localhost:8080/login.html
```

The database and tables are created automatically on first run. Sample data is seeded by `DataLoader` on startup.

---

## 🔐 Default Credentials (seeded on startup)

| Role | Email / Employee ID | Password |
|---|---|---|
| Admin | `admin123@gmail.com` or `ADMIN001` | `admin123` |
| Manager | `priya@snapqueue.com` or `EMP001` | `password123` |
| Employee | `arjun@snapqueue.com` or `EMP002` | `password123` |

> Login supports both **email** and **Employee ID** as identifier.

---

## 🌐 Pages

| URL | Page | Access |
|---|---|---|
| `/login.html` | Login with quick-login cards | Public |
| `/register.html` | Register new account | Public |
| `/index.html` | Role-based router | Authenticated |
| `/employee-dashboard.html` | My issues + stats | EMPLOYEE |
| `/submit.html` | Submit new issue | EMPLOYEE, MANAGER |
| `/manager-dashboard.html` | Approve / Reject issues | MANAGER |
| `/admin-dashboard.html` | Resolve approved issues | ADMIN |

---

## 📡 REST API Endpoints

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new employee |
| POST | `/api/auth/login` | Login with identifier + password |
| POST | `/api/auth/logout` | Invalidate session |
| GET | `/api/auth/me` | Get current logged-in user |

### Feedback
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/feedback` | EMPLOYEE | Submit new issue |
| GET | `/api/feedback/my` | EMPLOYEE | Get own issues |
| GET | `/api/feedback/pending` | MANAGER | Get all PENDING issues |
| GET | `/api/feedback/approved` | ADMIN | Get all APPROVED issues |
| GET | `/api/feedback/all` | MANAGER, ADMIN | Get all issues |
| PATCH | `/api/feedback/{id}/approve` | MANAGER | Approve a PENDING issue |
| PATCH | `/api/feedback/{id}/reject` | MANAGER | Reject with reason |
| PATCH | `/api/feedback/{id}/resolve` | ADMIN | Resolve an APPROVED issue |

---

## 🗄️ MySQL Workbench

After running the app, you can inspect data directly:
```sql
USE snapqueuedb;
SELECT * FROM employee;
SELECT * FROM feedback;
```

---


## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 🛠️ Installation Guide — Everything You Need

Follow these steps from scratch on a fresh Windows machine.

---

### 1. Install Java 17

**Download:**
https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

Pick → `Windows x64 Installer (.exe)` → install it.

**Verify installation:**
```bash
java -version
```
Expected output:
```
java version "17.x.x"
```

**Set JAVA_HOME (if not auto-set):**
```bash
# In Windows search → "Environment Variables"
# Under System Variables → New
# Variable name:  JAVA_HOME
# Variable value: C:\Program Files\Java\jdk-17
#
# Then edit PATH → add:  %JAVA_HOME%\bin
```

---

### 2. Install Maven

**Option A — Install via winget (recommended, one command):**
```bash
winget install Apache.Maven
```

**Option B — Install via Chocolatey:**
```bash
choco install maven
```
> To install Chocolatey first, run this in PowerShell as Administrator:
> ```powershell
> Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
> ```

**Option C — Manual download:**
https://maven.apache.org/download.cgi

Pick → `apache-maven-3.x.x-bin.zip` → extract to `C:\maven`

Then set environment variables manually:
```bash
# System Variables → New
# Variable name:  MAVEN_HOME
# Variable value: C:\maven\apache-maven-3.x.x
#
# Edit PATH → add:  %MAVEN_HOME%\bin
```

**Verify installation:**
```bash
mvn -version
```
Expected output:
```
Apache Maven 3.x.x
Java version: 17.x.x
```

---

### 3. Install MySQL

**Download MySQL Installer:**
https://dev.mysql.com/downloads/installer/

Pick → `mysql-installer-community-8.x.x.msi` → run installer.

During setup:
- Choose **Developer Default** setup type
- Set root password (remember it — you'll need it in `application.properties`)
- Default port is `3306` — if yours is `3308`, note that down

**Verify MySQL is running:**
```bash
mysql -u root -p
```
Enter your password. You should see the MySQL prompt:
```
mysql>
```

**Check which port MySQL is on:**
```sql
SHOW VARIABLES LIKE 'port';
```

Type `exit` to quit.

---

### 4. Install Git

**Download:**
https://git-scm.com/download/win

Run the installer with default options.

**Verify installation:**
```bash
git --version
```
Expected output:
```
git version 2.x.x.windows.x
```

---

### 5. Clone & Run SnapQueue

**Step 1 — Clone the repository:**
```bash
git clone https://github.com/VineetS46/SnapQueue.git
cd SnapQueue
```

**Step 2 — Update your MySQL password:**

Open `src/main/resources/application.properties` and edit:
```properties
spring.datasource.url=jdbc:mysql://localhost:3308/snapqueuedb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password_here
```
> Change `3308` to `3306` if that's your MySQL port.

**Step 3 — Build the project:**
```bash
mvn clean install -DskipTests
```

**Step 4 — Run the application:**
```bash
mvn spring-boot:run
```

You should see:
```
Tomcat started on port(s): 8080
Started SnapQueueApplication in x.xxx seconds
```

**Step 5 — Open in browser:**
```
http://localhost:8080/login.html
```

---

### 6. Verify Database (Optional)

Open MySQL Workbench or run in terminal:
```bash
mysql -u root -p
```
```sql
USE snapqueuedb;
SHOW TABLES;
SELECT * FROM employee;
SELECT * FROM feedback;
```

---

### 7. Quick Command Reference

| Task | Command |
|---|---|
| Check Java version | `java -version` |
| Check Maven version | `mvn -version` |
| Check Git version | `git --version` |
| Check MySQL port | `mysql> SHOW VARIABLES LIKE 'port';` |
| Clone project | `git clone https://github.com/VineetS46/SnapQueue.git` |
| Build project | `mvn clean install -DskipTests` |
| Run project | `mvn spring-boot:run` |
| Stop project | `Ctrl + C` in terminal |
| Open app | `http://localhost:8080/login.html` |
