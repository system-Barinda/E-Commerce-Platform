package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import model.User;
import model.Cart;
import dao.CartDAO;
import dao.OrderDAO;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private CartDAO cartDAO;
    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        cartDAO = new CartDAO();
        orderDAO = new OrderDAO();
    }

    // ===================== LOAD CHECKOUT PAGE =====================
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
            List<Cart> items = cartDAO.getCartItems(user.getId());

            // If cart is empty → redirect
            if (items == null || items.isEmpty()) {
                res.sendRedirect(req.getContextPath() + "/cart");
                return;
            }

            double total = cartDAO.getCartTotal(user.getId());

            req.setAttribute("cartItems", items);
            req.setAttribute("cartTotal", total);

            req.getRequestDispatcher("/WEB-INF/views/cart/checkout.jsp").forward(req, res);

        } catch (SQLException e) {
            throw new ServletException("Error loading checkout page", e);
        }
    }

    // ===================== PROCESS CHECKOUT =====================
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

        try {
            List<Cart> items = cartDAO.getCartItems(user.getId());

            // Prevent empty checkout
            if (items == null || items.isEmpty()) {
                res.sendRedirect(req.getContextPath() + "/cart?error=empty");
                return;
            }

            double total = cartDAO.getCartTotal(user.getId());

            int orderId = orderDAO.placeOrder(user.getId(), items, total);

            if (orderId > 0) {
                // Clear cart only after successful order
                cartDAO.clearCart(user.getId());

                res.sendRedirect(req.getContextPath() + "/order-success?id=" + orderId);
            } else {
                req.setAttribute("error", "Failed to place order. Please try again.");
                req.getRequestDispatcher("/WEB-INF/views/cart/checkout.jsp").forward(req, res);
            }

        } catch (SQLException e) {
            throw new ServletException("Database error during checkout", e);
        }
    }
}