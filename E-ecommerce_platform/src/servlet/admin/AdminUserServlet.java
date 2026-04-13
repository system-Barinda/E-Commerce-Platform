package servlet.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.AdminDAO;
import model.User;

@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        adminDAO = new AdminDAO();
    }

    // ===================== VIEW USERS =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User admin = getAuthenticatedAdmin(req, res);
        if (admin == null) return;

        try {
            List<User> users = adminDAO.getAllUsers();

            req.setAttribute("users", users);
            req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, res);

        } catch (SQLException e) {
            throw new ServletException("Error loading user list for admin", e);
        }
    }

    // ===================== DELETE USER =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User admin = getAuthenticatedAdmin(req, res);
        if (admin == null) return;

        String idStr = req.getParameter("id");

        // -------- VALIDATION --------
        if (idStr == null || idStr.trim().isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/admin/users?error=missingId");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            if (id <= 0) {
                res.sendRedirect(req.getContextPath() + "/admin/users?error=invalidId");
                return;
            }

            // Prevent admin from deleting themselves
            if (admin.getId() == id) {
                res.sendRedirect(req.getContextPath() + "/admin/users?error=selfDelete");
                return;
            }

            boolean deleted = adminDAO.deleteUser(id);

            if (deleted) {
                res.sendRedirect(req.getContextPath() + "/admin/users?success=deleted");
            } else {
                res.sendRedirect(req.getContextPath() + "/admin/users?error=notAllowed");
            }

        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/admin/users?error=invalidFormat");
        } catch (SQLException e) {
            // Likely due to FK constraints (orders exist)
            res.sendRedirect(req.getContextPath() + "/admin/users?error=hasDependencies");
        }
    }

    // ===================== AUTH HELPER =====================
    private User getAuthenticatedAdmin(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        HttpSession session = req.getSession(false);

        if (session == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return null;
        }

        User user = (User) session.getAttribute("loggedUser");

        if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
            res.sendRedirect(req.getContextPath() + "/login?error=unauthorized");
            return null;
        }

        return user;
    }
}