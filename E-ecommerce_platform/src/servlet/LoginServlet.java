package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

import model.User;
import dao.UserDAO;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    // ===================== SHOW LOGIN PAGE =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // ✅ If already logged in → redirect by role
        if (session != null && session.getAttribute("loggedUser") != null) {
            redirectByRole(res, req, (User) session.getAttribute("loggedUser"));
            return;
        }

        // ✅ Handle success message from register
        String message = req.getParameter("message");
        if ("registered".equals(message)) {
            req.setAttribute("success", "Registration successful! Please login.");
        }

        req.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(req, res);
    }

    // ===================== HANDLE LOGIN =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // -------- VALIDATION --------
        if (email == null || password == null ||
            email.trim().isEmpty() || password.trim().isEmpty()) {

            req.setAttribute("error", "Email and password are required.");
            req.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(req, res);
            return;
        }

        try {
            User user = userDAO.login(email.trim(), password.trim());

            if (user != null) {

                // -------- CREATE SESSION --------
                HttpSession session = req.getSession(true);
                session.setAttribute("loggedUser", user);

                // (optional but useful)
                session.setAttribute("role", user.getRole());

                // ⏱ 30 minutes timeout
                session.setMaxInactiveInterval(30 * 60);

                // -------- REDIRECT BASED ON ROLE --------
                redirectByRole(res, req, user);

            } else {
                req.setAttribute("error", "Invalid email or password.");
                req.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(req, res);
            }

        } catch (SQLException e) {
            throw new ServletException("Database authentication error", e);
        }
    }

    // ===================== ROLE-BASED REDIRECT =====================
    private void redirectByRole(HttpServletResponse res, HttpServletRequest req, User user)
            throws IOException {

        String role = user.getRole();

        if ("admin".equalsIgnoreCase(role)) {
            res.sendRedirect(req.getContextPath() + "/admin/dashboard");

        } else if ("manager".equalsIgnoreCase(role)) {
            // optional future feature
            res.sendRedirect(req.getContextPath() + "/manager/dashboard");

        } else {
            // default user
            res.sendRedirect(req.getContextPath() + "/products");
        }
    }
}