package com.geca.lostfound.servlet;

import com.geca.lostfound.model.User;
import com.geca.lostfound.service.ItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;

@WebServlet(urlPatterns = {"/items", "/items/post", "/items/delete", "/items/close"})
public class ItemServlet extends HttpServlet {

    private ItemService itemService = new ItemService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/items".equals(path)) {

            req.setAttribute("items", itemService.getAllItems());
            req.getRequestDispatcher("/WEB-INF/views/items/board.jsp").forward(req, resp);

        } else if ("/items/delete".equals(path)) {

            Long id = Long.parseLong(req.getParameter("id"));
            itemService.delete(id);
            resp.sendRedirect("../items");

        } else if ("/items/close".equals(path)) {

            Long id = Long.parseLong(req.getParameter("id"));
            itemService.closeItem(id);
            resp.sendRedirect("../items");

        } else {
            req.getRequestDispatcher("/WEB-INF/views/items/post.jsp").forward(req, resp);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {

            User user = (User) req.getSession().getAttribute("user");

            itemService.postItem(
                    req.getParameter("title"),
                    req.getParameter("description"),
                    req.getParameter("category"),
                    req.getParameter("location"),
                    new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("itemDate")),
                    req.getParameter("type"),
                    user
            );

            resp.sendRedirect("../items");

        } catch (Exception e) {
            req.setAttribute("error", "Invalid data");
            doGet(req, resp);
        }
    }
}