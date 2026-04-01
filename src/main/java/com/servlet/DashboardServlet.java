package com.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * DashboardServlet
 *
 * Handles GET /dashboard.
 * Logic:
 *   1. Check that a valid session with loginName exists.
 *      If not → redirect to /login.html (session guard / access control).
 *   2. Read name + age from session.
 *   3. Write a clean HTML page showing the user's data.
 *
 * This servlet renders HTML directly (no JSP needed) so the project
 * stays as a pure Servlet exercise matching the handout.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath();

        // ── 1. Session guard ─────────────────────────────────────────────────
        HttpSession session = request.getSession(false); // do NOT create a new one
        if (session == null || session.getAttribute("loginName") == null) {
            // No valid login → kick back to login page
            response.sendRedirect(contextPath + "/login.html?error=session");
            return;
        }

        // ── 2. Read data from session ────────────────────────────────────────
        String name = (String) session.getAttribute("loginName");
        int age = (int) session.getAttribute("loginAge");
        Integer rememberDays = (Integer) session.getAttribute("rememberDays");

        // ── 3. Render dashboard HTML ─────────────────────────────────────────
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("  <meta charset=\"UTF-8\">");
        out.println("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("  <title>Dashboard – " + escapeHtml(name) + "</title>");
        out.println("  <style>");
        out.println("    * { box-sizing: border-box; margin: 0; padding: 0; }");
        out.println("    body {");
        out.println("      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        out.println("      background: #f0f2f5;");
        out.println("      display: flex; justify-content: center; align-items: center;");
        out.println("      min-height: 100vh; padding: 20px;");
        out.println("    }");
        out.println("    .card {");
        out.println("      background: #ffffff;");
        out.println("      border-radius: 12px;");
        out.println("      padding: 40px 48px;");
        out.println("      max-width: 480px; width: 100%;");
        out.println("      box-shadow: 0 4px 24px rgba(0,0,0,0.08);");
        out.println("    }");
        out.println("    .badge {");
        out.println("      display: inline-block;");
        out.println("      background: #e8f5e9; color: #2e7d32;");
        out.println("      font-size: 12px; font-weight: 600;");
        out.println("      padding: 4px 10px; border-radius: 20px;");
        out.println("      margin-bottom: 20px; letter-spacing: 0.5px;");
        out.println("    }");
        out.println("    h1 { font-size: 26px; color: #1a1a2e; margin-bottom: 6px; }");
        out.println("    .subtitle { color: #888; font-size: 14px; margin-bottom: 32px; }");
        out.println("    .info-grid { display: grid; gap: 14px; }");
        out.println("    .info-row {");
        out.println("      display: flex; justify-content: space-between; align-items: center;");
        out.println("      padding: 14px 18px;");
        out.println("      background: #f8f9fa; border-radius: 8px;");
        out.println("      border-left: 4px solid #4f46e5;");
        out.println("    }");
        out.println("    .info-row .label { font-size: 13px; color: #666; font-weight: 500; }");
        out.println("    .info-row .value { font-size: 16px; color: #1a1a2e; font-weight: 700; }");
        out.println("    .cookie-info {");
        out.println("      margin-top: 20px; padding: 14px 18px;");
        out.println("      background: #fff8e1; border-radius: 8px;");
        out.println("      border-left: 4px solid #f59e0b;");
        out.println("      font-size: 13px; color: #92400e;");
        out.println("    }");
        out.println("    .session-info {");
        out.println("      margin-top: 14px; padding: 14px 18px;");
        out.println("      background: #e8f0fe; border-radius: 8px;");
        out.println("      border-left: 4px solid #4f46e5;");
        out.println("      font-size: 13px; color: #3730a3;");
        out.println("    }");
        out.println("    .logout-btn {");
        out.println("      display: block; width: 100%; margin-top: 28px;");
        out.println("      padding: 12px; text-align: center;");
        out.println("      background: #4f46e5; color: white;");
        out.println("      text-decoration: none; border-radius: 8px;");
        out.println("      font-weight: 600; font-size: 15px;");
        out.println("      transition: background 0.2s;");
        out.println("    }");
        out.println("    .logout-btn:hover { background: #4338ca; }");
        out.println("    .history-section { margin-top: 32px; }");
        out.println("    .history-section h2 { font-size: 16px; color: #1a1a2e; margin-bottom: 14px; font-weight: 700; }");
        out.println("    table { width: 100%; border-collapse: collapse; font-size: 14px; }");
        out.println("    thead tr { background: #4f46e5; color: white; }");
        out.println("    thead th { padding: 10px 14px; text-align: left; font-weight: 600; }");
        out.println("    tbody tr { border-bottom: 1px solid #f0f0f0; transition: background 0.15s; }");
        out.println("    tbody tr:hover { background: #f8f7ff; }");
        out.println("    tbody td { padding: 10px 14px; color: #333; }");
        out.println("    tbody tr:nth-child(even) { background: #fafafa; }");
        out.println("    tbody tr:nth-child(even):hover { background: #f8f7ff; }");
        out.println("    .no-history { text-align: center; padding: 20px; color: #aaa; font-size: 13px; }");
        out.println("    .row-num { color: #aaa; font-size: 12px; }");
        out.println("  </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("  <div class=\"card\">");
        out.println("    <div class=\"badge\">✓ Logged In</div>");
        out.println("    <h1>Welcome, " + escapeHtml(name) + "!</h1>");
        out.println("    <p class=\"subtitle\">Here is your profile information.</p>");

        out.println("    <div class=\"info-grid\">");
        out.println("      <div class=\"info-row\">");
        out.println("        <span class=\"label\">Name</span>");
        out.println("        <span class=\"value\">" + escapeHtml(name) + "</span>");
        out.println("      </div>");
        out.println("      <div class=\"info-row\">");
        out.println("        <span class=\"label\">Age</span>");
        out.println("        <span class=\"value\">" + age + " years old</span>");
        out.println("      </div>");
        out.println("    </div>");

        // Show Cookie info
        if (rememberDays != null) {
            out.println("    <div class=\"cookie-info\">");
            out.println("      🍪 <strong>Remember Me active</strong> — your login info is saved");
            out.println("      in a Cookie for <strong>" + rememberDays + " day(s)</strong>.");
            out.println("      Your name and age will be pre-filled next time you visit.");
            out.println("    </div>");
        } else {
            out.println("    <div class=\"cookie-info\">");
            out.println("      🍪 <strong>Remember Me is OFF</strong> — no Cookie was saved.");
            out.println("      Login info will not be pre-filled on your next visit.");
            out.println("    </div>");
        }

        // Show Session info
        out.println("    <div class=\"session-info\">");
        out.println("      🔐 <strong>Session active</strong> — your data is stored server-side");
        out.println("      and will expire after 30 minutes of inactivity.");
        out.println("    </div>");

        // Logout link
        out.println("    <a class=\"logout-btn\" href=\"" + contextPath + "/logout\">Logout</a>");

        // ── Login history table ───────────────────────────────────────────────
        List<UserHistoryManager.LoginRecord> history = UserHistoryManager.getAllRecords();

        out.println("    <div class=\"history-section\">");
        out.println("      <h2>Login History</h2>");
        out.println("      <table>");
        out.println("        <thead>");
        out.println("          <tr>");
        out.println("            <th>#</th>");
        out.println("            <th>Name</th>");
        out.println("            <th>Age</th>");
        out.println("            <th>Login Time</th>");
        out.println("          </tr>");
        out.println("        </thead>");
        out.println("        <tbody>");

        if (history.isEmpty()) {
            out.println("          <tr><td colspan='4' class='no-history'>No login history yet.</td></tr>");
        } else {
            int i = 1;
            for (UserHistoryManager.LoginRecord record : history) {
                out.println("          <tr>");
                out.println("            <td class='row-num'>" + i++ + "</td>");
                out.println("            <td>" + escapeHtml(record.getName()) + "</td>");
                out.println("            <td>" + record.getAge() + "</td>");
                out.println("            <td>" + escapeHtml(record.getLoginTime()) + "</td>");
                out.println("          </tr>");
            }
        }

        out.println("        </tbody>");
        out.println("      </table>");
        out.println("    </div>");

        out.println("  </div>");
        out.println("</body>");
        out.println("</html>");
    }

    /** Prevent XSS: escape user-supplied strings before writing into HTML. */
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;")
                .replace("'",  "&#x27;");
    }
}
