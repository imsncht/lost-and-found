package com.geca.lostfound.service;

import com.geca.lostfound.dao.UserDAO;
import com.geca.lostfound.model.User;
import com.geca.lostfound.util.PasswordUtil;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    public boolean register(String name, String email, String password) {

        User existing = userDAO.findByEmail(email);

        if (existing != null) {
            return false;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setRole("USER");

        userDAO.save(user);

        return true;
    }

    public User login(String email, String password) {

        User user = userDAO.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (PasswordUtil.checkPassword(password, user.getPassword())) {
            return user;
        }

        return null;
    }
}