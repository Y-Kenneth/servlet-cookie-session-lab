package com.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * LoginServlet
 *
 * Handles POST /login from login.html.
 * Logic:
 *   1. Read name, age, rememberMe, rememberDays from the form.
 *   2. Basic validation (name not empty, age is a valid positive number).
 *   3. Store name + age in the HttpSession so the dashboard can read them.
 *   4. If "Remember Me" is checked, write two Cookies (savedName, savedAge)
 *      with the chosen expiry so the login form pre-fills on next visit.
 *      If unchecked, delete those Cookies immediately (maxAge = 0).
 *   5. Redirect to /dashboard (DashboardServlet) after successful login.
 *   6. On validation error, redirect back to /login.html with an error param.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ── 1. Read form fields ──────────────────────────────────────────────
        String name        = request.getParameter("name");
        String ageStr      = request.getParameter("age");
        String rememberMe  = request.getParameter("rememberMe");   // "on" or null
        String daysStr     = request.getParameter("rememberDays"); // "1","7","30"

        String contextPath = request.getContextPath();

        // ── 2. Validation ────────────────────────────────────────────────────
        if (name == null || name.trim().isEmpty()) {
            response.sendRedirect(contextPath + "/login.html?error=name");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 150) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            response.sendRedirect(contextPath + "/login.html?error=age");
            return;
        }

        name = name.trim();

        // ── 3. Persist login record to CSV file ──────────────────────────────
        UserHistoryManager.addRecord(name, age);

        // ── 4. Store user data in Session ────────────────────────────────────
        HttpSession session = request.getSession();
        session.setAttribute("loginName", name);
        session.setAttribute("loginAge",  age);

        // ── 5. Handle "Remember Me" Cookie ───────────────────────────────────
        boolean remember = "on".equals(rememberMe);

        if (remember) {
            int days = 7;
            try {
                days = Integer.parseInt(daysStr);
            }
            catch (NumberFormatException ignored) { }

            int maxAgeSeconds = days * 24 * 60 * 60;

            // URL-encode name to safely handle spaces and special characters
            Cookie nameCookie = new Cookie("savedName", URLEncoder.encode(name, StandardCharsets.UTF_8));
            nameCookie.setMaxAge(maxAgeSeconds);
            nameCookie.setPath(contextPath + "/");   // scoped to this project
            response.addCookie(nameCookie);

            Cookie ageCookie = new Cookie("savedAge", String.valueOf(age));
            ageCookie.setMaxAge(maxAgeSeconds);
            ageCookie.setPath(contextPath + "/");
            response.addCookie(ageCookie);

            // Also store the chosen days in session so dashboard can display it
            session.setAttribute("rememberDays", days);

        } else {
            // Delete any previously saved cookies by setting maxAge to 0
            Cookie nameCookie = new Cookie("savedName", "");
            nameCookie.setMaxAge(0);
            nameCookie.setPath(contextPath + "/");
            response.addCookie(nameCookie);

            Cookie ageCookie = new Cookie("savedAge", "");
            ageCookie.setMaxAge(0);
            ageCookie.setPath(contextPath + "/");
            response.addCookie(ageCookie);

            session.setAttribute("rememberDays", null);
        }

        // ── 6. Redirect to dashboard ─────────────────────────────────────────
        response.sendRedirect(contextPath + "/dashboard");
    }
}