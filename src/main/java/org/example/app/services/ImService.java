package org.example.app.services;

import org.example.web.dto.Dialog;
import org.example.web.dto.Message;
import org.example.web.dto.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Service
public class ImService {

    final String FIRST_FOUND_IMAGE_A_CLASS = "serp-item__link";
    final String FIRST_FOUND_IMAGE_CLASS = "serp-item__thumb justifier__thumb";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ImService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Nullable("Dialog cannot be null")
    public List<Message> getDialogMessageList(int dialogId, @NotNull User user) {
        return
                (getCurrentDialogById(dialogId, user) != null) ?
                        Objects.requireNonNull(getCurrentDialogById(dialogId, user)).getMessageList() :
                            null;
    }

    @Nullable("No suck dialog")
    public Dialog getCurrentDialogById(int dialogId, @NotNull User user) {
        for (Dialog dialog : user.getDialogs())
            if (dialog.getDialog_id() == dialogId) {
                return dialog;
            }
        return null;
    }

    @Nullable("No user with this login")
    public User getUserByLogin(String login) {
        AtomicReference<User> user = new AtomicReference<>(null);

        jdbcTemplate.query("SELECT * FROM users_table", (ResultSet rs, int rowNum) -> {
            User tempUser = new User();
            if (rs.getString("email").equals(login)) {
                tempUser.setUser_id(rs.getInt("user_id"));
                tempUser.setEmail(rs.getString("email"));
                tempUser.setPassword(rs.getString("password"));
                tempUser.setName(rs.getString("name"));
                tempUser.setLastName(rs.getString("lastname"));
                tempUser.setOfflineTimeInMinutes(rs.getInt("offline_time_in_minutes"));
                user.set(tempUser);
            }
            return user;
        });

        return user.get();
    }

    @Nullable
    private String getImageSrc(String url) throws IOException {
        try {
            Document doc = Jsoup.connect(url).get();
            Element firstFoundImageDiv = doc.getElementsByClass(FIRST_FOUND_IMAGE_A_CLASS).first();
            Element imageItem = firstFoundImageDiv.getElementsByClass(FIRST_FOUND_IMAGE_CLASS).first();
            return imageItem.attributes().get("src");
        } catch (HttpStatusException ex) {
            return null;
        }
    }

    private void setAnyImageAttrToMessage(@NotNull String messageBody, Message message) throws IOException {
        String imageSrc = convertTextIntoImage(messageBody.toLowerCase());
        if (imageSrc == null) {
            byte[] imageData = Base64.getDecoder().decode(messageBody);
            ImageIcon imageIcon = new ImageIcon(imageData);
            Image image = imageIcon.getImage();
            message.setImageBody(image);
            message.setByte_code(imageData);
        } else {
            message.setImageSrc(imageSrc);
        }
    }

    @Nullable
    private String convertTextIntoImage(String text) throws IOException {
        if (text == null || text.chars().filter(c -> c != ' ').count() == 0) {
            return null;
        }
        String spaceChar = "%20";
        String url = "https://yandex.ru/images/search?from=tabbar&text=";
        if (text.contains(" ")) {
            String[] wordsInMessage = text.trim().split(" ");
            IntStream
                    .range(0, wordsInMessage.length - 1)
                    .forEach(i -> wordsInMessage[i] = wordsInMessage[i].concat(spaceChar));
            for (String s : wordsInMessage) {
                url = url.concat(s);
            }
        } else {
            url = url.concat(text);
        }
        return getImageSrc(url);
    }

    public Message createMessage(String messageBody, @NotNull Dialog dialog) throws IOException {
        Message message = new Message();
        message.setMessage_id(message.hashCode());
        message.setDialog_id(dialog.getDialog_id());
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));
        message.setBody(messageBody);
        setAnyImageAttrToMessage(messageBody, message);
        insertMessageIntoDb(message, dialog);
        return message;
    }

    private void insertMessageIntoDb(@NotNull Message message, @NotNull Dialog dialog) {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", message.getMessage_id());
        params.put("dialog_id", dialog.getDialog_id());
        params.put("timestamp", message.getTimestamp());
        params.put("body", message.getBody());
        params.put("image_src", message.getImageSrc());
        params.put("byte_code", Arrays.toString(message.getByte_code()));
        String exp = "INSERT INTO messages(message_id,timestamp,body,image_src,byte_code)" +
                " VALUES (:message_id,:timestamp,:body,:image_src,:byte_code)";
        jdbcTemplate.update(exp, params);
        exp = "INSERT INTO messages_in_dialog(dialog_id,message_id)" +
                " VALUES (:dialog_id,:message_id)";
        jdbcTemplate.update(exp, params);
    }

    public Dialog createDialog(User dialogOwner, User partner, String subject) {
        Dialog dialog = new Dialog();
        dialog.setDialog_id(dialog.hashCode());
        dialog.setDialogOwner(dialogOwner);
        dialog.setPartner(partner);
        dialog.setSubject(subject);
        insertDialogIntoDb(dialog);
        return dialog;
    }

    private void insertDialogIntoDb(@NotNull Dialog dialog) {
        Map<String, Object> params = new HashMap<>();
        params.put("dialog_id", dialog.hashCode());
        params.put("dialog_owner", dialog.getDialogOwner().getUser_id());
        params.put("partner", dialog.getPartner().getUser_id());
        params.put("subject", dialog.getSubject());
        params.put("new_messages_count", dialog.getNewMessagesCount());
        String exp = "INSERT INTO dialog(dialog_id,dialog_owner,partner,subject,new_messages_count)" +
                " VALUES (:dialog_id,:dialog_owner,:partner,:subject,:new_messages_count)";
        jdbcTemplate.update(exp, params);
    }
}