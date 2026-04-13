package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import model.User;
import dao.UserDAO;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // -------- GET PARAMETERS --------
        String name     = req.getParameter("name");
        String email    = req.getParameter("email");
        String password = req.getParameter("password");
        String confirm  = req.getParameter("confirmPassword");
        String role     = req.getParameter("role"); // 👈 NEW

        // -------- KEEP FORM DATA --------
        req.setAttribute("name", name);
        req.setAttribute("email", email);
        req.setAttribute("role", role);

        // -------- VALIDATION --------
        if (name == null || email == null || password == null ||
            name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {

            req.setAttribute("error", "All fields are required.");
            req.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(req, res);
            return;
        }

        if (!password.equals(confirm)) {
            req.setAttribute("error", "Passwords do not match.");
            req.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(req, res);
            return;
        }

        if (password.length() < 6) {
            req.setAttribute("error", "Password must be at least 6 characters.");
            req.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(req, res);
            return;
        }

        // -------- ROLE SECURITY --------
        if (role == null || (!role.equals("admin") && !role.equals("user"))) {
            role = "user"; // default fallback
        }

        try {
            // -------- CREATE USER --------
            User user = new User();
            user.setName(name.trim());
            user.setEmail(email.trim());
            user.setPassword(password); // hashing in DAO
            user.setRole(role); // 👈 IMPORTANT

            // -------- SAVE --------
            boolean success = userDAO.register(user);

            if (success) {
                res.sendRedirect(req.getContextPath() + "/login?success=registered");
            } else {
                req.setAttribute("error", "Email already exists.");
                req.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(req, res);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Database error occurred. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(req, res);
        }
    }
}