package com.geca.lostfound.servlet;

import com.geca.lostfound.model.User;
import com.geca.lostfound.service.ClaimService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/claims/submit")
public class ClaimServlet extends HttpServlet {

    private ClaimService claimService = new ClaimService();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");

        Long itemId = Long.parseLong(req.getParameter("itemId"));
        String message = req.getParameter("message");

        claimService.submitClaim(itemId, user, message);

        resp.sendRedirect("../items");
    }
}