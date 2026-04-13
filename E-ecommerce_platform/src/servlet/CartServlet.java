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

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private CartDAO cartDAO;

    @Override
    public void init() throws ServletException {
        cartDAO = new CartDAO();
    }

    // ===================== VIEW CART =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false); // safer
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
            double total = cartDAO.getCartTotal(user.getId());

            req.setAttribute("cartItems", items);
            req.setAttribute("cartTotal", total);

            req.getRequestDispatcher("/WEB-INF/views/cart/cart.jsp").forward(req, res);

        } catch (SQLException e) {
            throw new ServletException("Error retrieving cart data", e);
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
        if (action == null || action.trim().isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        try {
            switch (action) {

                // -------- ADD TO CART --------
                case "add":
                    handleAdd(req, res, user);
                    break;

                // -------- UPDATE QUANTITY --------
                case "update":
                    handleUpdate(req, res);
                    break;

                // -------- REMOVE ITEM --------
                case "remove":
                    handleRemove(req, res);
                    break;

                default:
                    res.sendRedirect(req.getContextPath() + "/cart");
            }

        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/cart?error=invalidInput");
        } catch (SQLException e) {
            throw new ServletException("Database error processing cart action", e);
        }
    }

    // ===================== HELPER METHODS =====================

    private void handleAdd(HttpServletRequest req, HttpServletResponse res, User user)
            throws SQLException, IOException {

        String productIdStr = req.getParameter("productId");

        if (productIdStr == null) {
            res.sendRedirect(req.getContextPath() + "/cart?error=missingProduct");
            return;
        }

        int productId = Integer.parseInt(productIdStr);

        String qtyStr = req.getParameter("quantity");
        int quantity = (qtyStr != null && !qtyStr.isEmpty()) ? Integer.parseInt(qtyStr) : 1;

        if (quantity <= 0) quantity = 1;

        cartDAO.addToCart(user.getId(), productId, quantity);

        res.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse res)
            throws SQLException, IOException {

        String cartIdStr = req.getParameter("cartId");
        String qtyStr = req.getParameter("quantity");

        if (cartIdStr == null || qtyStr == null) {
            res.sendRedirect(req.getContextPath() + "/cart?error=missingData");
            return;
        }

        int cartId = Integer.parseInt(cartIdStr);
        int quantity = Integer.parseInt(qtyStr);

        if (quantity <= 0) {
            cartDAO.removeItem(cartId);
        } else {
            cartDAO.updateQuantity(cartId, quantity);
        }

        res.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse res)
            throws SQLException, IOException {

        String cartIdStr = req.getParameter("cartId");

        if (cartIdStr == null) {
            res.sendRedirect(req.getContextPath() + "/cart?error=missingCartId");
            return;
        }

        int cartId = Integer.parseInt(cartIdStr);

        cartDAO.removeItem(cartId);

        res.sendRedirect(req.getContextPath() + "/cart");
    }
}