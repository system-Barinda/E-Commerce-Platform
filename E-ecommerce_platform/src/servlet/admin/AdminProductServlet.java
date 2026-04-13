package servlet.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.AdminDAO;
import dao.ProductDAO;
import model.Product;
import model.User;

@WebServlet("/admin/products")

// 🔥 ENABLE FILE UPLOAD
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 5,   // 5MB
    maxRequestSize = 1024 * 1024 * 10
)

public class AdminProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private AdminDAO adminDAO;
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        adminDAO = new AdminDAO();
        productDAO = new ProductDAO();
    }

    // ===================== VIEW PRODUCTS =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (!isAdminAuthenticated(req, res)) return;

        try {
            List<Product> products = productDAO.getAllProducts();
            req.setAttribute("products", products);

            req.getRequestDispatcher("/WEB-INF/views/admin/products.jsp")
               .forward(req, res);

        } catch (SQLException e) {
            throw new ServletException("Error loading products", e);
        }
    }

    // ===================== HANDLE ACTIONS =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (!isAdminAuthenticated(req, res)) return;

        String action = req.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/admin/products");
            return;
        }

        try {
            switch (action) {
                case "add":
                    handleAdd(req);
                    break;

                case "update":
                    handleUpdate(req);
                    break;

                case "delete":
                    handleDelete(req);
                    break;

                default:
                    res.sendRedirect(req.getContextPath() + "/admin/products?error=invalidAction");
                    return;
            }

            res.sendRedirect(req.getContextPath() + "/admin/products?success=true");

        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect(req.getContextPath() + "/admin/products?error=serverError");
        }
    }

    // ===================== ADD =====================
    private void handleAdd(HttpServletRequest req) throws Exception {
        Product product = buildProduct(req);
        adminDAO.addProduct(product);
    }

    // ===================== UPDATE =====================
    private void handleUpdate(HttpServletRequest req) throws Exception {

        int id = parseId(req.getParameter("id"));
        if (id <= 0) {
            throw new NumberFormatException("Invalid ID");
        }

        Product product = buildProduct(req);
        product.setId(id);

        adminDAO.updateProduct(product);
    }

    // ===================== DELETE =====================
    private void handleDelete(HttpServletRequest req) throws SQLException {

        int id = parseId(req.getParameter("id"));
        if (id <= 0) {
            throw new NumberFormatException("Invalid ID");
        }

        adminDAO.deleteProduct(id);
    }

    // ===================== BUILD PRODUCT =====================
    private Product buildProduct(HttpServletRequest req) throws Exception {

        String name = safeTrim(req.getParameter("name"));
        String description = safeTrim(req.getParameter("description"));
        String category = safeTrim(req.getParameter("category"));

        double price = parseDouble(req.getParameter("price"));
        int stock = parseInt(req.getParameter("stock"));

        // 🔥 HANDLE IMAGE FILE
        Part filePart = req.getPart("imageFile");

        String imageUrl = "";

        if (filePart != null && filePart.getSize() > 0) {

            String fileName = new File(filePart.getSubmittedFileName()).getName();

            // 🔥 CREATE UPLOAD FOLDER
            String uploadPath = getServletContext().getRealPath("") + "uploads";
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            // 🔥 SAVE FILE
            String filePath = uploadPath + File.separator + fileName;
            filePart.write(filePath);

            // 🔥 SAVE PATH TO DB
            imageUrl = "uploads/" + fileName;
        } else {
            // fallback if no image uploaded (for update)
            imageUrl = safeTrim(req.getParameter("imageUrl"));
        }

        // -------- VALIDATION --------
        if (name.isEmpty()) {
            throw new Exception("Product name is required");
        }

        if (price < 0 || stock < 0) {
            throw new Exception("Invalid price or stock");
        }

        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setImageUrl(imageUrl);
        p.setPrice(price);
        p.setStock(stock);

        return p;
    }

    // ===================== AUTH =====================
    private boolean isAdminAuthenticated(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        HttpSession session = req.getSession(false);

        if (session == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        User user = (User) session.getAttribute("loggedUser");

        if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
            res.sendRedirect(req.getContextPath() + "/login?error=unauthorized");
            return false;
        }

        return true;
    }

    // ===================== UTIL METHODS =====================
    private String safeTrim(String value) {
        return (value != null) ? value.trim() : "";
    }

    private int parseId(String value) {
        return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : -1;
    }

    private int parseInt(String value) {
        return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : 0;
    }

    private double parseDouble(String value) {
        return (value != null && !value.isEmpty()) ? Double.parseDouble(value) : 0.0;
    }
}