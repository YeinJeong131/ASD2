# Betterpedia - Assignment 3
**Group Project - ASD2 (2025)**

A collaborative Wikipedia-like platform with enhanced features for content creation, community engagement, and contributor recognition.

---

## 🎯 Project Overview

Betterpedia is a knowledge-sharing platform that extends traditional wiki functionality with:
- Community discussions on articles
- Contributor badge system
- Personal notes and highlighting
- Customizable appearance settings
- Advanced search and filters
- User management and roles

---

## 👥 Team Members & Features

| Member | Student ID | Features | Code Location |
|--------|-----------|----------|---------------|
| **Yu-Han** | 14542423 | F103 (Discussion), F104 (Badges) | `betterpedia/discussion/`, `betterpedia/badge/` |
| **Yein** | 14650170 | F109 (Appearance), F110 (Highlight & Notes) | `betterpedia/appearance/`, `betterpedia/notes/` |
| **Esha** | 24461093 | F101 (User Management), F102 (Popular Page) | `betterpedia/user/` |
| **Nima** | 14503407 | F105-F108 (Search, Download, Reading Time, TTS) | `betterpedia/nimaFeature` |

---

## 🏗️ Architecture

**Tech Stack:**
- **Backend:** Java 21, Spring Boot 3.5.4
- **Database:** MySQL 8.0
- **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript
- **Build:** Gradle 8.14.3
- **CI/CD:** Azure DevOps Pipeline
- **Hosting:** Azure App Service

**Architecture Pattern:** MVC (Model-View-Controller)
- **Model:** JPA Entities (`Comment`, `Badge`, `User`, etc.)
- **Repository:** Spring Data JPA interfaces
- **Service:** Business logic layer
- **Controller:** REST APIs + Page controllers
- **View:** Thymeleaf templates

---

## 📦 Project Structure

```
ASD2/
├── src/
│   ├── main/
│   │   ├── java/betterpedia/
│   │   │   ├── discussion/          # Yu-Han: F103
│   │   │   │   ├── entity/Comment.java
│   │   │   │   ├── repository/CommentRepository.java
│   │   │   │   ├── service/CommentService.java
│   │   │   │   └── controller/
│   │   │   │       ├── CommentController.java (REST API)
│   │   │   │       └── DiscussionPageController.java
│   │   │   ├── badge/               # Yu-Han: F104
│   │   │   │   ├── entity/Badge.java
│   │   │   │   ├── repository/BadgeRepository.java
│   │   │   │   ├── service/BadgeService.java
│   │   │   │   └── controller/
│   │   │   │       ├── BadgeController.java (REST API)
│   │   │   │       └── BadgePageController.java
│   │   │   ├── user/                # Esha: F101
│   │   │   ├── notes/               # Yein: F110
│   │   │   ├── appearance/          # Yein: F109
│   │   │   └── wiki/                # Articles
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── discussion/discussion.html
│   │       │   ├── badges/badges.html
│   │       │   └── wiki/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   └── js/
│   │       └── application.properties
│   └── test/
│       └── java/betterpedia/
│           ├── discussion/
│           │   ├── service/CommentServiceTest.java (20 tests)
│           │   └── controller/CommentControllerTest.java (15 tests)
│           └── badge/
│               ├── service/BadgeServiceTest.java (25 tests)
│               └── controller/BadgeControllerTest.java (15 tests)
├── build.gradle
├── azure-pipelines-cd.yml           # CD Pipeline config
└── README.md
```

---

## 🚀 Setup Instructions

### Prerequisites

