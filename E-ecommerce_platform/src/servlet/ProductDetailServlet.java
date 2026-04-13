package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

import model.Product;
import dao.ProductDAO;

@WebServlet("/product")
public class ProductDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    // ===================== VIEW PRODUCT DETAIL =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");

        // -------- VALIDATE ID PARAM --------
        if (idParam == null || idParam.trim().isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        try {
            int id = Integer.parseInt(idParam.trim());

            // Prevent invalid IDs like negative numbers
            if (id <= 0) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID.");
                return;
            }

            // -------- FETCH PRODUCT --------
            Product product = productDAO.getProductById(id);

            if (product == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found.");
                return;
            }

            // -------- FORWARD TO VIEW --------
            req.setAttribute("product", product);
            req.getRequestDispatcher("/WEB-INF/views/product/detail.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format.");
        } catch (SQLException e) {
            throw new ServletException("Database error retrieving product details", e);
        }
    }

    // ===================== OPTIONAL: BLOCK POST =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Product detail should not accept POST → redirect
        res.sendRedirect(req.getContextPath() + "/products");
    }
}