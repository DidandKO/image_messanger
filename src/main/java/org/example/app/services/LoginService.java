package org.example.app.services;

import org.example.web.dto.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@ComponentScan(value = "jdbc_template")
public class LoginService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LoginService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User isRegistered(String email) {
        AtomicReference<User> regUser = new AtomicReference<>(null);

        jdbcTemplate.query("SELECT * FROM users_table", (ResultSet rs, int rowNum) -> {
            User user = new User();
            if (rs.getString("email").equals(email)) {
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                regUser.set(user);
            }
            return user;
        });

        return regUser.get();
    }

    public boolean authenticate(@NotNull User loginForm) {
        User user = isRegistered(loginForm.getEmail());
        if (user == null) {
            return false;
        }

        return loginForm.getPassword().equals(user.getPassword());
    }

    public boolean register(@NotNull User loginForm) {
        User user = isRegistered(loginForm.getEmail());
        if (user == null) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", loginForm.hashCode());
            params.put("email", loginForm.getEmail());
            params.put("password", loginForm.getPassword());
            String exp = "INSERT INTO users_table(user_id,email,password,name,lastname,offline_time_in_minutes)" +
                    " VALUES (:id,:email,:password,:id,'',0)";
            jdbcTemplate.update(exp, params);
            return false;
        }
        return true;
    }
}