1. **Java 21** - [Download](https://www.oracle.com/java/technologies/downloads/#java21)
   ```bash
   java -version  # Should show version 21
   ```

2. **MySQL 8.0** - [Download](https://dev.mysql.com/downloads/mysql/)
   ```bash
   mysql --version
   ```

3. **Gradle 8.14+** (included via wrapper)
   ```bash
   ./gradlew --version
   ```

### Step 1: Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/ASD2.git
cd ASD2
```

### Step 2: Setup Database

**Option A: MySQL (Recommended for Production)**

```bash
# Start MySQL
mysql.server start   # Mac
# or
sudo systemctl start mysql  # Linux
# or
net start MySQL  # Windows

# Create database
mysql -u root -p
```

In MySQL prompt:
```sql
CREATE DATABASE IF NOT EXISTS wiki;
EXIT;
```

Configure `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/wiki?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

**Option B: H2 Database (Easier for Testing)**

Configure `src/main/resources/application.properties`:
```properties
# Comment out MySQL, uncomment H2:
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

### Step 3: Build & Run

```bash
# Make gradlew executable (Mac/Linux)
chmod +x gradlew

# Build project
./gradlew clean build

# Run application
./gradlew bootRun
```

The application will start on: **http://localhost:8080**

### Step 4: Verify Setup

1. **Check tables were created:**
   ```sql
   mysql -u root -p wiki
   SHOW TABLES;
   -- Should see: article, badges, comments, notes, users, roles, user_settings
   ```

2. **Access the application:**
   - Home: http://localhost:8080/wiki
   - Discussion: http://localhost:8080/discussion/1
   - Badges: http://localhost:8080/badges

3. **Run tests:**
   ```bash
   ./gradlew test
   # Should show 75+ tests passing
   ```

---

## 🧪 Testing

### Run All Tests
```bash
./gradlew test
```

### Run Specific Feature Tests
```bash
# Discussion tests only
./gradlew test --tests "betterpedia.discussion.*"

# Badge tests only
./gradlew test --tests "betterpedia.badge.*"
```

### Test Coverage
- **Total Tests:** 75+
- **CommentServiceTest:** 20 tests
- **CommentControllerTest:** 15 tests
- **BadgeServiceTest:** 25 tests
- **BadgeControllerTest:** 15 tests

---

## 📚 Features Documentation

### F103 - Discussion/Comment System (Yu-Han)

**Description:** Community discussion boards for each article

**User Stories Implemented:**
- U103: Admin deletes inappropriate comments
- U142a: User submits comment through form
- U142b: Comment saved and displayed under correct article
- U143: User replies to another user's comment

**API Endpoints:**
- `POST /api/comments` - Create comment
- `POST /api/comments/reply` - Reply to comment
- `GET /api/comments/article/{id}` - Get all comments
- `PUT /api/comments/{id}` - Update comment
- `DELETE /api/comments/{id}` - Delete comment
- `GET /api/comments/article/{id}/count` - Get comment count

**Pages:**
- `/discussion/{articleId}` - Discussion page for specific article

**Security Features:**
- Input validation and XSS prevention
- Authentication required for posting
- Authorization checks for edit/delete
- Admin can delete any comment
- Soft delete to preserve thread integrity

---

### F104 - Contributor Badges (Yu-Han)

**Description:** Recognition system for active contributors

**User Stories Implemented:**
- U104: User views badges on profiles
- U105: User earns badges for contributions
- U106: Admin assigns/revokes badges manually

**Badge Levels:**
| Level | Contributions | Icon |
|-------|---------------|------|
| Bronze | 0-19 | 🥉 |
| Silver | 20-49 | 🥈 |
| Gold | 50-99 | 🥇 |
| Platinum | 100+ | 💎 |

**API Endpoints:**
- `GET /api/badges/my-badge` - Get user's badge
- `GET /api/badges/all` - Get leaderboard
- `GET /api/badges/stats` - Get badge statistics
- `PUT /api/badges/admin/set-level` - Admin: set badge level
- `PUT /api/badges/admin/set-contributions` - Admin: set count

**Pages:**
- `/badges` - Badges leaderboard and personal badge

**Features:**
- Automatic badge level calculation
- Real-time contribution tracking
- Integration with comment system (auto +1 per comment)
- Admin dashboard for manual badge management
- Progress tracking to next level

---

## 🔐 Security Features

### Authentication & Authorization
- Session-based authentication
- Role-based access control (User, Admin)
- Protected endpoints require login
- Admin-only operations verified

### Input Validation
```java
// Example from CommentService
if (content == null || content.trim().isEmpty()) {
    throw new IllegalArgumentException("Comment content cannot be empty");
}
```

### XSS Prevention
```javascript
// JavaScript sanitization
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
```

### SQL Injection Prevention
- Using JPA/Hibernate with parameterized queries
- No raw SQL in application code

---

## 🚀 Continuous Deployment

### Pipeline Configuration

**File:** `azure-pipelines-cd.yml`

**Stages:**
1. **Build Stage:**
   - Install Java 21
   - Run Gradle build
   - Execute all tests
   - Package JAR file
   - Publish artifacts

2. **Deploy Stage (prod branch only):**
   - Download artifacts
   - Deploy to Azure App Service
   - Configure environment
   - Health check

**Branching Strategy:**
- `main` - Development branch (builds only)
- `prod` - Production branch (builds + deploys)

### Deployment Process
1. Push to `main` → Build & test
2. Merge to `prod` → Build, test, & deploy to Azure
3. Live at: https://betterpedia-app.azurewebsites.net (example)

---

## 🐛 Troubleshooting

### Application Won't Start

**Problem:** Database connection error
```
Access denied for user 'root'@'localhost'
```

**Solution:**
- Check MySQL is running: `mysql.server status`
- Verify credentials in `application.properties`
- Try password: `mysql -u root -p`

---

**Problem:** White label error on pages
```
Whitelabel Error Page
```

**Solution:**
- Ensure all tables created: `spring.jpa.hibernate.ddl-auto=update`
- Check logs for specific error
- Verify database connection

---

**Problem:** Tests fail
```
Test failures in CommentServiceTest
```

**Solution:**
```bash
# Clean and rebuild
./gradlew clean test --rerun-tasks
```

---

### Port Already in Use

**Problem:**
```
Port 8080 is already in use
```

**Solution:**
```bash
# Find process using port 8080
lsof -ti:8080

# Kill the process
kill -9 $(lsof -ti:8080)

# Or change port in application.properties:
server.port=8081
```

---

## 📊 Performance Metrics

- Page load time: < 2 seconds
- Comment submission: < 1 second
- Badge update: < 1 second
- API response time: < 500ms average
- 75+ unit tests: < 10 seconds total execution

---

## 🤝 Contributing

This is a university project. Each team member is responsible for their assigned features.

**Code Style:**
- Follow Java naming conventions
- Write meaningful comments
- Add tests for new features
- Keep controllers thin, services thick

**Git Workflow:**
```bash
# Create feature branch
git checkout -b feature/your-feature

# Commit changes
git add .
git commit -m "feat: add new feature"

# Push and create PR
git push origin feature/your-feature
```

---

## 📝 License

University project - Not for commercial use

---

## 📞 Support

**For Issues:**
- Check this README first
- Review project documentation
- Contact team lead

**Team Lead:** TBD
**Tutor:** TBD

---

**Built by the Betterpedia Team**

