package com.geca.lostfound.servlet;

import com.geca.lostfound.model.User;
import com.geca.lostfound.service.ItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
//import java.text.SimpleDateFormat;

@WebServlet(urlPatterns = {"/items", "/items/post", "/items/delete", "/items/close", "/items/detail", "/items/archive"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
public class ItemServlet extends HttpServlet {

    private ItemService itemService = new ItemService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/items".equals(path)) {

            req.setAttribute("items", itemService.getAllItems());
            req.getRequestDispatcher("/WEB-INF/views/items/board.jsp").forward(req, resp);

        } else if ("/items/detail".equals(path)) {

            Long id = Long.parseLong(req.getParameter("id"));

            req.setAttribute("item", itemService.getById(id));

        req.getRequestDispatcher("/WEB-INF/views/items/detail.jsp")
            .forward(req, resp);
            
        } else if ("/items/delete".equals(path)) {

            Long id = Long.parseLong(req.getParameter("id"));
            itemService.delete(id);
            resp.sendRedirect("../items");

        } else if ("/items/close".equals(path)) {

            Long id = Long.parseLong(req.getParameter("id"));
            itemService.closeItem(id);
            resp.sendRedirect("../items");

        } else if ("/items/archive".equals(path)) {

            req.setAttribute(
                "items",
                itemService.getArchive()
            );

            req.getRequestDispatcher(
                "/WEB-INF/views/items/archive.jsp"
            ).forward(req, resp);
        }
        else {
            req.getRequestDispatcher("/WEB-INF/views/items/post.jsp").forward(req, resp);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        try {

            User user = (User) req.getSession().getAttribute("user");

            Part filePart = req.getPart("image");

            String fileName = System.currentTimeMillis() + "_" +
                    filePart.getSubmittedFileName();

            String uploadPath =
                    System.getProperty("user.home")
                    + java.io.File.separator
                    + "lostfound"
                    + java.io.File.separator
                    + "uploads";

            java.io.File folder = new java.io.File(uploadPath);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            if (filePart.getSize() > 0) {
            filePart.write(uploadPath + java.io.File.separator + fileName);
            } else {
            fileName = null;
            }

            com.geca.lostfound.model.Item item =
                    new com.geca.lostfound.model.Item();

            item.setTitle(req.getParameter("title"));
            item.setDescription(req.getParameter("description"));
            item.setCategory(req.getParameter("category"));
            item.setLocation(req.getParameter("location"));
            item.setItemDate(
                    new java.text.SimpleDateFormat("yyyy-MM-dd")
                            .parse(req.getParameter("itemDate"))
            );
            item.setType(req.getParameter("type"));
            item.setStatus("OPEN");
            item.setUser(user);
            item.setImagePath(fileName);

            new com.geca.lostfound.dao.ItemDAO().save(item);

            resp.sendRedirect("../items");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("../items/post");
        }
    }
}