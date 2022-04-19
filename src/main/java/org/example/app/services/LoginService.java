package org.example.app.services;

import org.example.app.db.FindInDb;
import org.example.web.dto.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    private boolean isUserAdmin(@NotNull User loginForm) throws IOException {
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

    public boolean authenticate(@NotNull User loginForm) throws IOException {
        User user = findRegisteredUserByLogin(loginForm.getEmail());
        if (user == null) {
            return isUserAdmin(loginForm);
        }
        return loginForm.getPassword().equals(user.getPassword());
    }

    public boolean register(@NotNull User loginForm) throws IOException {
        User user = findRegisteredUserByLogin(loginForm.getEmail());
        if (user == null) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", loginForm.hashCode());
            params.put("email", loginForm.getEmail());
            params.put("password", loginForm.getPassword());
            params.put("avatar", setDefaultAvatarToUser(loginForm));
            String exp = "INSERT INTO users_table(user_id,email,password,name,lastname,offline_time_in_minutes,info,avatar)" +
                    " VALUES (:id,:email,:password,:id,'',0,null,:avatar)";
            jdbcTemplate.update(exp, params);
            logger.info("user '" + loginForm.getEmail() + "' is now registered with avatar: " + params.get("avatar"));
            return false;
        }
        return true;
    }

    @Nullable
    public String setDefaultAvatarToUser(@NotNull User user) throws IOException {
        String imageSrc = user.getEmail().toLowerCase() + " who is it";
        if (imageSrc.chars().filter(c -> c != ' ').count() == 0) {
            return null;
        }
        String spaceChar = "%20";
        String url = "https://yandex.ru/images/search?from=tabbar&text=";
        imageSrc = imageSrc.trim().replace(" ", spaceChar);
        url = url.concat(imageSrc);

        try {
            Document doc = Jsoup.connect(url).get();
            Element firstFoundImageDiv = doc.getElementsByClass("serp-item__link").first();
            if (firstFoundImageDiv == null) {
                return null;
            }
            Element imageItem = firstFoundImageDiv.getElementsByClass("serp-item__thumb justifier__thumb").first();
            assert imageItem != null;
            return imageItem.attributes().get("src").concat(".png");
        } catch (HttpStatusException ex) {
            return null;
        }
    }


}
