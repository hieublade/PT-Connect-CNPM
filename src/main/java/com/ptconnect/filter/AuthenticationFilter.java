package com.ptconnect.filter;

import com.ptconnect.model.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (path.isEmpty() || path.equals("/") || path.startsWith("/static/") || path.startsWith("/uploads/")
                || path.equals("/login") || path.equals("/register") || path.equals("/logout")) {
            chain.doFilter(req, res);
            return;
        }

        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if ("SUSPENDED".equals(user.getStatus())) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login?error=suspended");
            return;
        }

        if ((path.startsWith("/admin-") || path.startsWith("/user-management") || path.startsWith("/pt-pending-approval"))
                && !"ADMIN".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if ((path.startsWith("/pt-home") || path.startsWith("/pt-booking-approval") || path.startsWith("/manage-"))
                && !"PT".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if ((path.startsWith("/student-home") || path.startsWith("/discovery-pt") || path.startsWith("/pt-detail")
                || path.startsWith("/wallet") || path.startsWith("/my-course") || path.startsWith("/my-schedule")
                || path.startsWith("/cancel-booking") || path.startsWith("/booking-package")
                || path.startsWith("/confirm-purchase") || path.startsWith("/profile"))
                && !"STUDENT".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(req, res);
    }
}
