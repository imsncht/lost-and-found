package com.geca.lostfound.servlet;

import com.geca.lostfound.service.ClaimService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {
    "/admin/dashboard",
    "/admin/claims",
    "/admin/items",
    "/admin/resolved",
    "/admin/approve",
    "/admin/reject"
})
public class AdminServlet extends HttpServlet {

    private ClaimService claimService = new ClaimService();

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/admin/dashboard".equals(path)) {

            req.getRequestDispatcher(
            "/WEB-INF/views/admin/dashboard.jsp"
            ).forward(req, resp);

        } else if ("/admin/claims".equals(path)) {

            req.setAttribute(
                "claims",
                claimService.getPendingClaims()
            );

            req.getRequestDispatcher(
                "/WEB-INF/views/admin/claims.jsp"
            ).forward(req, resp);

        } else if ("/admin/items".equals(path)) {

            String type = req.getParameter("type");

            req.setAttribute(
            "items",
            new com.geca.lostfound.service.ItemService()
                .getByType(type)
            );

            req.setAttribute("title", type + " Items");

            req.getRequestDispatcher(
                "/WEB-INF/views/admin/items.jsp"
            ).forward(req, resp);

        } else if ("/admin/resolved".equals(path)) {

            req.setAttribute(
                "items",
                new com.geca.lostfound.service.ItemService()
                    .getArchive()
            );

            req.setAttribute("title", "Resolved Items");

            req.getRequestDispatcher(
                "/WEB-INF/views/admin/items.jsp"
            ).forward(req, resp);

        } else if ("/admin/approve".equals(path)) {

            Long id =
                Long.parseLong(req.getParameter("id"));

            claimService.approve(id);

            resp.sendRedirect("claims");

        } else if ("/admin/reject".equals(path)) {

            Long id =
                Long.parseLong(req.getParameter("id"));

            claimService.reject(id);

            resp.sendRedirect("claims");
        }
    }
}