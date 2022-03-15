package org.example.app.services;

import org.example.app.db.FindInDb;
import org.example.web.dto.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
@ComponentScan(value = "jdbc_template")
public class LoginService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    Logger logger = Logger.getLogger("logger");

    public LoginService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private boolean isUserAdmin(@NotNull User loginForm) {
        if (loginForm.getEmail().equals("root") && loginForm.getPassword().equals("123")) {
            register(loginForm);
            return true;
        }
        return false;
    }

    @Nullable
    public User findRegisteredUserByLogin(String email) {
        User user = FindInDb.findUserWithoutDialogsByLogin(email, jdbcTemplate);
        if (user == null) {
            return null;
        }
        user.setDialogs(FindInDb.findDialogsByUserLogin(email, jdbcTemplate));
        return user;
    }

    public boolean authenticate(@NotNull User loginForm) {
        User user = findRegisteredUserByLogin(loginForm.getEmail());
        if (user == null) {
            return isUserAdmin(loginForm);
        }
        return loginForm.getPassword().equals(user.getPassword());
    }

    public boolean register(@NotNull User loginForm) {
        User user = findRegisteredUserByLogin(loginForm.getEmail());
        if (user == null) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", loginForm.hashCode());
            params.put("email", loginForm.getEmail());
            params.put("password", loginForm.getPassword());
            String exp = "INSERT INTO users_table(user_id,email,password,name,lastname,offline_time_in_minutes)" +
                    " VALUES (:id,:email,:password,:id,'',0)";
            jdbcTemplate.update(exp, params);
            logger.info("user '" + loginForm.getEmail() + "' is now registered");
            return false;
        }
        return true;
    }
}
