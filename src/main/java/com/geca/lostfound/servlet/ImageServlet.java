package com.geca.lostfound.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/images")
public class ImageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        String fileName = req.getParameter("name");

        if (fileName == null || fileName.trim().isEmpty()) {
            resp.sendError(404);
            return;
        }

        String path =
                System.getProperty("user.home")
                + File.separator
                + "lostfound"
                + File.separator
                + "uploads"
                + File.separator
                + fileName;

        File file = new File(path);

        if (!file.exists()) {
            resp.sendError(404);
            return;
        }

        String mime =
                getServletContext().getMimeType(file.getName());

        if (mime == null) mime = "application/octet-stream";

        resp.setContentType(mime);
        resp.setContentLengthLong(file.length());

        FileInputStream in = new FileInputStream(file);
        OutputStream out = resp.getOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }

        in.close();
        out.flush();
    }
}