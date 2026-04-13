# SnapQueue - Anonymous Office Feedback System

A full-stack Spring Boot web application that allows employees to anonymously submit workplace feedback with a structured approval workflow across three role-based dashboards.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.2.5, Spring Web, Spring Data JPA, Spring Security |
| Database | MySQL 8.x |
| Frontend | HTML5, Bootstrap 5, Vanilla JavaScript (fetch API) |
| Build Tool | Maven |
| Auth | Session-based (no JWT) with BCrypt password encoding |

---

## Roles & Access

| Role | Can Do |
|---|---|
| **EMPLOYEE** | Submit issues, view own issues and their status |
| **MANAGER** | View all pending issues, approve or reject with reason |
| **ADMIN** | View all approved issues, resolve them |

---

## Feedback Status Flow

```
EMPLOYEE submits  -->  PENDING
MANAGER reviews   -->  APPROVED or REJECTED
ADMIN acts        -->  RESOLVED
```

| Transition | Who |
|---|---|
| PENDING -> APPROVED | MANAGER only |
| PENDING -> REJECTED | MANAGER only (with reason) |
| APPROVED -> RESOLVED | ADMIN only |

---

## Project Structure

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
├── index.html                  <- role-based router
├── employee-dashboard.html
├── manager-dashboard.html
├── admin-dashboard.html
├── submit.html
└── manage.html                 <- redirects to manager-dashboard
```

---

## Default Credentials (seeded on startup)

| Role | Email / Employee ID | Password |
|---|---|---|
| Admin | `admin123@gmail.com` or `ADMIN001` | `admin123` |
| Manager | `priya@snapqueue.com` or `EMP001` | `password123` |
| Employee | `arjun@snapqueue.com` or `EMP002` | `password123` |

> Login supports both **email** and **Employee ID** as identifier.

---

## Pages

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

## REST API Endpoints

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

## MySQL Workbench

After running the app, you can inspect data directly:
```sql
USE snapqueuedb;
SELECT * FROM employee;
SELECT * FROM feedback;
```

---

## Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.x
- Git

---

### Step 1 - Install Java 17

Download: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

Pick `Windows x64 Installer (.exe)` and install it.

Verify:
```bash
java -version
```

Set JAVA_HOME if not auto-set:
```
Windows Search -> Environment Variables -> System Variables -> New
Variable name:  JAVA_HOME
Variable value: C:\Program Files\Java\jdk-17

Edit PATH -> Add:  %JAVA_HOME%\bin
```

---

### Step 2 - Install Maven

Option A - via winget (recommended):
```bash
winget install Apache.Maven
```

Option B - via Chocolatey:
```bash
choco install maven
```

Option C - Manual:
Download from https://maven.apache.org/download.cgi
Extract to `C:\maven` then set:
```
System Variables -> New
Variable name:  MAVEN_HOME
Variable value: C:\maven\apache-maven-3.x.x

Edit PATH -> Add:  %MAVEN_HOME%\bin
```

Verify:
```bash
mvn -version
```

---

### Step 3 - Install MySQL

Download: https://dev.mysql.com/downloads/installer/

Pick `mysql-installer-community-8.x.x.msi` and run it.
- Choose Developer Default setup
- Set a root password (you will need this later)
- Note your port (default is 3306, this project uses 3308)

Verify:
```bash
mysql -u root -p
```

Check your port:
```sql
SHOW VARIABLES LIKE 'port';
```

---

### Step 4 - Install Git

Download: https://git-scm.com/download/win

Verify:
```bash
git --version
```

---

### Step 5 - Clone the Repository

```bash
git clone https://github.com/VineetS46/SnapQueue.git
cd SnapQueue
```

---

### Step 6 - Configure MySQL Password

Open `src/main/resources/application.properties` and update:
```properties
spring.datasource.url=jdbc:mysql://localhost:3308/snapqueuedb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password_here
```
Change `3308` to `3306` if that is your MySQL port.

---

### Step 7 - Install Dependencies

```bash
mvn clean install -DskipTests
```

---

### Step 8 - Run the Application

```bash
mvn spring-boot:run
```

You should see:
```
Tomcat started on port(s): 8080
Started SnapQueueApplication in x.xxx seconds
```

---

### Step 9 - Open in Browser

```
http://localhost:8080/login.html
```

The database and all tables are created automatically on first run.
Sample data (admin, manager, employee + feedbacks) is seeded by DataLoader on startup.

---

## Quick Command Reference

| Task | Command |
|---|---|
| Check Java | `java -version` |
| Check Maven | `mvn -version` |
| Check Git | `git --version` |
| Install Maven | `winget install Apache.Maven` |
| Clone project | `git clone https://github.com/VineetS46/SnapQueue.git` |
| Install dependencies | `mvn clean install -DskipTests` |
| Run project | `mvn spring-boot:run` |
| Stop project | `Ctrl + C` |
| Open app | `http://localhost:8080/login.html` |
| Check MySQL port | `SHOW VARIABLES LIKE 'port';` |

---

## License

This project is open source and available under the [MIT License](LICENSE).
