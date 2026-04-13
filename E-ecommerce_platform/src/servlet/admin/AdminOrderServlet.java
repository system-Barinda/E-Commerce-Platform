package servlet.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.AdminDAO;
import model.Order;
import model.User;

@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        adminDAO = new AdminDAO();
    }

    // ===================== VIEW ORDERS =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = getAuthenticatedAdmin(req, res);
        if (user == null) return;

        try {
            List<Order> orders = adminDAO.getAllOrders();

            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(req, res);

        } catch (SQLException e) {
            throw new ServletException("Error fetching orders for admin", e);
        }
    }

    // ===================== UPDATE ORDER STATUS =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = getAuthenticatedAdmin(req, res);
        if (user == null) return;

        String orderIdStr = req.getParameter("orderId");
        String status = req.getParameter("status");

        // -------- VALIDATION --------
        if (orderIdStr == null || status == null || status.trim().isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/admin/orders?error=missingFields");
            return;
        }

        status = status.trim();

        try {
            int orderId = Integer.parseInt(orderIdStr);

            if (orderId <= 0) {
                res.sendRedirect(req.getContextPath() + "/admin/orders?error=invalidId");
                return;
            }

            boolean updated = adminDAO.updateOrderStatus(orderId, status);

            if (updated) {
                res.sendRedirect(req.getContextPath() + "/admin/orders?success=updated");
            } else {
                res.sendRedirect(req.getContextPath() + "/admin/orders?error=notFound");
            }

        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/admin/orders?error=invalidId");
        } catch (SQLException e) {
            throw new ServletException("Database error updating order status", e);
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