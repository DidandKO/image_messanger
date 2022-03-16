package org.example.app.db;

import org.example.web.dto.Dialog;
import org.example.web.dto.Message;
import org.example.web.dto.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FindInDb {

    public static User findUserWithoutDialogsByLogin(String login, @NotNull NamedParameterJdbcTemplate jdbcTemplate) {
        AtomicReference<User> tempUser = new AtomicReference<>(null);
        jdbcTemplate.query("SELECT * FROM users_table", (ResultSet rs, int rowNum) -> {
            User user = new User();
            if (rs.getString("email").equals(login)) {
                user = findUser(rs);
                tempUser.set(user);
            }
            return user;
        });
        return tempUser.get();
    }

    public static User findUserWithoutDialogsById(int id, @NotNull NamedParameterJdbcTemplate jdbcTemplate) {
        AtomicReference<User> tempUser = new AtomicReference<>(null);
        jdbcTemplate.query("SELECT * FROM users_table", (ResultSet rs, int rowNum) -> {
            User user = new User();
            if (rs.getInt("user_id") == id) {
                user = findUser(rs);
                tempUser.set(user);
            }
            return user;
        });
        return tempUser.get();
    }

    @NotNull
    public static List<Dialog> findDialogsByUserId(int user_id, @NotNull NamedParameterJdbcTemplate jdbcTemplate) {
        List<Dialog> tempDialogs = new ArrayList<>();
        jdbcTemplate.query("SELECT * FROM dialog", (ResultSet rs, int rowNum) -> {
            Dialog dialog = new Dialog();
            if (rs.getInt("dialog_owner") == user_id) {
                dialog = findDialog(rs, jdbcTemplate);
                dialog.setMessageList(findMessagesByDialogId(dialog.getDialog_id(), jdbcTemplate));
                tempDialogs.add(dialog);
            }
            return dialog;
        });
        return tempDialogs;
    }

    @NotNull
    public static List<Dialog> findDialogsByUserLogin(String login, @NotNull NamedParameterJdbcTemplate jdbcTemplate) {
        int user_id = findUserWithoutDialogsByLogin(login, jdbcTemplate).getUser_id();
        return findDialogsByUserId(user_id, jdbcTemplate);
    }

    @NotNull
    public static List<Message> findMessagesByDialogId(int dialog_id, @NotNull NamedParameterJdbcTemplate jdbcTemplate) {
        List<Message> tempMessages = new ArrayList<>();
        jdbcTemplate.query("SELECT * FROM messages_in_dialog", (ResultSet rs, int rowNum) -> {
            Message message = new Message();
            if (rs.getInt("dialog_id") == dialog_id) {
                message.setMessage_id(rs.getInt("message_id"));
                message.setDialog_id(rs.getInt("dialog_id"));

                jdbcTemplate.query("SELECT * FROM messages", (ResultSet rs1, int rowNum1) -> {
                    if (rs1.getInt("message_id") == message.getMessage_id()) {
                        findMessage(message, rs1);
                    }
                    return message;
                });

                tempMessages.add(message);
            }
            return message;
        });
        return tempMessages;
    }

    @NotNull
    public static Message findMessageWithoutDialogIdById(int id, @NotNull NamedParameterJdbcTemplate jdbcTemplate) {
        Message message = new Message();
        jdbcTemplate.query("SELECT * FROM messages", (ResultSet rs, int rowNum) -> {
            if (rs.getInt("message_id") == id) {
                message.setMessage_id(rs.getInt("message_id"));
                findMessage(message, rs);
            }
            return message;
        });
        return message;
    }

    private static void findMessage(@NotNull Message message, @NotNull ResultSet rs) throws SQLException {
        message.setTimestamp(rs.getString("timestamp"));
        message.setBody(rs.getString("body"));
        message.setImageSrc(rs.getString("image_src"));
        if (message.getImageSrc() == null) {
            message.setByte_code(rs.getString("byte_code").getBytes());
            ImageIcon imageIcon = new ImageIcon(rs.getString("byte_code").getBytes());
            Image image = imageIcon.getImage();
            message.setImageBody(image);
        }
    }

    @NotNull
    private static User findUser(@NotNull ResultSet rs) throws SQLException {
        User user = new User();
        user.setUser_id(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("name"));
        user.setLastName(rs.getString("lastname"));
        user.setOfflineTimeInMinutes(rs.getInt("offline_time_in_minutes"));
        user.setInfo(rs.getString("info"));
        user.setAvatar(rs.getString("avatar"));
        return user;
    }

    @NotNull
    private static Dialog findDialog(@NotNull ResultSet rs, NamedParameterJdbcTemplate jdbcTemplate) throws SQLException {
        Dialog dialog = new Dialog();
        dialog.setDialog_id(rs.getInt("dialog_id"));
        dialog.setDialogOwner(findUserWithoutDialogsById(rs.getInt("dialog_owner"), jdbcTemplate));
        dialog.setPartner(findUserWithoutDialogsById(rs.getInt("partner"), jdbcTemplate));
        dialog.setSubject(rs.getString("subject"));
        dialog.setNewMessagesCount(rs.getInt("new_messages_count"));
        return dialog;
    }
}
