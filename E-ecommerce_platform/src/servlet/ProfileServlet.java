package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import model.User;
import model.Order;
import dao.UserDAO;
import dao.OrderDAO;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;
    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        orderDAO = new OrderDAO();
    }

    // ===================== VIEW PROFILE =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            loadProfileData(req, user);
            req.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(req, res);

        } catch (SQLException e) {
            throw new ServletException("Error loading profile data", e);
        }
    }

    // ===================== HANDLE ACTIONS =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");

        try {

            if ("updateProfile".equals(action)) {
                handleUpdateProfile(req, session, user);

            } else if ("changePassword".equals(action)) {
                handleChangePassword(req, user);
            }

            // Reload updated data
            loadProfileData(req, user);

            req.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(req, res);

        } catch (SQLException e) {
            throw new ServletException("Error updating profile", e);
        }
    }

    // ===================== HELPER METHODS =====================

    private void loadProfileData(HttpServletRequest req, User user) throws SQLException {
        List<Order> orders = orderDAO.getOrdersByUser(user.getId());
        req.setAttribute("user", user);
        req.setAttribute("orders", orders);
    }

    private void handleUpdateProfile(HttpServletRequest req, HttpSession session, User user)
            throws SQLException {

        String name = req.getParameter("name");
        String email = req.getParameter("email");

        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {

            req.setAttribute("error", "Name and email are required.");
            return;
        }

        user.setName(name.trim());
        user.setEmail(email.trim());

        boolean updated = userDAO.updateProfile(user);

        if (updated) {
            session.setAttribute("loggedUser", user); // update session
            req.setAttribute("success", "Profile updated successfully!");
        } else {
            req.setAttribute("error", "Failed to update profile.");
        }
    }

    private void handleChangePassword(HttpServletRequest req, User user)
            throws SQLException {

        String oldPass = req.getParameter("oldPassword");
        String newPass = req.getParameter("newPassword");
        String confirm = req.getParameter("confirmPassword");

        if (newPass == null || newPass.trim().isEmpty()) {
            req.setAttribute("error", "New password cannot be empty.");
            return;
        }

        if (!newPass.equals(confirm)) {
            req.setAttribute("error", "New passwords do not match.");
            return;
        }

        boolean changed = userDAO.changePassword(user.getId(), oldPass, newPass);

        if (changed) {
            req.setAttribute("success", "Password changed successfully!");
        } else {
            req.setAttribute("error", "Old password is incorrect.");
        }
    }
}