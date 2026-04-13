package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

import java.io.IOException;

import model.User;

@WebFilter("/admin/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Optional init
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);

        // -------- PREVENT CACHING --------
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // -------- GET USER --------
        User user = (session != null) ? (User) session.getAttribute("loggedUser") : null;

        boolean isAdmin = (user != null && "admin".equalsIgnoreCase(user.getRole()));

        // -------- ALLOW ACCESS --------
        if (isAdmin) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
        }
    }

    @Override
    public void destroy() {
        // Optional cleanup
    }
}