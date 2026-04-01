# servlet-cookie-session-lab

A Jakarta EE web application demonstrating core Servlet fundamentals — login flow, HTTP Session, Cookie with Remember Me, and persistent login history. Built with Apache Tomcat 10.1 and Maven.

---

## Features

- **Login Form** — accepts name and age input with basic validation
- **Remember Me** — saves credentials in a browser Cookie for 1, 7, or 30 days; auto pre-fills the form on next visit
- **Session Management** — stores login state server-side using `HttpSession`; protected dashboard redirects unauthenticated users back to login
- **Login History Table** — displays all previous logins (name, age, timestamp) persisted to a CSV file that survives Tomcat restarts
- **Logout** — destroys the session and redirects back to the login page
- **XSS Protection** — all user input is HTML-escaped before rendering

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Servlet API | Jakarta EE 10 (Servlet 6.0) |
| Server | Apache Tomcat 10.1 |
| Build Tool | Maven |
| Frontend | HTML, CSS, Vanilla JS |
| Persistence | CSV file (via `java.io`) |

---

## Project Structure

```
servlet-cookie-session-lab/
├── src/
│   └── main/
│       ├── java/com/servlet/login/
│       │   ├── LoginServlet.java         # Handles POST /login
│       │   ├── DashboardServlet.java     # Handles GET /dashboard
│       │   ├── LogoutServlet.java        # Handles GET /logout
│       │   └── UserHistoryManager.java   # Reads/writes login history CSV
│       └── webapp/
│           ├── css/
│           │   └── style.css
│           ├── js/
│           │   └── script.js
│           ├── WEB-INF/
│           │   └── web.xml
│           └── login.html
└── pom.xml
```

---

## How It Works

### Login Flow
1. User submits name + age via `login.html`
2. `LoginServlet` validates the input
3. On success — stores data in `HttpSession`, writes a record to the CSV, and redirects to `/dashboard`
4. If Remember Me is checked — writes `savedName` and `savedAge` Cookies with the chosen expiry
5. `DashboardServlet` reads from the session and renders the user profile + login history table
6. `LogoutServlet` calls `session.invalidate()` and redirects back to login

### Cookie vs Session
| | Remember Me Cookie | Session |
|---|---|---|
| Stored in | Browser (client) | Server memory |
| Purpose | Pre-fill login form | Track login state |
| Expires | After 1 / 7 / 30 days | 30 min inactivity |

### Login History Persistence
Login records are written to a CSV file at:
```
{java.io.tmpdir}/login_history.csv
```
On Windows this is typically `C:\Users\<username>\AppData\Local\Temp\login_history.csv`.  
Records persist across Tomcat restarts and are displayed newest-first on the dashboard.

---

## Getting Started

### Prerequisites
- JDK 17+
- Apache Tomcat 10.1.x
- Maven 3.8+
- IntelliJ IDEA (recommended)

### Running the Project

**In IntelliJ IDEA:**
1. Clone the repository
2. Open as a Maven project
3. Configure a Tomcat 10.1 local server in **Run → Edit Configurations**
4. Set the application context to `/LoginSystem` under the **Deployment** tab
5. Click **Run**
6. Visit `http://localhost:8099/LoginSystem/login.html`

**Via Maven WAR:**
```bash
mvn clean package
# Copy target/LoginSystem.war to your Tomcat webapps/ folder
# Start Tomcat and visit http://localhost:8099/LoginSystem/
```

---

## Key Concepts Demonstrated

- `@WebServlet` annotation-based servlet mapping
- `HttpSession` for server-side state management
- `Cookie` API — creating, setting expiry, scoping to context path, and deleting
- `RequestDispatcher` forward vs `sendRedirect` redirect
- `request.getContextPath()` for portable URL construction
- URL encoding cookie values to handle special characters safely
- Basic file I/O persistence using `BufferedReader` / `BufferedWriter`
- XSS prevention via HTML escaping of user input

---

## License

This project is for educational purposes.
