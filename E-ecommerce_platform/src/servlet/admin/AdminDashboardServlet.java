package servlet.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.AdminDAO;
import model.User;
import model.Order;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        adminDAO = new AdminDAO();
    }

    // ===================== ADMIN DASHBOARD =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = getAuthenticatedAdmin(req, res);
        if (user == null) return; // already redirected

        try {
            // -------- LOAD DASHBOARD DATA --------
            req.setAttribute("totalUsers", adminDAO.getTotalUsers());
            req.setAttribute("totalProducts", adminDAO.getTotalProducts());
            req.setAttribute("totalOrders", adminDAO.getTotalOrders());
            req.setAttribute("totalRevenue", adminDAO.getTotalRevenue());

            // Optional: limit recent orders (better performance)
            List<Order> recentOrders = adminDAO.getAllOrders();
            req.setAttribute("recentOrders", recentOrders);

            // -------- FORWARD --------
            req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp")
               .forward(req, res);

        } catch (SQLException e) {
            throw new ServletException("Error loading admin dashboard", e);
        }
    }

    // ===================== BLOCK POST =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.sendRedirect(req.getContextPath() + "/admin/dashboard");
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

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return null;
        }

        if (!"admin".equalsIgnoreCase(user.getRole())) {
            res.sendRedirect(req.getContextPath() + "/login?error=unauthorized");
            return null;
        }

        return user;
    }
}