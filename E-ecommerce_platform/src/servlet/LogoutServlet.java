package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // ===================== LOGOUT =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // -------- GET EXISTING SESSION --------
        HttpSession session = req.getSession(false);

        if (session != null) {
            session.invalidate(); // destroy session
        }

        // -------- CLEAR COOKIES (important for JSESSIONID) --------
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath(req.getContextPath());
                    cookie.setMaxAge(0); // delete cookie
                    res.addCookie(cookie);
                }
            }
        }

        // -------- PREVENT CACHING --------
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        res.setHeader("Pragma", "no-cache"); // HTTP 1.0
        res.setDateHeader("Expires", 0); // Proxies

        // -------- REDIRECT --------
        res.sendRedirect(req.getContextPath() + "/login?status=logged_out");
    }

    // ===================== HANDLE POST =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Use same logic as GET
        doGet(req, res);
    }
}