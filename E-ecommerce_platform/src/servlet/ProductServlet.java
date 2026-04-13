package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.ProductDAO;
import model.Product;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    // ===================== PRODUCT LIST =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // -------- PARAMETERS --------
        String category = req.getParameter("category");
        String keyword  = req.getParameter("search");

        // Normalize inputs
        category = (category != null) ? category.trim() : "";
        keyword  = (keyword != null) ? keyword.trim() : "";

        try {
            List<Product> products;

            // -------- FILTER LOGIC --------
            if (!keyword.isEmpty()) {
                products = productDAO.searchProducts(keyword);

            } else if (!category.isEmpty()) {
                products = productDAO.getByCategory(category);

            } else {
                products = productDAO.getAllProducts();
            }

            // -------- LOAD CATEGORIES --------
            List<String> categories = productDAO.getAllCategories();

            // -------- ATTRIBUTES --------
            req.setAttribute("products", products);
            req.setAttribute("categories", categories);
            req.setAttribute("selectedCategory", category);
            req.setAttribute("keyword", keyword);

            // -------- FORWARD --------
            req.getRequestDispatcher("/WEB-INF/views/product/list.jsp")
               .forward(req, res);

        } catch (SQLException e) {
            throw new ServletException("Error loading products", e);
        }
    }

    // ===================== BLOCK POST =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Always redirect POST → GET
        res.sendRedirect(req.getContextPath() + "/products");
    }
}