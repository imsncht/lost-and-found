package com.geca.lostfound.servlet;

import com.geca.lostfound.model.User;
import com.geca.lostfound.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private UserService userService = new UserService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            req.getSession().invalidate();
            resp.sendRedirect("login");
            return;
        }

        if ("/register".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/register".equals(path)) {

            boolean ok = userService.register(
                    req.getParameter("name"),
                    req.getParameter("email"),
                    req.getParameter("password")
            );

            if (ok) {
                resp.sendRedirect("login");
            } else {
                req.setAttribute("error", "Email already exists");
                doGet(req, resp);
            }

        } else {

            User user = userService.login(
                    req.getParameter("email"),
                    req.getParameter("password")
            );

            if (user != null) {

                HttpSession session = req.getSession();
                session.setAttribute("user", user);

                if (user.isAdmin()) {
                    resp.sendRedirect("admin/dashboard");
                } else {
                    resp.sendRedirect("items");
                }

            } else {
                req.setAttribute("error", "Invalid credentials");
                doGet(req, resp);
            }
        }
    }
}