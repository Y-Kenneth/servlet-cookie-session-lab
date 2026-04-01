package com.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * LogoutServlet
 *
 * Handles GET /logout.
 * Logic:
 *   1. Retrieve the current Session (if any) and call invalidate() to destroy it.
 *      This clears loginName, loginAge, and rememberDays from server memory.
 *   2. Note: We intentionally do NOT delete the savedName/savedAge Cookies here —
 *      those are controlled by the user's "Remember Me" choice on login.
 *      If they chose to be remembered, the cookies persist across logouts.
 *   3. Redirect to /login.html.
 */

// GET / Logout
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Destroy the session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Redirect back to login
        response.sendRedirect(request.getContextPath() + "/login.html");
    }
}
